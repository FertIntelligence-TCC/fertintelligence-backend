package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class PropertyDataSeeder implements CommandLineRunner {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;

    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    @Override
    public void run(String... args) {
        System.out.println("🔄 Resetando propriedades...");
        resetDatabase();
        seedProperties();
        System.out.println("✅ Propriedades carregadas com sucesso.");
    }

    /* ======================================================
       RESET
    ====================================================== */

    private void resetDatabase() {
        propertyRepository.deleteAll();
    }

    /* ======================================================
       SEED
    ====================================================== */

    private void seedProperties() {

        create("Fazenda Vale do Sol",
                "Rodovia BR-163, km 740, Zona Rural, Sorriso - MT",
                "10000000000101",
                12.5420, LatitudeDirection.SUL,
                55.7210, LongitudeDirection.OESTE,
                365.0);

        create("Sítio Recanto Verde",
                "Estrada Municipal Ribeirão Preto, s/n, Ribeirão Preto - SP",
                "20000000000102",
                21.1704, LatitudeDirection.SUL,
                47.8103, LongitudeDirection.OESTE,
                546.0);

        create("Estância das Águas Claras",
                "Rodovia do Café, km 45, Varginha - MG",
                "30000000000103",
                21.5544, LatitudeDirection.SUL,
                45.4312, LongitudeDirection.OESTE,
                980.0);

        create("Agropecuária Terra Fértil",
                "BR-060, km 120, Rio Verde - GO",
                "40000000000104",
                17.7915, LatitudeDirection.SUL,
                50.9197, LongitudeDirection.OESTE,
                748.0);

        create("Fazenda Boa Safra",
                "Linha das Palmeiras, Zona Rural, Cascavel - PR",
                "50000000000105",
                24.9573, LatitudeDirection.SUL,
                53.4590, LongitudeDirection.OESTE,
                781.0);

        create("Rancho da Serra Gaúcha",
                "Estrada dos Vinhedos, Bento Gonçalves - RS",
                "60000000000106",
                29.1691, LatitudeDirection.SUL,
                51.5178, LongitudeDirection.OESTE,
                691.0);

        create("Chácara Santa Luzia",
                "Anel da Soja, Barreiras - BA",
                "70000000000107",
                12.1485, LatitudeDirection.SUL,
                44.9922, LongitudeDirection.OESTE,
                452.0);

        create("Sítio Horizonte Azul",
                "Serra do Rio do Rastro, São Joaquim - SC",
                "80000000000108",
                28.2933, LatitudeDirection.SUL,
                49.9324, LongitudeDirection.OESTE,
                1360.0);

        create("Fazenda Ouro Branco",
                "Estrada Boiadeira, km 10, Campo Grande - MS",
                "90000000000109",
                20.4697, LatitudeDirection.SUL,
                54.6201, LongitudeDirection.OESTE,
                532.0);

        create("Estância Vida Nova",
                "PB-079, Zona Rural, Alagoa Grande - PB",
                "99000000000110",
                7.0512, LatitudeDirection.SUL,
                35.6321, LongitudeDirection.OESTE,
                145.0);
    }

    /* ======================================================
       CREATE HELPER
    ====================================================== */

    private void create(String nome,
                        String endereco,
                        String cnpj,
                        Double lat,
                        LatitudeDirection latDir,
                        Double lon,
                        LongitudeDirection lonDir,
                        Double alt) {

        try {

            LocalizacaoDto localizacao = new LocalizacaoDto(
                    lat,
                    latDir,
                    lon,
                    lonDir,
                    alt
            );

            PropertyCreateRequestDto dto =
                    PropertyCreateRequestDto.builder()
                            .nome(nome)
                            .endereco(endereco)
                            .cnpj(cnpj) // já sem máscara
                            .localizacao(localizacao)
                            .build();

            propertyService.createProperty(dto, SYSTEM_USER);

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar '" + nome + "': " + e.getMessage());
        }
    }
}