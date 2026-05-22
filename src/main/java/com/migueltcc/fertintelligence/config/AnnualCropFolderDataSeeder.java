package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
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
@Order(10)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AnnualCropFolderDataSeeder implements CommandLineRunner {

    private final PlotRepository plotRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;

    private static final List<Integer> CROPS_YEARS = List.of(2024, 2025, 2026);

    @Override
    @Transactional
    public void run(String... args) {
        List<PlotModel> plots = plotRepository.findAll();

        if (plots.isEmpty()) {
            log.warn("⚠️ Nenhum talhão encontrado. AnnualCropFolderDataSeeder ignorado.");
            return;
        }

        for (PlotModel plot : plots) {
            for (Integer cropsYear : CROPS_YEARS) {
                createIfNotExists(plot, cropsYear);
            }
        }
    }

    private void createIfNotExists(PlotModel plot, Integer cropsYear) {
        if (plot == null || cropsYear == null) {
            return;
        }

        boolean exists = annualCropFolderRepository.findByPlotAndCropsYear(plot, cropsYear).isPresent();
        if (exists) {
            log.info("↩️ Pasta anual já existe: talhão={} safra={}", plot.getIdentification(), cropsYear);
            return;
        }

        AnnualCropFolderModel annualCropFolder = AnnualCropFolderModel.builder()
                .plot(plot)
                .cropsYear(cropsYear)
                .build();

        annualCropFolderRepository.save(annualCropFolder);
        log.info("✅ Pasta anual criada: talhão={} safra={}", plot.getIdentification(), cropsYear);
    }
}
