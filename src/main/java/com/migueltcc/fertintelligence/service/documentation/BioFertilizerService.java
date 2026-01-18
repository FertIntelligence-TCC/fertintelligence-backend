package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;

import java.util.List;

public interface BioFertilizerService {

    BioFertilizerResponseDto createBioFertilizer(BioFertilizerCreateRequestDto createRequestDto, String username);

    BioFertilizerResponseDto getBioFertilizerById(Long bioFertilizerId, String username);

    List<BioFertilizerResponseDto> getAllBioFertilizers(String username);

    List<BioFertilizerResponseDto> getBioFertilizersByName(String name, String username);

    BioFertilizerResponseDto updateBioFertilizer(Long bioFertilizerId,
                                                 BioFertilizerPostRequestDto updateRequestDto,
                                                 String username);

    void deleteBioFertilizer(Long bioFertilizerId, String username);
}