package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.persistance.repository.ChildRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    public Flux<ChildDto> children() {
        return childRepository.findAll()
                .map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ChildDto> addChild(ChildDto childDto) {
        var childDoc = childMapper.fromChildDtoToChildDocument(childDto);
            return childRepository.save(childDoc)
                .map(childMapper::fromChildDocumentToChildDto);

    }
}
