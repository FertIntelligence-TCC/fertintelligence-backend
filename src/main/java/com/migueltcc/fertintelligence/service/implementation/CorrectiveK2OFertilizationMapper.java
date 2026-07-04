package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveK2OFertilizationModel;
import org.springframework.stereotype.Component;

@Component
public class CorrectiveK2OFertilizationMapper {

    public CorrectiveK2OFertilizationResponseDto toDto(CorrectiveK2OFertilizationModel model) {
        if (model == null) return null;
        return CorrectiveK2OFertilizationResponseDto.builder()
                .id(model.getId())
                .tableId(model.getTable() != null ? model.getTable().getId() : null)
                .displayName(CorrectiveK2OFertilizationModel.DISPLAY_NAME)
                .ctcUnit(CorrectiveK2OFertilizationModel.CTC_UNIT)
                .exchangeableKUnit(CorrectiveK2OFertilizationModel.EXCHANGEABLE_K_UNIT)
                .doseUnit(CorrectiveK2OFertilizationModel.DOSE_UNIT)
                .ctcMinimum(model.getCtcMinimum())
                .ctcMaximum(model.getCtcMaximum())
                .exchangeableKMinimum(model.getExchangeableKMinimum())
                .exchangeableKMaximum(model.getExchangeableKMaximum())
                .recommendedK2ODose(model.getRecommendedK2ODose())
                .observations(model.getObservations())
                .sources(model.getSources())
                .build();
    }
}
