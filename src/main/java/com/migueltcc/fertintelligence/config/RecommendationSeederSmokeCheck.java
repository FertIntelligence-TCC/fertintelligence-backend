package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Order(17)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RecommendationSeederSmokeCheck implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final PlotRepository plotRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;
    private final CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        log.info("🔎 Iniciando RecommendationSeederSmokeCheck...");

        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("Usuários", countSafely("Usuários", userRepository::count));
        counts.put("Propriedades", countSafely("Propriedades", propertyRepository::count));
        counts.put("Acessos de propriedade", countSafely("Acessos de propriedade", propertyAccessRequestRepository::count));
        counts.put("Talhões", countSafely("Talhões", plotRepository::count));
        counts.put("Permissões de talhão", countSafely("Permissões de talhão", plotAccessRequestRepository::count));
        counts.put("Análises de solo", countSafely("Análises de solo", soilAnalysisRepository::count));
        counts.put("Extratos de camada", countSafely("Extratos de camada", layerExtractRepository::count));
        counts.put("Extratos de faixa", countSafely("Extratos de faixa", rangeExtractRepository::count));
        counts.put("Extratos físicos", countSafely("Extratos físicos", physicalAnalysisExtractRepository::count));
        counts.put("Extratos de fertilidade", countSafely("Extratos de fertilidade", fertilityAnalysisExtractRepository::count));
        counts.put("Extratos de saturação", countSafely("Extratos de saturação", saturationExtractAnalysisExtractRepository::count));
        counts.put("Pastas anuais", countSafely("Pastas anuais", annualCropFolderRepository::count));
        counts.put("Culturas", countSafely("Culturas", cropRepository::count));
        counts.put("Análises foliares", countSafely("Análises foliares", foliarAnalysisRepository::count));
        counts.put("Fertilizantes simples", countSafely("Fertilizantes simples", simpleMineralFertilizerRepository::count));
        counts.put("Fertilizantes formulados", countSafely("Fertilizantes formulados", formulatedMineralFertilizerRepository::count));
        counts.put("Tabelas de adubação", countSafely("Tabelas de adubação", cropFertilizationTableRepository::count));
        counts.put("Tabelas de fertilidade", countSafely("Tabelas de fertilidade", soilFertilityInterpretationCriteriaTableRepository::count));
        counts.put("Tabelas foliares", countSafely("Tabelas foliares", cropFoliarAnalysisInterpretationTableRepository::count));

        boolean allMinimumsOk = true;
        allMinimumsOk &= checkMinimum("Usuários", counts.get("Usuários"), 6);
        allMinimumsOk &= checkMinimum("Propriedades", counts.get("Propriedades"), 5);
        allMinimumsOk &= checkMinimum("Acessos de propriedade", counts.get("Acessos de propriedade"), 10);
        allMinimumsOk &= checkMinimum("Talhões", counts.get("Talhões"), 20);
        allMinimumsOk &= checkMinimum("Permissões de talhão", counts.get("Permissões de talhão"), 10);
        allMinimumsOk &= checkMinimum("Análises de solo", counts.get("Análises de solo"), 10);
        allMinimumsOk &= checkMinimum("Pastas anuais", counts.get("Pastas anuais"), 20);
        allMinimumsOk &= checkMinimum("Culturas", counts.get("Culturas"), 20);
        allMinimumsOk &= checkMinimum("Análises foliares", counts.get("Análises foliares"), 10);
        allMinimumsOk &= checkMinimum("Fertilizantes simples", counts.get("Fertilizantes simples"), 1);
        allMinimumsOk &= checkMinimum("Fertilizantes formulados", counts.get("Fertilizantes formulados"), 1);
        allMinimumsOk &= checkMinimum("Tabelas de adubação", counts.get("Tabelas de adubação"), 1);
        allMinimumsOk &= checkMinimum("Tabelas de fertilidade", counts.get("Tabelas de fertilidade"), 1);
        allMinimumsOk &= checkMinimum("Tabelas foliares", counts.get("Tabelas foliares"), 1);

        List.of(
                "admin@fertintelligence.com",
                "gilvan@email.com",
                "miguel@email.com",
                "mateus@email.com",
                "marcos@email.com",
                "rebeca@email.com"
        ).forEach(this::logUserStatus);

        boolean payloadOk = tryBuildRecommendationPayload();

        logSummary(counts, allMinimumsOk && payloadOk);

        if (allMinimumsOk) {
            log.info("✅ DataSeeders prontos para testar Recommendation.");
        } else {
            log.warn("⚠️ DataSeeders incompletos. Verifique os seeders anteriores.");
        }
    }

    private long countSafely(String label, Supplier<Long> counter) {
        try {
            long count = counter.get();
            log.info("{}: {}", label, count);
            return count;
        } catch (Exception ex) {
            log.error("❌ Falha ao contar {}: {}", label, ex.getMessage());
            return -1L;
        }
    }

    private boolean checkMinimum(String label, long actual, long expected) {
        if (actual >= expected) {
            log.info("✅ {} OK: {} >= {}", label, actual, expected);
            return true;
        }
        log.warn("⚠️ {} abaixo do mínimo: {} < {}", label, actual, expected);
        return false;
    }

    private Optional<UserModel> findUser(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (Exception ex) {
            log.error("❌ Erro ao buscar usuário {}: {}", email, ex.getMessage());
            return Optional.empty();
        }
    }

    private void logUserStatus(String email) {
        Optional<UserModel> userOpt = findUser(email);
        if (userOpt.isEmpty()) {
            log.warn("⚠️ Usuário não encontrado: {}", email);
            return;
        }

        UserModel user = userOpt.get();
        long accessibleProperties = countSafely("Propriedades acessíveis para " + email,
                () -> propertyAccessRequestRepository.findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED).stream()
                        .map(PropertyAccessRequestModel::getProperty)
                        .filter(p -> p != null)
                        .map(PropertyModel::getId)
                        .distinct()
                        .count());

        long accessiblePlots = countSafely("Talhões acessíveis para " + email,
                () -> plotAccessRequestRepository.findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED).stream()
                        .map(PlotAccessRequestModel::getPlot)
                        .filter(p -> p != null)
                        .map(PlotModel::getId)
                        .distinct()
                        .count());

        log.info("👤 Usuário {} encontrado. Cargo: {}. Propriedades acessíveis: {}. Talhões acessíveis: {}",
                email, user.getCargo(), accessibleProperties, accessiblePlots);

        if (accessibleProperties < 1) {
            log.warn("⚠️ Usuário {} sem propriedade acessível aprovada.", email);
        }
        if (accessiblePlots < 1) {
            log.warn("⚠️ Usuário {} sem talhão acessível aprovado.", email);
        }
    }

    private boolean tryBuildRecommendationPayload() {
        try {
            Optional<UserModel> userOpt = userRepository.findAll().stream().findFirst();
            if (userOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: usuário inexistente.");
                return false;
            }

            Optional<PropertyModel> propertyOpt = propertyRepository.findAll().stream().findFirst();
            if (propertyOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: propriedade inexistente.");
                return false;
            }

            PropertyModel property = propertyOpt.get();
            Optional<PlotModel> plotOpt = plotRepository.findAllByProperty(property).stream().findFirst();
            if (plotOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: talhão inexistente para propriedade {}.", property.getId());
                return false;
            }

            PlotModel plot = plotOpt.get();
            Optional<AnnualCropFolderModel> folderOpt = annualCropFolderRepository.findAll().stream()
                    .filter(folder -> folder.getPlot() != null && plot.getId().equals(folder.getPlot().getId()))
                    .findFirst();
            if (folderOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: pasta anual inexistente para talhão {}.", plot.getId());
                return false;
            }

            AnnualCropFolderModel folder = folderOpt.get();
            Optional<CropModel> cropOpt = cropRepository.findAllByFolder(folder).stream().findFirst();
            if (cropOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: cultura inexistente para pasta anual {}.", folder.getId());
                return false;
            }

            Optional<CropFertilizationTableModel> cropTableOpt = cropFertilizationTableRepository.findAll().stream().findFirst();
            if (cropTableOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: tabela de adubação inexistente.");
                return false;
            }

            Optional<SoilFertilityInterpretationCriteriaTableModel> soilTableOpt = soilFertilityInterpretationCriteriaTableRepository.findAll().stream().findFirst();
            if (soilTableOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: tabela de fertilidade inexistente.");
                return false;
            }

            Optional<CropFoliarAnalysisInterpretationTableModel> foliarTableOpt = cropFoliarAnalysisInterpretationTableRepository.findAll().stream().findFirst();
            if (foliarTableOpt.isEmpty()) {
                log.error("❌ Não foi possível montar payload: tabela foliar inexistente.");
                return false;
            }

            CropModel crop = cropOpt.get();
            log.info("Payload sugerido para POST /recommendation/generate: {{\"tipo_recomendacao\":\"BOTH\",\"id_propriedade\":{},\"id_talhao\":{},\"ano_safra\":{},\"cultura\":\"{}\",\"id_tabela_adubacao_cultura\":{},\"id_tabela_interpretacao_fertilidade_solo\":{},\"id_tabela_interpretacao_analise_foliar\":{},\"criterio_calagem\":\"SATURACAO_POR_BASES_TROCAVEIS\"}}",
                    property.getId(),
                    plot.getId(),
                    folder.getCropsYear(),
                    crop.getName().name(),
                    cropTableOpt.get().getId(),
                    soilTableOpt.get().getId(),
                    foliarTableOpt.get().getId());

            return true;
        } catch (Exception ex) {
            log.error("❌ Erro ao montar payload de Recommendation: {}", ex.getMessage(), ex);
            return false;
        }
    }

    private void logSummary(Map<String, Long> counts, boolean success) {
        log.info("====================================================");
        log.info("SMOKE CHECK DOS DATASEEDERS");
        log.info("Usuários: {}", counts.getOrDefault("Usuários", -1L));
        log.info("Propriedades: {}", counts.getOrDefault("Propriedades", -1L));
        log.info("Acessos de propriedade: {}", counts.getOrDefault("Acessos de propriedade", -1L));
        log.info("Talhões: {}", counts.getOrDefault("Talhões", -1L));
        log.info("Permissões de talhão: {}", counts.getOrDefault("Permissões de talhão", -1L));
        log.info("Análises de solo: {}", counts.getOrDefault("Análises de solo", -1L));
        log.info("Pastas anuais: {}", counts.getOrDefault("Pastas anuais", -1L));
        log.info("Culturas: {}", counts.getOrDefault("Culturas", -1L));
        log.info("Análises foliares: {}", counts.getOrDefault("Análises foliares", -1L));
        log.info("Fertilizantes simples: {}", counts.getOrDefault("Fertilizantes simples", -1L));
        log.info("Fertilizantes formulados: {}", counts.getOrDefault("Fertilizantes formulados", -1L));
        log.info("Tabelas de adubação: {}", counts.getOrDefault("Tabelas de adubação", -1L));
        log.info("Tabelas de fertilidade: {}", counts.getOrDefault("Tabelas de fertilidade", -1L));
        log.info("Tabelas foliares: {}", counts.getOrDefault("Tabelas foliares", -1L));
        log.info("Status geral: {}", success ? "OK" : "ATENÇÃO");
        log.info("====================================================");
    }
}
