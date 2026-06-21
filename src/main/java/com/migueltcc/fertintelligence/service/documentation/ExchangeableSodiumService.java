package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumResponseDto;

public interface ExchangeableSodiumService {

    ExchangeableSodiumResponseDto createExchangeableSodium(Long tableId, ExchangeableSodiumCreateRequestDto createRequestDto, String username);

    ExchangeableSodiumResponseDto getExchangeableSodiumById(Long criterionId, String username);

    ExchangeableSodiumResponseDto getExchangeableSodiumByTable(Long tableId, String username);

    ExchangeableSodiumResponseDto updateExchangeableSodium(Long criterionId, ExchangeableSodiumPostRequestDto updateRequestDto, String username);

    void deleteExchangeableSodium(Long criterionId, String username);
}
