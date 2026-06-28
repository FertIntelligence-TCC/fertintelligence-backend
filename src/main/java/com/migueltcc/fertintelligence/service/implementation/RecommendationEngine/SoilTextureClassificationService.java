package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SoilTextureClassificationService {

    public SoilTextureClassificationResult classify(RecommendationModel recommendation,
                                                   PhysicalAnalysisExtractModel physicalAnalysis) {
        Optional<TexturalClassification> strategy = selectStrategy(recommendation);
        if (strategy.isEmpty()) {
            return unclassified(null, null,
                    "Classificacao textural da Recommendation ausente; estrategia nao selecionada.");
        }
        return classify(strategy.get(), physicalAnalysis);
    }

    public SoilTextureClassificationResult classify(TexturalClassification strategy,
                                                   PhysicalAnalysisExtractModel physicalAnalysis) {
        if (strategy == null) {
            return unclassified(null, null,
                    "Estrategia de classificacao textural ausente.");
        }

        if (strategy == TexturalClassification.BRASILEIRO) {
            return classifyBrazilian(physicalAnalysis);
        }

        return classifyAmerican(physicalAnalysis);
    }

    public SoilTextureClassificationResult classifyBrazilian(PhysicalAnalysisExtractModel physicalAnalysis) {
        GranulometricFractions fractions = readBrazilianFractionsInGramsPerKg(physicalAnalysis);
        if (fractions.hasErrors()) {
            return unclassified(TexturalClassification.BRASILEIRO, fractions.toPercentFractions(), fractions.warnings());
        }
        return classifyBrazilian(fractions);
    }

    public SoilTextureClassificationResult classifyAmerican(PhysicalAnalysisExtractModel physicalAnalysis) {
        GranulometricFractions fractions = readAmericanFractionsInGramsPerKg(physicalAnalysis);
        if (fractions.hasErrors()) {
            return unclassified(TexturalClassification.AMERICANO, fractions.toPercentFractions(), fractions.warnings());
        }
        return classifyAmerican(fractions);
    }

    public Optional<TexturalClassification> selectStrategy(RecommendationModel recommendation) {
        return Optional.ofNullable(recommendation)
                .map(RecommendationModel::getTexturalClassification);
    }

    private GranulometricFractions readBrazilianFractionsInGramsPerKg(PhysicalAnalysisExtractModel physicalAnalysis) {
        List<String> warnings = new ArrayList<>();
        if (physicalAnalysis == null) {
            warnings.add("Analise fisica ausente; nao ha fracoes granulometricas para classificar textura.");
            return new GranulometricFractions(null, null, null, warnings, true);
        }

        Double sand = physicalAnalysis.getTeorAreia();
        Double silt = physicalAnalysis.getTeorSilte();
        Double clay = physicalAnalysis.getTeorArgila();
        if (hasMissingFraction(sand, silt, clay)) {
            warnings.add("Areia, silte e argila devem estar informados para classificar textura brasileira.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }
        if (hasInvalidFraction(sand, silt, clay)) {
            warnings.add("Fracoes granulometricas invalidas; valores devem ser finitos e nao negativos.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }
        if (!isBrazilianUnitConfirmed(physicalAnalysis)) {
            warnings.add("Classificacao brasileira requer areia, silte e argila em g/kg; unidade informada nao confirmada como g/kg.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }

        return new GranulometricFractions(sand, silt, clay, warnings, false);
    }

    private GranulometricFractions readAmericanFractionsInGramsPerKg(PhysicalAnalysisExtractModel physicalAnalysis) {
        List<String> warnings = new ArrayList<>();
        if (physicalAnalysis == null) {
            warnings.add("Analise fisica ausente; nao ha fracoes granulometricas para classificar textura.");
            return new GranulometricFractions(null, null, null, warnings, true);
        }

        Double sand = physicalAnalysis.getTeorAreia();
        Double silt = physicalAnalysis.getTeorSilte();
        Double clay = physicalAnalysis.getTeorArgila();
        if (hasMissingFraction(sand, silt, clay)) {
            warnings.add("Areia, silte e argila devem estar informados para classificar textura americana.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }
        if (hasInvalidFraction(sand, silt, clay)) {
            warnings.add("Fracoes granulometricas invalidas; valores devem ser finitos e nao negativos.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }
        if (!isAmericanUnitConfirmed(physicalAnalysis)) {
            warnings.add("Classificacao americana requer areia, silte e argila em g/kg; unidade informada nao confirmada como g/kg.");
            return new GranulometricFractions(sand, silt, clay, warnings, true);
        }

        return new GranulometricFractions(sand, silt, clay, warnings, false);
    }

    private NormalizedFractions readAndNormalizeFractions(PhysicalAnalysisExtractModel physicalAnalysis) {
        List<String> warnings = new ArrayList<>();
        if (physicalAnalysis == null) {
            warnings.add("Analise fisica ausente; nao ha fracoes granulometricas para classificar textura.");
            return new NormalizedFractions(null, warnings, true);
        }

        Double sand = physicalAnalysis.getTeorAreia();
        Double silt = physicalAnalysis.getTeorSilte();
        Double clay = physicalAnalysis.getTeorArgila();
        if (hasMissingFraction(sand, silt, clay)) {
            warnings.add("Areia, silte e argila devem estar informados para classificar textura.");
            return new NormalizedFractions(null, warnings, true);
        }
        if (hasInvalidFraction(sand, silt, clay)) {
            warnings.add("Fracoes granulometricas invalidas; valores devem ser finitos e nao negativos.");
            return new NormalizedFractions(null, warnings, true);
        }

        double total = sand + silt + clay;
        if (total <= 0d) {
            warnings.add("Soma das fracoes granulometricas igual a zero; textura nao classificada.");
            return new NormalizedFractions(null, warnings, true);
        }
        if (!isExpectedTotal(total)) {
            warnings.add("Soma das fracoes granulometricas diferente de 100 ou 1000; valores normalizados proporcionalmente para percentual.");
        }

        SoilTextureFractions fractions = new SoilTextureFractions(
                toPercent(sand, total),
                toPercent(silt, total),
                toPercent(clay, total)
        );
        return new NormalizedFractions(fractions, warnings, false);
    }

    private SoilTextureClassificationResult classifyBrazilian(NormalizedFractions normalized) {
        double clay = normalized.fractions().clayPercent();
        String texturalClass = clay <= 15d
                ? "Textura Arenosa"
                : clay <= 35d
                ? "Textura Media"
                : clay <= 60d
                ? "Textura Argilosa"
                : "Muito Argilosa";

        return classified(TexturalClassification.BRASILEIRO, texturalClass, normalized);
    }

    private SoilTextureClassificationResult classifyBrazilian(GranulometricFractions fractions) {
        String texturalClass = isSiltTextured(fractions)
                ? "Textura Siltosa"
                : brazilianClassByClay(fractions.clay());

        return new SoilTextureClassificationResult(
                true,
                TexturalClassification.BRASILEIRO,
                texturalClass,
                fractions.toPercentFractions(),
                List.copyOf(fractions.warnings())
        );
    }

    private String brazilianClassByClay(double clay) {
        if (clay <= 150d) return "Textura Arenosa";
        if (clay <= 350d) return "Textura Media";
        if (clay <= 600d) return "Textura Argilosa";
        return "Muito Argilosa";
    }

    private boolean isSiltTextured(GranulometricFractions fractions) {
        return fractions.clay() <= 350d
                && fractions.sand() <= 150d
                && fractions.silt() >= 650d;
    }

    private SoilTextureClassificationResult classifyAmerican(GranulometricFractions fractions) {
        String texturalClass = americanTextureClass(
                fractions.sand(),
                fractions.silt(),
                fractions.clay()
        );
        if (texturalClass == null) {
            List<String> warnings = new ArrayList<>(fractions.warnings());
            warnings.add("Fracoes granulometricas nao se enquadraram nas regras americanas modeladas.");
            return unclassified(TexturalClassification.AMERICANO, fractions.toPercentFractions(), warnings);
        }
        return new SoilTextureClassificationResult(
                true,
                TexturalClassification.AMERICANO,
                texturalClass,
                fractions.toPercentFractions(),
                List.copyOf(fractions.warnings())
        );
    }

    private String americanTextureClass(double sand, double silt, double clay) {
        // Ordem segue o triangulo textural americano para resolver bordas compartilhadas.
        if (isSand(sand, silt, clay)) return "Areia";
        if (isLoamySand(sand, silt, clay)) return "Areia franca";
        if (isSandyLoam(sand, silt, clay)) return "Franco arenosa";
        if (isLoam(sand, silt, clay)) return "Franca";
        if (isSiltLoam(silt, clay)) return "Franco siltosa";
        if (isSilt(silt, clay)) return "Silte";
        if (isSandyClayLoam(sand, clay)) return "Franco argilo arenosa";
        if (isClayLoam(sand, clay)) return "Franco argilosa";
        if (isSiltyClayLoam(sand, clay)) return "Franco argilo siltosa";
        if (isSandyClay(sand, clay)) return "Argilo arenosa";
        if (isClay(sand, silt, clay)) return "Argila";
        if (isSiltyClay(silt, clay)) return "Argilo siltosa";
        if (isVeryClayey(clay)) return "Muito argilosa";
        return null;
    }

    private boolean isSand(double sand, double silt, double clay) {
        return sand >= 850d && silt + 1.5d * clay < 150d;
    }

    private boolean isLoamySand(double sand, double silt, double clay) {
        return sand >= 700d && sand < 900d && silt + 1.5d * clay >= 150d && silt + 2d * clay < 300d;
    }

    private boolean isSandyLoam(double sand, double silt, double clay) {
        return (clay >= 70d && clay < 200d && sand > 520d && silt + 2d * clay >= 300d)
                || (clay < 70d && silt < 500d && sand > 430d && silt + 2d * clay >= 300d);
    }

    private boolean isLoam(double sand, double silt, double clay) {
        return clay >= 70d && clay < 270d && silt >= 280d && silt < 500d && sand <= 520d;
    }

    private boolean isSiltLoam(double silt, double clay) {
        return (silt >= 500d && silt < 800d && clay < 120d)
                || (silt >= 500d && clay >= 120d && clay < 270d);
    }

    private boolean isSilt(double silt, double clay) {
        return silt >= 800d && clay < 120d;
    }

    private boolean isSandyClayLoam(double sand, double clay) {
        return clay >= 200d && clay < 350d && sand > 450d;
    }

    private boolean isClayLoam(double sand, double clay) {
        return clay >= 270d && clay < 400d && sand > 200d && sand <= 450d;
    }

    private boolean isSiltyClayLoam(double sand, double clay) {
        return clay >= 270d && clay < 400d && sand <= 200d;
    }

    private boolean isSandyClay(double sand, double clay) {
        return clay >= 350d && clay <= 600d && sand > 450d;
    }

    private boolean isSiltyClay(double silt, double clay) {
        return clay >= 400d && clay <= 600d && silt > 400d;
    }

    private boolean isClay(double sand, double silt, double clay) {
        return clay >= 400d && clay <= 600d && sand <= 450d && silt <= 400d;
    }

    private boolean isVeryClayey(double clay) {
        return clay > 600d;
    }

    private boolean hasMissingFraction(Double sand, Double silt, Double clay) {
        return sand == null || silt == null || clay == null;
    }

    private boolean hasInvalidFraction(Double sand, Double silt, Double clay) {
        return isInvalidNumber(sand) || isInvalidNumber(silt) || isInvalidNumber(clay)
                || sand < 0d || silt < 0d || clay < 0d;
    }

    private boolean isInvalidNumber(Double value) {
        return value.isNaN() || value.isInfinite();
    }

    private boolean isBrazilianUnitConfirmed(PhysicalAnalysisExtractModel physicalAnalysis) {
        return physicalAnalysis.getUnidadeTeorAreia() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorSilte() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorArgila() == PhysicalAnalysisUnit.G_PER_KG;
    }

    private boolean isAmericanUnitConfirmed(PhysicalAnalysisExtractModel physicalAnalysis) {
        return physicalAnalysis.getUnidadeTeorAreia() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorSilte() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorArgila() == PhysicalAnalysisUnit.G_PER_KG;
    }

    private boolean isExpectedTotal(double total) {
        return Math.abs(total - 100d) <= 1d || Math.abs(total - 1000d) <= 10d;
    }

    private double toPercent(double value, double total) {
        return value / total * 100d;
    }

    private SoilTextureClassificationResult classified(TexturalClassification strategy,
                                                       String texturalClass,
                                                       NormalizedFractions normalized) {
        return new SoilTextureClassificationResult(
                true,
                strategy,
                texturalClass,
                normalized.fractions(),
                List.copyOf(normalized.warnings())
        );
    }

    private SoilTextureClassificationResult unclassified(TexturalClassification strategy,
                                                         SoilTextureFractions fractions,
                                                         String warning) {
        return unclassified(strategy, fractions, List.of(warning));
    }

    private SoilTextureClassificationResult unclassified(TexturalClassification strategy,
                                                         SoilTextureFractions fractions,
                                                         List<String> warnings) {
        return new SoilTextureClassificationResult(
                false,
                strategy,
                null,
                fractions,
                List.copyOf(warnings)
        );
    }

    private record NormalizedFractions(SoilTextureFractions fractions, List<String> warnings, boolean hasErrors) {}

    private record GranulometricFractions(Double sand,
                                          Double silt,
                                          Double clay,
                                          List<String> warnings,
                                          boolean hasErrors) {

        SoilTextureFractions toPercentFractions() {
            if (sand == null || silt == null || clay == null) {
                return null;
            }
            return new SoilTextureFractions(sand / 10d, silt / 10d, clay / 10d);
        }
    }

    public record SoilTextureFractions(double sandPercent, double siltPercent, double clayPercent) {}

    public record SoilTextureClassificationResult(boolean classified,
                                                  TexturalClassification strategy,
                                                  String texturalClass,
                                                  SoilTextureFractions fractions,
                                                  List<String> warnings) {}
}
