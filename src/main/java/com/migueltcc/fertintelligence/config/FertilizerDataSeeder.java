package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.service.documentation.FormulatedMineralFertilizerService;
import com.migueltcc.fertintelligence.service.documentation.SimpleMineralFertilizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FertilizerDataSeeder implements CommandLineRunner {

    @Autowired
    private SimpleMineralFertilizerService simpleService;

    @Autowired
    private FormulatedMineralFertilizerService formulatedService;

    // Defina um usuário do sistema para ser o "dono" desses registros iniciais
    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    @Override
    public void run(String... args) throws Exception {
        // Carrega Adubos Minerais Simples
        loadSimpleFertilizers();

        // Carrega Adubos Formulados
        loadFormulatedFertilizers();

        System.out.println("Carga de adubos finalizada com sucesso!");
    }

    private void loadSimpleFertilizers() {
        // Exemplo 1: Fosfato Diamônio
        // Dados originais: N=18, P2O5=46, Acidez=-589
        // Obs: Assumindo que você removeu a restrição @DecimalMin de indiceAcidez
        createSimple("Fosfato Diamônio", 18.0, 46.0, 0.0, -589.0);

        // Exemplo 2: Uréia
        createSimple("Uréia", 45.0, 0.0, 0.0, -840.0);

        // Exemplo 3: Cloreto de Potássio
        createSimple("Cloreto de Potássio", 0.0, 0.0, 60.0, 0.0);

        // ... Adicione os outros adubos da tabela seguindo o padrão
    }

    private void createSimple(String nome, Double n, Double p, Double k, Double acidez) {
        try {
            SimpleMineralFertilizerCreateRequestDto dto = SimpleMineralFertilizerCreateRequestDto.builder()
                    .name(nome)
                    .n(n).p2o5(p).k2o(k)
                    .ca(0.0).mg(0.0).s(0.0) // Preencha se houver dados na tabela
                    .b(0.0).cu(0.0).fe(0.0).mn(0.0).mo(0.0).zn(0.0)
                    .indiceSalino(0.0) // A tabela tem muitos valores vazios aqui, use 0.0 ou null se permitido
                    .indiceAcidez(acidez)
                    .build();

            simpleService.createSimpleMineralFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar " + nome + ": " + e.getMessage());
        }
    }

    private void loadFormulatedFertilizers() {
        // Exemplo: 04-14-08 | Relação 1:3,5:2
        createFormulated("04-14-08", 4, 14, 8, 1.0, 3.5, 2.0);

        // Exemplo: 20-05-20 | Relação 4:1:4
        createFormulated("20-05-20", 20, 5, 20, 4.0, 1.0, 4.0);
    }

    private void createFormulated(String nome, Integer nInt, Integer pInt, Integer kInt,
                                  Double nRel, Double pRel, Double kRel) {
        try {
            FormulateDto formula = new FormulateDto(nInt, pInt, kInt);
            NPKrelationDto relacao = new NPKrelationDto(nRel, pRel, kRel);

            FormulatedMineralFertilizerCreateRequestDto dto = FormulatedMineralFertilizerCreateRequestDto.builder()
                    .formulate(formula)
                    .relation(relacao)
                    .n(Double.valueOf(nInt))
                    .p2o5(Double.valueOf(pInt))
                    .k2o(Double.valueOf(kInt))
                    // Preencha os secundários/micro se a tabela fornecer
                    .ca(0.0).mg(0.0).s(0.0)
                    .b(0.0).cu(0.0).fe(0.0).mn(0.0).mo(0.0).zn(0.0)
                    .build();

            // Nota: O Service espera que passemos o DTO.
            // O nome do adubo formulado geralmente é derivado da fórmula,
            // mas verifique se o seu Service exige um campo "nome" explícito.
            // O DTO FormulatedMineralFertilizerCreateRequestDto não tem campo "name",
            // ele usa o objeto "FormulateDto".

            formulatedService.createFormulatedMineralFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar formulado " + nome + ": " + e.getMessage());
        }
    }
}