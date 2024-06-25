package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.fixtures.service.ParentFixture;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ParentRepository;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParentServiceTest {

    @InjectMocks
    private ParentService parentService;

    @Mock
    private ParentRepository parentRepository;

    @Mock
    private ParentMapper parentMapper;

    @Test
    void validateParentShouldReturnParentDtoWhenPhoneNumbersAreValid() {
        // Arrange
        var parentDto = ParentFixture.getParentDtoWithValidPhoneNumbers();

        // Act
        var validatedParentMono = parentService.validateParent(parentDto);

        // Assert
        assertThat(validatedParentMono.block()).isEqualTo(parentDto);
    }

    @Test
    void validateParentShouldThrowExceptionWhenPhoneNumbersAreInvalid() {
        // Arrange
        var parentDto = ParentFixture.getParentDtoWithInvalidPhoneNumbers();

        // Act & Assert
        assertThatThrownBy(() -> parentService.validateParent(parentDto).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid phone number");
    }

    @Test
    public void validateParentShouldThrowExceptionWhenSameNumbersAreGiven() throws Exception {
        // Arrange
        var parentDto = ParentFixture.getParentDtoWithIdenticalNames();

        // Act & Assert
        assertThatThrownBy(() -> parentService.validateParent(parentDto).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate phone numbers are not allowed");
    }

    @Test
    void validateParentShouldThrowExceptionWhenPhoneNumbersAreNull() {
        // Arrange
        var parentDto = ParentFixture.getParentDtoWithOutValidPhoneNumbers();

        // Act & Assert
        assertThatThrownBy(() -> parentService.validateParent(parentDto).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Phone number list is empty");
    }

    @Test
    void findByFullNameShouldReturnParentsByName() {
        // Arrange
        when(parentRepository.findByFullName(anyString()))
                .thenReturn(Flux.just(ParentFixture.getParentDocument()));
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var parentFlux = parentService.findByFullName("John Doe");

        // Assert
        assertThat(parentFlux.collectList().block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDtoList());
        verify(parentRepository, times(1)).findByFullName(anyString());
        verifyNoMoreInteractions(parentRepository);
    }

    @Test
    void findByIdShouldReturnParentById() {
        // Arrange
        when(parentRepository.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDocument()));
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var parentMono = parentService.findById("1234");

        // Assert
        assertThat(parentMono.block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDto());
        verify(parentRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(parentRepository);
    }

    @Test
    void deleteByIdShouldDeleteParentById() {
        // Arrange
        when(parentRepository.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDocument()));
        when(parentRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var deletedParentMono = parentService.deleteById("1234");

        // Assert
        assertThat(deletedParentMono.block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDto());
        verify(parentRepository, times(1)).findById(anyString());
        verify(parentRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(parentRepository);
    }

    @Test
    void findAllByIdShouldReturnAllParentsById() {
        // Arrange
        when(parentRepository.findAllById(anyList()))
                .thenReturn(Flux.just(ParentFixture.getParentDocument()));
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var parentFlux = parentService.findAllById(List.of("1234"));

        // Assert
        assertThat(parentFlux.collectList().block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDtoList());
        verify(parentRepository, times(1)).findAllById(anyList());
        verifyNoMoreInteractions(parentRepository);
    }

    @Test
    void saveShouldAddParent() {
        // Arrange
        when(parentRepository.save(any(ParentDocument.class)))
                .thenReturn(Mono.just(ParentFixture.getParentDocument()));
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var savedParentMono = parentService.save(ParentFixture.getParentDocument());

        // Assert
        assertThat(savedParentMono.block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDto());
        verify(parentRepository, times(1)).save(any(ParentDocument.class));
        verifyNoMoreInteractions(parentRepository);
    }

    @Test
    void findAllShouldReturnAllParents() {
        // Arrange
        when(parentRepository.findAll())
                .thenReturn(Flux.just(ParentFixture.getParentDocument()));
        when(parentMapper.fromParentDocumentToParentDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentDto());

        // Act
        var parentFlux = parentService.findAll();

        // Assert
        assertThat(parentFlux.collectList().block()).usingRecursiveComparison().isEqualTo(ParentFixture.getParentDtoList());
        verify(parentRepository, times(1)).findAll();
        verifyNoMoreInteractions(parentRepository);
    }
}
