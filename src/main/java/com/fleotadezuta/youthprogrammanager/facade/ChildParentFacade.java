package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParents;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class ChildParentFacade {
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;
    private final ParentRepository parentRepository;

    public Flux<ParentDto> getPotentialParents(String name) {
        var full = parentRepository.findAll();
        var allParents = full
                .filter(parent -> matchesFullName(name, parent));
        var results = Flux.merge(allParents);
        return results.map(parentMapper::fromParentDocumentToParentDto);
    }

    private boolean matchesFullName(String name, ParentDocument parent) {
        String fullName1 = parent.getFamilyName() + " " + parent.getGivenName();
        String fullName2 = parent.getGivenName() + " " + parent.getFamilyName();
        return fullName1.toLowerCase().startsWith(name.toLowerCase())
                || fullName2.toLowerCase().startsWith(name.toLowerCase());
    }

    public Mono<ChildDto> addChild(ChildDto childDto) {
        childDto.getRelativeParents().stream()
                .map(parent -> parentRepository.findById(parent.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid parentId: " + parent.getId()))));
        return Mono.just(childDto)
                .map(childMapper::fromChildDtoToChildDocument)
                .flatMap(childRepository::save)
                .map(childMapper::fromChildDocumentToChildDto);
    }

    private boolean matchesChildFullName(String name, ChildDocument child) {
        String fullName1 = child.getFamilyName() + " " + child.getGivenName();
        String fullName2 = child.getGivenName() + " " + child.getFamilyName();
        return fullName1.toLowerCase().startsWith(name.toLowerCase())
                || fullName2.toLowerCase().startsWith(name.toLowerCase());
    }

    public Flux<ChildDto> getPotentialChildren(String name) {
        var full = childRepository.findAll();
        var allChildren = full
                .filter(child -> matchesChildFullName(name, child));
        var results = Flux.merge(allChildren);
        return results.map(childMapper::fromChildDocumentToChildDto);
    }

    public Mono<ParentDto> addParent(ParentDto parentDto) {
        return childRepository.findById(parentDto.getChildId())
                .flatMap(childDocument -> {
                    childDocument.setRelativeParents(List.of(new RelativeParents(parentDto.getChildId(), true)));
                    return childRepository.save(childDocument)
                            .thenReturn(childDocument);
                })
                .then(ParentService.validateParent(parentDto)
                        .map(parentMapper::fromParentDtoToParentDocument)
                        .flatMap(parentRepository::save)
                        .map(parentMapper::fromParentDocumentToParentDto)
                );
    }

}
