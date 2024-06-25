package com.fleotadezuta.youthprogrammanager.unit.facade;

import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ParentFixture;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.ChildDto;
import com.fleotadezuta.youthprogrammanager.model.ChildUpdateDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import com.fleotadezuta.youthprogrammanager.service.EmailService;
import com.fleotadezuta.youthprogrammanager.service.ParentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChildParentFacadeTest {

    @InjectMocks
    private ChildParentFacade childParentFacade;

    @Mock
    private ParentService parentService;

    @Mock
    private ChildService childService;

    @Mock
    private EmailService emailService;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private ChildMapper childMapper;


    @Test
    void deleteChildShouldDeleteChild() {
        // Arrange
        when(childRepository.findById(anyString()))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());
        when(parentService.findAllById(anyList()))
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));
        doNothing().when(emailService).sendSimpleMessage(any(), any(), any());


        // Act
        var deletedChildMono = childParentFacade.deleteChild("1234");

        // Assert
        assertThat(deletedChildMono.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDto());
        verify(childRepository, times(1)).findById(anyString());
        verify(childRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void updateChildShouldUpdateChild() {
        // Arrange
        when(childMapper.fromChildUpdateDtoToChildDocument(any(ChildUpdateDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        when(childRepository.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildUpdateDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildUpdateDto());
        when(parentService.findAllById(anyList()))
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));
        doNothing().when(emailService).sendSimpleMessage(any(), any(), any());

        // Act
        var updatedChildMono = childParentFacade.updateChild(ChildFixture.getChildUpdateDto());

        // Assert
        assertThat(updatedChildMono.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildUpdateDto());
        verify(childRepository, times(1)).save(any(ChildDocument.class));
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void updateChildShouldThrowExceptionWhenParentIdsAreNotUnique() {
        // Arrange
        var invalidChildUpdateDto = ChildFixture.getChildUpdateDtoWithDuplicateParentIds();

        // Act & Assert
        assertThatThrownBy(() -> childParentFacade.updateChild(invalidChildUpdateDto).block())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Relative parent IDs are not unique");
    }

    @Test
    void removeParentFromChildrenShouldRemoveParentFromChildren() {
        // Arrange
        when(childMapper.fromChildDocumentToChildUpdateDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildUpdateDto());
        when(childMapper.fromChildUpdateDtoToChildDocument(any(ChildUpdateDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        when(childRepository.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDtoToChildDocument(any(ChildDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.just(ChildFixture.getChildDto()));
        when(parentService.findAllById(anyList()))
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));
        doNothing().when(emailService).sendSimpleMessage(any(), any(), any());

        // Act
        Mono<Void> result = childParentFacade.removeParentFromChildren("parent123");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(childRepository, times(1)).save(any(ChildDocument.class));
        verifyNoMoreInteractions(childRepository);
    }
}