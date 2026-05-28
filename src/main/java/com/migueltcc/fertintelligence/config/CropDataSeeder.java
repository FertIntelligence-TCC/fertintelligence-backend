package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(11)
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class CropDataSeeder implements CommandLineRunner {

    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;

    private static final NomeComum[] CULTURAS = {
            NomeComum.MILHO,
            NomeComum.SOJA,
            NomeComum.FEIJAO_COMUM,
            NomeComum.ALGODAO,
            NomeComum.CANA_DE_ACUCAR,
            NomeComum.AMENDOIM,
            NomeComum.GERGELIM,
            NomeComum.MAMONA,
            NomeComum.SISAL,
            NomeComum.FEIJAO_CAUPI
    };

    private static final Map<NomeComum, List<String>> VARIEDADES_POR_CULTURA = Map.of(
            NomeComum.MILHO, List.of("BRS Caatingueiro", "AG 1051", "BM 3061"),
            NomeComum.SOJA, List.of("BMX Potência", "TMG 7062", "M8644 IPRO"),
            NomeComum.FEIJAO_COMUM, List.of("Carioca", "Pérola", "BRS Estilo"),
            NomeComum.ALGODAO, List.of("FM 975 WS", "BRS 286", "DP 1536 B2RF"),
            NomeComum.CANA_DE_ACUCAR, List.of("RB 92579", "CTC 4", "SP 80-3280")
    );

    private static final List<String> VARIEDADES_GENERICAS = List.of(
            "Cultivar Experimental 1",
            "Cultivar Regional 2",
            "Variedade Local 3"
    );

    private static final double[] DISTANCIAS_ENTRE_LINHAS = {0.45, 0.50, 0.70, 0.90, 1.00};
    private static final double[] NUMERO_PLANTAS_POR_METRO = {8, 10, 12, 14, 16};

    @Override
    @Transactional
    public void run(String... args) {
        List<AnnualCropFolderModel> folders = annualCropFolderRepository.findAll();

        if (folders.isEmpty()) {
            log.warn("⚠️ Nenhuma pasta anual encontrada. CropDataSeeder ignorado.");
            return;
        }

        for (int folderIndex = 0; folderIndex < folders.size(); folderIndex++) {
            AnnualCropFolderModel folder = folders.get(folderIndex);
            int quantidadeCulturas = 3 - (folderIndex % 3);

            for (int cropIndex = 0; cropIndex < quantidadeCulturas; cropIndex++) {
                NomeComum nome = CULTURAS[(folderIndex + cropIndex) % CULTURAS.length];
                CultivationType tipoCultivo = (cropIndex % 2 == 0) ? CultivationType.SAFRA : CultivationType.SAFRINHA;
                String variedade = resolveVariedade(nome, folderIndex, cropIndex);

                createIfNotExists(folder, nome, variedade, tipoCultivo, cropIndex, folderIndex);
            }
        }
    }

    private void createIfNotExists(
            AnnualCropFolderModel folder,
            NomeComum nome,
            String variedade,
            CultivationType tipoCultivo,
            int cropIndex,
            int folderIndex
    ) {
        if (folder == null || nome == null || variedade == null || tipoCultivo == null) {
            return;
        }

        boolean exists = cropRepository.findByNameAndVarietyAndFolder(nome, variedade, folder).isPresent();
        if (exists) {
            log.info("↩️ Cultura já existe: pasta={} cultura={} variedade={}", folder.getId(), nome, variedade);
            return;
        }

        int year = folder.getCropsYear() != null ? folder.getCropsYear() : 2026;
        int dayOffset = cropIndex * 5;

        double produtividadeEsperada = 2500 + (folderIndex % 8) * 350 + cropIndex * 200;

        CropModel crop = CropModel.builder()
                .folder(folder)
                .name(nome)
                .variety(variedade)
                .cultivationType(tipoCultivo)
                .cycle(90 + (cropIndex * 15) + (folderIndex % 20))
                .distanceBetweenLines(DISTANCIAS_ENTRE_LINHAS[(folderIndex + cropIndex) % DISTANCIAS_ENTRE_LINHAS.length])
                .plantsPerMeter(NUMERO_PLANTAS_POR_METRO[(folderIndex + cropIndex) % NUMERO_PLANTAS_POR_METRO.length])
                .usedAreaInThePlot(calculateUsedArea(folder, folderIndex, cropIndex))
                .expectedProductivity(produtividadeEsperada)
                .obtainedProductivity(calculateObtainedProductivity(year, produtividadeEsperada, cropIndex))
                .plantingDate(new Date(10 + dayOffset, 2, year))
                .emergenceDate(new Date(18 + dayOffset, 2, year))
                .buttoningDate(new Date(25 + dayOffset, 4, year))
                .floweringDate(new Date(20 + dayOffset, 5, year))
                .harvestDate(new Date(15 + dayOffset, 8, year))
                .build();

        cropRepository.save(crop);
        log.info("✅ Cultura criada: pasta={} cultura={} variedade={}", folder.getId(), nome, variedade);
    }

    private String resolveVariedade(NomeComum nome, int folderIndex, int cropIndex) {
        List<String> variedades = VARIEDADES_POR_CULTURA.getOrDefault(nome, VARIEDADES_GENERICAS);
        return variedades.get((folderIndex + cropIndex) % variedades.size());
    }

    private double calculateUsedArea(AnnualCropFolderModel folder, int folderIndex, int cropIndex) {
        Double plotArea = folder.getPlot() != null ? folder.getPlot().getArea() : null;

        if (plotArea != null && plotArea > 0) {
            double maxArea = plotArea * 0.8;
            double deterministicArea = 2 + ((folderIndex * 3.0) + (cropIndex * 2.0));
            return Math.min(maxArea, deterministicArea);
        }

        return 2 + ((folderIndex + cropIndex) % 29);
    }

    private double calculateObtainedProductivity(int year, double produtividadeEsperada, int cropIndex) {
        if (year == 2026) {
            return 0.0;
        }

        if (year == 2024 || year == 2025) {
            double fator = (cropIndex % 2 == 0) ? 0.85 : 0.95;
            return produtividadeEsperada * fator;
        }

        return (cropIndex % 2 == 0) ? 0.0 : produtividadeEsperada * 0.9;
    }
}
