package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseResponseDto;

public interface MicronutrientDoseService {

    MicronutrientDoseResponseDto createMicronutrientDose(Long tableId, MicronutrientDoseCreateRequestDto createRequestDto, String username);

    MicronutrientDoseResponseDto getMicronutrientDoseById(Long criterionId, String username);

    MicronutrientDoseResponseDto getMicronutrientDoseByTable(Long tableId, String username);

    MicronutrientDoseResponseDto updateMicronutrientDose(Long criterionId, MicronutrientDosePostRequestDto updateRequestDto, String username);

    void deleteMicronutrientDose(Long criterionId, String username);
}
