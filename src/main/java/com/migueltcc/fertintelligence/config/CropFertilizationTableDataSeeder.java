package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CropFertilizationTableDataSeeder implements CommandLineRunner {

    private final CropFertilizationTableRepository tableRepository;
    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🌾 Iniciando o Seeding Detalhado de Tabelas de Adubação...");

        Optional<UserModel> creatorOpt = userRepository.findByEmail("admin@fertintelligence.com");

        if (creatorOpt.isEmpty()) {
            log.error("❌ Erro Crítico: Usuário 'admin@fertintelligence.com' não encontrado.");
            return;
        }

        UserModel creator = creatorOpt.get();

        // 10 Cenários distintos
        createScenario(creator, NomeComum.MILHO, Regiao.NORDESTE, NomeCientifico.Zea_mays, "BRS Caatingueiro");
        createScenario(creator, NomeComum.FEIJAO_COMUM, Regiao.NORDESTE, NomeCientifico.Phaseolus_vulgaris, "Carioca");
        createScenario(creator, NomeComum.SOJA, Regiao.SUL, NomeCientifico.Glycine_max, "BMX Potência");
        createScenario(creator, NomeComum.ALGODAO, Regiao.CENTRO_OESTE, NomeCientifico.Gossypium_hirsutum, "FM 975 WS");
        createScenario(creator, NomeComum.CANA_DE_ACUCAR, Regiao.NORDESTE, NomeCientifico.Saccharum_officinarum, "RB 92579");
        createScenario(creator, NomeComum.AMENDOIM, Regiao.SUL, NomeCientifico.Arachis_hypogaea, "IAC OL 3");
        createScenario(creator, NomeComum.GERGELIM, Regiao.NORDESTE, NomeCientifico.Sesamum_indicum, "BRS Seda");
        createScenario(creator, NomeComum.MAMONA, Regiao.NORDESTE, NomeCientifico.Ricinus_communis, "BRS Nordestina");
        createScenario(creator, NomeComum.SISAL, Regiao.NORDESTE, NomeCientifico.Agave_sisalana, "Híbrido 11648");
        createScenario(creator, NomeComum.FEIJAO_CAUPI, Regiao.CENTRO_OESTE, NomeCientifico.Vigna_unguiculata, "BRS Tumucumaque");

        log.info("✅ Seeding de Tabelas, Intervalos (4 P, 4 K) e Coberturas concluído.");
    }

    private void createScenario(UserModel creator, NomeComum nome, Regiao regiao, NomeCientifico cientifico, String cultivar) {
        if (tableRepository.existsByCropCommonNameAndRegion(nome, regiao)) {
            log.info("Tabela de {} ({}) já existe. Pulando.", nome, regiao);
            return;
        }

        try {
            // 1. Criar e Salvar a Tabela Pai
            CropFertilizationTableModel table = CropFertilizationTableModel.builder()
                    .creator(creator)
                    .publicTable(true)
                    .region(regiao)
                    .crop_common_name(nome)
                    .crop_scientific_nome(cientifico)
                    .cultivares(cultivar)
                    .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                    .initial_value(0.20)
                    .final_value(0.80)
                    .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                    .used_spacing_value(0.50)
                    .regional_productivity(3000.0)
                    .expected_productivity(4000.0)
                    .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                    .manure(TipoEsterco.BOVINO)
                    .manure_qtd(5.0)
                    .gessing(1.0)
                    .micronutrients(100.0)
                    .npk(150.0)
                    .observations("Dados fictícios gerados via Seeder com 4 faixas de P e K.")
                    .build();

            CropFertilizationTableModel savedTable = tableRepository.save(table);

            // --- CRIAÇÃO DOS INTERVALOS (RANGES) ---

            // 1. Nitrogênio (Geralmente 1 faixa única)
            createFullRange(savedTable, Nutriente.NITROGENIO, 1, null, null, 20.0);

            // 2. Fósforo (4 Faixas)
            // Faixa 1: Muito Baixo (< 10)
            createFullRange(savedTable, Nutriente.FOSFORO, 1, null, 10.0, 100.0);
            // Faixa 2: Baixo (10 a 20)
            createFullRange(savedTable, Nutriente.FOSFORO, 2, 10.0, 20.0, 80.0);
            // Faixa 3: Médio (20 a 40)
            createFullRange(savedTable, Nutriente.FOSFORO, 3, 20.0, 40.0, 60.0);
            // Faixa 4: Alto (> 40)
            createFullRange(savedTable, Nutriente.FOSFORO, 4, 40.0, null, 40.0);

            // 3. Potássio (4 Faixas)
            // Faixa 1: Muito Baixo (< 30)
            createFullRange(savedTable, Nutriente.POTASSIO, 1, null, 30.0, 90.0);
            // Faixa 2: Baixo (30 a 60)
            createFullRange(savedTable, Nutriente.POTASSIO, 2, 30.0, 60.0, 70.0);
            // Faixa 3: Médio (60 a 90)
            createFullRange(savedTable, Nutriente.POTASSIO, 3, 60.0, 90.0, 50.0);
            // Faixa 4: Alto (> 90)
            createFullRange(savedTable, Nutriente.POTASSIO, 4, 90.0, null, 30.0);

            log.info("➕ Tabela completa criada para {} no {} com 9 intervalos.", nome, regiao);

        } catch (Exception e) {
            log.error("❌ Erro ao criar cenário para {}: {}", nome, e.getMessage());
        }
    }

    private void createFullRange(CropFertilizationTableModel table, Nutriente nutriente, Integer ordem, Double min, Double max, Double plantio) {
        // Criar o Intervalo (ContentRange)
        ContentRangeModel range = ContentRangeModel.builder()
                .table(table)
                .nutrient(nutriente)
                .order(ordem)
                .smallest(min)
                .largest(max)
                .application(plantio)
                .build();

        ContentRangeModel savedRange = contentRangeRepository.save(range);

        // Criar 5 Coberturas (Coverages) para este intervalo
        for (int i = 1; i <= 5; i++) {
            CoverageModel coverage = CoverageModel.builder()
                    .range(savedRange)
                    .order(i)
                    // Valor fictício decrescente ou variável para simular dados reais
                    .application(Math.max(0, (plantio / 2.0) - (i * 2.0)))
                    .build();
            coverageRepository.save(coverage);
        }
    }
}