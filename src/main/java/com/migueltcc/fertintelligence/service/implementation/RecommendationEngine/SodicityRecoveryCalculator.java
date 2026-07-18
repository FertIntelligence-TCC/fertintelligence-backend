package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class SodicityRecoveryCalculator {

    static final double HIGH_PST_THRESHOLD = 6d;
    private static final double TARGET_PST = 5d;
    private static final double GYPSUM_FACTOR = 172d;

    private SodicityRecoveryCalculator() {
    }

    static Result calculate(List<FertilityAnalysisExtractModel> extracts) {
        List<LayerResult> layers = new ArrayList<>();
        addLayer(layers, extracts, 0, 20, "0–20 cm");
        addLayer(layers, extracts, 21, 40, "21–40 cm");
        double total = layers.stream().filter(LayerResult::calculated)
                .mapToDouble(LayerResult::gypsumKgHa).sum();
        return new Result(List.copyOf(layers), total);
    }

    private static void addLayer(List<LayerResult> target,
                                 List<FertilityAnalysisExtractModel> extracts,
                                 int initialDepth,
                                 int finalDepth,
                                 String label) {
        FertilityAnalysisExtractModel extract = extracts == null ? null : extracts.stream()
                .filter(value -> matchesDepth(value, initialDepth, finalDepth))
                .min(Comparator.comparing(value -> value.getId() == null ? Long.MAX_VALUE : value.getId()))
                .orElse(null);
        if (extract == null) return;
        Double sodium = finite(extract.getSodio());
        Double ctc = finite(extract.getCtcPh7());
        Double pst = finite(extract.getPst());
        boolean compatibleUnits = compatible(extract.getUnidadeSodio()) && compatible(extract.getUnidadeCtcPh7());
        List<String> missing = new ArrayList<>();
        if (sodium == null) missing.add("Na");
        if (ctc == null || ctc <= 0d) missing.add("CTC(T)");
        if (pst == null) missing.add("PST");
        if (!compatibleUnits) missing.add("unidade mmolc/dm³");
        boolean calculated = missing.isEmpty();
        double gypsum = calculated ? Math.max(0d, ctc * (pst - TARGET_PST) / 100d * GYPSUM_FACTOR) : 0d;
        target.add(new LayerResult(label, initialDepth, finalDepth, sodium, ctc, pst,
                calculated, calculated && pst >= HIGH_PST_THRESHOLD, gypsum, List.copyOf(missing)));
    }

    private static boolean matchesDepth(FertilityAnalysisExtractModel extract, int initial, int end) {
        if (extract == null) return false;
        if (extract.getRangeExtract() != null) {
            return Integer.valueOf(initial).equals(extract.getRangeExtract().getProfundidade_inicial())
                    && Integer.valueOf(end).equals(extract.getRangeExtract().getProfundidade_final());
        }
        return extract.getLayerExtract() != null
                && Integer.valueOf(initial).equals(extract.getLayerExtract().getProfundidade_inicial())
                && Integer.valueOf(end).equals(extract.getLayerExtract().getProfundidade_final());
    }

    private static boolean compatible(FertilityAnalysisUnit unit) {
        return unit == null || unit == FertilityAnalysisUnit.MMOLC_PER_DM3;
    }

    private static Double finite(Double value) {
        return value != null && Double.isFinite(value) ? value : null;
    }

    record Result(List<LayerResult> layers, double totalGypsumKgHa) {
        boolean hasCalculatedLayer() {
            return layers.stream().anyMatch(LayerResult::calculated);
        }

        boolean hasHighPst() {
            return layers.stream().anyMatch(LayerResult::highPst);
        }
    }

    record LayerResult(String label, int initialDepth, int finalDepth, Double sodium, Double ctc, Double pst,
                       boolean calculated, boolean highPst, double gypsumKgHa, List<String> missing) {
    }
}
