package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;

import java.util.List;

public interface ChelatedFertilizerService {

    ChelatedFertilizerResponseDto createChelatedFertilizer(ChelatedFertilizerCreateRequestDto createRequestDto, String username);

    ChelatedFertilizerResponseDto getChelatedFertilizerById(Long chelatedFertilizerId, String username);

    List<ChelatedFertilizerResponseDto> getAllChelatedFertilizers(String username);
    List<ChelatedFertilizerResponseDto> getAllPublicChelatedFertilizers(String username);


    List<ChelatedFertilizerResponseDto> getChelatedFertilizersByName(String name, String username);

    ChelatedFertilizerResponseDto updateChelatedFertilizer(Long chelatedFertilizerId,
                                                           ChelatedFertilizerPostRequestDto updateRequestDto,
                                                           String username);

    void deleteChelatedFertilizer(Long chelatedFertilizerId, String username);
}