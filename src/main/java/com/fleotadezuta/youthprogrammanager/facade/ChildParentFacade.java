package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

}
