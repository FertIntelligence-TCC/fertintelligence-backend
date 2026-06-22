package com.migueltcc.fertintelligence.service.implementation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FertAiClient {

    private static final String NARRATIVE_ENDPOINT = "/api/ai/recommendation/narrative";

    private final RestTemplate restTemplate;

    @Value("${fert-ai.base-url}")
    private String baseUrl;

    public String improveNarrative(String technicalReport) {
        int technicalReportLength = technicalReport != null ? technicalReport.length() : 0;
        log.info("Fert-AI narrative baseUrl={}, technicalReportLength={}", baseUrl, technicalReportLength);
        log.info("Starting Fert-AI narrative improvement request");

        ResponseEntity<NarrativeResponse> response = restTemplate.postForEntity(
                normalizeBaseUrl(baseUrl) + NARRATIVE_ENDPOINT,
                new NarrativeRequest(technicalReport),
                NarrativeResponse.class
        );

        NarrativeResponse body = response.getBody();
        String improvedReport = body != null ? body.getImprovedReport() : null;
        int improvedReportLength = improvedReport != null ? improvedReport.length() : 0;

        log.info("Fert-AI narrative response status={}, success={}, improvedReportLength={}",
                response.getStatusCode(), response.getStatusCode().is2xxSuccessful(), improvedReportLength);

        if (!response.getStatusCode().is2xxSuccessful() || improvedReport == null || improvedReport.isBlank()) {
            throw new IllegalStateException("Fert-AI returned an empty or unsuccessful narrative response");
        }

        return improvedReport;
    }

    private String normalizeBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("fert-ai.base-url is not configured");
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private record NarrativeRequest(@JsonProperty("technical_report") String technicalReport) {
    }

    @Getter
    @NoArgsConstructor
    public static class NarrativeResponse {
        @JsonProperty("improved_report")
        private String improvedReport;
    }
}
