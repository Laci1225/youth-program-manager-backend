package com.fleotadezuta.youthprogrammanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TicketTypeDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer numberOfParticipation;
    private Integer standardValidityPeriod;
}

