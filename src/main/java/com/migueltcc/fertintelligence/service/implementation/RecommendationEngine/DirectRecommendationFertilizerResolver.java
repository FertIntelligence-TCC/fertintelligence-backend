package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectRecommendationFertilizerResolver {

    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;

    public SimpleMineralFertilizerData simple(Long fertilizerId, AppliedMicronutrient micronutrient) {
        if (fertilizerId == null) {
            return SimpleMineralFertilizerData.unresolved();
        }
        Optional<SimpleMineralFertilizerModel> fertilizer = simpleMineralFertilizerRepository.findById(fertilizerId);
        return fertilizer
                .map(value -> new SimpleMineralFertilizerData(value.getName(), concentration(value, micronutrient)))
                .orElseGet(SimpleMineralFertilizerData::unresolved);
    }

    public FormulatedMineralFertilizerData formulated(Long fertilizerId) {
        if (fertilizerId == null) {
            return FormulatedMineralFertilizerData.unresolved();
        }
        Optional<FormulatedMineralFertilizerModel> fertilizer = formulatedMineralFertilizerRepository.findById(fertilizerId);
        return fertilizer
                .map(value -> new FormulatedMineralFertilizerData(
                        formatFormulatedName(value),
                        value.getN(),
                        value.getP2O5(),
                        value.getK2O()))
                .orElseGet(FormulatedMineralFertilizerData::unresolved);
    }

    private Double concentration(SimpleMineralFertilizerModel fertilizer, AppliedMicronutrient micronutrient) {
        if (fertilizer == null || micronutrient == null) {
            return null;
        }
        return switch (micronutrient) {
            case B -> fertilizer.getB();
            case Cu -> fertilizer.getCu();
            case Fe -> fertilizer.getFe();
            case Mn -> fertilizer.getMn();
            case Mo -> fertilizer.getMo();
            case Zn -> fertilizer.getZn();
            default -> null;
        };
    }

    private String formatFormulatedName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return null;
        }
        return String.format(Locale.US, "NPK %.2f-%.2f-%.2f", fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    public record SimpleMineralFertilizerData(String name, Double micronutrientConcentrationPercent) {
        static SimpleMineralFertilizerData unresolved() {
            return new SimpleMineralFertilizerData(null, null);
        }
    }

    public record FormulatedMineralFertilizerData(String name, Double nitrogenPercent, Double p2o5Percent, Double k2oPercent) {
        static FormulatedMineralFertilizerData unresolved() {
            return new FormulatedMineralFertilizerData(null, null, null, null);
        }
    }
}
