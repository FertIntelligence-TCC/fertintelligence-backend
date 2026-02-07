package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;

import java.util.List;

public interface AnnualCropFolderService {

    AnnualCropFolderResponseDto createAnnualCropFolder(Long plotId,
                                                       AnnualCropFolderCreateRequestDto createRequestDto,
                                                       String username);

    AnnualCropFolderResponseDto getAnnualCropFolderById(Long annualCropFolderId, String username);

    List<AnnualCropFolderResponseDto> getAllAnnualCropFoldersByPlot(Long plotId, String username);

    List<AnnualCropFolderResponseDto> getAllByPlotId(Long plotId, String username);

    AnnualCropFolderResponseDto updateAnnualCropFolder(Long annualCropFolderId,
                                                       AnnualCropFolderPostRequestDto updateRequestDto,
                                                       String username);

    void deleteAnnualCropFolder(Long annualCropFolderId, String username);
}