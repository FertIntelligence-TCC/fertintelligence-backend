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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;

import java.util.Optional;

@Component
@Order(15)
@RequiredArgsConstructor
@Profile("dev")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CropFoliarInterpretationTableSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CropFoliarInterpretationTableSeeder.class);

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

        Optional<UserModel> supremeUserOpt = userRepository.findAll().stream().filter(u -> u.getCargo() == Cargo.USUARIO_SUPREMO).findFirst();
        if (supremeUserOpt.isPresent()) {
            seededTables += seedSpecTable(supremeUserOpt.get(), "Interpretação Foliar Milho Padrão", NomeComum.MILHO, true, Regiao.NORDESTE);
            seededTables += seedSpecTable(supremeUserOpt.get(), "Interpretação Foliar Soja Padrão", NomeComum.SOJA, true, Regiao.CENTRO_OESTE);
        }

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
        seedLinesForTable(table, name, crop);
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

        CropFoliarAnalysisInterpretationTableModel table = new CropFoliarAnalysisInterpretationTableModel();
        table.setCreator(creator);
        table.setName(name);
        table.setRegion(region);
        table.setObservations("Coletar folhas diagnósticas em plantas sadias e representativas do talhão.");
        table.setSources("Manual de diagnose foliar; recomendações técnicas regionais");
        table.setPublicTable(publicTable);

        CropFoliarAnalysisInterpretationTableModel saved = tableRepository.save(table);
        log.info("✅ Tabela foliar criada: criador={} cultura={} nome={}", creator.getEmail(), "N/A", name);
        return saved;
    }

    private void seedLinesForTable(CropFoliarAnalysisInterpretationTableModel table, String tableName, NomeComum crop) {
        createLineIfNotExists(table, tableName, crop);
    }

    private void createLineIfNotExists(CropFoliarAnalysisInterpretationTableModel table, String tableName, NomeComum crop) {
        if (table == null || crop == null) {
            return;
        }

        if (lineRepository.existsByTableAndCrop(table, crop)) {
            log.info("↩️ Linha foliar já existe: tabela={} nutriente={} faixa={}-{}", tableName, "MULTIPLOS", 0.0, 9999.0);
            return;
        }

        CropFoliarAnalysisInterpretationTableLineModel line = new CropFoliarAnalysisInterpretationTableLineModel();
        line.setTable(table);
        line.setCrop(crop);
        line.setN_content(createContentRange(20.0, 35.0, UnidadeTeor.g_per_kg));
        line.setP_content(createContentRange(1.5, 3.0, UnidadeTeor.g_per_kg));
        line.setK_content(createContentRange(15.0, 30.0, UnidadeTeor.g_per_kg));
        line.setCa_content(createContentRange(5.0, 15.0, UnidadeTeor.g_per_kg));
        line.setMg_content(createContentRange(2.0, 6.0, UnidadeTeor.g_per_kg));
        line.setS_content(createContentRange(1.5, 4.0, UnidadeTeor.g_per_kg));
        line.setB_content(createContentRange(20.0, 60.0, UnidadeTeor.mg_per_kg));
        line.setCu_content(createContentRange(5.0, 20.0, UnidadeTeor.mg_per_kg));
        line.setFe_content(createContentRange(50.0, 250.0, UnidadeTeor.mg_per_kg));
        line.setMn_content(createContentRange(30.0, 150.0, UnidadeTeor.mg_per_kg));
        line.setMo_content(createContentRange(0.1, 1.0, UnidadeTeor.mg_per_kg));
        line.setZn_content(createContentRange(15.0, 80.0, UnidadeTeor.mg_per_kg));

        lineRepository.save(line);
        log.info("✅ Linha foliar criada: tabela={} nutriente={} faixa={}-{}", tableName, "MULTIPLOS", 0.0, 9999.0);
    }

    private MenorMaiorTeores createContentRange(Double menor, Double maior, UnidadeTeor unity) {
        MenorMaiorTeores contentRange = new MenorMaiorTeores();
        contentRange.setMenor(menor);
        contentRange.setMaior(maior);
        contentRange.setUnity(unity);
        return contentRange;
    }
}
