package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "ticket")
public class TicketDto {
    private String id;
    private ChildDto child;
    private TicketTypeDto ticketType;
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;
    private Integer price;
    private Integer numberOfParticipation;
}
