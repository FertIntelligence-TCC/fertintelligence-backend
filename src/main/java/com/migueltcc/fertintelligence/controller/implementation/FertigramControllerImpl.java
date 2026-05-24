package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.FertigramController;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.service.documentation.FertigramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fertigram")
@RequiredArgsConstructor
public class FertigramControllerImpl implements FertigramController {

    private final FertigramService fertigramService;

    @Override
    @GetMapping("/generate")
    public ResponseEntity<FertigramResponseDto> generate(@RequestParam(name = "foliarAnalysisId") Long foliarAnalysisId,
                                                         @RequestParam(name = "tableId") Long tableId,
                                                         Authentication authentication) {
        return ResponseEntity.ok(fertigramService.generate(foliarAnalysisId, tableId, authentication.getName()));
    }
}
