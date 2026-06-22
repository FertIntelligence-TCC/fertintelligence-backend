package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SaturationExtractAnalysisExtractRepository;
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
@Order(9)
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ExtractsDataSeeder implements CommandLineRunner {

    private final SoilAnalysisRepository soilAnalysisRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<SoilAnalysisModel> analyses = soilAnalysisRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SoilAnalysisModel::getId))
                .toList();

        if (analyses.isEmpty()) {
            log.warn("⚠️ Nenhuma análise de solo encontrada. ExtractsDataSeeder ignorado.");
            return;
        }

        for (int i = 0; i < analyses.size(); i++) {
            SoilAnalysisModel soilAnalysis = analyses.get(i);

            LayerExtractModel layerExtract = null;
            RangeExtractModel rangeExtract = null;

            if (i % 2 == 0) {
                layerExtract = createLayerExtractIfNotExists(soilAnalysis, i);
            } else {
                rangeExtract = createRangeExtractIfNotExists(soilAnalysis, i);
            }

            createPhysicalAnalysisIfNotExists(soilAnalysis, layerExtract, rangeExtract, i);
            createFertilityAnalysisIfNotExists(soilAnalysis, layerExtract, rangeExtract, i);
            createSaturationAnalysisIfNotExists(soilAnalysis, layerExtract, rangeExtract, i);
        }
    }

    private LayerExtractModel createLayerExtractIfNotExists(SoilAnalysisModel soilAnalysis, int index) {
        if (soilAnalysis == null) {
            return null;
        }

        Camada[] camadas = {Camada.A, Camada.B, Camada.C, Camada.O, Camada.E};
        Camada camada = camadas[index % camadas.length];
        int subLayer = (index % 3) + 1;
        int[] range = depthRangeFor(index);

        return layerExtractRepository
                .findByAnalysisAndLayerAndSubLayer(soilAnalysis, camada, subLayer)
                .orElseGet(() -> {
                    LayerExtractModel created = layerExtractRepository.save(LayerExtractModel.builder()
                            .analysis(soilAnalysis)
                            .layer(camada)
                            .sub_layer(subLayer)
                            .profundidade_inicial(range[0])
                            .profundidade_final(range[1])
                            .build());
                    log.info("✅ Extrato de camada criado: soilAnalysis={}", soilAnalysis.getId());
                    return created;
                });
    }

    private RangeExtractModel createRangeExtractIfNotExists(SoilAnalysisModel soilAnalysis, int index) {
        if (soilAnalysis == null) {
            return null;
        }

        int[] range = depthRangeFor(index);

        return rangeExtractRepository
                .findByAnalysisAndProfundidadeInicialAndProfundidadeFinal(soilAnalysis, range[0], range[1])
                .orElseGet(() -> {
                    RangeExtractModel created = rangeExtractRepository.save(RangeExtractModel.builder()
                            .analysis(soilAnalysis)
                            .profundidade_inicial(range[0])
                            .profundidade_final(range[1])
                            .build());
                    log.info("✅ Extrato de intervalo criado: soilAnalysis={}", soilAnalysis.getId());
                    return created;
                });
    }

    private void createPhysicalAnalysisIfNotExists(SoilAnalysisModel soilAnalysis, LayerExtractModel layerExtract, RangeExtractModel rangeExtract, int index) {
        if (soilAnalysis == null) {
            return;
        }

        boolean exists = layerExtract != null
                ? physicalAnalysisExtractRepository.existsByLayerExtract(layerExtract)
                : physicalAnalysisExtractRepository.existsByRangeExtract(rangeExtract);

        if (exists) {
            log.info("↩️ Registro já existente: análise física soilAnalysis={}", soilAnalysis.getId());
            return;
        }

        double areia = 350 + (index % 5) * 70;
        double silte = 150 + (index % 4) * 30;
        double argila = 1000 - areia - silte;

        physicalAnalysisExtractRepository.save(PhysicalAnalysisExtractModel.builder()
                .layerExtract(layerExtract)
                .rangeExtract(rangeExtract)
                .teorAreia(areia)
                .teorSilte(silte)
                .teorArgila(argila)
                .densidadeAparente(1.1 + (index % 4) * 0.1)
                .densidadeReal(2.55 + (index % 3) * 0.03)
                .porosidadeTotal(45.0 - (index % 5) * 2.0)
                .microporosidade(35.0 - (index % 4) * 1.5)
                .umidadeCapacidadeCampo(12.0 + (index % 6))
                .umidadePontoMurchaPermanente(7.0 + (index % 4))
                .aguaDisponivel(6.0 + (index % 5))
                .resistenciaPenetracao(1.0 + (index % 5) * 0.3)
                .percAgregados6_0mm(10.0 + (index % 4) * 2.0)
                .percAgregados4_1a6_0mm(12.0 + (index % 4) * 2.0)
                .percAgregados2_1a4_0mm(15.0 + (index % 4) * 2.0)
                .percAgregados1_0a2_0mm(18.0 + (index % 4) * 2.0)
                .percAgregados0_5a1_0mm(20.0 - (index % 5) * 1.0)
                .percAgregados0_25a0_5mm(15.0 - (index % 5) * 1.0)
                .percAgregadosMenor0_25mm(10.0 - (index % 5) * 1.0)
                .build());

        log.info("✅ Análise física criada: soilAnalysis={}", soilAnalysis.getId());
    }

    private void createFertilityAnalysisIfNotExists(SoilAnalysisModel soilAnalysis, LayerExtractModel layerExtract, RangeExtractModel rangeExtract, int index) {
        if (soilAnalysis == null) {
            return;
        }

        boolean exists = layerExtract != null
                ? fertilityAnalysisExtractRepository.existsByLayerExtract(layerExtract)
                : fertilityAnalysisExtractRepository.existsByRangeExtract(rangeExtract);

        if (exists) {
            log.info("↩️ Registro já existente: análise de fertilidade soilAnalysis={}", soilAnalysis.getId());
            return;
        }

        double ca = 1.0 + (index % 5) * 0.45;
        double mg = 0.4 + (index % 4) * 0.25;
        double k = 25.0 + (index % 8) * 12.0;
        double na = 0.2 + (index % 5) * 0.15;
        double al = (index % 3 == 0) ? 0.4 : 0.0;
        double alH = 2.0 + (index % 5) * 0.5;
        double somaBases = ca + mg + k + na;
        double ctcEfetiva = somaBases + al;
        double ctcPh7 = somaBases + alH;

        fertilityAnalysisExtractRepository.save(FertilityAnalysisExtractModel.builder()
                .layerExtract(layerExtract)
                .rangeExtract(rangeExtract)
                .phAgua(4.8 + (index % 6) * 0.25)
                .phCacl2(4.5 + (index % 6) * 0.2)
                .fosforoMehlich1(5.0 + (index % 7) * 6.0)
                .potassio(k)
                .sodio(na)
                .calcio(ca)
                .magnesio(mg)
                .aluminio(al)
                .aluminioMaisHidrogenio(alH)
                .materiaOrganica(8.0 + (index % 6) * 3.0)
                .somaBases(somaBases)
                .ctcEfetiva(ctcEfetiva)
                .ctcPh7(ctcPh7)
                .saturacaoBasesV(percentage(somaBases, ctcPh7))
                .saturacaoAluminioM(percentage(al, ctcEfetiva))
                .pst(percentage(na, ctcPh7))
                .build());

        log.info("✅ Análise de fertilidade criada: soilAnalysis={}", soilAnalysis.getId());
    }

    private void createSaturationAnalysisIfNotExists(SoilAnalysisModel soilAnalysis, LayerExtractModel layerExtract, RangeExtractModel rangeExtract, int index) {
        if (soilAnalysis == null) {
            return;
        }

        boolean exists = layerExtract != null
                ? saturationExtractAnalysisExtractRepository.existsByLayerExtract(layerExtract)
                : saturationExtractAnalysisExtractRepository.existsByRangeExtract(rangeExtract);

        if (exists) {
            log.info("↩️ Registro já existente: análise de saturação soilAnalysis={}", soilAnalysis.getId());
            return;
        }

        saturationExtractAnalysisExtractRepository.save(SaturationExtractAnalysisExtractModel.builder()
                .layerExtract(layerExtract)
                .rangeExtract(rangeExtract)
                .ph(5.2 + (index % 5) * 0.3)
                .ce(0.5 + (index % 6) * 0.4)
                .teorNa(1.0 + (index % 5) * 0.25)
                .teorCa(1.5 + (index % 4) * 0.3)
                .teorMg(0.8 + (index % 3) * 0.2)
                .teorK(0.2 + (index % 4) * 0.1)
                .teorCO3(0.5 + (index % 4) * 0.15)
                .teorHCO3(1.0 + (index % 4) * 0.25)
                .teorNO3(2.0 + (index % 5) * 0.5)
                .teorH2PO4(0.4 + (index % 4) * 0.15)
                .teorSO4(1.2 + (index % 5) * 0.25)
                .teorCl(1.1 + (index % 5) * 0.2)
                .ras(1.0 + (index % 5) * 0.4)
                .residuosSuspensao(10.0 + (index % 4) * 3.0)
                .durezaCaCO3(20.0 + (index % 4) * 5.0)
                .durezaTotalCaCO3(45.0 + (index % 4) * 6.0)
                .build());

        log.info("✅ Análise de saturação criada: soilAnalysis={}", soilAnalysis.getId());
    }

    private int[] depthRangeFor(int index) {
        int mod = index % 3;
        if (mod == 0) return new int[]{0, 20};
        if (mod == 1) return new int[]{20, 40};
        return new int[]{40, 60};
    }

    private Double percentage(double numerator, double denominator) {
        if (denominator == 0.0) {
            return null;
        }
        return Math.round((100.0 * numerator / denominator) * 10.0) / 10.0;
    }
}
