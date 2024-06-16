package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.DiseaseDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Disease;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiseaseFixture {

    private static final String NAME = "Asthma";
    private static final String DESCRIPTION = "A condition in which your airways narrow and swell and may produce extra mucus.";
    private static final LocalDateTime DIAGNOSED_AT = LocalDateTime.of(2019, 5, 15, 0, 0);

    public static Disease getDisease() {
        return Disease.builder()
                .name(NAME)
                .diagnosedAt(DIAGNOSED_AT)
                .build();
    }

    public static DiseaseDto getDiseaseDto() {
        return DiseaseDto.builder()
                .name(NAME)
                .diagnosedAt(DIAGNOSED_AT)
                .build();
    }
}
