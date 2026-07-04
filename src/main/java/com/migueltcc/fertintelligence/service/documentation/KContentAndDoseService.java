package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseResponseDto;

public interface KContentAndDoseService {

    KContentAndDoseResponseDto createKContentAndDose(Long tableId, KContentAndDoseCreateRequestDto createRequestDto, String username);

    KContentAndDoseResponseDto getKContentAndDoseById(Long criterionId, String username);

    KContentAndDoseResponseDto getKContentAndDoseByTable(Long tableId, String username);

    KContentAndDoseResponseDto updateKContentAndDose(Long criterionId, KContentAndDosePostRequestDto updateRequestDto, String username);

    void deleteKContentAndDose(Long criterionId, String username);
}
