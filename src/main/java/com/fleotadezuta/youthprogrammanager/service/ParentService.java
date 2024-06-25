package com.fleotadezuta.youthprogrammanager.service;

import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.ParentDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ParentService {
    private final ParentMapper parentMapper;
    private final ParentRepository parentRepository;

    public Mono<ParentDto> validateParent(ParentDto parentDto) {
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
                    Set<String> uniquePhoneNumbers = new HashSet<>(phoneNumbers);
                    if (uniquePhoneNumbers.size() != phoneNumbers.size()) {
                        return Mono.error(new IllegalArgumentException("Duplicate phone numbers are not allowed"));
                    }
                    if (validatedPhoneNumbers.size() == phoneNumbers.size()) {
                        return Mono.just(parentDto);
                    } else { //cannot happen
                        return Mono.error(new IllegalArgumentException("Some phone numbers are invalid"));
                    }
                });
    }

    public Flux<ParentDto> findByFullName(String name) {
        return parentRepository.findByFullName(name).map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> findById(String id) {
        return parentRepository.findById(id)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> deleteById(String id) {
        return parentRepository.findById(id)
                .flatMap(parentRepository.deleteById(id)::thenReturn)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Flux<ParentDto> findAllById(List<String> parentIds) {
        return parentRepository.findAllById(parentIds)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Mono<ParentDto> save(ParentDocument parentDocument) {
        return parentRepository.save(parentDocument)
                .map(parentMapper::fromParentDocumentToParentDto);
    }

    public Flux<ParentDto> findAll() {
        return parentRepository.findAll()
                .map(parentMapper::fromParentDocumentToParentDto);
    }
}
