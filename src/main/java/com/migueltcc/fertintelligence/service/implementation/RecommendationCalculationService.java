package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecommendationCalculationService {

    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;

    public RecommendationCalculationService(PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository,
                                            SoilAnalysisRepository soilAnalysisRepository,
                                            SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository,
                                            AnnualCropFolderRepository annualCropFolderRepository,
                                            CropRepository cropRepository,
                                            FoliarAnalysisRepository foliarAnalysisRepository) {
        this.physicalAnalysisExtractRepository = physicalAnalysisExtractRepository;
        this.soilAnalysisRepository = soilAnalysisRepository;
        this.saturationExtractAnalysisExtractRepository = saturationExtractAnalysisExtractRepository;
        this.annualCropFolderRepository = annualCropFolderRepository;
        this.cropRepository = cropRepository;
        this.foliarAnalysisRepository = foliarAnalysisRepository;
    }

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

        List<String> warnings = new ArrayList<>();

        Optional<PhysicalAnalysisExtractModel> physicalAnalysis = findLatestPhysicalAnalysis(plot);
        Optional<SoilAnalysisModel> soilFertilityAnalysis = findLatestSoilFertilityAnalysis(plot);
        Optional<SaturationExtractAnalysisExtractModel> saturationExtractAnalysis = findLatestSaturationExtractAnalysis(plot);
        Optional<AnnualCropFolderModel> annualCropFolder = findAnnualCropFolder(plot, dto.getCropYear());
        Optional<CropModel> crop = findCropByNameAndYear(plot, dto.getCropYear(), dto.getCropName());
        Optional<FoliarAnalysisModel> foliarAnalysis = crop.flatMap(this::findLatestFoliarAnalysis);

        String physicalSummary = physicalAnalysis
                .map(model -> "Análise física encontrada com ID " + model.getId() + ".")
                .orElseGet(() -> {
                    warnings.add("Nenhuma análise física foi encontrada para o talhão selecionado.");
                    return "Nenhuma análise física foi encontrada para o talhão selecionado.";
                });

        String soilFertilitySummary = soilFertilityAnalysis
                .map(model -> "Análise de fertilidade encontrada com ID " + model.getId() + ".")
                .orElseGet(() -> {
                    warnings.add("Nenhuma análise de fertilidade do solo foi encontrada para o talhão selecionado.");
                    return "Nenhuma análise de fertilidade do solo foi encontrada para o talhão selecionado.";
                });

        String saturationSummary = saturationExtractAnalysis
                .map(model -> "Extrato de saturação encontrado com ID " + model.getId() + ".")
                .orElseGet(() -> {
                    warnings.add("Nenhuma análise de extrato de saturação foi encontrada para o talhão selecionado.");
                    return "Nenhuma análise de extrato de saturação foi encontrada para o talhão selecionado.";
                });

        String cropSummary = annualCropFolder
                .map(folder -> crop
                        .map(foundCrop -> "Cultura encontrada: " + foundCrop.getName() + " (safra " + folder.getCropsYear() + ").")
                        .orElseGet(() -> {
                            warnings.add("Nenhuma cultura correspondente foi encontrada para a cultura e safra informadas.");
                            return "Nenhuma cultura correspondente foi encontrada para a cultura e safra informadas.";
                        }))
                .orElseGet(() -> {
                    warnings.add("Nenhuma pasta de culturas anuais foi encontrada para o ano da safra informado.");
                    return "Nenhuma pasta de culturas anuais foi encontrada para o ano da safra informado.";
                });

        String foliarSummary = crop
                .map(foundCrop -> foliarAnalysis
                        .map(model -> "Análise foliar encontrada com ID " + model.getId() + ".")
                        .orElseGet(() -> {
                            warnings.add("Nenhuma análise foliar foi encontrada para a cultura selecionada.");
                            return "Nenhuma análise foliar foi encontrada para a cultura selecionada.";
                        }))
                .orElse("Análise foliar não pôde ser buscada porque a cultura não foi encontrada.");

        warnings.add("Versão preliminar: a lógica agronômica detalhada ainda não foi aplicada.");
        warnings.add("Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional.");

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
                .physicalAnalysisId(physicalAnalysis.map(PhysicalAnalysisExtractModel::getId).orElse(null))
                .soilFertilityAnalysisId(soilFertilityAnalysis.map(SoilAnalysisModel::getId).orElse(null))
                .saturationExtractAnalysisId(saturationExtractAnalysis.map(SaturationExtractAnalysisExtractModel::getId).orElse(null))
                .annualCropFolderId(annualCropFolder.map(AnnualCropFolderModel::getId).orElse(null))
                .cropId(crop.map(CropModel::getId).orElse(null))
                .foliarAnalysisId(foliarAnalysis.map(FoliarAnalysisModel::getId).orElse(null))
                .physicalAnalysisSummary(physicalSummary)
                .soilFertilityAnalysisSummary(soilFertilitySummary)
                .saturationExtractAnalysisSummary(saturationSummary)
                .cropSummary(cropSummary)
                .foliarAnalysisSummary(foliarSummary)
                .build();
    }

    private Optional<PhysicalAnalysisExtractModel> findLatestPhysicalAnalysis(PlotModel plot) {
        return physicalAnalysisExtractRepository.findTopByRangeExtractAnalysisPlotOrderByIdDesc(plot)
                .or(() -> physicalAnalysisExtractRepository.findTopByLayerExtractAnalysisPlotOrderByIdDesc(plot));
    }

    private Optional<SoilAnalysisModel> findLatestSoilFertilityAnalysis(PlotModel plot) {
        return soilAnalysisRepository.findTopByPlotOrderByIdDesc(plot);
    }

    private Optional<SaturationExtractAnalysisExtractModel> findLatestSaturationExtractAnalysis(PlotModel plot) {
        return saturationExtractAnalysisExtractRepository.findTopByRangeExtractAnalysisPlotOrderByIdDesc(plot)
                .or(() -> saturationExtractAnalysisExtractRepository.findTopByLayerExtractAnalysisPlotOrderByIdDesc(plot));
    }

    private Optional<AnnualCropFolderModel> findAnnualCropFolder(PlotModel plot, Integer cropYear) {
        return annualCropFolderRepository.findByPlotAndCropsYear(plot, cropYear);
    }

    private Optional<CropModel> findCropByNameAndYear(PlotModel plot, Integer cropYear, com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum cropName) {
        return findAnnualCropFolder(plot, cropYear)
                .flatMap(folder -> cropRepository.findTopByFolderAndNameOrderByIdDesc(folder, cropName));
    }

    private Optional<FoliarAnalysisModel> findLatestFoliarAnalysis(CropModel crop) {
        return foliarAnalysisRepository.findTopByCropOrderByIdDesc(crop);
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
        private Long physicalAnalysisId;
        private Long soilFertilityAnalysisId;
        private Long saturationExtractAnalysisId;
        private Long annualCropFolderId;
        private Long cropId;
        private Long foliarAnalysisId;
        private String physicalAnalysisSummary;
        private String soilFertilityAnalysisSummary;
        private String saturationExtractAnalysisSummary;
        private String cropSummary;
        private String foliarAnalysisSummary;
    }
}
