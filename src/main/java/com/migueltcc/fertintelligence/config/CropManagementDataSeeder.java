package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.BeneficialElementsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropDeficiencyToxicityModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.TopdressingFertilizationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.LiquidSourceModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.SolidSourceModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import com.migueltcc.fertintelligence.repository.CropDeficiencyToxicityRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
import com.migueltcc.fertintelligence.repository.LiquidSourceRepository;
import com.migueltcc.fertintelligence.repository.SolidSourceRepository;
import com.migueltcc.fertintelligence.repository.TopDressingFertilizationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(12)
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CropManagementDataSeeder implements CommandLineRunner {
    private static final String SEEDED_DEFICIENCY_TOXICITY_OBSERVATION_PREFIX = "Registro fictício de deficiência/toxidez para testes #";

    private final CropRepository cropRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final TopDressingFertilizationRepository topDressingFertilizationRepository;
    private final LiquidSourceRepository liquidSourceRepository;
    private final SolidSourceRepository solidSourceRepository;
    private final CropDeficiencyToxicityRepository cropDeficiencyToxicityRepository;

    private static final List<String> LABORATORIOS = List.of(
            "Laboratório Foliar Nordeste",
            "AgroLab Tecido Vegetal",
            "Instituto de Diagnose Foliar",
            "Centro de Análise Vegetal"
    );

    private static final List<String> LIQUID_SOURCE_NAMES = List.of(
            "Fonte Líquida Balanceada",
            "Foliar Nitro Potássico",
            "Complexo Líquido Micronutrientes",
            "Bioestimulante Foliar Teste"
    );

    private static final List<String> SOLID_SOURCE_NAMES = List.of(
            "Fonte Sólida Micronutrientes",
            "Mistura Sólida Foliar",
            "Quelato Sólido de Zinco",
            "Composto Sólido Foliar Teste"
    );

    @Override
    @Transactional
    public void run(String... args) {
        cleanupLegacyFakeImageIds();

        List<CropModel> crops = cropRepository.findAll();

        if (crops.isEmpty()) {
            log.warn("⚠️ Nenhuma cultura encontrada. CropManagementDataSeeder ignorado.");
            return;
        }

        for (int cropIndex = 0; cropIndex < crops.size(); cropIndex++) {
            CropModel crop = crops.get(cropIndex);

            createFoliarAnalysisIfNotExists(crop, cropIndex, 0);
            if (cropIndex % 4 == 0) {
                createFoliarAnalysisIfNotExists(crop, cropIndex, 1);
            }

            createTopdressingIfNotExists(crop, cropIndex, 1);
            createTopdressingIfNotExists(crop, cropIndex, 2);
            if (cropIndex % 3 == 0) {
                createTopdressingIfNotExists(crop, cropIndex, 3);
            }

            createLiquidSourceIfNotExists(crop, cropIndex);
            createSolidSourceIfNotExists(crop, cropIndex);
            createDeficiencyToxicityIfNotExists(crop, cropIndex);
        }
    }

    private void createFoliarAnalysisIfNotExists(CropModel crop, int cropIndex, int analysisIndex) {
        if (crop == null) {
            return;
        }

        int year = resolveYear(crop);
        String laboratory = LABORATORIOS.get((cropIndex + analysisIndex) % LABORATORIOS.size());
        Date collectDate = new Date(15 + analysisIndex, 4, year);

        boolean exists = foliarAnalysisRepository.existsByCropAndCollectDateAndLaboratory(crop, collectDate, laboratory)
                || foliarAnalysisRepository.existsByCrop(crop);
        if (exists) {
            log.info("↩️ Registro já existe: análise foliar cultura={} laboratório={}", crop.getId(), laboratory);
            return;
        }

        FoliarAnalysisModel analysis = FoliarAnalysisModel.builder()
                .crop(crop)
                .collectDate(collectDate)
                .laboratory(laboratory)
                .macronutrients(new MacronutrientsContent(
                        25.0 + (cropIndex % 6),
                        2.0 + (cropIndex % 4) * 0.2,
                        18.0 + (cropIndex % 5),
                        8.0 + (cropIndex % 4),
                        3.0 + (cropIndex % 3) * 0.3,
                        2.0 + (cropIndex % 3) * 0.2
                ))
                .micronutrients(new MicronutrientsContent(
                        30.0 + (cropIndex % 5) * 2,
                        8.0 + (cropIndex % 4),
                        80.0 + (cropIndex % 6) * 10,
                        1.0 + (cropIndex % 3) * 0.2,
                        60.0 + (cropIndex % 5) * 6,
                        0.2 + (cropIndex % 3) * 0.1,
                        25.0 + (cropIndex % 5) * 3
                ))
                .elements(new BeneficialElementsContent(
                        0.5 + (cropIndex % 4) * 0.1,
                        2.0 + (cropIndex % 5) * 0.3,
                        0.1 + (cropIndex % 3) * 0.05,
                        0.05 + (cropIndex % 3) * 0.01,
                        0.02 + (cropIndex % 3) * 0.01
                ))
                .build();

        FoliarAnalysisModel saved = foliarAnalysisRepository.save(analysis);
        log.info("✅ Análise foliar criada: cultura={} id={}", crop.getId(), saved.getId());
    }

    private void createTopdressingIfNotExists(CropModel crop, int cropIndex, int order) {
        if (crop == null) {
            return;
        }

        boolean exists = topDressingFertilizationRepository.existsByCropAndOrder(crop, order);
        if (exists) {
            log.info("↩️ Registro já existe: adubação cobertura cultura={} ordem={}", crop.getId(), order);
            return;
        }

        int year = resolveYear(crop);
        TopdressingFertilizationModel.TopdressingFertilizationModelBuilder builder = TopdressingFertilizationModel.builder()
                .crop(crop)
                .order(order)
                .date(resolveTopdressingDate(order, year))
                .triple_superphosphate(0.0)
                .simple_superphosphate(0.0)
                .monoammonium_phosphate(0.0);

        if (order == 1) {
            builder
                    .urea(60.0 + (cropIndex % 5) * 5)
                    .potassium_chloride(40.0 + (cropIndex % 4) * 5)
                    .ammonium_sulfate(0.0)
                    .formulated(0.0);
        } else if (order == 2) {
            builder
                    .urea(45.0 + (cropIndex % 4) * 5)
                    .potassium_chloride(30.0 + (cropIndex % 3) * 5)
                    .ammonium_sulfate(20.0 + (cropIndex % 3) * 5)
                    .formulated(0.0);
        } else {
            builder
                    .formulated(100.0 + (cropIndex % 5) * 10)
                    .urea(0.0)
                    .potassium_chloride(0.0)
                    .ammonium_sulfate(0.0);
        }

        topDressingFertilizationRepository.save(builder.build());
        log.info("✅ Adubação de cobertura criada: cultura={} ordem={}", crop.getId(), order);
    }

    private void createLiquidSourceIfNotExists(CropModel crop, int cropIndex) {
        if (crop == null) {
            return;
        }

        String sourceName = LIQUID_SOURCE_NAMES.get(cropIndex % LIQUID_SOURCE_NAMES.size());
        boolean exists = liquidSourceRepository.existsByCropAndSource(crop, sourceName);
        if (exists) {
            log.info("↩️ Registro já existe: fonte líquida cultura={} nome={}", crop.getId(), sourceName);
            return;
        }

        LiquidSourceModel source = LiquidSourceModel.builder()
                .crop(crop)
                .date(new Date(12, 4, resolveYear(crop)))
                .micronutrient(AppliedMicronutrient.values()[cropIndex % AppliedMicronutrient.values().length])
                .source(sourceName)
                .concentration(5.0 + (cropIndex % 4))
                .density(1.0 + (cropIndex % 3) * 0.1)
                .applied_volume(100.0 + (cropIndex % 5) * 10)
                .tail_volume(150.0 + (cropIndex % 4) * 10)
                .build();

        liquidSourceRepository.save(source);
        log.info("✅ Fonte líquida criada: cultura={} nome={}", crop.getId(), sourceName);
    }

    private void createSolidSourceIfNotExists(CropModel crop, int cropIndex) {
        if (crop == null) {
            return;
        }

        String sourceName = SOLID_SOURCE_NAMES.get(cropIndex % SOLID_SOURCE_NAMES.size());
        boolean exists = solidSourceRepository.existsByCropAndSource(crop, sourceName);
        if (exists) {
            log.info("↩️ Registro já existe: fonte sólida cultura={} nome={}", crop.getId(), sourceName);
            return;
        }

        SolidSourceModel source = SolidSourceModel.builder()
                .crop(crop)
                .date(new Date(20, 4, resolveYear(crop)))
                .micronutrient(AppliedMicronutrient.values()[(cropIndex + 1) % AppliedMicronutrient.values().length])
                .source(sourceName)
                .concentration(2.0 + (cropIndex % 3))
                .quantity(8.0 + (cropIndex % 5) * 2)
                .build();

        solidSourceRepository.save(source);
        log.info("✅ Fonte sólida criada: cultura={} nome={}", crop.getId(), sourceName);
    }


    private void createDeficiencyToxicityIfNotExists(CropModel crop, int cropIndex) {
        if (crop == null) return;

        DeficiencyToxicityNutrient nutrient = DeficiencyToxicityNutrient.values()[cropIndex % DeficiencyToxicityNutrient.values().length];
        NutrientType type = cropIndex % 2 == 0 ? NutrientType.MACRONUTRIENT : NutrientType.MICRONUTRIENT;
        String observations = SEEDED_DEFICIENCY_TOXICITY_OBSERVATION_PREFIX + crop.getId();

        boolean exists = cropDeficiencyToxicityRepository.existsByCropAndNutrientAndObservations(crop, nutrient, observations);
        if (exists) {
            log.info("↩️ Registro já existe: deficiência/toxidez cultura={} nutriente={}", crop.getId(), nutrient);
            return;
        }

        CropDeficiencyToxicityModel model = CropDeficiencyToxicityModel.builder()
                .crop(crop)
                .nutrientType(type)
                .nutrient(nutrient)
                .healthyPlantImageId(null)
                .symptomaticPlantImageId(null)
                .observations(observations)
                .build();

        cropDeficiencyToxicityRepository.save(model);
        log.info("✅ Deficiência/toxidez criada: cultura={} nutriente={}", crop.getId(), nutrient);
    }

    private void cleanupLegacyFakeImageIds() {
        List<CropDeficiencyToxicityModel> seededRecords = cropDeficiencyToxicityRepository
                .findAllByObservationsStartingWith(SEEDED_DEFICIENCY_TOXICITY_OBSERVATION_PREFIX);

        int updatedRecords = 0;
        for (CropDeficiencyToxicityModel record : seededRecords) {
            boolean hasLegacyHealthyImageId = record.getHealthyPlantImageId() != null
                    && !record.getHealthyPlantImageId().matches("^[a-fA-F0-9]{24}$");
            boolean hasLegacySymptomaticImageId = record.getSymptomaticPlantImageId() != null
                    && !record.getSymptomaticPlantImageId().matches("^[a-fA-F0-9]{24}$");

            if (!hasLegacyHealthyImageId && !hasLegacySymptomaticImageId) {
                continue;
            }

            record.setHealthyPlantImageId(null);
            record.setSymptomaticPlantImageId(null);
            cropDeficiencyToxicityRepository.save(record);
            updatedRecords++;
        }

        if (updatedRecords > 0) {
            log.warn("🧹 {} registros seedados antigos de deficiência/toxidez tiveram IDs de imagem inválidos limpos (null).", updatedRecords);
        }
    }

    private Date resolveTopdressingDate(int order, int year) {
        if (order == 1) {
            return new Date(15, 3, year);
        }
        if (order == 2) {
            return new Date(5, 4, year);
        }
        return new Date(25, 4, year);
    }

    private int resolveYear(CropModel crop) {
        if (crop.getFolder() != null && crop.getFolder().getCropsYear() != null) {
            return crop.getFolder().getCropsYear();
        }
        return 2026;
    }
}
