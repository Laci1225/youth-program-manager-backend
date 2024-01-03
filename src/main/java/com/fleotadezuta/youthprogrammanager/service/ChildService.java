package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParents;
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

    public Flux<ChildDto> getAllChildren() {
        return childRepository.findAll()
                .map(childMapper::fromChildDocumentToChildDto);
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
                .map(RelativeParents::getId)
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

    public Flux<ChildDto> findByFullName(String name) {
        return childRepository.findByFullName(name).map(childMapper::fromChildDocumentToChildDto);
    }

    public Flux<ChildDocument> findByParentId(String parentIdToRemove) {
        return childRepository.findChildDocumentsByRelativeParents_Id(parentIdToRemove);
    }

    public Mono<ChildDocument> findById(String id) {
        return childRepository.findById(id);
    }

    public Flux<ChildDocument> findAllByIds(List<String> childDtoIds) {
        return childRepository.findAllById(childDtoIds);
    }

    public Mono<ChildDocument> save(ChildDocument childDocument) {
        return childRepository.save(childDocument);
    }
}
