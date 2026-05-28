package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.repository.*;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(16)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RecommendationTestScenarioSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;
    private final CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;

    private static final List<NomeComum> PREFERRED_CROPS = List.of(
            NomeComum.MILHO,
            NomeComum.SOJA,
            NomeComum.FEIJAO_COMUM,
            NomeComum.ALGODAO,
            NomeComum.CANA_DE_ACUCAR
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🌱 Iniciando RecommendationTestScenarioSeeder...");

        List<TargetScenario> targets = List.of(
                new TargetScenario("Cenário Proprietário", "admin@fertintelligence.com", Cargo.PROPRIETARIO, true),
                new TargetScenario("Cenário Gerente", "gilvan@email.com", Cargo.GERENTE, true),
                new TargetScenario("Cenário Agrônomo Residente", "miguel@email.com", Cargo.AGRONOMO_RESIDENTE, true),
                new TargetScenario("Cenário Agrônomo Consultor", "mateus@email.com", Cargo.AGRONOMO_CONSULTOR, true),
                new TargetScenario("Cenário Secretário", "marcos@email.com", Cargo.SECRETARIO, false),
                new TargetScenario("Cenário Supervisor de Área", "rebeca@email.com", Cargo.SUPERVISOR_DE_AREA, false)
        );

        boolean fertilizersAvailable = hasFertilizers();
        if (!fertilizersAvailable) {
            log.warn("⚠️ Nenhum fertilizante simples/formulado encontrado. O endpoint pode falhar no fluxo completo.");
        }

        int validScenarios = 0;
        for (TargetScenario target : targets) {
            Optional<UserModel> userOpt = findUser(target.username());
            if (userOpt.isEmpty()) {
                log.warn("⚠️ Usuário {} não encontrado. Cenário '{}' será ignorado.", target.username(), target.label());
                continue;
            }

            UserModel user = userOpt.get();

            Optional<PropertyModel> propertyOpt = findAccessibleProperty(user);
            if (propertyOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma propriedade acessível encontrada para {}. Cenário '{}' ignorado.", target.username(), target.label());
                continue;
            }

            PropertyModel property = propertyOpt.get();

            Optional<PlotModel> plotOpt = findAccessiblePlot(user, property);
            if (plotOpt.isEmpty()) {
                log.warn("⚠️ Nenhum talhão acessível encontrado para {} na propriedade {}. Cenário ignorado.",
                        target.username(), property.getId());
                continue;
            }

            PlotModel plot = plotOpt.get();

            Optional<AnnualCropFolderModel> folderOpt = findFolderForPlot(plot);
            if (folderOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma pasta anual (2026/2025/2024) encontrada para talhão {}. Cenário ignorado.", plot.getId());
                continue;
            }

            AnnualCropFolderModel folder = folderOpt.get();

            Optional<CropModel> cropOpt = findCropForFolder(folder);
            if (cropOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma cultura encontrada para pasta anual {}. Cenário ignorado.", folder.getId());
                continue;
            }

            CropModel crop = cropOpt.get();

            Optional<CropFertilizationTableModel> cropTableOpt = findCropFertilizationTableForCrop(crop);
            if (cropTableOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma tabela de adubação encontrada. Cenário ignorado para usuário {}.", target.username());
                continue;
            }

            Optional<SoilFertilityInterpretationCriteriaTableModel> soilTableOpt = findSoilFertilityTable(user);
            if (soilTableOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma tabela de fertilidade do solo encontrada. Cenário ignorado para usuário {}.", target.username());
                continue;
            }

            Optional<CropFoliarAnalysisInterpretationTableModel> foliarTableOpt = findFoliarInterpretationTableForCrop(user, crop);
            if (foliarTableOpt.isEmpty()) {
                log.warn("⚠️ Nenhuma tabela de interpretação foliar encontrada. Cenário ignorado para usuário {}.", target.username());
                continue;
            }

            if (!hasSoilAnalysis(plot)) {
                log.warn("⚠️ Talhão {} não possui análise de solo. Recomendação pode ser parcial/limitada.", plot.getId());
            }

            if (!hasFoliarAnalysis(crop)) {
                log.warn("⚠️ Cultura {} (id={}) não possui análise foliar. Recomendação pode ser parcial/limitada.", crop.getName(), crop.getId());
            }

            ScenarioSummary summary = ScenarioSummary.builder()
                    .label(target.label())
                    .username(target.username())
                    .propertyId(property.getId())
                    .propertyName(property.getNome())
                    .plotId(plot.getId())
                    .plotIdentification(plot.getIdentification())
                    .cropYear(folder.getCropsYear())
                    .cropName(crop.getName().name())
                    .cropFertilizationTableId(cropTableOpt.get().getId())
                    .soilFertilityInterpretationTableId(soilTableOpt.get().getId())
                    .cropFoliarInterpretationTableId(foliarTableOpt.get().getId())
                    .canPrint(target.canPrint())
                    .build();

            logScenario(summary, target.expectedRole());
            validScenarios++;
        }

        log.info("✅ RecommendationTestScenarioSeeder finalizado. Cenários válidos: {}", validScenarios);
    }

    private Optional<UserModel> findUser(String emailOrUsername) {
        return userRepository.findByEmail(emailOrUsername)
                .or(() -> userRepository.findByUsername(emailOrUsername));
    }

    private Optional<PropertyModel> findAccessibleProperty(UserModel user) {
        if (user.getCargo() == Cargo.PROPRIETARIO) {
            return propertyRepository.findAll().stream()
                    .sorted(Comparator.comparing(PropertyModel::getId))
                    .filter(property -> !plotRepository.findAllByProperty(property).isEmpty())
                    .findFirst()
                    .or(() -> propertyRepository.findAll().stream().findFirst());
        }

        List<PropertyAccessRequestModel> approved = propertyAccessRequestRepository
                .findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED);

        if (approved.isEmpty()) {
            return Optional.empty();
        }

        if (user.getCargo() == Cargo.GERENTE) {
            return approved.stream()
                    .map(PropertyAccessRequestModel::getProperty)
                    .filter(Objects::nonNull)
                    .findFirst();
        }

        return approved.stream()
                .map(PropertyAccessRequestModel::getProperty)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PropertyModel::getId))
                .max(Comparator.comparingInt(this::countApprovedPlotScopesForProperty));
    }

    private int countApprovedPlotScopesForProperty(PropertyModel property) {
        return (int) plotAccessRequestRepository.findAllByPropertyAndStatus(property, AccessRequestStatus.APPROVED)
                .stream()
                .filter(par -> par.getScope() == PermissionScope.PLOT)
                .count();
    }

    private Optional<PlotModel> findAccessiblePlot(UserModel user, PropertyModel property) {
        List<PlotModel> plots = plotRepository.findAllByProperty(property);
        if (plots.isEmpty()) {
            return Optional.empty();
        }

        if (user.getCargo() == Cargo.PROPRIETARIO || user.getCargo() == Cargo.GERENTE) {
            return plots.stream().sorted(Comparator.comparing(PlotModel::getId)).findFirst();
        }

        return plots.stream()
                .filter(plot -> plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndStatus(
                        property, plot, user, AccessRequestStatus.APPROVED
                ))
                .findFirst();
    }

    private Optional<AnnualCropFolderModel> findFolderForPlot(PlotModel plot) {
        List<Integer> preferredYears = List.of(2026, 2025, 2024);
        for (Integer year : preferredYears) {
            Optional<AnnualCropFolderModel> folder = annualCropFolderRepository.findByPlotAndCropsYear(plot, year);
            if (folder.isPresent()) {
                return folder;
            }
        }
        return Optional.empty();
    }

    private Optional<CropModel> findCropForFolder(AnnualCropFolderModel folder) {
        List<CropModel> crops = cropRepository.findAllByFolder(folder);
        for (NomeComum preferredCrop : PREFERRED_CROPS) {
            Optional<CropModel> match = crops.stream().filter(c -> c.getName() == preferredCrop).findFirst();
            if (match.isPresent()) {
                return match;
            }
        }
        return crops.stream().findFirst();
    }

    private Optional<CropFertilizationTableModel> findCropFertilizationTableForCrop(CropModel crop) {
        List<CropFertilizationTableModel> all = cropFertilizationTableRepository.findAll();
        List<CropFertilizationTableModel> compatibles = all.stream()
                .filter(table -> table.getCrop_common_name() == crop.getName())
                .toList();

        Optional<CropFertilizationTableModel> preferred = compatibles.stream()
                .filter(CropFertilizationTableModel::isPublicTable)
                .findFirst()
                .or(() -> compatibles.stream().findFirst());

        if (preferred.isPresent()) {
            return preferred;
        }

        Optional<CropFertilizationTableModel> fallback = all.stream().findFirst();
        fallback.ifPresent(table -> log.warn("⚠️ Tabela de adubação não compatível usada apenas para smoke test. tabelaId={}", table.getId()));
        return fallback;
    }

    private Optional<SoilFertilityInterpretationCriteriaTableModel> findSoilFertilityTable(UserModel user) {
        List<SoilFertilityInterpretationCriteriaTableModel> all = soilFertilityInterpretationCriteriaTableRepository.findAll();
        return all.stream().filter(SoilFertilityInterpretationCriteriaTableModel::isPublicTable).findFirst()
                .or(() -> all.stream().filter(t -> t.getCreator() != null && t.getCreator().getId().equals(user.getId())).findFirst())
                .or(() -> all.stream().findFirst());
    }

    private Optional<CropFoliarAnalysisInterpretationTableModel> findFoliarInterpretationTableForCrop(UserModel user, CropModel crop) {
        List<CropFoliarAnalysisInterpretationTableModel> all = cropFoliarAnalysisInterpretationTableRepository.findAll();
        String cropName = crop.getName().name();

        List<CropFoliarAnalysisInterpretationTableModel> compatibles = all.stream()
                .filter(t -> t.getName() != null && t.getName().toUpperCase().contains(cropName))
                .toList();

        Optional<CropFoliarAnalysisInterpretationTableModel> preferred = compatibles.stream()
                .filter(CropFoliarAnalysisInterpretationTableModel::isPublicTable)
                .findFirst()
                .or(() -> compatibles.stream().filter(t -> t.getCreator() != null && t.getCreator().getId().equals(user.getId())).findFirst())
                .or(() -> compatibles.stream().findFirst());

        if (preferred.isPresent()) {
            return preferred;
        }

        Optional<CropFoliarAnalysisInterpretationTableModel> fallback = all.stream().findFirst();
        fallback.ifPresent(table -> log.warn("⚠️ Tabela foliar não compatível com cultura {} usada como fallback (tableId={}).",
                crop.getName(), table.getId()));
        return fallback;
    }

    private boolean hasSoilAnalysis(PlotModel plot) {
        return !soilAnalysisRepository.findAllByPlot(plot).isEmpty();
    }

    private boolean hasFoliarAnalysis(CropModel crop) {
        return foliarAnalysisRepository.existsByCrop(crop);
    }

    private boolean hasFertilizers() {
        return !simpleMineralFertilizerRepository.findAll().isEmpty()
                || !formulatedMineralFertilizerRepository.findAll().isEmpty();
    }

    private void logScenario(ScenarioSummary scenario, Cargo expectedRole) {
        log.info("====================================================");
        log.info("CENÁRIO RECOMMENDATION DISPONÍVEL");
        log.info("Usuário: {}", scenario.getUsername());
        log.info("Cargo esperado: {}", expectedRole);
        log.info("Pode imprimir: {}", scenario.isCanPrint());
        log.info("Payload sugerido:");
        log.info("{{\n  \"tipo_recomendacao\": \"{}\",\n  \"id_propriedade\": {},\n  \"id_talhao\": {},\n  \"ano_safra\": {},\n  \"cultura\": \"{}\",\n  \"id_tabela_adubacao_cultura\": {},\n  \"id_tabela_interpretacao_fertilidade_solo\": {},\n  \"id_tabela_interpretacao_analise_foliar\": {},\n  \"criterio_calagem\": \"{}\"\n}}",
                RecommendationType.BOTH.name(),
                scenario.getPropertyId(),
                scenario.getPlotId(),
                scenario.getCropYear(),
                scenario.getCropName(),
                scenario.getCropFertilizationTableId(),
                scenario.getSoilFertilityInterpretationTableId(),
                scenario.getCropFoliarInterpretationTableId(),
                CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS.name()
        );
        log.info("Endpoint:");
        log.info("POST /recommendation/generate");
        log.info("====================================================");
    }

    private record TargetScenario(String label, String username, Cargo expectedRole, boolean canPrint) {
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ScenarioSummary {
        private String label;
        private String username;
        private Long propertyId;
        private String propertyName;
        private Long plotId;
        private String plotIdentification;
        private Integer cropYear;
        private String cropName;
        private Long cropFertilizationTableId;
        private Long soilFertilityInterpretationTableId;
        private Long cropFoliarInterpretationTableId;
        private boolean canPrint;
    }
}
