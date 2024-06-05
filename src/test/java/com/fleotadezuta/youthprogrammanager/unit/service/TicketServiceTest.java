package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
import com.fleotadezuta.youthprogrammanager.model.TicketDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.TicketDocument;
import com.fleotadezuta.youthprogrammanager.persistence.repository.TicketRepository;
import com.fleotadezuta.youthprogrammanager.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Test
    void findAllShouldReturnAllTickets() {
        // Arrange
        var ticketDocumentList = List.of(new TicketDocument(), new TicketDocument());
        var ticketDtoList = List.of(new TicketDto(), new TicketDto());
        when(ticketRepository.findAll()).thenReturn(Flux.fromIterable(ticketDocumentList));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenAnswer(invocation -> {
                    TicketDocument document = invocation.getArgument(0);
                    int index = ticketDocumentList.indexOf(document);
                    return ticketDtoList.get(index);
                });

        // Act
        var ticketFlux = ticketService.findAll();

        // Assert
        var ticketDtos = ticketFlux.collectList().block();
        assertThat(ticketDtos).usingRecursiveComparison().isEqualTo(ticketDtoList);
        verify(ticketRepository, times(1)).findAll();
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void findByIdShouldReturnTicket() {
        // Arrange
        var ticketDocument = new TicketDocument();
        var ticketDto = new TicketDto();
        when(ticketRepository.findById(anyString())).thenReturn(Mono.just(ticketDocument));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenReturn(ticketDto);

        // Act
        var ticketMono = ticketService.findById("1234");

        // Assert
        var ticket = ticketMono.block();
        assertThat(ticket).usingRecursiveComparison().isEqualTo(ticketDto);
        verify(ticketRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void saveShouldSaveTicket() {
        // Arrange
        var ticketDocument = new TicketDocument();
        var ticketDto = new TicketDto();
        when(ticketRepository.save(any(TicketDocument.class))).thenReturn(Mono.just(ticketDocument));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenReturn(ticketDto);

        // Act
        var ticketMono = ticketService.save(ticketDocument);

        // Assert
        var ticket = ticketMono.block();
        assertThat(ticket).usingRecursiveComparison().isEqualTo(ticketDto);
        verify(ticketRepository, times(1)).save(any(TicketDocument.class));
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void deleteByIdShouldDeleteTicket() {
        // Arrange
        when(ticketRepository.deleteById(anyString())).thenReturn(Mono.empty());

        // Act
        var voidMono = ticketService.deleteById("1234");

        // Assert
        voidMono.block();
        verify(ticketRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void findAllByChildIdShouldReturnAllTicketsByChildId() {
        // Arrange
        var ticketDocumentList = List.of(new TicketDocument(), new TicketDocument());
        var ticketDtoList = List.of(new TicketDto(), new TicketDto());
        when(ticketRepository.findAllByChildId(anyString())).thenReturn(Flux.fromIterable(ticketDocumentList));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenAnswer(invocation -> {
                    TicketDocument document = invocation.getArgument(0);
                    int index = ticketDocumentList.indexOf(document);
                    return ticketDtoList.get(index);
                });

        // Act
        var ticketFlux = ticketService.findAllByChildId("1234");

        // Assert
        var ticketDtos = ticketFlux.collectList().block();
        assertThat(ticketDtos).usingRecursiveComparison().isEqualTo(ticketDtoList);
        verify(ticketRepository, times(1)).findAllByChildId(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }
}
