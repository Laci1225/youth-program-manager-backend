package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.List;

@Service
@AllArgsConstructor
public class ParentService {
    private final ParentMapper parentMapper;
    private final ParentRepository parentRepository;

    public Flux<ParentDto> getAllParents() {
        return parentRepository.findAll()
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> getParentById(String id) {
        return parentRepository.findById(id)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> addParent(ParentDto parentDto) {
        return Mono.just(parentDto)
                .flatMap(this::validateParent)
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parentRepository::save)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> deleteParent(String id) {
        return parentRepository.findById(id)
                .flatMap(parent -> parentRepository.deleteById(id)
                        .then(Mono.just(parent)))
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> updateParent(String id, ParentDto parentDto) {
        return Mono.just(parentDto)
                .flatMap(this::validateParent)
                .map(parentMapper::fromParentDtoToParentDocument)
                .flatMap(parentDoc -> {
                    parentDoc.setId(id);
                    return parentRepository.save(parentDoc);
                })
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    private Mono<ParentDto> validateParent(ParentDto parentDto) {
        List<String> phoneNumbers = parentDto.getPhoneNumbers();
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Phone number list is empty"));
        }
        
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return Flux.fromIterable(phoneNumbers)
                .flatMap(phoneNumber -> Mono.fromCallable(() -> phoneNumberUtil.parse(phoneNumber, "XX")))
                .onErrorMap(NumberParseException.class, e -> new IllegalArgumentException("Invalid phone number: " + e.getMessage()))
                .collectList()
                .flatMap(validatedPhoneNumbers -> {
                    if (validatedPhoneNumbers.size() == phoneNumbers.size()) {
                        return Mono.just(parentDto);
                    } else {
                        return Mono.error(new IllegalArgumentException("Some phone numbers are invalid"));
                    }
                });
    }
}
