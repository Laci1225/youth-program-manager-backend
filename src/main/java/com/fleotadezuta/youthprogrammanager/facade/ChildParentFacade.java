package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    /* public Mono<ChildWithParentsDto> getChildById(String id) {
        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = Optional.ofNullable(child.getRelativeParents())
                            .map(relParents -> relParents.stream()
                                    .map(RelativeParents::getId)
                                    .toList())
                            .orElse(Collections.emptyList());

                    return parentRepository.findAllById(parentIds)
                            .collectList()
                            .map(parents -> {
                                ChildDto childDto = childMapper.fromChildDocumentToChildDto(child);

                                ChildWithParentsDto childWithParentsDto = ChildWithParentsDto.builder()
                                        .id(childDto.getId())
                                        .familyName(childDto.getFamilyName())
                                        .givenName(childDto.getGivenName())
                                        .birthDate(childDto.getBirthDate())
                                        .birthPlace(childDto.getBirthPlace())
                                        .address(childDto.getAddress())
                                        .diagnosedDiseases(childDto.getDiagnosedDiseases())
                                        .regularMedicines(childDto.getRegularMedicines())
                                        .createdDate(childDto.getCreatedDate())
                                        .modifiedDate(childDto.getModifiedDate())
                                        .build();

                                List<ParentWithContactDto> parentsList = parents.stream()
                                        .map(parent -> ParentWithContactDto.builder()
                                                .parentId(parent.getId())
                                                .familyName(parent.getFamilyName())
                                                .givenName(parent.getGivenName())
                                                .phoneNumbers(parent.getPhoneNumbers())
                                                .address(parent.getAddress())
                                                .isEmergencyContact(true)
                                                .build())
                                        .collect(Collectors.toList());

                                childWithParentsDto.setParents(parentsList);

                                return childWithParentsDto;
                            });
                });
    }*/
}