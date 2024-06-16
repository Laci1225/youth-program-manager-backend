package com.fleotadezuta.youthprogrammanager.fixtures.service;

import com.fleotadezuta.youthprogrammanager.model.MedicineDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Medicine;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicineFixture {

    private static final String NAME = "Ventolin";
    private static final String DOSE = "2 puffs every 4-6 hours";
    private static final LocalDateTime TAKEN_SINCE = LocalDateTime.of(2019, 5, 15, 0, 0);

    public static Medicine getMedicine() {
        return Medicine.builder()
                .name(NAME)
                .dose(DOSE)
                .takenSince(TAKEN_SINCE)
                .build();
    }

    public static MedicineDto getMedicineDto() {
        return MedicineDto.builder()
                .name(NAME)
                .dose(DOSE)
                .takenSince(TAKEN_SINCE)
                .build();
    }
}
