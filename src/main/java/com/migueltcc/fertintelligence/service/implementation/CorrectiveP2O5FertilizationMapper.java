package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveP2O5FertilizationModel;
import org.springframework.stereotype.Component;

@Component
public class CorrectiveP2O5FertilizationMapper {

    public CorrectiveP2O5FertilizationResponseDto toDto(CorrectiveP2O5FertilizationModel model) {
        if (model == null) return null;
        return CorrectiveP2O5FertilizationResponseDto.builder()
                .id(model.getId())
                .tableId(model.getTable() != null ? model.getTable().getId() : null)
                .displayName(CorrectiveP2O5FertilizationModel.DISPLAY_NAME)
                .clayContentUnit(CorrectiveP2O5FertilizationModel.CLAY_CONTENT_UNIT)
                .availablePMehlich1Unit(CorrectiveP2O5FertilizationModel.AVAILABLE_P_MEHLICH_1_UNIT)
                .doseUnit(CorrectiveP2O5FertilizationModel.DOSE_UNIT)
                .clayContentMinimum(model.getClayContentMinimum())
                .clayContentMaximum(model.getClayContentMaximum())
                .availablePMehlich1Minimum(model.getAvailablePMehlich1Minimum())
                .availablePMehlich1Maximum(model.getAvailablePMehlich1Maximum())
                .recommendedP2O5Dose(model.getRecommendedP2O5Dose())
                .observations(model.getObservations())
                .sources(model.getSources())
                .build();
    }
}
