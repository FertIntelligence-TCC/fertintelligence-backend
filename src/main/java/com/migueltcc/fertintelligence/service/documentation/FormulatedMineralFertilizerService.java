package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;

import java.util.List;

public interface FormulatedMineralFertilizerService {

    FormulatedMineralFertilizerResponseDto createFormulatedMineralFertilizer(
            FormulatedMineralFertilizerCreateRequestDto createRequestDto,
            String username
    );

    FormulatedMineralFertilizerResponseDto getFormulatedMineralFertilizerById(
            Long formulatedMineralFertilizerId,
            String username
    );

    List<FormulatedMineralFertilizerResponseDto> getAllFormulatedMineralFertilizers(String username);

    FormulatedMineralFertilizerResponseDto updateFormulatedMineralFertilizer(
            Long formulatedMineralFertilizerId,
            FormulatedMineralFertilizerPostRequestDto updateRequestDto,
            String username
    );

    void deleteFormulatedMineralFertilizer(Long formulatedMineralFertilizerId, String username);
}