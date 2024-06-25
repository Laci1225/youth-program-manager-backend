package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.fleotadezuta.youthprogrammanager.constants.Employee.EMPLOYEE_USER_TYPES;


@Service
@AllArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    public Flux<ChildDto> getAllChildren(UserDetails userDetails) {
        if (EMPLOYEE_USER_TYPES.contains(userDetails.getUserType())) {
            return childRepository.findAll()
                    .map(childMapper::fromChildDocumentToChildDto);
        } else {
            return childRepository.findChildDocumentsByRelativeParents_Id(userDetails.getUserId())
                    .map(childMapper::fromChildDocumentToChildDto);
        }
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
