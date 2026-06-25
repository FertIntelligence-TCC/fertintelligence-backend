package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.service.implementation.FertAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationNarrativeService {

    private final FertAiClient fertAiClient;

    public String improveNarrative(String technicalReport) {
        int technicalReportLength = technicalReport != null ? technicalReport.length() : 0;
        if (technicalReport == null || technicalReport.isBlank()) {
            log.info("Skipping Fert-AI narrative improvement because technicalReport is blank, falling back to original report");
            return technicalReport;
        }

        try {
            String improvedReport = fertAiClient.improveNarrative(technicalReport);
            log.info("Fert-AI narrative improvement succeeded: technicalReportLength={}, improvedReportLength={}, fallback=false",
                    technicalReportLength, improvedReport.length());
            return improvedReport;
        } catch (Exception ex) {
            log.warn("Fert-AI narrative improvement failed, using technicalReport fallback: technicalReportLength={}, fallback=true, cause={}",
                    technicalReportLength, ex.toString(), ex);
            return technicalReport;
        }
    }
}
