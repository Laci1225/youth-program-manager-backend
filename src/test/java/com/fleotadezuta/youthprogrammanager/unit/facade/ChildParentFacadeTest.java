package com.fleotadezuta.youthprogrammanager.unit.facade;

import com.fleotadezuta.youthprogrammanager.config.Auth0Service;
import com.fleotadezuta.youthprogrammanager.facade.ChildParentFacade;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.fixtures.service.ParentFixture;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.mapper.ParentMapper;
import com.fleotadezuta.youthprogrammanager.model.*;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.document.ParentDocument;
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

import static org.assertj.core.api.Assertions.*;
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
    private ParentMapper parentMapper;

    @Mock
    private ChildMapper childMapper;

    @Mock
    private Auth0Service auth0Service;

    @Test
    void getPotentialParentsShouldReturnParents() {
        // Arrange
        when(parentService.findByFullName(anyString()))
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));

        // Act
        Flux<ParentDto> result = childParentFacade.getPotentialParents("John Doe");

        // Assert
        assertThat(result.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentDtoList());

        verify(parentService, times(1)).findByFullName(anyString());
        verifyNoMoreInteractions(parentService);
    }

    @Test
    void getPotentialChildrenShouldReturnChildren() {
        // Arrange
        when(childService.findByFullName(anyString()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));

        // Act
        Flux<ChildDto> result = childParentFacade.getPotentialChildren("John Doe");

        // Assert
        assertThat(result.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childService, times(1)).findByFullName(anyString());
        verifyNoMoreInteractions(childService);
    }

    @Test
    void addChildShouldAddChildWithoutParent() {
        // Arrange
        when(childMapper.fromChildCreationDtoToChildDocument(any(ChildCreateDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        when(childRepository.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        Mono<ChildDto> result = childParentFacade.addChild(ChildFixture.getChildCreateDtoWithoutParent());

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDto());

        verify(childRepository, times(1)).save(any(ChildDocument.class));
        verifyNoInteractions(parentService);
    }

    @Test
    void addChildShouldAddChildWithParent() {
        // Arrange

        when(parentService.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentMapper.fromParentDtoToParentDocument(any()))
                .thenReturn(ParentFixture.getParentDocument());
        when(childMapper.fromChildCreationDtoToChildDocument(any(ChildCreateDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        when(childRepository.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // Act
        Mono<ChildDto> result = childParentFacade.addChild(ChildFixture.getChildCreateDto());

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDto());

        verify(parentService, times(1)).findById(anyString());
        verifyNoMoreInteractions(parentService);
    }

    @Test
    void getAllParentsShouldReturnAllParentsWithChildren() {
        // Arrange
        when(parentService.findAll())
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));
        when(parentMapper.fromParentDtoToParentDocument(any()))
                .thenReturn(ParentFixture.getParentDocument());
        when(childMapper.fromChildDtoToChildDocument(any()))
                .thenReturn(ChildFixture.getChildDocument());
        when(parentMapper.fromParentDocumentToParentUpdateDto(any()))
                .thenReturn(ParentFixture.getParentUpdateDto());

        // Act
        Flux<ParentUpdateDto> result = childParentFacade.getAllParents();

        // Assert
        assertThat(result.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentUpdateDtoList());

        verify(parentService, times(1)).findAll();
        verify(childService, times(1)).findByParentId(anyString());
        verifyNoMoreInteractions(parentService, childService);
    }

    @Test
    void deleteParentShouldDeleteParent() {
        // Arrange
        when(parentService.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentService.deleteById(anyString()))
                .thenReturn(Mono.empty());
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.empty());

        // Act
        Mono<ParentDto> result = childParentFacade.deleteParent("parent123");

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentDto());

        verify(parentService, times(1)).findById(anyString());
        verify(parentService, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(parentService);
    }

    @Test
    void getChildByIdShouldReturnChildWithParents() {
        // Arrange
        when(childRepository.findById(anyString()))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(parentService.findAllById(anyList()))
                .thenReturn(Flux.fromIterable(ParentFixture.getParentDtoList()));
        when(childMapper.fromChildDtoToChildWithParentsDocument(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildWithParentsDto());

        // Act
        Mono<ChildWithParentsDto> result = childParentFacade.getChildById("child123");

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildWithParentsDto());

        verify(childRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void addParentShouldAddParentWithoutChild() {
        // Arrange
        when(parentService.validateParent(any()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentMapper.fromParentCreateDtoToParentDto(any(ParentCreateDto.class)))
                .thenReturn(ParentFixture.getParentDto());
        when(parentMapper.fromParentDtoToParentDocument(any()))
                .thenReturn(ParentFixture.getParentDocument());
        when(parentService.save(any(ParentDocument.class)))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        doNothing().when(auth0Service).createUser(anyString(), anyString(), anyString(), anyString(), any());
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // Act
        Mono<ParentDto> result = childParentFacade.addParent(ParentFixture.getParentCreateDtoWithoutChildIds());

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentDto());

        verify(parentService, times(1)).validateParent(any());
        verify(parentService, times(1)).save(any(ParentDocument.class));
        verifyNoMoreInteractions(parentService);
    }

    @Test
    void addParentShouldAddParentWithChild() {
        // Arrange
        when(parentService.validateParent(any()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentMapper.fromParentCreateDtoToParentDto(any(ParentCreateDto.class)))
                .thenReturn(ParentFixture.getParentDto());
        when(parentMapper.fromParentDtoToParentDocument(any()))
                .thenReturn(ParentFixture.getParentDocument());
        when(parentService.save(any(ParentDocument.class)))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(childService.findById(anyString()))
                .thenReturn(Mono.just(ChildFixture.getChildDto()));
        when(childMapper.fromChildDtoToChildDocument(any()))
                .thenReturn(ChildFixture.getChildDocument());
        when(childService.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDto()));
        doNothing().when(auth0Service).createUser(anyString(), anyString(), anyString(), anyString(), any());
        doNothing().when(emailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // Act
        Mono<ParentDto> result = childParentFacade.addParent(ParentFixture.getParentCreateDto());

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentDto());

        verify(parentService, times(1)).validateParent(any());
        verify(parentService, times(1)).save(any(ParentDocument.class));
        verify(childService, times(1)).findById(anyString());
        verify(childService, times(1)).save(any(ChildDocument.class));
        verifyNoMoreInteractions(parentService, childService);

    }

    @Test
    void getParentByIdShouldReturnParentWithChildren() {
        // Arrange
        when(parentService.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));
        when(parentService.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentMapper.fromParentDtoToParentDocument(any(ParentDto.class)))
                .thenReturn(ParentFixture.getParentDocument());
        when(parentMapper.fromParentDocumentToParentWithChildrenDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentWithChildrenDto());
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));

        // Act
        var result = childParentFacade.getParentById("parent123");

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentWithChildrenDto());

        verify(parentService, times(1)).findById(anyString());
        verify(childService, times(1)).findByParentId(anyString());
        verifyNoMoreInteractions(parentService, childService);
    }

    @Test
    void updateParentShouldUpdateParent() {
        // Arrange
        when(parentService.findById(anyString()))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentService.save(any(ParentDocument.class)))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(parentMapper.fromParentUpdateDtoToParentDto(any(ParentUpdateDto.class)))
                .thenReturn(ParentFixture.getParentDto());
        when(parentMapper.fromParentDtoToParentDocument(any(ParentDto.class)))
                .thenReturn(ParentFixture.getParentDocument());
        when(parentMapper.fromParentDocumentToParentWithChildrenDto(any(ParentDocument.class)))
                .thenReturn(ParentFixture.getParentWithChildrenDto());
        when(parentService.validateParent(any(ParentDto.class)))
                .thenReturn(Mono.just(ParentFixture.getParentDto()));
        when(childService.findAllById(anyList()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));
        when(childService.findByParentId(anyString()))
                .thenReturn(Flux.fromIterable(ChildFixture.getChildDtoList()));
        when(childService.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDto()));
        when(childMapper.fromChildDtoToChildDocument(any(ChildDto.class)))
                .thenReturn(ChildFixture.getChildDocument());
        doNothing().when(emailService).sendSimpleMessage(any(), any(), any());

        // Act
        var result = childParentFacade.updateParent(ParentFixture.getParentUpdateDto());

        // Assert
        assertThat(result.block())
                .usingRecursiveComparison()
                .isEqualTo(ParentFixture.getParentDto());

        verify(parentService, times(1)).findById(anyString());
        verify(parentService, times(1)).validateParent(any(ParentDto.class));
        verify(parentService, times(1)).save(any(ParentDocument.class));
        verify(emailService, times(1)).sendSimpleMessage(any(), any(), any());
    }


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