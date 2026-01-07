package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Executa APÓS a criação dos usuários (Order 1) e ANTES dos fertilizantes (Order 3)
public class PropertyDataSeeder implements CommandLineRunner {

    @Autowired
    private PropertyService propertyService;

    // Email do usuário admin criado no UserDataSeeder
    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Iniciando carga de dados das Propriedades...");
        loadProperties();
        System.out.println("Carga de Propriedades finalizada.");
    }

    private void loadProperties() {
        // 1. Mato Grosso (Grãos/Soja) - Grande porte
        createProperty("Fazenda Vale do Sol",
                "Rodovia BR-163, km 740, Zona Rural, Sorriso - MT",
                "10.000.000/0001-01",
                12.5420, LatitudeDirection.SUL,
                55.7210, LongitudeDirection.OESTE,
                365.0);

        // 2. São Paulo (Cana/Laranja)
        createProperty("Sítio Recanto Verde",
                "Estrada Municipal Ribeirão Preto, s/n, Ribeirão Preto - SP",
                "20.000.000/0001-02",
                21.1704, LatitudeDirection.SUL,
                47.8103, LongitudeDirection.OESTE,
                546.0);

        // 3. Minas Gerais (Café) - Região montanhosa
        createProperty("Estância das Águas Claras",
                "Rodovia do Café, km 45, Varginha - MG",
                "30.000.000/0001-03",
                21.5544, LatitudeDirection.SUL,
                45.4312, LongitudeDirection.OESTE,
                980.0);

        // 4. Goiás (Milho/Soja)
        createProperty("Agropecuária Terra Fértil",
                "BR-060, km 120, Rio Verde - GO",
                "40.000.000/0001-04",
                17.7915, LatitudeDirection.SUL,
                50.9197, LongitudeDirection.OESTE,
                748.0);

        // 5. Paraná (Trigo/Soja)
        createProperty("Fazenda Boa Safra",
                "Linha das Palmeiras, Zona Rural, Cascavel - PR",
                "50.000.000/0001-05",
                24.9573, LatitudeDirection.SUL,
                53.4590, LongitudeDirection.OESTE,
                781.0);

        // 6. Rio Grande do Sul (Arroz/Uva)
        createProperty("Rancho da Serra Gaúcha",
                "Estrada dos Vinhedos, Bento Gonçalves - RS",
                "60.000.000/0001-06",
                29.1691, LatitudeDirection.SUL,
                51.5178, LongitudeDirection.OESTE,
                691.0);

        // 7. Bahia (Algodão/Soja) - Matopiba
        createProperty("Chácara Santa Luzia",
                "Anel da Soja, Barreiras - BA",
                "70.000.000/0001-07",
                12.1485, LatitudeDirection.SUL,
                44.9922, LongitudeDirection.OESTE,
                452.0);

        // 8. Santa Catarina (Maçã/Pequenas Culturas)
        createProperty("Sítio Horizonte Azul",
                "Serra do Rio do Rastro, São Joaquim - SC",
                "80.000.000/0001-08",
                28.2933, LatitudeDirection.SUL,
                49.9324, LongitudeDirection.OESTE,
                1360.0);

        // 9. Mato Grosso do Sul (Pecuária/Integração)
        createProperty("Fazenda Ouro Branco",
                "Estrada Boiadeira, km 10, Campo Grande - MS",
                "90.000.000/0001-09",
                20.4697, LatitudeDirection.SUL,
                54.6201, LongitudeDirection.OESTE,
                532.0);

        // 10. Paraíba (Agricultura Familiar/Nordeste)
        createProperty("Estância Vida Nova",
                "PB-079, Zona Rural, Alagoa Grande - PB",
                "99.000.000/0001-10",
                7.0512, LatitudeDirection.SUL,
                35.6321, LongitudeDirection.OESTE,
                145.0);
    }

    private void createProperty(String nome, String endereco, String cnpj,
                                Double lat, LatitudeDirection latDir,
                                Double lon, LongitudeDirection lonDir,
                                Double alt) {
        try {
            LocalizacaoDto localizacao = new LocalizacaoDto(lat, latDir, lon, lonDir, alt);

            PropertyCreateRequestDto dto = PropertyCreateRequestDto.builder()
                    .nome(nome)
                    .endereco(endereco)
                    .cnpj(cnpj)
                    .localizacao(localizacao)
                    .build();

            // Assume que o usuário SYSTEM_USER já foi criado pelo UserDataSeeder
            propertyService.createProperty(dto, SYSTEM_USER);
            // System.out.println("Propriedade criada: " + nome);

        } catch (Exception e) {
            System.err.println("Erro ao criar propriedade '" + nome + "': " + e.getMessage());
        }
    }
}