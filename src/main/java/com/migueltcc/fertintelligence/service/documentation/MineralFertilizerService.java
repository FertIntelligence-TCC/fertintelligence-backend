package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;

import java.util.List;

public interface MineralFertilizerService {

    MineralFertilizerResponseDto createMineralFertilizer(MineralFertilizerCreateRequestDto createRequestDto, String username);

    MineralFertilizerResponseDto getMineralFertilizerById(Long mineralFertilizerId, String username);

    List<MineralFertilizerResponseDto> getAllMineralFertilizers(String username);
    List<MineralFertilizerResponseDto> getAllPublicMineralFertilizers(String username);


    List<MineralFertilizerResponseDto> getMineralFertilizersByName(String name, String username);

    MineralFertilizerResponseDto updateMineralFertilizer(Long mineralFertilizerId,
                                                         MineralFertilizerPostRequestDto updateRequestDto,
                                                         String username);

    void deleteMineralFertilizer(Long mineralFertilizerId, String username);
}