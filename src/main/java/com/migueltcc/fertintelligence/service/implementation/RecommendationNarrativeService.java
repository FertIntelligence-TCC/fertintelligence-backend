package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class RecommendationNarrativeService {

    private static final String CLARITY_NOTICE =
            "Texto revisado para maior clareza. Os cálculos técnicos permanecem inalterados.";

    public String improveNarrative(String technicalReport) {
        if (technicalReport == null || technicalReport.isBlank()) {
            return CLARITY_NOTICE;
        }

        String formattedReport = Arrays.stream(technicalReport.split("\\R", -1))
                .map(String::stripTrailing)
                .collect(Collectors.joining("\n"))
                .trim();

        return formattedReport + "\n\n" + CLARITY_NOTICE;
    }
}
