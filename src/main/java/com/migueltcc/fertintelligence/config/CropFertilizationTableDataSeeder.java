package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(4) // Executa APÓS UserDataSeeder, PropertyDataSeeder e FertilizerDataSeeder
@RequiredArgsConstructor
@Slf4j
public class CropFertilizationTableDataSeeder implements CommandLineRunner {

    private final CropFertilizationTableRepository repository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        log.info("🌾 Iniciando o Seeding de Tabelas de Adubação e Calagem...");

        // 1. Busca o usuário "Admin" para ser o criador das tabelas
        // Certifique-se de que este email corresponde ao criado no UserDataSeeder
        Optional<UserModel> creatorOpt = userRepository.findByEmail("admin@fertintelligence.com");

        if (creatorOpt.isEmpty()) {
            log.error("❌ Erro Crítico: Usuário 'admin@fertintelligence.com' não encontrado. O seeding de tabelas será abortado.");
            return;
        }

        UserModel creator = creatorOpt.get();

        // 2. Inicia o carregamento das tabelas
        loadTables(creator);

        log.info("✅ Seeding de Tabelas de Adubação concluído.");
    }

    private void loadTables(UserModel creator) {
        // --- 1. MILHO (Nordeste) ---
        if (!repository.existsByCropCommonNameAndRegion(NomeComum.MILHO, Regiao.NORDESTE)) {
            createTable(CropFertilizationTableModel.builder()
                    .creator(creator)
                    .region(Regiao.NORDESTE)
                    .crop_common_name(NomeComum.MILHO)
                    .crop_scientific_nome(NomeCientifico.Zea_mays)
                    .cultivares("BRS Caatingueiro")
                    .suggested_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                    .initial_value(0.20)
                    .final_value(0.40)
                    .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                    .used_spacing_value(0.25)
                    .regional_productivity(3000.0)
                    .expected_productivity(4500.0)
                    .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS) // Corrigido Enum
                    .manure(TipoEsterco.CAPRINO)
                    .manure_qtd(4.0)
                    .gessing(0.8)
                    .micronutrients(140.0)
                    .npk(200.0)
                    .observations("Tabela fictícia para milho com plantio adensado.")
                    .build());
        } else {
            log.info("Tabela de MILHO (Nordeste) já existe. Pulando.");
        }

        // --- 2. FEIJÃO (Nordeste) ---
        if (!repository.existsByCropCommonNameAndRegion(NomeComum.FEIJAO_COMUM, Regiao.NORDESTE)) {
            createTable(CropFertilizationTableModel.builder()
                    .creator(creator)
                    .region(Regiao.NORDESTE)
                    .crop_common_name(NomeComum.FEIJAO_COMUM)
                    .crop_scientific_nome(NomeCientifico.Phaseolus_vulgaris)
                    .cultivares("Carioca, BRS Estilo")
                    .suggested_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                    .initial_value(0.25)
                    .final_value(0.35)
                    .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                    .used_spacing_value(0.30)
                    .regional_productivity(1800.0)
                    .expected_productivity(2200.0)
                    .criteria(CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL_MAIS_ELEVACAO_DO_TEOR_DE_CALCIO_MAIS_MAGNESIO)
                    .manure(TipoEsterco.BOVINO)
                    .manure_qtd(5.0)
                    .gessing(0.6)
                    .micronutrients(80.0)
                    .npk(110.0)
                    .observations("Tabela fictícia para feijão irrigado.")
                    .build());
        } else {
            log.info("Tabela de FEIJÃO (Nordeste) já existe. Pulando.");
        }

        // --- 3. SOJA (Sul) ---
        if (!repository.existsByCropCommonNameAndRegion(NomeComum.SOJA, Regiao.SUL)) {
            createTable(CropFertilizationTableModel.builder()
                    .creator(creator)
                    .region(Regiao.SUL)
                    .crop_common_name(NomeComum.SOJA)
                    .crop_scientific_nome(NomeCientifico.Glycine_max)
                    .cultivares("BMX Potência")
                    .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS) // Corrigido Enum (era BETWEEN_ROWS)
                    .initial_value(0.40) // [CORREÇÃO CRÍTICA] Valor adicionado (antes null)
                    .final_value(1.2)
                    .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                    .used_spacing_value(0.50)
                    .regional_productivity(3500.0)
                    .expected_productivity(4200.0)
                    .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS) // Corrigido Enum
                    .manure(TipoEsterco.FRANGO) // Corrigido Enum (era AVES)
                    .manure_qtd(2.0)
                    .gessing(0.0)
                    .micronutrients(0.0)
                    .npk(0.0)
                    .observations("Tabela fictícia para soja no sul.")
                    .build());
        } else {
            log.info("Tabela de SOJA (Sul) já existe. Pulando.");
        }
    }

    private void createTable(CropFertilizationTableModel model) {
        try {
            repository.save(model);
            log.info("➕ Tabela criada: {} ({})", model.getCrop_common_name(), model.getRegion());
        } catch (Exception e) {
            log.error("❌ Erro inesperado ao salvar tabela de {}: {}", model.getCrop_common_name(), e.getMessage());
        }
    }
}