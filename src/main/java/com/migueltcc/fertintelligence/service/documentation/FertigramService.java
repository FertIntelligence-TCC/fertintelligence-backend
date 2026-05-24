package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;

public interface FertigramService {
    FertigramResponseDto generate(Long foliarAnalysisId, Long tableId, String username);
}
