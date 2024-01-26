package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParents;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ChildParentFacade {
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;
    private final ParentService parentService;
    private final ChildService childService;

    public Flux<ParentDto> getPotentialParents(String name) {
        return parentService.findByFullName(name);
    }

    public Flux<ChildDto> getPotentialChildren(String name) {
        return childService.findByFullName(name);
    }

    public Flux<ParentUpdateDto> getAllParents() {
        return parentService.findAll()
                .flatMap(parent ->
                        childService.findByParentId(parent.getId())
                                .map(ChildDocument::getId)
                                .collectList()
                                .map(childIds -> {
                                    ParentUpdateDto parentUpdateDto = parentMapper.fromParentDocumentToParentUpdateDto(parent);
                                    parentUpdateDto.setChildIds(childIds);
                                    return parentUpdateDto;
                                })
                );
    }


    public Mono<ChildDto> addChild(ChildDto childDto) {
        List<RelativeParents> relativeParents = childDto.getRelativeParents();
        if (relativeParents == null || relativeParents.isEmpty()) {
            ChildDocument childDocument = childMapper.fromChildDtoToChildDocument(childDto);
            return childRepository.save(childDocument)
                    .map(childMapper::fromChildDocumentToChildDto);
        }
        List<Mono<ParentDocument>> parentMonos = relativeParents.stream()
                .map(parent -> parentService.findById(parent.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid parentId: " + parent.getId()))))
                .collect(Collectors.toList());

        return Flux.merge(parentMonos)
                .then(childRepository.save(childMapper.fromChildDtoToChildDocument(childDto)))
                .map(childMapper::fromChildDocumentToChildDto)
                .onErrorResume(Mono::error);
    }

    public Mono<ParentDto> deleteParent(String id) {
        return parentService.findById(id)
                .flatMap(parent -> parentService.deleteById(id)
                        .then(childService.removeParentFromChildren(parent.getId()))
                        .thenReturn(parent))
                .map(parentMapper::fromParentDocumentToParentDto);
    }


    public Mono<ChildWithParentsDto> getChildById(String id) {
        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = Optional.ofNullable(child.getRelativeParents())
                            .map(relParents -> relParents.stream()
                                    .map(RelativeParents::getId)
                                    .toList())
                            .orElse(Collections.emptyList());
                    return parentService.findAllById(parentIds)
                            .collectMap(parentDto -> parentDto, parent -> child.getRelativeParents()
                                    .stream()
                                    .filter(rp -> rp.getId().equals(parent.getId()))
                                    .findFirst()
                                    .map(RelativeParents::getIsEmergencyContact)
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

    public Mono<ParentWithChildrenDto> getParentById(String id) {
        return parentService.findById(id)
                .flatMap(parent -> childService.findByParentId(parent.getId())
                        .collectList()
                        .map(children -> {
                            var parentWithChildrenDto = parentMapper.fromParentDocumentToParentWithChildrenDto(parent);
                            List<ChildDto> childDtos = children.stream()
                                    .map(childMapper::fromChildDocumentToChildDto)
                                    .collect(Collectors.toList());
                            parentWithChildrenDto.setChildDtos(childDtos);
                            return parentWithChildrenDto;
                        }));
    }

    public Mono<ParentDto> addParent(ParentCreateDto parentCreateDto) {
        var parentDto = parentMapper.fromParentCreateDtoToParentDto(parentCreateDto);
        return parentService.validateParent(parentDto)
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parentService::save)
                .flatMap(validatedParent -> {
                    String childId = parentCreateDto.getChildId();
                    if (childId == null || childId.isEmpty()) {
                        return Mono.just(validatedParent);
                    } else {
                        return childService.findById(childId)
                                .flatMap(childDocument -> {
                                    var relativeParents = childDocument.getRelativeParents();
                                    relativeParents.add(new RelativeParents(validatedParent.getId(), true));
                                    childDocument.setRelativeParents(relativeParents);
                                    return childService.save(childDocument)
                                            .thenReturn(validatedParent);
                                });
                    }
                })
                .map(parentMapper::fromParentDocumentToParentDto);
    }


    public Mono<ParentDto> updateParent(ParentUpdateDto parentUpdateDto) {
        return getParentById(parentUpdateDto.getId())
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
                                        .flatMap(child -> {
                                            if (!childIds.contains(child.getId()) && previousChildIds.contains(child.getId())) {
                                                List<RelativeParents> childRelativeParents = child.getRelativeParents();
                                                if (childRelativeParents != null) {
                                                    childRelativeParents.removeIf(rp -> rp.getId().equals(parentDoc.getId()));
                                                    child.setRelativeParents(childRelativeParents);
                                                }
                                                return childService.save(child);
                                            }
                                            List<RelativeParents> childRelativeParents = child.getRelativeParents();
                                            if (childRelativeParents == null) {
                                                childRelativeParents = new ArrayList<>();
                                            }
                                            if (childRelativeParents.stream().noneMatch(rp -> rp.getId().equals(parentDoc.getId()))) {
                                                childRelativeParents.add(new RelativeParents(parentDoc.getId(), true));
                                            }
                                            child.setRelativeParents(childRelativeParents);
                                            return childService.save(child);
                                        })
                                        .collectList()
                                        .then(parentService.save(parentDoc));
                            })
                            .map(parentMapper::fromParentDocumentToParentDto);
                });
    }
}