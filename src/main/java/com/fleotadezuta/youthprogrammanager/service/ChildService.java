package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    public Flux<ChildDto> getAllChildren() {
        return childRepository.findAll()
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildDto> getChildById(String id) {
        return childRepository.findById(id)
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildDto> addChild(ChildDto childDto) {
        var childDoc = childMapper.fromChildDtoToChildDocument(childDto);
        return childRepository.save(childDoc)
                .map(childMapper::fromChildDocumentToChildDto);

    }

    public Mono<ChildDto> deleteChild(String id) {
        return childRepository.findById(id)
                .flatMap(child -> childRepository.deleteById(id)
                        .then(Mono.just(child)))
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildDto> updateChild(String id, ChildDto childDto) {
        var childDoc = childMapper.fromChildDtoToChildDocument(childDto);
        return childRepository.findById(id)
                .flatMap(existingChildDoc -> {
                    childDoc.setId(existingChildDoc.getId());
                    return childRepository.save(childDoc);
                })
                .map(childMapper::fromChildDocumentToChildDto);
        /*
        var childDoc = childMapper.fromChildDtoToChildDocument(childDto);
        var existingChildDoc = childRepository.findById(id);
        childDoc.setId(existingChildDoc.getId());
        return childRepository.save(childDoc)
                .map(childMapper::fromChildDocumentToChildDto);*/
    }
}
