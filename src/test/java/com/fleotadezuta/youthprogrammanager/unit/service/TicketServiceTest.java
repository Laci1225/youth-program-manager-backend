package com.fleotadezuta.youthprogrammanager.unit.service;

import com.fleotadezuta.youthprogrammanager.fixtures.service.TicketFixture;
import com.fleotadezuta.youthprogrammanager.mapper.TicketMapper;
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
import reactor.test.StepVerifier;


import static org.assertj.core.api.Assertions.assertThat;
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
        when(ticketRepository.findAll()).thenReturn(Flux.fromIterable(TicketFixture.getTicketDocumentList()));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenAnswer(invocation -> {
                    TicketDocument document = invocation.getArgument(0);
                    int index = TicketFixture.getTicketDocumentList().indexOf(document);
                    return TicketFixture.getTicketDtoList().get(index);
                });

        // Act
        var ticketFlux = ticketService.findAll();

        // Assert
        StepVerifier.create(ticketFlux)
                .expectNextSequence(TicketFixture.getTicketDtoList())
                .verifyComplete();
        verify(ticketRepository, times(1)).findAll();
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void findByIdShouldReturnTicket() {
        // Arrange
        when(ticketRepository.findById(anyString())).thenReturn(Mono.just(TicketFixture.getTicketDocument()));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenReturn(TicketFixture.getTicketDto());

        // Act
        var ticketMono = ticketService.findById("1234");

        // Assert
        assertThat(ticketMono.block()).usingRecursiveComparison().isEqualTo(TicketFixture.getTicketDto());
        verify(ticketRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void saveShouldSaveTicket() {
        // Arrange
        when(ticketRepository.save(any(TicketDocument.class))).thenReturn(Mono.just(TicketFixture.getTicketDocument()));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenReturn(TicketFixture.getTicketDto());

        // Act
        var ticketMono = ticketService.save(TicketFixture.getTicketDocument());

        // Assert
        assertThat(ticketMono.block()).usingRecursiveComparison().isEqualTo(TicketFixture.getTicketDto());
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
        when(ticketRepository.findAllByChildId(anyString())).thenReturn(Flux.fromIterable(TicketFixture.getTicketDocumentList()));
        when(ticketMapper.fromTicketDocumentToTicketDto(any(TicketDocument.class)))
                .thenAnswer(invocation -> {
                    TicketDocument document = invocation.getArgument(0);
                    int index = TicketFixture.getTicketDocumentList().indexOf(document);
                    return TicketFixture.getTicketDtoList().get(index);
                });

        // Act
        var ticketFlux = ticketService.findAllByChildId("1234");

        // Assert
        assertThat(ticketFlux.collectList().block()).usingRecursiveComparison().isEqualTo(TicketFixture.getTicketDtoList());
        verify(ticketRepository, times(1)).findAllByChildId(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void findByIdShouldFailWhenTicketNotFound() {
        // Arrange
        when(ticketRepository.findById(anyString())).thenReturn(Mono.empty());

        // Act
        var ticketMono = ticketService.findById("wrong_id");

        // Assert
        StepVerifier.create(ticketMono)
                //todo .expectError(TicketNotFoundException.class)
                .expectErrorMessage("Ticket not found")
                .verify();

        verify(ticketRepository, times(1)).findById(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void saveShouldFailWhenTicketDataIsInvalid() {
        // Arrange
        TicketDocument invalidTicket = TicketFixture.getTicketDocument();
        invalidTicket.setId(null); // Making it invalid

        when(ticketRepository.save(any(TicketDocument.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid ticket data")));

        // Act
        var ticketMono = ticketService.save(invalidTicket);

        // Assert
        StepVerifier.create(ticketMono)
                .expectErrorMessage("Invalid ticket data")
                .verify();
        verify(ticketRepository, times(1)).save(any(TicketDocument.class));
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void deleteByIdShouldFailWhenTicketNotFound() {
        // Arrange
        when(ticketRepository.deleteById(anyString())).thenReturn(Mono.error(new IllegalArgumentException("Ticket not found")));

        // Act
        var voidMono = ticketService.deleteById("wrong_id");

        // Assert
        StepVerifier.create(voidMono)
                .expectErrorMessage("Ticket not found")
                .verify();
        verify(ticketRepository, times(1)).deleteById(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void findAllByChildIdShouldFailWhenChildIdNotFound() {
        // Arrange
        when(ticketRepository.findAllByChildId(anyString())).thenReturn(Flux.error(new IllegalArgumentException("Child ID not found")));

        // Act
        var ticketFlux = ticketService.findAllByChildId("wrong_child_id");

        // Assert
        StepVerifier.create(ticketFlux)
                .expectErrorMessage("Child ID not found")
                .verify();
        verify(ticketRepository, times(1)).findAllByChildId(anyString());
        verifyNoMoreInteractions(ticketRepository);
    }
}
