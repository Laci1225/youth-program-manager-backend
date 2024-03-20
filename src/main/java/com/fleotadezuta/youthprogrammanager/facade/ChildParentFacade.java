package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChildParentFacade {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;
    private final ParentService parentService;
    private final ChildService childService;
    private final Auth0Service auth0Service;

    public Flux<ParentDto> getPotentialParents(String name) {
        return parentService.findByFullName(name);
    }

    public Flux<ChildDto> getPotentialChildren(String name) {
        return childService.findByFullName(name);
    }

    public Flux<ParentUpdateDto> getAllParents() {
        return parentService.findAll()
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parent ->
                        childService.findByParentId(parent.getId())
                                .map(childMapper::fromChildDtoToChildDocument)
                                .map(ChildDocument::getId)
                                .collectList()
                                .map(childIds -> {
                                    ParentUpdateDto parentUpdateDto = parentMapper.fromParentDocumentToParentUpdateDto(parent);
                                    parentUpdateDto.setChildIds(childIds);
                                    return parentUpdateDto;
                                })
                );
    }


    public Mono<ChildDto> addChild(ChildCreateDto childCreateDto) {
        RelativeParent relativeParent = childCreateDto.getRelativeParent();
        if (relativeParent == null) {
            ChildDocument childDocument = childMapper.fromChildCreationDtoToChildDocument(childCreateDto);
            return childRepository.save(childDocument)
                    .map(childMapper::fromChildDocumentToChildDto);
        }
        Mono<ParentDocument> parentMono = parentService.findById(relativeParent.getId())
                .map(parentMapper::fromParentDtoToParentDocument)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid parentId: " + relativeParent.getId())));

        return Flux.merge(parentMono)
                .then(childRepository.save(childMapper.fromChildCreationDtoToChildDocument(childCreateDto)))
                .map(childMapper::fromChildDocumentToChildDto)
                .onErrorResume(Mono::error);
    }

    public Mono<ParentDto> deleteParent(String id) {
        return parentService.findById(id)
                .flatMap(parent -> parentService.deleteById(id)
                        .then(childService.removeParentFromChildren(parent.getId()))
                        .thenReturn(parent));
    }


    public Mono<ChildWithParentsDto> getChildById(String id) {

        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = Optional.ofNullable(child.getRelativeParents())
                            .map(relParents -> relParents.stream()
                                    .map(RelativeParent::getId)
                                    .toList())
                            .orElse(Collections.emptyList());
                    return parentService.findAllById(parentIds)
                            .collectMap(parentDto -> parentDto, parent -> child.getRelativeParents()
                                    .stream()
                                    .filter(rp -> rp.getId().equals(parent.getId()))
                                    .findFirst()
                                    .map(RelativeParent::getIsEmergencyContact)
                                    .orElseThrow(() -> new IllegalArgumentException(""))
                            )
                            .flatMap(parentsEmergencyContactMap -> {
                                ChildWithParentsDto childWithParentsDto = childMapper.fromChildDtoToChildWithParentsDocument(child);
                                Mono<List<ParentWithContactDto>> parentsListMono = Flux.fromIterable(parentsEmergencyContactMap.entrySet())
                                        .flatMap(entry -> Mono.just(entry.getKey())
                                                .map(parentDto -> ParentWithContactDto.builder()
                                                        .parentDto(parentDto)
                                                        .isEmergencyContact(entry.getValue())
                                                        .build()))
                                        .collectList();
                                return parentsListMono.map(parentsList -> {
                                    childWithParentsDto.setParents(parentsList);
                                    return childWithParentsDto;
                                });
                            });
                });
    }

    public Mono<ParentWithChildrenDto> getParentById(UserDetails userDetails, String id) {
        if (id.equals("me") || userDetails.getUserId().equals(id)) {
            return parentService.findById(userDetails.getUserId())
                    .map(parentMapper::fromParentDtoToParentDocument)
                    .flatMap(this::getChildDetails);
        } else if (userDetails.getUserType().equals("ADMIN")) {
            return parentService.findById(id)
                    .map(parentMapper::fromParentDtoToParentDocument)
                    .flatMap(this::getChildDetails);
        }
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private Mono<ParentWithChildrenDto> getChildDetails(ParentDocument parent) {
        return childService.findByParentId(parent.getId())
                .collectList()
                .map(children -> {
                    var parentWithChildrenDto = parentMapper.fromParentDocumentToParentWithChildrenDto(parent);
                    List<ChildDto> childDtos = new ArrayList<>(children);
                    parentWithChildrenDto.setChildDtos(childDtos);
                    return parentWithChildrenDto;
                });
    }

    public Mono<ParentDto> addParent(ParentCreateDto parentCreateDto) {
        var parentDtoMono = parentService.validateParent(parentMapper.fromParentCreateDtoToParentDto(parentCreateDto))
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parentService::save)
                .flatMap(validatedParent -> {
                    String childId = parentCreateDto.getChildId();
                    if (childId == null || childId.isEmpty()) {
                        return Mono.just(validatedParent);
                    } else {
                        return childService.findById(childId)
                                .map(childMapper::fromChildDtoToChildDocument)
                                .flatMap(childDocument -> {
                                    var relativeParents = childDocument.getRelativeParents();
                                    relativeParents.add(new RelativeParent(validatedParent.getId(), true));
                                    childDocument.setRelativeParents(relativeParents);
                                    return childService.save(childDocument)
                                            .thenReturn(validatedParent);
                                });
                    }
                });
        return parentDtoMono.doOnSuccess(auth0Service::addAuth0Parent);
    }


    public Mono<ParentDto> updateParent(UserDetails userDetails, ParentUpdateDto parentUpdateDto) {
        return getParentById(userDetails, parentUpdateDto.getId())
                .flatMap(previousParent -> {
                    var parentDto = parentMapper.fromParentUpdateDtoToParentDto(parentUpdateDto);
                    return Mono.just(parentDto)
                            .flatMap(parentService::validateParent)
                            .map(parentMapper::fromParentDtoToParentDocument)
                            .flatMap(parentDoc -> {
                                parentDoc.setId(parentDto.getId());
                                List<String> childIds = parentUpdateDto.getChildIds();
                                List<String> previousChildIds = previousParent.getChildDtos().stream()
                                        .map(ChildDto::getId)
                                        .toList();
                                Set<String> combinedChildIds = new HashSet<>(childIds);
                                combinedChildIds.addAll(previousChildIds);

                                return childService.findAllById(combinedChildIds.stream().toList())
                                        .map(childMapper::fromChildDtoToChildDocument)
                                        .flatMap(child -> {
                                            if (!childIds.contains(child.getId()) && previousChildIds.contains(child.getId())) {
                                                List<RelativeParent> childRelativeParents = child.getRelativeParents();
                                                if (childRelativeParents != null) {
                                                    childRelativeParents.removeIf(rp -> rp.getId().equals(parentDoc.getId()));
                                                    child.setRelativeParents(childRelativeParents);
                                                }
                                                return childService.save(child);
                                            }
                                            List<RelativeParent> childRelativeParents = child.getRelativeParents();
                                            if (childRelativeParents == null) {
                                                childRelativeParents = new ArrayList<>();
                                            }
                                            if (childRelativeParents.stream().noneMatch(rp -> rp.getId().equals(parentDoc.getId()))) {
                                                childRelativeParents.add(new RelativeParent(parentDoc.getId(), true));
                                            }
                                            child.setRelativeParents(childRelativeParents);
                                            return childService.save(child);
                                        })
                                        .collectList()
                                        .then(parentService.save(parentDoc));
                            });
                });
    }
}