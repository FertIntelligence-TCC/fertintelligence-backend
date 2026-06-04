package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
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
@Order(13)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SoilFertilityInterpretationTableSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

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

        int createdCount = 0;

        if (adminOptional.isPresent()) {
            createdCount += createIfNotExists(
                    adminOptional.get(),
                    "Tabela Fertilidade Solo Nordeste Pública",
                    true,
                    Regiao.NORDESTE,
                    "Critérios fictícios para testes de interpretação da fertilidade do solo."
            );
        }

        if (gilvanOptional.isPresent()) {
            createdCount += createIfNotExists(
                    gilvanOptional.get(),
                    "Tabela Fertilidade Solo Semiárido Pública",
                    true,
                    Regiao.NORDESTE,
                    "Tabela pública para simulações agronômicas."
            );

            createdCount += createIfNotExists(
                    gilvanOptional.get(),
                    "Tabela Fertilidade Solo Privada Gilvan",
                    false,
                    Regiao.NORDESTE,
                    "Tabela privada para testes de acesso por usuário."
            );
        }

        if (miguelOptional.isPresent()) {
            createdCount += createIfNotExists(
                    miguelOptional.get(),
                    "Tabela Fertilidade Solo Cerrado Pública",
                    true,
                    Regiao.CENTRO_OESTE,
                    "Dados criados automaticamente para validação do módulo Recommendation."
            );

            createdCount += createIfNotExists(
                    miguelOptional.get(),
                    "Tabela Fertilidade Solo Privada Miguel",
                    false,
                    Regiao.SUL,
                    "Tabela privada para testes de acesso por usuário."
            );
        }

        if (mateusOptional.isPresent()) {
            createdCount += createIfNotExists(
                    mateusOptional.get(),
                    "Tabela Fertilidade Solo Consultoria Mateus",
                    false,
                    Regiao.CENTRO_OESTE,
                    "Dados criados automaticamente para validação do módulo Recommendation."
            );
        }

        if (createdCount == 0
                && adminOptional.isEmpty()
                && gilvanOptional.isEmpty()
                && miguelOptional.isEmpty()
                && mateusOptional.isEmpty()) {
            log.warn("⚠️ Nenhuma tabela criada porque usuários base não foram encontrados.");
        }
    }

    private int createIfNotExists(UserModel creator,
                                  String name,
                                  boolean publicTable,
                                  Regiao region,
                                  String description) {
        if (creator == null) {
            return 0;
        }

        boolean exists = soilFertilityInterpretationCriteriaTableRepository.existsByCreatorAndName(creator, name);
        if (exists) {
            log.info("↩️ Tabela de interpretação de fertilidade já existe: criador={} nome={}", creator.getEmail(), name);
            return 0;
        }

        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .creator(creator)
                .name(name)
                .description(description)
                .region(region)
                .observations("Interpretar resultados considerando textura do solo, histórico de manejo e cultura implantada.")
                .sources("Manual de calagem e adubação; recomendações oficiais estaduais")
                .publicTable(publicTable)
                .build();

        soilFertilityInterpretationCriteriaTableRepository.save(table);
        log.info("✅ Tabela de interpretação de fertilidade criada: criador={} nome={}", creator.getEmail(), name);
        return 1;
    }
}
