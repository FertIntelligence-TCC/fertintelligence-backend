package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;

import java.util.List;

public interface OrganicFertilizerService {

    OrganicFertilizerResponseDto createOrganicFertilizer(OrganicFertilizerCreateRequestDto createRequestDto, String username);

    OrganicFertilizerResponseDto getOrganicFertilizerById(Long organicFertilizerId, String username);

    List<OrganicFertilizerResponseDto> getAllOrganicFertilizers(String username);
    List<OrganicFertilizerResponseDto> getAllPublicOrganicFertilizers(String username);
    List<OrganicFertilizerResponseDto> getAllDefaultOrganicFertilizers(String username);


    List<OrganicFertilizerResponseDto> getOrganicFertilizersByName(String name, String username);

    OrganicFertilizerResponseDto updateOrganicFertilizer(Long organicFertilizerId,
                                                         OrganicFertilizerPostRequestDto updateRequestDto,
                                                         String username);

    void deleteOrganicFertilizer(Long organicFertilizerId, String username);
}
