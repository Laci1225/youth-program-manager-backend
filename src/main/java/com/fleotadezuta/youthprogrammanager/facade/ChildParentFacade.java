package com.fleotadezuta.youthprogrammanager.facade;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import com.fleotadezuta.youthprogrammanager.persistence.document.Role;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.EmailService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChildParentFacade {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;
    private final ParentService parentService;
    private final ChildService childService;
    private final Auth0Service auth0Service;
    private final EmailService emailService;

    public Flux<ParentDto> getPotentialParents(String name) {
        return parentService.findByFullName(name);
    }

    public Flux<ChildDto> getPotentialChildren(String name) {
        return childService.findByFullName(name);
    }

    public Flux<ParentUpdateDto> getAllParents() {
        return parentService.findAll()
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parent ->
                        childService.findByParentId(parent.getId())
                                .map(childMapper::fromChildDtoToChildDocument)
                                .map(ChildDocument::getId)
                                .collectList()
                                .map(childIds -> {
                                    ParentUpdateDto parentUpdateDto = parentMapper.fromParentDocumentToParentUpdateDto(parent);
                                    parentUpdateDto.setChildIds(childIds);
                                    return parentUpdateDto;
                                })
                );
    }


    public Mono<ChildDto> addChild(ChildCreateDto childCreateDto) {
        RelativeParent relativeParent = childCreateDto.getRelativeParent();
        if (relativeParent == null) {
            ChildDocument childDocument = childMapper.fromChildCreationDtoToChildDocument(childCreateDto);
            return childRepository.save(childDocument)
                    .map(childMapper::fromChildDocumentToChildDto);
        }
        Mono<ParentDocument> parentMono = parentService.findById(relativeParent.getId())
                .map(parentMapper::fromParentDtoToParentDocument)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid parentId: " + relativeParent.getId())));

        return parentMono.flatMap(parentDocument -> childRepository.save(childMapper.fromChildCreationDtoToChildDocument(childCreateDto))
                        .map(childMapper::fromChildDocumentToChildDto)

                        .doOnSuccess(childDto ->
                                emailService.sendSimpleMessage(parentDocument.getEmail(),
                                        "New Child Assigned to You",
                                        "Dear " + parentDocument.getGivenName() + " " + parentDocument.getFamilyName() + ",\n\n" +
                                                "We are pleased to inform you that a new child, " + childCreateDto.getGivenName() + " " + childCreateDto.getFamilyName() + ", has been assigned to you. " +
                                                "Please log in to your account to view more details.\n\n" +
                                                "Best regards,\nYouth Program Manager Team")))
                .onErrorResume(Mono::error);
    }

    public Mono<ChildDto> deleteChild(String id) {
        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = child.getRelativeParents().stream()
                            .map(RelativeParent::getId)
                            .toList();
                    return childRepository.deleteById(id)
                            .thenMany(parentService.findAllById(parentIds))
                            .collectList()
                            .doOnNext(parents -> {
                                String subject = "Child Deletion Notification";
                                String message = "The child " + child.getGivenName() + " " + child.getFamilyName() + " has been removed from the Youth Program Manager system.";
                                parents.forEach(parent -> emailService.sendSimpleMessage(parent.getEmail(), subject, message));
                            })
                            .then(Mono.just(child));
                })
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
                    return childRepository.save(childDoc)
                            .thenMany(parentService.findAllById(parentIds))
                            .collectList()
                            .doOnNext(parents -> {
                                String subject = "Parent Addition Notification";
                                String message = "You have been added as a parent to the child " + childDoc.getGivenName() + " " + childDoc.getFamilyName() + ".";
                                parents.forEach(parent -> emailService.sendSimpleMessage(parent.getEmail(), subject, message));
                            })
                            .then(Mono.just(childDoc));
                })
                .map(childMapper::fromChildDocumentToChildUpdateDto);
    }

    public Mono<Void> removeParentFromChildren(String parentIdToRemove) {
        return childService.findByParentId(parentIdToRemove)
                .map(childMapper::fromChildDtoToChildDocument)
                .flatMap(child -> {
                    child.getRelativeParents().removeIf(parent -> parent.getId().equals(parentIdToRemove));
                    return updateChild(childMapper.fromChildDocumentToChildUpdateDto(child));
                }).then(); //to return Mono<Void>
    }

    public Mono<ParentDto> deleteParent(String id) {
        return parentService.findById(id)
                .flatMap(parent -> parentService.deleteById(id)
                        .then(removeParentFromChildren(parent.getId()))
                        .thenReturn(parent)
                        .doOnSuccess(deletedParent -> emailService.sendSimpleMessage(deletedParent.getEmail(),
                                "Your Account Has Been Deleted",
                                "Dear " + deletedParent.getGivenName() + " " + deletedParent.getFamilyName() + ",\n\n" +
                                        "Your account has been successfully delet   ed from Youth Program Manager.\n\n" +
                                        "Best regards,\nYouth Program Manager Team")));
    }


    public Mono<ChildWithParentsDto> getChildById(String id) {

        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = Optional.ofNullable(child.getRelativeParents())
                            .map(relParents -> relParents.stream()
                                    .map(RelativeParent::getId)
                                    .toList())
                            .orElse(Collections.emptyList());
                    return parentService.findAllById(parentIds)
                            .collectMap(parentDto -> parentDto, parent -> child.getRelativeParents()
                                    .stream()
                                    .filter(rp -> rp.getId().equals(parent.getId()))
                                    .findFirst()
                                    .map(RelativeParent::getIsEmergencyContact)
                                    .orElseThrow(() -> new IllegalArgumentException(""))
                            )
                            .flatMap(parentsEmergencyContactMap -> {
                                ChildWithParentsDto childWithParentsDto = childMapper.fromChildDtoToChildWithParentsDocument(child);
                                Mono<List<ParentWithContactDto>> parentsListMono = Flux.fromIterable(parentsEmergencyContactMap.entrySet())
                                        .flatMap(entry -> Mono.just(entry.getKey())
                                                .map(parentDto -> ParentWithContactDto.builder()
                                                        .parentDto(parentDto)
                                                        .isEmergencyContact(entry.getValue())
                                                        .build()))
                                        .collectList();
                                return parentsListMono.map(parentsList -> {
                                    childWithParentsDto.setParents(parentsList);
                                    return childWithParentsDto;
                                });
                            });
                });
    }

    public Mono<ParentWithChildrenDto> getParentById(String id) {
        return parentService.findById(id)
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(this::getChildDetails);
    }

    private Mono<ParentWithChildrenDto> getChildDetails(ParentDocument parent) {
        return childService.findByParentId(parent.getId())
                .collectList()
                .map(children -> {
                    var parentWithChildrenDto = parentMapper.fromParentDocumentToParentWithChildrenDto(parent);
                    List<ChildDto> childDtos = new ArrayList<>(children);
                    parentWithChildrenDto.setChildDtos(childDtos);
                    return parentWithChildrenDto;
                });
    }

    public Mono<ParentDto> addParent(ParentCreateDto parentCreateDto) {
        return parentService.validateParent(parentMapper.fromParentCreateDtoToParentDto(parentCreateDto))
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parentService::save)
                .flatMap(validatedParent -> {
                    String childId = parentCreateDto.getChildId();
                    if (childId == null || childId.isEmpty()) {
                        return Mono.just(validatedParent);
                    } else {
                        return childService.findById(childId)
                                .map(childMapper::fromChildDtoToChildDocument)
                                .flatMap(childDocument -> {
                                    var relativeParents = childDocument.getRelativeParents();
                                    relativeParents.add(new RelativeParent(validatedParent.getId(), true));
                                    childDocument.setRelativeParents(relativeParents);
                                    return childService.save(childDocument)
                                            .thenReturn(validatedParent);
                                });
                    }
                }).doOnNext(parentDto -> {
                    auth0Service.createUser(parentDto.getEmail(), parentDto.getId(), parentDto.getGivenName(), parentDto.getFamilyName(), Role.PARENT);
                    emailService.sendSimpleMessage(parentDto.getEmail(), "Welcome to Youth Program Manager",
                            "Dear " + parentDto.getGivenName() + " " + parentDto.getFamilyName() + ",\n\n" +
                                    "Welcome to Youth Program Manager! Your account has been created successfully.\n\n" +
                                    "Best regards,\nYouth Program Manager Team");
                });
    }


    public Mono<ParentDto> updateParent(ParentUpdateDto parentUpdateDto) {
        return getParentById(parentUpdateDto.getId())
                .flatMap(previousParent -> {
                    var parentDto = parentMapper.fromParentUpdateDtoToParentDto(parentUpdateDto);
                    return Mono.just(parentDto)
                            .flatMap(parentService::validateParent)
                            .map(parentMapper::fromParentDtoToParentDocument)
                            .flatMap(parentDoc -> {
                                parentDoc.setId(parentDto.getId());
                                List<String> childIds = parentUpdateDto.getChildIds();
                                List<String> previousChildIds = previousParent.getChildDtos().stream()
                                        .map(ChildDto::getId)
                                        .toList();
                                Set<String> combinedChildIds = new HashSet<>(childIds);
                                combinedChildIds.addAll(previousChildIds);

                                return childService.findAllById(combinedChildIds.stream().toList())
                                        .map(childMapper::fromChildDtoToChildDocument)
                                        .flatMap(child -> {
                                            if (!childIds.contains(child.getId()) && previousChildIds.contains(child.getId())) {
                                                List<RelativeParent> childRelativeParents = child.getRelativeParents();
                                                if (childRelativeParents != null) {
                                                    childRelativeParents.removeIf(rp -> rp.getId().equals(parentDoc.getId()));
                                                    child.setRelativeParents(childRelativeParents);
                                                }
                                                return childService.save(child);
                                            }
                                            List<RelativeParent> childRelativeParents = child.getRelativeParents();
                                            if (childRelativeParents == null) {
                                                childRelativeParents = new ArrayList<>();
                                            }
                                            if (childRelativeParents.stream().noneMatch(rp -> rp.getId().equals(parentDoc.getId()))) {
                                                childRelativeParents.add(new RelativeParent(parentDoc.getId(), true));
                                            }
                                            child.setRelativeParents(childRelativeParents);
                                            return childService.save(child);
                                        })
                                        .collectList()
                                        .then(parentService.save(parentDoc));
                            });
                }).doOnNext(parentDto -> emailService.sendSimpleMessage(parentDto.getEmail(), "Your Profile Has Been Updated",
                        "Dear " + parentDto.getGivenName() + " " + parentDto.getFamilyName() + ",\n\n" +
                                "Your profile has been successfully updated.\n\n" +
                                "Best regards,\nYouth Program Manager Team"));
    }
}