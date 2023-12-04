package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ParentService {
    private final ParentMapper parentMapper;
    private final ParentRepository parentRepository;

    public Flux<ParentDto> getAllParents() {
        return parentRepository.findAll()
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> addParent(ParentDto parentDto) {
        var parentDoc = parentMapper.fromParentDtoToParentDocument(parentDto);
        return parentRepository.save(parentDoc)
                .map(parentMapper::fromParentDocumentToParentDto);
    }
}