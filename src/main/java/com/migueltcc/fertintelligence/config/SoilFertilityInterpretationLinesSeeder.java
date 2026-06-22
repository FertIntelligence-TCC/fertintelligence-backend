package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.*;
import com.migueltcc.fertintelligence.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;

@Component
@Order(14)
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SoilFertilityInterpretationLinesSeeder implements CommandLineRunner {

    private final SoilFertilityInterpretationCriteriaTableRepository tableRepository;
    private final AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;
    private final AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;
    private final AvailableSRepository availableSRepository;
    private final KExchangeableContentRepository kExchangeableContentRepository;
    private final SalinityInterpretationRepository salinityInterpretationRepository;
    private final DiverseContentRangeRepository diverseContentRangeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<SoilFertilityInterpretationCriteriaTableModel> tables = tableRepository.findAll();
        if (tables.isEmpty()) {
            log.warn("⚠️ Nenhuma tabela de interpretação de fertilidade encontrada. Seeder ignorado.");
            return;
        }

        for (SoilFertilityInterpretationCriteriaTableModel table : tables) {
            if (table == null) {
                continue;
            }
            seedAvailablePResin(table);
            seedAvailablePMehlich(table);
            seedAvailableS(table);
            seedExchangeableK(table);
            seedSalinity(table);
            seedDiverseRanges(table);
        }
    }

    private void seedAvailablePResin(SoilFertilityInterpretationCriteriaTableModel table) {
        createPResinIfNotExists(table);
    }

    private void seedAvailablePMehlich(SoilFertilityInterpretationCriteriaTableModel table) {
        createPMehlichIfNotExists(table);
    }

    private void seedAvailableS(SoilFertilityInterpretationCriteriaTableModel table) {
        createAvailableSIfNotExists(table);
    }

    private void seedExchangeableK(SoilFertilityInterpretationCriteriaTableModel table) {
        createKIfNotExists(table);
    }

    private void seedSalinity(SoilFertilityInterpretationCriteriaTableModel table) {
        createSalinityIfNotExists(table);
    }

    private void seedDiverseRanges(SoilFertilityInterpretationCriteriaTableModel table) {
        createDiverseRangeIfNotExists(table);
    }

    private void createPResinIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (availablePAnionExchangeResinExtractorRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: P Resina tabela={}", table.getId());
            return;
        }

        AvailablePAnionExchangeResinExtractorModel model = AvailablePAnionExchangeResinExtractorModel.builder()
                .table(table)
                .pContentTooLow(0.0)
                .pContentLowI(5.0)
                .pContentLowF(10.0)
                .pContentMediumI(10.0)
                .pContentMediumF(20.0)
                .pContentHighI(20.0)
                .pContentHighF(999.0)
                .pContentTooHigh(999.0)
                .build();
        availablePAnionExchangeResinExtractorRepository.save(model);
        log.info("✅ Linha P Resina criada: tabela={} faixa={}-{}", table.getId(), 0.0, 20.0);
    }

    private void createPMehlichIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (availablePMehlich1ExtractorRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: P Mehlich tabela={}", table.getId());
            return;
        }

        AvailablePMehlich1ExtractorModel model = AvailablePMehlich1ExtractorModel.builder().table(table).build();
        applyRangePattern(model, 0.0, 4.0, 8.0, 16.0, 999.0);
        availablePMehlich1ExtractorRepository.save(model);
        log.info("✅ Linha P Mehlich criada: tabela={} faixa={}-{}", table.getId(), 0.0, 16.0);
    }

    private void createAvailableSIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (availableSRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: S tabela={}", table.getId());
            return;
        }

        AvailableSModel model = AvailableSModel.builder()
                .table(table)
                .sContentLess400TooLow(0.0)
                .sContentLess400LowI(0.0)
                .sContentLess400LowF(3.0)
                .sContentLess400MediumI(3.0)
                .sContentLess400MediumF(6.0)
                .sContentLess400HighI(6.0)
                .sContentLess400HighF(12.0)
                .sContentLess400TooHigh(12.0)
                .sContentGreater400TooLow(0.0)
                .sContentGreater400LowI(0.0)
                .sContentGreater400LowF(3.0)
                .sContentGreater400MediumI(3.0)
                .sContentGreater400MediumF(6.0)
                .sContentGreater400HighI(6.0)
                .sContentGreater400HighF(12.0)
                .sContentGreater400TooHigh(12.0)
                .literatureSource("A definir")
                .observations("Os teores de S disponível são os estimados por solução de 500 mg/L de P em ácido acético glacial 0,5 mol/L.\n\nE o valor considerado é a média das camadas de 0 a 20 cm e 21 a 40 cm.")
                .build();
        availableSRepository.save(model);
        log.info("✅ Linha S criada: tabela={} faixa={}-{}", table.getId(), 0.0, 12.0);
    }

    private void createKIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (kExchangeableContentRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: K tabela={}", table.getId());
            return;
        }

        KExchangeableContentModel model = KExchangeableContentModel.builder()
                .table(table)
                .kContentTooLow(0.0)
                .kContentLowI(0.0)
                .kContentLowF(30.0)
                .kContentMediumI(30.0)
                .kContentMediumF(60.0)
                .kContentHighI(60.0)
                .kContentHighF(90.0)
                .kContentTooHigh(90.0)
                .build();
        kExchangeableContentRepository.save(model);
        log.info("✅ Linha K criada: tabela={} faixa={}-{}", table.getId(), 0.0, 90.0);
    }

    private void createSalinityIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (salinityInterpretationRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: salinidade tabela={}", table.getId());
            return;
        }

        SalinityInterpretationModel model = SalinityInterpretationModel.builder()
                .table(table)
                .normal_soil_highest_ce(0.7)
                .normal_soil_highest_pst(15.0)
                .normal_soil_highest_ph(8.5)
                .normal_soil_highest_ras(13.0)
                .saline_soil_lowest_ce(0.7)
                .saline_soil_highest_pst(15.0)
                .saline_soil_highest_ph(8.5)
                .saline_soil_highest_ras(13.0)
                .sodic_saline_soil_highest_ce(3.0)
                .sodic_saline_soil_lowest_pst(15.0)
                .sodic_saline_soil_lowest_ph(8.5)
                .sodic_saline_soil_lowest_ras(13.0)
                .sodic_soil_highest_ce(3.0)
                .sodic_soil_lowest_pst(15.0)
                .sodic_soil_lowest_ph(8.5)
                .sodic_soil_lowest_ras(13.0)
                .build();

        salinityInterpretationRepository.save(model);
        log.info("✅ Linha salinidade criada: tabela={} faixa={}-{}", table.getId(), 0.0, 3.0);
    }

    private void createDiverseRangeIfNotExists(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) return;
        if (diverseContentRangeRepository.findByTable(table).isPresent()) {
            log.info("↩️ Linha já existe: diversa tabela={}", table.getId());
            return;
        }

        DiverseContentRangeModel model = DiverseContentRangeModel.builder().table(table).build();
        applyRangePattern(model, 0.0, 5.0, 10.0, 20.0, 999.0);
        diverseContentRangeRepository.save(model);
        log.info("✅ Linha diversa criada: tabela={} parametro={}", table.getId(), "MULTIPLOS");
    }

    private void applyRangePattern(Object target,
                                   Double tooLow,
                                   Double lowStart,
                                   Double mediumStart,
                                   Double highStart,
                                   Double tooHigh) {
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!field.getType().equals(Double.class)) {
                continue;
            }
            setBySuffix(target, field, tooLow, lowStart, mediumStart, highStart, tooHigh);
        }
    }

    private void setBySuffix(Object target,
                             Field field,
                             Double tooLow,
                             Double lowStart,
                             Double mediumStart,
                             Double highStart,
                             Double tooHigh) {
        String name = field.getName();
        Supplier<Double> valueSupplier;
        if (name.endsWith("_too_low")) valueSupplier = () -> tooLow;
        else if (name.endsWith("_low_i")) valueSupplier = () -> lowStart;
        else if (name.endsWith("_low_f")) valueSupplier = () -> mediumStart;
        else if (name.endsWith("_medium_i")) valueSupplier = () -> mediumStart;
        else if (name.endsWith("_medium_f")) valueSupplier = () -> highStart;
        else if (name.endsWith("_hight_i")) valueSupplier = () -> highStart;
        else if (name.endsWith("_hight_f")) valueSupplier = () -> tooHigh;
        else if (name.endsWith("_too_hight")) valueSupplier = () -> tooHigh;
        else return;

        try {
            field.setAccessible(true);
            field.set(target, valueSupplier.get());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Falha ao preencher campo " + field.getName(), e);
        }
    }
}
