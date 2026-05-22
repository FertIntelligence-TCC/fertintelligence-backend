package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(15)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CropFoliarInterpretationTableSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CropFoliarAnalysisInterpretationTableRepository tableRepository;
    private final CropFoliarAnalysisInterpretationTableLineRepository lineRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Optional<UserModel> adminOptional = userRepository.findByEmail("admin@fertintelligence.com");
        Optional<UserModel> gilvanOptional = userRepository.findByEmail("gilvan@email.com");
        Optional<UserModel> miguelOptional = userRepository.findByEmail("miguel@email.com");
        Optional<UserModel> mateusOptional = userRepository.findByEmail("mateus@email.com");

        if (adminOptional.isEmpty()) {
            log.warn("⚠️ Usuário não encontrado: {}", "admin@fertintelligence.com");
        }
        if (gilvanOptional.isEmpty()) {
            log.warn("⚠️ Usuário não encontrado: {}", "gilvan@email.com");
        }
        if (miguelOptional.isEmpty()) {
            log.warn("⚠️ Usuário não encontrado: {}", "miguel@email.com");
        }
        if (mateusOptional.isEmpty()) {
            log.warn("⚠️ Usuário não encontrado: {}", "mateus@email.com");
        }

        int seededTables = 0;

        seededTables += seedSpecTable(adminOptional.orElse(null), "Interpretação Foliar Milho Pública", NomeComum.MILHO, true, Regiao.NORDESTE);
        seededTables += seedSpecTable(adminOptional.orElse(null), "Interpretação Foliar Soja Pública", NomeComum.SOJA, true, Regiao.CENTRO_OESTE);
        seededTables += seedSpecTable(gilvanOptional.orElse(null), "Interpretação Foliar Feijão Pública", NomeComum.FEIJAO_COMUM, true, Regiao.NORDESTE);
        seededTables += seedSpecTable(miguelOptional.orElse(null), "Interpretação Foliar Algodão Pública", NomeComum.ALGODAO, true, Regiao.SUL);
        seededTables += seedSpecTable(mateusOptional.orElse(null), "Interpretação Foliar Cana Pública", NomeComum.CANA_DE_ACUCAR, true, Regiao.CENTRO_OESTE);
        seededTables += seedSpecTable(miguelOptional.orElse(null), "Interpretação Foliar Milho Privada Miguel", NomeComum.MILHO, false, Regiao.SUL);
        seededTables += seedSpecTable(gilvanOptional.orElse(null), "Interpretação Foliar Soja Privada Gilvan", NomeComum.SOJA, false, Regiao.NORDESTE);
        seededTables += seedSpecTable(mateusOptional.orElse(null), "Interpretação Foliar Consultoria Mateus", NomeComum.MILHO, false, Regiao.CENTRO_OESTE);

        if (seededTables == 0
                && adminOptional.isEmpty()
                && gilvanOptional.isEmpty()
                && miguelOptional.isEmpty()
                && mateusOptional.isEmpty()) {
            log.warn("⚠️ Nenhuma tabela foliar foi criada porque usuários base não foram encontrados.");
        }
    }

    private int seedSpecTable(UserModel creator, String name, NomeComum crop, boolean publicTable, Regiao region) {
        CropFoliarAnalysisInterpretationTableModel table = createTableIfNotExists(creator, name, publicTable, region);
        if (table == null) {
            return 0;
        }
        seedLinesForTable(table, crop);
        return 1;
    }

    private CropFoliarAnalysisInterpretationTableModel createTableIfNotExists(UserModel creator,
                                                                               String name,
                                                                               boolean publicTable,
                                                                               Regiao region) {
        if (creator == null) {
            return null;
        }

        Optional<CropFoliarAnalysisInterpretationTableModel> existing = tableRepository.findByCreatorAndName(creator, name);
        if (existing.isPresent()) {
            log.info("↩️ Tabela foliar já existe: criador={} cultura={} nome={}", creator.getEmail(), "N/A", name);
            return existing.get();
        }

        CropFoliarAnalysisInterpretationTableModel table = CropFoliarAnalysisInterpretationTableModel.builder()
                .creator(creator)
                .name(name)
                .region(region)
                .publicTable(publicTable)
                .build();

        CropFoliarAnalysisInterpretationTableModel saved = tableRepository.save(table);
        log.info("✅ Tabela foliar criada: criador={} cultura={} nome={}", creator.getEmail(), "N/A", name);
        return saved;
    }

    private void seedLinesForTable(CropFoliarAnalysisInterpretationTableModel table, NomeComum crop) {
        createLineIfNotExists(table, crop);
    }

    private void createLineIfNotExists(CropFoliarAnalysisInterpretationTableModel table, NomeComum crop) {
        if (table == null || crop == null) {
            return;
        }

        if (lineRepository.existsByTableAndCrop(table, crop)) {
            log.info("↩️ Linha foliar já existe: tabela={} nutriente={} faixa={}-{}", table.getName(), "MULTIPLOS", 0.0, 9999.0);
            return;
        }

        CropFoliarAnalysisInterpretationTableLineModel line = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .table(table)
                .crop(crop)
                .n_content(new MenorMaiorTeores(20.0, 35.0, UnidadeTeor.dag_per_kg))
                .p_content(new MenorMaiorTeores(1.5, 3.0, UnidadeTeor.dag_per_kg))
                .k_content(new MenorMaiorTeores(15.0, 30.0, UnidadeTeor.dag_per_kg))
                .ca_content(new MenorMaiorTeores(5.0, 15.0, UnidadeTeor.dag_per_kg))
                .mg_content(new MenorMaiorTeores(2.0, 6.0, UnidadeTeor.dag_per_kg))
                .s_content(new MenorMaiorTeores(1.5, 4.0, UnidadeTeor.dag_per_kg))
                .b_content(new MenorMaiorTeores(20.0, 60.0, UnidadeTeor.mg_per_kg))
                .cu_content(new MenorMaiorTeores(5.0, 20.0, UnidadeTeor.mg_per_kg))
                .fe_content(new MenorMaiorTeores(50.0, 250.0, UnidadeTeor.mg_per_kg))
                .mn_content(new MenorMaiorTeores(30.0, 150.0, UnidadeTeor.mg_per_kg))
                .mo_content(new MenorMaiorTeores(0.1, 1.0, UnidadeTeor.mg_per_kg))
                .zn_content(new MenorMaiorTeores(15.0, 80.0, UnidadeTeor.mg_per_kg))
                .build();

        lineRepository.save(line);
        log.info("✅ Linha foliar criada: tabela={} nutriente={} faixa={}-{}", table.getName(), "MULTIPLOS", 0.0, 9999.0);
    }
}
