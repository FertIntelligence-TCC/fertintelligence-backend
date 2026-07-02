package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioResponseDto;

public interface ExchangeableBaseRatioService {

    ExchangeableBaseRatioResponseDto createExchangeableBaseRatio(Long tableId, ExchangeableBaseRatioCreateRequestDto createRequestDto, String username);

    ExchangeableBaseRatioResponseDto getExchangeableBaseRatioById(Long criterionId, String username);

    ExchangeableBaseRatioResponseDto getExchangeableBaseRatioByTable(Long tableId, String username);

    ExchangeableBaseRatioResponseDto updateExchangeableBaseRatio(Long criterionId, ExchangeableBaseRatioPostRequestDto updateRequestDto, String username);

    void deleteExchangeableBaseRatio(Long criterionId, String username);
}
