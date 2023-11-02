package com.fleotadezuta.youthprogrammanager.mapper;

import com.fleotadezuta.youthprogrammanager.model.MedicineDto;
import com.fleotadezuta.youthprogrammanager.persistence.document.Medicine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicineMapper {
    MedicineDto fromMedicineToMedicineDto(Medicine medicine);

    Medicine fromMedicineDtoToMedicine(MedicineDto medicineDto);
}
