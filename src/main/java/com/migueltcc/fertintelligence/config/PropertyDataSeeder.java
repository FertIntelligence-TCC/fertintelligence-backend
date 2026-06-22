package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Order(2)
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class PropertyDataSeeder implements CommandLineRunner {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    // Pelo seu log, é esse nome (sem "c")
    private static final String TBL_PROPERTY_ACCESS_REQUEST = "solitacoes_de_acesso_a_propriedades";
    private static final String TBL_PROPERTIES = "propriedades";

    /**
     * ⚠️ Por padrão: NÃO reseta.
     * Se você quiser resetar em dev/local, ligue:
     * app.seed.reset-properties=true
     */
    @Value("${app.seed.reset-properties:false}")
    private boolean resetProperties;

    @Override
    public void run(String... args) {
        if (resetProperties) {
            System.out.println("🔄 Resetando propriedades (habilitado por config)...");
            TransactionTemplate tx = new TransactionTemplate(transactionManager);
            tx.execute(status -> {
                resetDatabase();
                return null;
            });
        } else {
            System.out.println("ℹ️ Reset de propriedades desativado (app.seed.reset-properties=false).");
        }

        seedProperties();
        System.out.println("✅ Propriedades carregadas com sucesso (se necessário).");
    }

    protected void resetDatabase() {

        // 1) TRUNCATE (mais limpo / evita FK)
        try {
            entityManager.createNativeQuery("""
                TRUNCATE TABLE
                    %s,
                    %s
                RESTART IDENTITY
                CASCADE
            """.formatted(TBL_PROPERTY_ACCESS_REQUEST, TBL_PROPERTIES)).executeUpdate();

            entityManager.flush();
            System.out.println("🧹 Reset via TRUNCATE (requests + propriedades) concluído.");
            return;

        } catch (Exception e) {
            System.out.println("⚠️ TRUNCATE falhou, tentando DELETE ordenado. Motivo: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // 2) Fallback seguro: DELETE na ordem certa (dependente -> pai)
        try {
            entityManager.createNativeQuery("DELETE FROM " + TBL_PROPERTY_ACCESS_REQUEST).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM " + TBL_PROPERTIES).executeUpdate();
            entityManager.flush();
            System.out.println("🧹 Reset via DELETE (requests -> propriedades) concluído.");
        } catch (Exception e) {
            System.out.println("❌ Reset via DELETE também falhou: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw e;
        }
    }

    /* ======================================================
       SEED
    ====================================================== */

    private void seedProperties() {

        createIfNotExists("Fazenda Vale do Sol",
                "Rodovia BR-163, km 740, Zona Rural, Sorriso - MT",
                "10000000000101",
                12.5420, LatitudeDirection.SUL,
                55.7210, LongitudeDirection.OESTE,
                365.0);

        createIfNotExists("Sítio Recanto Verde",
                "Estrada Municipal Ribeirão Preto, s/n, Ribeirão Preto - SP",
                "20000000000102",
                21.1704, LatitudeDirection.SUL,
                47.8103, LongitudeDirection.OESTE,
                546.0);

        createIfNotExists("Estância das Águas Claras",
                "Rodovia do Café, km 45, Varginha - MG",
                "30000000000103",
                21.5544, LatitudeDirection.SUL,
                45.4312, LongitudeDirection.OESTE,
                980.0);

        createIfNotExists("Agropecuária Terra Fértil",
                "BR-060, km 120, Rio Verde - GO",
                "40000000000104",
                17.7915, LatitudeDirection.SUL,
                50.9197, LongitudeDirection.OESTE,
                748.0);

        createIfNotExists("Fazenda Boa Safra",
                "Linha das Palmeiras, Zona Rural, Cascavel - PR",
                "50000000000105",
                24.9573, LatitudeDirection.SUL,
                53.4590, LongitudeDirection.OESTE,
                781.0);

        createIfNotExists("Rancho da Serra Gaúcha",
                "Estrada dos Vinhedos, Bento Gonçalves - RS",
                "60000000000106",
                29.1691, LatitudeDirection.SUL,
                51.5178, LongitudeDirection.OESTE,
                691.0);

        createIfNotExists("Chácara Santa Luzia",
                "Anel da Soja, Barreiras - BA",
                "70000000000107",
                12.1485, LatitudeDirection.SUL,
                44.9922, LongitudeDirection.OESTE,
                452.0);

        createIfNotExists("Sítio Horizonte Azul",
                "Serra do Rio do Rastro, São Joaquim - SC",
                "80000000000108",
                28.2933, LatitudeDirection.SUL,
                49.9324, LongitudeDirection.OESTE,
                1360.0);

        createIfNotExists("Fazenda Ouro Branco",
                "Estrada Boiadeira, km 10, Campo Grande - MS",
                "90000000000109",
                20.4697, LatitudeDirection.SUL,
                54.6201, LongitudeDirection.OESTE,
                532.0);

        createIfNotExists("Estância Vida Nova",
                "PB-079, Zona Rural, Alagoa Grande - PB",
                "99000000000110",
                7.0512, LatitudeDirection.SUL,
                35.6321, LongitudeDirection.OESTE,
                145.0);
    }

    /* ======================================================
       CREATE HELPER
    ====================================================== */

    private void createIfNotExists(String nome,
                                   String endereco,
                                   String cnpj,
                                   Double lat,
                                   LatitudeDirection latDir,
                                   Double lon,
                                   LongitudeDirection lonDir,
                                   Double alt) {

        if (propertyRepository.existsByCnpj(cnpj)) {
            System.out.println("↩️ Propriedade já existe (cnpj=" + cnpj + "): " + nome);
            return;
        }

        LocalizacaoDto localizacao = new LocalizacaoDto(lat, latDir, lon, lonDir, alt);

        PropertyCreateRequestDto dto = PropertyCreateRequestDto.builder()
                .nome(nome)
                .endereco(endereco)
                .cnpj(cnpj)
                .localizacao(localizacao)
                .build();

        propertyService.createProperty(dto, SYSTEM_USER);
        System.out.println("➕ Criada: " + nome);
    }
}