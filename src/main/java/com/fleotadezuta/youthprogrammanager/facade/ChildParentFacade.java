package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildWithParentsDto;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.model.ParentWithContactDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParents;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
                .collectList()
                .flatMap(parents -> {
                    ChildDocument childDocument = childMapper.fromChildDtoToChildDocument(childDto);
                    return childRepository.save(childDocument)
                            .map(childMapper::fromChildDocumentToChildDto);
                })
                .onErrorResume(Mono::error);
    }

    public Mono<ParentDto> deleteParent(String id) {
        return parentService.findById(id)
                .flatMap(parent -> parentService.deleteById(id)
                        .then(updateChildren(parent.getId()))
                        .thenReturn(parent))
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    private Mono<Void> updateChildren(String parentIdToRemove) {
        return childService.findByParentId(parentIdToRemove)
                .flatMap(child -> {
                    child.getRelativeParents().removeIf(parent -> parent.getId().equals(parentIdToRemove));
                    return childService.updateChild(childMapper.fromChildDocumentToChildUpdateDto(child));
                })
                .then();
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
                            .collectList()
                            .map(parents -> {
                                ChildDto childDto = childMapper.fromChildDocumentToChildDto(child);

                                ChildWithParentsDto childWithParentsDto = ChildWithParentsDto.builder()
                                        .childDto(childDto).build();

                                List<ParentWithContactDto> parentsList = parents.stream()
                                        .map(parent -> ParentWithContactDto.builder()
                                                .parentDto(parent)
                                                .isEmergencyContact(childDto.getRelativeParents()
                                                        .stream()
                                                        .filter(a -> a.getId().equals(parent.getId()))
                                                        .findFirst()
                                                        .map(RelativeParents::getIsEmergencyContact)
                                                        .orElse(false))
                                                .build())
                                        .collect(Collectors.toList());
                                childWithParentsDto.setParents(parentsList);
                                return childWithParentsDto;
                            });
                });
    }
}