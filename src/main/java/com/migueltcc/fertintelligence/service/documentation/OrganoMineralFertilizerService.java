package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;

import java.util.List;

public interface OrganoMineralFertilizerService {

    OrganoMineralFertilizerResponseDto createOrganoMineralFertilizer(
            OrganoMineralFertilizerCreateRequestDto createRequestDto,
            String username
    );

    OrganoMineralFertilizerResponseDto getOrganoMineralFertilizerById(
            Long organoMineralFertilizerId,
            String username
    );

    List<OrganoMineralFertilizerResponseDto> getAllOrganoMineralFertilizers();
    List<OrganoMineralFertilizerResponseDto> getAllPublicOrganoMineralFertilizers();


    List<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizersByName(
            String name,
            String username
    );

    OrganoMineralFertilizerResponseDto updateOrganoMineralFertilizer(
            Long organoMineralFertilizerId,
            OrganoMineralFertilizerPostRequestDto updateRequestDto,
            String username
    );

    void deleteOrganoMineralFertilizer(Long organoMineralFertilizerId, String username);
}