package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationCalculationService {

    public RecommendationCalculationResult calculate(
            RecommendationCreateRequestDto dto,
            UserModel user,
            PropertyModel property,
            PlotModel plot
    ) {
        List<String> diagnostics = new ArrayList<>();
        diagnostics.add("Usuário solicitante: " + user.getName() + " (" + user.getUsername() + ")");
        diagnostics.add("Propriedade selecionada: " + property.getNome() + " (ID " + property.getId() + ")");
        diagnostics.add("Talhão selecionado: " + plot.getIdentification() + " (ID " + plot.getId() + ")");
        diagnostics.add("Cultura informada: " + dto.getCropName());
        diagnostics.add("Ano da safra: " + dto.getCropYear());

        List<String> warnings = List.of(
                "Versão preliminar: a lógica agronômica detalhada ainda não foi aplicada.",
                "Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional."
        );

        List<String> fertilizationRows = List.of(
                "Tabela de adubação da cultura selecionada: " + dto.getCropFertilizationTableId(),
                "Tabela de interpretação de análise foliar selecionada: " + dto.getCropFoliarAnalysisInterpretationTableId()
        );

        List<String> correctionMessages = List.of(
                "Tabela de interpretação de fertilidade do solo selecionada: "
                        + dto.getSoilFertilityInterpretationCriteriaTableId(),
                "Critério de calagem selecionado: " + dto.getLimingCriteria()
        );

        return RecommendationCalculationResult.builder()
                .warnings(warnings)
                .diagnosticMessages(diagnostics)
                .fertilizationRows(fertilizationRows)
                .correctionMessages(correctionMessages)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationCalculationResult {
        private List<String> warnings;
        private List<String> diagnosticMessages;
        private List<String> fertilizationRows;
        private List<String> correctionMessages;
    }
}
