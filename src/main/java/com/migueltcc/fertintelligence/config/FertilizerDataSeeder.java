package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.*;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.service.documentation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FertilizerDataSeeder implements CommandLineRunner {

    @Autowired
    private SimpleMineralFertilizerService simpleService;

    @Autowired
    private FormulatedMineralFertilizerService formulatedService;

    @Autowired
    private OrganoMineralFertilizerService organoService;

    @Autowired
    private GreenFertilizerService greenService;

    @Autowired
    private UserService userService; // Injeção do serviço de usuário

    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Iniciando verificação de pré-requisitos (Usuário Admin)...");

        // 1. Garante que o usuário existe antes de tentar criar os adubos
        createSystemUserIfNotFound();

        System.out.println("Iniciando carga de dados dos adubos...");

        loadSimpleFertilizers();
        loadFormulatedFertilizers();
        loadOrganoMineralFertilizers();
        loadGreenFertilizers();

        System.out.println("Carga finalizada.");
    }

    private void createSystemUserIfNotFound() {
        try {
            // Tenta buscar o usuário. Se lançar exceção ou retornar null, criamos.
            userService.getUser(SYSTEM_USER);
            System.out.println("Usuário admin já existe.");
        } catch (Exception e) {
            System.out.println("Usuário admin não encontrado. Criando...");

            UserCreateRequestDto adminUser = UserCreateRequestDto.builder()
                    .name("Administrador do Sistema")
                    .username(SYSTEM_USER)
                    .email(SYSTEM_USER)
                    .password("admin123")
                    .cpf("00000000000")
                    .profissao("System Admin")
                    .datanasc(new DataNasc(1, 1, 2000))
                    .genero(Genero.OUTRO)
                    .telefone(new Telefone("55", "11", "999999999"))
                    .formacao(Formacao.DOUTORADO)
                    .cargo(Cargo.PROPRIETARIO)
                    .build();

            try {
                userService.createUser(adminUser);
                System.out.println("Usuário admin criado com sucesso.");
            } catch (Exception creationEx) {
                System.err.println("Erro crítico: Não foi possível criar o usuário admin. " + creationEx.getMessage());
            }
        }
    }

    // ============================================================================================
    // 1. ADUBOS MINERAIS SIMPLES
    // ============================================================================================
    private void loadSimpleFertilizers() {
        // [Nome, N, P, K, Ca, Mg, S, B, Cu, Fe, Mn, Mo, Zn, Ind. Salino, Ind. Acidez]
        createSimple("Fosfato Diamônio", 18.0, 46.0, null, null, null, null, null, null, null, null, null, null, null, -589.0);
        createSimple("Fosfato Monoamônio", 11.0, 50.0, null, null, null, null, null, null, null, null, null, null, null, -635.0);
        createSimple("Nitrato de Amônio", 33.0, null, null, null, null, null, null, null, null, null, null, null, null, -535.0);
        createSimple("Nitrato de Cálcio", 15.0, null, null, 20.0, null, null, null, 0.0022, null, null, 0.0001, 0.0015, null, 181.0);
        createSimple("Nitrato de Potássio", 13.0, null, 44.0, null, null, null, null, null, null, null, null, null, null, 236.0);
        createSimple("Nitrocálcio", 22.0, null, null, 7.0, null, null, null, null, null, null, null, null, null, -280.0);
        createSimple("Sulfato de Amônio", 21.0, null, null, null, null, 24.0, 0.0006, 0.0002, null, 0.0006, null, null, null, -996.0);
        createSimple("Sulfonitrato de Amônio", 26.0, null, null, null, null, 15.0, null, null, null, null, null, null, null, -770.0);
        createSimple("Uréia", 45.0, null, null, null, null, null, null, null, null, null, null, null, null, -840.0);
        createSimple("Superfosfato Simples", null, 18.0, null, 19.0, null, 12.0, 0.0011, 0.0044, null, 0.0011, 0.0002, 0.0150, null, 0.0);
        createSimple("Superfosfato Triplo", null, 45.0, null, 13.0, null, 2.0, null, null, null, null, null, null, null, 0.0);
        createSimple("Fosfato Bicálcico", null, 30.0, null, 21.0, null, null, null, null, null, null, null, null, null, 0.0);
        createSimple("Cloreto de Potássio", null, null, 60.0, null, null, null, null, null, null, null, null, null, null, 0.0);
        createSimple("Sulfato de Potássio", null, null, 50.0, null, null, 17.0, 0.0004, 0.0004, null, 0.0006, null, 0.0002, null, 0.0);
        createSimple("Sulfato de Potássio e Magnésio", null, null, 22.0, null, 11.0, 22.0, null, null, null, null, null, null, null, 0.0);
        createSimple("Sulfato de Cálcio", null, null, null, 16.0, null, 13.0, null, null, null, null, null, null, null, 0.0);
        createSimple("Gesso", null, null, null, 23.0, 19.0, null, null, null, null, null, null, null, null, 0.0);
        createSimple("Sulfato de Magnésio", null, null, null, null, 9.0, 13.0, null, null, null, null, null, null, null, 0.0);
        createSimple("Magnesita (Óxido de Magnésio)", null, null, null, null, 33.0, null, null, null, null, null, null, null, null, 1190.0);
        createSimple("Carbonato de Magnésio", null, null, null, null, 26.0, null, null, null, null, null, null, null, null, 0.0);
        createSimple("Bórax", null, null, null, null, null, null, 11.0, null, null, null, null, null, null, 0.0);
        createSimple("Ácido Bórico", null, null, null, null, null, null, 17.0, null, null, null, null, null, null, 0.0);
        createSimple("Pentaborato de Sódio", null, null, null, null, null, null, 18.0, null, null, null, null, null, null, 0.0);
        createSimple("Sulfato de Cobre", null, null, null, null, null, null, null, 17.0, 13.0, null, null, null, null, 0.0);
        createSimple("Quelato de Cobre", null, null, null, null, null, null, null, 5.0, null, null, null, null, null, 0.0);
        createSimple("Sulfato férrico", null, null, null, null, null, null, null, null, 19.0, null, 23.0, null, null, 0.0);
        createSimple("Quelato de Ferro", null, null, null, null, null, null, null, null, 5.0, null, null, null, null, 0.0);
        createSimple("Sulfato Manganoso", null, null, null, null, null, null, null, null, null, 26.0, null, null, null, 0.0);
        createSimple("Quelato de Manganês", null, null, null, null, null, null, null, null, null, 12.0, null, null, null, 0.0);
        createSimple("Molibdato de Amônio", null, null, null, null, null, null, null, null, null, null, 54.0, null, null, 0.0);
        createSimple("Molibdato de Sódio", null, null, null, null, null, null, null, null, null, null, 39.0, null, null, 0.0);
        createSimple("Sulfato de Zinco", null, null, null, null, null, null, null, null, null, null, null, 20.0, null, 0.0);
        createSimple("Óxido de Zinco", null, null, null, null, null, null, null, null, null, null, null, 50.0, null, 0.0);
        createSimple("Quelato de Zinco", null, null, null, null, null, null, null, null, null, null, null, 7.0, null, 0.0);
    }

    private void createSimple(String nome, Double n, Double p, Double k,
                              Double ca, Double mg, Double s,
                              Double b, Double cu, Double fe, Double mn, Double mo, Double zn,
                              Double indSalino, Double indAcidez) {
        try {
            SimpleMineralFertilizerCreateRequestDto dto = SimpleMineralFertilizerCreateRequestDto.builder()
                    .name(nome)
                    .n(n).p2o5(p).k2o(k)
                    .ca(ca).mg(mg).s(s)
                    .b(b).cu(cu).fe(fe).mn(mn).mo(mo).zn(zn)
                    .indiceSalino(indSalino)
                    .indiceAcidez(indAcidez)
                    .build();
            simpleService.createSimpleMineralFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar adubo simples '" + nome + "': " + e.getMessage());
        }
    }

    // ============================================================================================
    // 2. ADUBOS FORMULADOS
    // ============================================================================================
    private void loadFormulatedFertilizers() {
        createFormulated("04-14-08", 4, 14, 8, 1.0, 3.5, 2.0);
        createFormulated("04-30-10", 4, 30, 10, 1.0, 7.5, 2.5);
        createFormulated("04-30-16", 4, 30, 16, 1.0, 7.5, 4.0);
        createFormulated("06-24-12", 6, 24, 12, 1.0, 4.0, 2.0);
        createFormulated("08-30-10", 8, 30, 10, 1.0, 3.75, 1.25);
        createFormulated("08-30-16", 8, 30, 16, 1.0, 3.75, 2.0);
        createFormulated("08-30-20", 8, 30, 20, 1.0, 3.75, 5.0);
        createFormulated("10-05-10", 10, 5, 10, 2.0, 1.0, 2.0);
        createFormulated("10-10-10", 10, 10, 10, 1.0, 1.0, 1.0);
        createFormulated("10-25-10", 10, 25, 10, 1.0, 2.5, 10.0);
        createFormulated("10-28-20", 10, 28, 20, 1.0, 2.8, 2.0);
        createFormulated("15-15-15", 15, 15, 15, 1.0, 1.0, 1.0);
        createFormulated("20-10-20", 20, 10, 20, 2.0, 1.0, 2.0);
        createFormulated("34-04-05", 34, 4, 5, 8.5, 1.0, 1.25);

        createFormulatedFull("20-0-20", 20, 0, 20, 1.0, 0.0, 1.0, 2.0, 2.0, 4.0, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("20-0-10", 20, 0, 10, 2.0, 0.0, 1.0, 2.4, 2.0, 4.5, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("10-10-10 (com micro)", 10, 10, 10, 1.0, 1.0, 1.0, 8.8, 2.0, 5.2, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("10-10-20", 10, 10, 20, 1.0, 1.0, 2.0, 4.4, 2.0, 2.6, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("12-06-12", 12, 6, 12, 2.0, 1.0, 2.0, 6.8, 2.0, 6.4, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("10-05-20", 10, 5, 20, 2.0, 1.0, 4.0, 6.0, 2.0, 3.6, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("20-05-10", 20, 5, 10, 4.0, 1.0, 2.0, 3.2, 2.0, 2.0, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("20-05-20", 20, 5, 20, 4.0, 1.0, 4.0, 2.0, 1.0, 1.7, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("4-16-8", 4, 16, 8, 1.0, 4.0, 2.0, 12.0, 2.0, 7.2, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("4-16-16", 4, 16, 16, 1.0, 4.0, 4.0, 6.5, 2.0, 7.5, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("2-20-4", 2, 20, 4, 1.0, 10.0, 2.0, 13.4, 2.0, 8.0, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("2-20-8", 2, 20, 8, 1.0, 10.0, 4.0, 12.0, 2.0, 7.5, 0.2, null, null, null, null, 0.4);
        createFormulatedFull("0-20-10", 0, 20, 10, 0.0, 2.0, 1.0, 13.0, 2.0, 6.8, null, null, null, null, null, 0.4);
        createFormulatedFull("0-20-20", 0, 20, 20, 0.0, 1.0, 1.0, 9.0, 2.0, 5.0, null, null, null, null, null, 0.4);
        createFormulatedFull("0-10-20", 0, 10, 20, 0.0, 1.0, 2.0, 11.0, 2.0, 6.6, null, null, null, null, null, 0.4);
        createFormulatedFull("0-17-0", 0, 17, 0, 0.0, 1.0, 0.0, 19.0, 2.0, 11.0, 0.1, null, null, null, null, 0.4);
    }

    private void createFormulated(String formulaStr, Integer n, Integer p, Integer k, Double rN, Double rP, Double rK) {
        createFormulatedFull(formulaStr, n, p, k, rN, rP, rK, null, null, null, null, null, null, null, null, null);
    }

    private void createFormulatedFull(String formulaStr, Integer n, Integer p, Integer k,
                                      Double rN, Double rP, Double rK,
                                      Double ca, Double mg, Double s,
                                      Double b, Double cu, Double fe, Double mn, Double mo, Double zn) {
        try {
            FormulatedMineralFertilizerCreateRequestDto dto = FormulatedMineralFertilizerCreateRequestDto.builder()
                    .formulate(new FormulateDto(n, p, k))
                    .relation(new NPKrelationDto(rN, rP, rK))
                    .n(Double.valueOf(n))
                    .p2o5(Double.valueOf(p))
                    .k2o(Double.valueOf(k))
                    .indicatedFormulaNumber(null)
                    .ca(ca).mg(mg).s(s)
                    .b(b).cu(cu).fe(fe).mn(mn).mo(mo).zn(zn)
                    .build();
            formulatedService.createFormulatedMineralFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar formulado '" + formulaStr + "': " + e.getMessage());
        }
    }

    // ============================================================================================
    // 3. ADUBOS ORGANO-MINERAIS
    // ============================================================================================
    private void loadOrganoMineralFertilizers() {
        createOrgano("Esterco de Cabra", null, 3.0, 2.0, 3.0, null, null, null, null, null, null, null, null, null);
        createOrgano("Esterco de Boi (seco)", null, 1.3, 2.0, 1.1, 0.5, 0.6, 0.04, 0.01, null, null, 0.04, null, 0.05);
        createOrgano("Esterco de Galinha", null, 2.95, 4.6, 2.2, 3.7, 0.6, 0.4, null, null, 0.04, null, 0.03, null);
        createOrgano("Esterco de Cabra (V2)", null, 2.1, 1.0, 2.5, null, null, null, null, null, null, null, null, null);
        createOrgano("Torta de algodão", null, 6.0, 3.0, 1.4, 0.2, 0.6, 0.3, null, null, null, null, 0.02, null);
        createOrgano("Torta de Mamona", null, 5.0, 2.0, 1.0, 0.4, 0.5, 0.04, 0.01, null, null, 0.04, null, 0.05);
        createOrgano("Torta misturada (casca e semente) Mamona", null, 2.0, 2.0, 1.0, 0.5, 0.02, null, null, null, null, null, null, null);
    }

    private void createOrgano(String nome, Double c, Double n, Double p, Double k,
                              Double ca, Double mg, Double s,
                              Double b, Double cu, Double fe, Double mn, Double mo, Double zn) {
        try {
            OrganoMineralFertilizerCreateRequestDto dto = OrganoMineralFertilizerCreateRequestDto.builder()
                    .name(nome)
                    .c(c).n(n).p2o5(p).k2o(k)
                    .ca(ca).mg(mg).s(s)
                    .b(b).cu(cu).fe(fe).mn(mn).mo(mo).zn(zn)
                    .indiceSalino(null).indiceAcidez(null)
                    .build();
            organoService.createOrganoMineralFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar organo-mineral '" + nome + "': " + e.getMessage());
        }
    }

    // ============================================================================================
    // 4. ADUBOS VERDES
    // ============================================================================================
    private void loadGreenFertilizers() {
        createGreen("Feijão de Porco (Canavalia ensiforme)", 3.39, 10.0, 0.35, 2.65);
        createGreen("Crotalária juncea", 1.80, 16.0, 0.24, 1.26);
        createGreen("Guandu (Cajanus cajans)", 2.55, 15.0, 0.25, 1.57);
        createGreen("Lab-Lab (Dolichos lab-lab)", 2.04, 25.0, 0.80, 2.11);
        createGreen("Mucuna Preta (Stylozobium aterrimum)", 2.67, 14.0, 0.33, 1.95);
        createGreen("Caupi (Vigna sinensis)", 2.73, 15.0, 0.23, 1.23);
        createGreen("Stilosantes (Stylosantes guinensis)", 2.30, null, 0.27, 1.23);
    }

    private void createGreen(String nome, Double n, Double cnRatio, Double p, Double k) {
        try {
            GreenFertilizerCreateRequestDto dto = GreenFertilizerCreateRequestDto.builder()
                    .name(nome)
                    .c(null)
                    .n(n).p2o5(p).k2o(k)
                    .ca(null).mg(null).s(null)
                    .b(null).cu(null).fe(null).mn(null).mo(null).zn(null)
                    .indiceSalino(null).indiceAcidez(null)
                    .build();
            greenService.createGreenFertilizer(dto, SYSTEM_USER);
        } catch (Exception e) {
            System.err.println("Erro ao criar adubo verde '" + nome + "': " + e.getMessage());
        }
    }
}