package com.fleotadezuta.youthprogrammanager.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.RelativeParent;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChildParentFacade {

    @Value("${auth0.management.audience}")
    private String audience;
    @Value("${auth0.management.api.token}")
    private String accessToken;

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final ParentMapper parentMapper;
    private final ParentService parentService;
    private final ChildService childService;

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

        return Flux.merge(parentMono)
                .then(childRepository.save(childMapper.fromChildCreationDtoToChildDocument(childCreateDto)))
                .map(childMapper::fromChildDocumentToChildDto)
                .onErrorResume(Mono::error);
    }

    public Mono<ParentDto> deleteParent(UserDetails userDetails, String id) {
        return parentService.findById(id)
                .flatMap(parent -> parentService.deleteById(id)
                        .then(childService.removeParentFromChildren(userDetails, parent.getId()))
                        .thenReturn(parent));
    }


    public Mono<ChildWithParentsDto> getChildById(UserDetails userDetails, String id) {

        return childRepository.findById(id)
                .flatMap(child -> {
                    List<String> parentIds = Optional.ofNullable(child.getRelativeParents())
                            .map(relParents -> relParents.stream()
                                    .map(RelativeParent::getId)
                                    .toList())
                            .orElse(Collections.emptyList());
                    if (!userDetails.getUserType().equals("ADMIN") && !parentIds.contains(userDetails.getUserId())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
                    }
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

    public Mono<ParentWithChildrenDto> getParentById(UserDetails userDetails, String id) {
        if (userDetails.getUserId().equals(id) || userDetails.getUserType().equals("ADMIN")) {
            return parentService.findById(id)
                    .map(parentMapper::fromParentDtoToParentDocument)
                    .flatMap(parent -> childService.findByParentId(parent.getId())
                            .collectList()
                            .map(children -> {
                                var parentWithChildrenDto = parentMapper.fromParentDocumentToParentWithChildrenDto(parent);
                                List<ChildDto> childDtos = new ArrayList<>(children);
                                parentWithChildrenDto.setChildDtos(childDtos);
                                return parentWithChildrenDto;
                            }));
        } else {
            log.error(userDetails.getUserId() + " is not authorized to view parent with id: " + id);
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
    }

    public Mono<ParentDto> addParent(ParentCreateDto parentCreateDto) {
        var parentDtoMono = parentService.validateParent(parentMapper.fromParentCreateDtoToParentDto(parentCreateDto))
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
                });
        return parentDtoMono.doOnSuccess(parentDto -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(
                    "{\"email\":\"" + parentDto.getEmail() + "\"," +
                            "\"connection\":\"email\"," +
                            "\"app_metadata\":{\"app_user_id\":\"" + parentDto.getId() + "\", \"app_user_type\":\"PARENT\"}," +
                            "\"given_name\":\"" + parentDto.getGivenName() + "\"," +
                            "\"family_name\":\"" + parentDto.getFamilyName() + "\"}",
                    mediaType);
            Request request = new Request.Builder()
                    .url(audience + "users")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String userId = jsonNode.get("user_id").asText();
                    log.error("User created with id: " + userId);
                    RequestBody body2 = RequestBody.create(
                            "{\"users\":[\"" + userId + "\"]}",
                            mediaType);
                    Request request2 = new Request.Builder()
                            .url(audience + "roles/rol_Mjt9yu2PlPadWRn5/users")
                            .method("POST", body2)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();
                    Response response2 = client.newCall(request2).execute();
                    log.error(response2.body().string());
                } else {
                    System.err.println("Error: " + response.code() + ", " + response.message());
                    System.err.println(response.body().string());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public Mono<ParentDto> updateParent(UserDetails userDetails, ParentUpdateDto parentUpdateDto) {
        return getParentById(userDetails, parentUpdateDto.getId())
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
                });
    }
}