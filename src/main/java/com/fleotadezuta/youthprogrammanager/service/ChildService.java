package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    public Flux<ChildDto> getAllChildren(UserDetails userDetails) {
        if (userDetails.getUserType().equals(Role.ADMINISTRATOR.name())
                || userDetails.getUserType().equals(Role.RECEPTIONIST.name())
                || userDetails.getUserType().equals(Role.TEACHER.name())) {
            return childRepository.findAll()
                    .map(childMapper::fromChildDocumentToChildDto);
        } else {
            return childRepository.findChildDocumentsByRelativeParents_Id(userDetails.getUserId())
                    .map(childMapper::fromChildDocumentToChildDto);
        }
    }

    public Mono<ChildDto> deleteChild(String id) {
        return childRepository.findById(id)
                .flatMap(child -> childRepository.deleteById(id)
                        .then(Mono.just(child)))
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildUpdateDto> updateChild(ChildUpdateDto childUpdateDto) {
        List<String> parentIds = childUpdateDto.getRelativeParents()
                .stream()
                .map(RelativeParent::getId)
                .toList();
        Set<String> uniqueParentIds = new HashSet<>(parentIds);
        if (parentIds.size() != uniqueParentIds.size()) {
            return Mono.error(new IllegalArgumentException("Relative parent IDs are not unique"));
        }
        return Mono.just(childUpdateDto)
                .map(childMapper::fromChildUpdateDtoToChildDocument)
                .flatMap(childDoc -> {
                    childDoc.setId(childUpdateDto.getId());
                    return childRepository.save(childDoc);
                })
                .map(childMapper::fromChildDocumentToChildUpdateDto);
    }

    public Mono<Void> removeParentFromChildren(String parentIdToRemove) {
        return findByParentId(parentIdToRemove)
                .map(childMapper::fromChildDtoToChildDocument)
                .flatMap(child -> {
                    child.getRelativeParents().removeIf(parent -> parent.getId().equals(parentIdToRemove));
                    return updateChild(childMapper.fromChildDocumentToChildUpdateDto(child));
                }).then(); //to return Mono<Void>
    }


    public Flux<ChildDto> findByFullName(String name) {
        return childRepository.findByFullName(name).map(childMapper::fromChildDocumentToChildDto);
    }

    public Flux<ChildDto> findByParentId(String parentIdToRemove) {
        return childRepository.findChildDocumentsByRelativeParents_Id(parentIdToRemove)
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildDto> findById(String id) {
        return childRepository.findById(id)
                .map(childMapper::fromChildDocumentToChildDto);
    }


    public Mono<ChildDto> save(ChildDocument childDocument) {
        return childRepository.save(childDocument)
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Flux<ChildDto> findAllById(List<String> childIds) {
        return childRepository.findAllById(childIds)
                .map(childMapper::fromChildDocumentToChildDto);
    }
}
