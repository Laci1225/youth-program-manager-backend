package com.fleotadezuta.youthprogrammanager.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "tickets")
public class TicketTypeDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer numberOfParticipation;
    private Integer standardValidityPeriod;
}
