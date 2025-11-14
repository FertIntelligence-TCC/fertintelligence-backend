package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentResponseDto;

public interface KExchangeableContentService {

    KExchangeableContentResponseDto createKExchangeableContent(
            Long tableId,
            KExchangeableContentCreateRequestDto createRequestDto,
            String username);

    KExchangeableContentResponseDto getKExchangeableContentById(Long criterionId, String username);

    KExchangeableContentResponseDto getKExchangeableContentByTable(Long tableId, String username);

    KExchangeableContentResponseDto updateKExchangeableContent(
            Long criterionId,
            KExchangeableContentPostRequestDto updateRequestDto,
            String username);

    void deleteKExchangeableContent(Long criterionId, String username);
}
