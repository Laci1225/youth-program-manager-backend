package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TicketDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer numberOfParticipants;
    private Integer standardValidityPeriod;
}

