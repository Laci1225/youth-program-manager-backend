package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.fixtures.service.ChildFixture;
import com.fleotadezuta.youthprogrammanager.mapper.ChildMapper;
import com.fleotadezuta.youthprogrammanager.model.UserDetails;
import com.fleotadezuta.youthprogrammanager.persistence.document.ChildDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.ChildRepository;
import com.fleotadezuta.youthprogrammanager.service.ChildService;
import graphql.GraphQLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_ID;
import static com.fleotadezuta.youthprogrammanager.constants.HttpConstants.APP_USER_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChildServiceTest {

    @InjectMocks
    private ChildService childService;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private ChildMapper childMapper;

    @Test
    void getAllChildrenShouldReturnAllChildren() {
        // Arrange
        when(childRepository.findAll())
                .thenReturn(Flux.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childrenFlux = childService.getAllChildren(new UserDetails(
                GraphQLContext.newContext()
                        .of(APP_USER_ID, "parent123")
                        .of(APP_USER_TYPE, "TEACHER")
                        .build()));

        // Assert
        assertThat(childrenFlux.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childRepository, times(1)).findAll();
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void getAllChildrenShouldReturnChildrenByParentIdWhenUserIsNotEmployee() {
        // Arrange
        when(childRepository.findChildDocumentsByRelativeParents_Id(anyString()))
                .thenReturn(Flux.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childrenFlux = childService.getAllChildren(new UserDetails(
                GraphQLContext.newContext()
                        .of(APP_USER_ID, "parent123")
                        .of(APP_USER_TYPE, "PARENT")
                        .build()));

        // Assert
        assertThat(childrenFlux.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childRepository, times(1)).findChildDocumentsByRelativeParents_Id(anyString());
        verifyNoMoreInteractions(childRepository);
    }


    @Test
    void findByFullNameShouldReturnChildrenByFullName() {
        // Arrange
        when(childRepository.findByFullName(anyString()))
                .thenReturn(Flux.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childrenFlux = childService.findByFullName("John Doe");

        // Assert
        assertThat(childrenFlux.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childRepository, times(1)).findByFullName(anyString());
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void findByParentIdShouldReturnChildrenByParentId() {
        // Arrange
        when(childRepository.findChildDocumentsByRelativeParents_Id(anyString()))
                .thenReturn(Flux.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childrenFlux = childService.findByParentId("parent123");

        // Assert
        assertThat(childrenFlux.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childRepository, times(1)).findChildDocumentsByRelativeParents_Id(anyString());
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void findByIdShouldReturnChildById() {
        // Arrange
        when(childRepository.findById(anyString()))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childMono = childService.findById("1234");

        // Assert
        assertThat(childMono.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDto());
        verify(childRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void saveShouldSaveChild() {
        // Arrange
        when(childRepository.save(any(ChildDocument.class)))
                .thenReturn(Mono.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var savedChildMono = childService.save(ChildFixture.getChildDocument());

        // Assert
        assertThat(savedChildMono.block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDto());
        verify(childRepository, times(1)).save(any(ChildDocument.class));
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    void findAllByIdShouldReturnAllChildrenById() {
        // Arrange
        when(childRepository.findAllById(anyList()))
                .thenReturn(Flux.just(ChildFixture.getChildDocument()));
        when(childMapper.fromChildDocumentToChildDto(any(ChildDocument.class)))
                .thenReturn(ChildFixture.getChildDto());

        // Act
        var childrenFlux = childService.findAllById(List.of("1234", "5678"));

        // Assert
        assertThat(childrenFlux.collectList().block())
                .usingRecursiveComparison()
                .isEqualTo(ChildFixture.getChildDtoList());
        verify(childRepository, times(1)).findAllById(anyList());
        verifyNoMoreInteractions(childRepository);
    }
}
