package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Order(8)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class SoilAnalysisDataSeeder implements CommandLineRunner {

    private final PlotRepository plotRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;

    private static final String[] LABORATORIES = {
            "Laboratório Solo Nordeste",
            "AgroLab Solos",
            "Instituto de Análise Agrícola",
            "Laboratório Terra Forte",
            "Centro de Diagnóstico do Solo"
    };

    @Override
    @Transactional
    public void run(String... args) {
        List<PlotModel> plots = plotRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(PlotModel::getId))
                .toList();

        if (plots.isEmpty()) {
            log.warn("⚠️ Nenhum talhão encontrado. SoilAnalysisDataSeeder ignorado.");
            return;
        }

        for (int plotIndex = 0; plotIndex < plots.size(); plotIndex++) {
            PlotModel plot = plots.get(plotIndex);
            String laboratory = LABORATORIES[plotIndex % LABORATORIES.length];

            createIfNotExists(plot, 2025, laboratory, TipoExtrato.CAMADAS);

            if (plotIndex % 3 == 0) {
                createIfNotExists(plot, 2026, laboratory, TipoExtrato.INTERVALOS);
            }
        }
    }

    private void createIfNotExists(
            PlotModel plot,
            int analysisYear,
            String laboratory,
            TipoExtrato extractType
    ) {
        if (plot == null) {
            return;
        }

        if (soilAnalysisRepository.findByPlotAndAnalysisYear(plot, analysisYear).isPresent()) {
            log.info("↩️ Análise de solo já existe: talhão={} laudo={}", plot.getIdentification(), "SOLO-" + plot.getId() + "-" + analysisYear + "-A");
            return;
        }

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder()
                .plot(plot)
                .analysisYear(analysisYear)
                .responsibleLaboratory(laboratory)
                .extractType(extractType)
                .build();

        soilAnalysisRepository.save(soilAnalysis);
        log.info("✅ Análise de solo criada: talhão={} laudo={}", plot.getIdentification(), "SOLO-" + plot.getId() + "-" + analysisYear + "-A");
    }
}
