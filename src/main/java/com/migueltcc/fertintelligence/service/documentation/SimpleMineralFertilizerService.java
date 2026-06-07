package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;

import java.util.List;

public interface SimpleMineralFertilizerService {

    SimpleMineralFertilizerResponseDto createSimpleMineralFertilizer(
            SimpleMineralFertilizerCreateRequestDto createRequestDto,
            String username
    );

    // CORREÇÃO: O nome aqui deve ser getAllSimpleMineralFertilizers para bater com o Controller
    List<SimpleMineralFertilizerResponseDto> getAllSimpleMineralFertilizers(String username);
    List<SimpleMineralFertilizerResponseDto> getAllPublicSimpleMineralFertilizers(String username);
    List<SimpleMineralFertilizerResponseDto> getAllDefaultSimpleMineralFertilizers(String username);


    List<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizersByName(
            String name,
            String username
    );

    SimpleMineralFertilizerResponseDto updateSimpleMineralFertilizer(
            Long fertilizerId,
            SimpleMineralFertilizerPostRequestDto updateRequestDto,
            String username
    );

    void deleteSimpleMineralFertilizer(Long fertilizerId, String username);
}
