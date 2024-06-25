package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.fixtures.service.TicketTypeFixture;
import com.fleotadezuta.youthprogrammanager.mapper.TicketTypeMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketTypeDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketTypeDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.TicketTypeRepository;
import com.fleotadezuta.youthprogrammanager.service.TicketTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketTypeServiceTest {

    @InjectMocks
    private TicketTypeService ticketTypeService;

    @Mock
    private TicketTypeRepository ticketTypeRepository;

    @Mock
    private TicketTypeMapper ticketTypeMapper;

    @Test
    void getAllTicketTypesShouldReturnAllTicketTypes() {
        // Arrange
        when(ticketTypeRepository.findAll())
                .thenReturn(Flux.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var ticketTypeFlux = ticketTypeService.getAllTicketTypes();

        // Assert
        assertThat(ticketTypeFlux.collectList().block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDtoList());
        verify(ticketTypeRepository, times(1)).findAll();
        verifyNoMoreInteractions(ticketTypeRepository);
    }

    @Test
    void getTicketTypeByIdShouldReturnTicketTypeById() {
        // Arrange

        when(ticketTypeRepository.findById(anyString()))
                .thenReturn(Mono.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var ticketTypeMono = ticketTypeService.getTicketTypeById("1234");

        // Assert
        assertThat(ticketTypeMono.block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDto());
        verify(ticketTypeRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(ticketTypeRepository);
    }

    @Test
    void addTicketTypeShouldAddTicketType() {
        // Arrange

        when(ticketTypeMapper.fromTicketTypeDtoToTicketTypeDocument(any(TicketTypeDto.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDocument());
        when(ticketTypeRepository.save(any(TicketTypeDocument.class)))
                .thenReturn(Mono.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var addedTicketTypeMono = ticketTypeService.addTicketType(TicketTypeFixture.getTicketTypeDto());

        // Assert
        assertThat(addedTicketTypeMono.block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDto());
        verify(ticketTypeRepository, times(1)).save(TicketTypeFixture.getTicketTypeDocument());
        verifyNoMoreInteractions(ticketTypeRepository);
    }

    @Test
    void deleteTicketTypeShouldDeleteTicketType() {
        // Arrange
        when(ticketTypeRepository.findById(anyString()))
                .thenReturn(Mono.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var deletedTicketTypeMono = ticketTypeService.deletedTicketType("1234");

        // Assert
        assertThat(deletedTicketTypeMono.block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDto());
        verify(ticketTypeRepository, times(1)).findById(anyString());
        verify(ticketTypeRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(ticketTypeRepository);
    }

    @Test
    void updateTicketTypeShouldUpdateTicketType() {
        // Arrange
        when(ticketTypeMapper.fromTicketTypeDtoToTicketTypeDocument(any(TicketTypeDto.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDocument());
        when(ticketTypeRepository.save(any(TicketTypeDocument.class)))
                .thenReturn(Mono.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var updatedTicketTypeMono = ticketTypeService.updateTicketType("1234", TicketTypeFixture.getTicketTypeDto());

        // Assert
        assertThat(updatedTicketTypeMono.block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDto());
        verify(ticketTypeRepository, times(1)).save(any(TicketTypeDocument.class));
        verifyNoMoreInteractions(ticketTypeRepository);
    }

    @Test
    void findByNameShouldReturnTicketTypesByName() {
        // Arrange
        when(ticketTypeRepository.findAllByNameContaining(anyString()))
                .thenReturn(Flux.just(TicketTypeFixture.getTicketTypeDocument()));
        when(ticketTypeMapper.fromTicketTypeDocumentToTicketTypeDto(any(TicketTypeDocument.class)))
                .thenReturn(TicketTypeFixture.getTicketTypeDto());

        // Act
        var ticketTypesFlux = ticketTypeService.findByName("Standard");

        // Assert
        assertThat(ticketTypesFlux.collectList().block()).usingRecursiveComparison().isEqualTo(TicketTypeFixture.getTicketTypeDtoList());
        verify(ticketTypeRepository, times(1)).findAllByNameContaining(anyString());
        verifyNoMoreInteractions(ticketTypeRepository);
    }
}
