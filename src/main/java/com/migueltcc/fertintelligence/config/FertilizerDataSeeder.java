package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.*;
import com.migueltcc.fertintelligence.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Order(3) // Executa após a criação de Usuários
@RequiredArgsConstructor
@Slf4j
public class FertilizerDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    // Repositórios Solo
    private final SimpleMineralFertilizerRepository simpleRepository;
    private final FormulatedMineralFertilizerRepository formulatedRepository;
    private final OrganoMineralFertilizerRepository organoRepository;
    private final GreenFertilizerRepository greenRepository;

    // Repositórios Foliares
    private final MineralFertilizerRepository foliarMineralRepository;
    private final ChelatedFertilizerRepository chelatedRepository;
    private final BioFertilizerRepository bioRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🧪 Iniciando o Seeding de Fertilizantes baseado na Tabela Oficial...");

        Optional<UserModel> creatorOpt = userRepository.findByEmail("admin@fertintelligence.com");
        if (creatorOpt.isEmpty()) {
            log.warn("⚠️ Usuário admin não encontrado. Pulando seeding.");
            return;
        }
        UserModel creator = creatorOpt.get();

        loadSimpleMineralFromDoc(creator);
        loadFormulatedMineral(creator);
        loadOrganoMineral(creator);
        loadGreen(creator);
        loadFoliarMineral(creator);
        loadChelated(creator);
        loadBio(creator);

        log.info("✅ Seeding de Fertilizantes concluído.");
    }

    // ==========================================
    // 1. ADUBOS MINERAIS SIMPLES (Dados do DOCX)
    // ==========================================
    private void loadSimpleMineralFromDoc(UserModel user) {
        // Verifica se já existem dados para não duplicar
        if (simpleRepository.count() > 0) return;

        // Fosfato Diamônio (DAP)
        // N:18, P:46, Acidez: -589
        createSimple(user, "Fosfato Diamônio", 18.0, 46.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 34.0, -589.0);

        // Fosfato Monoamônio (MAP)
        // N:11, P:50, Acidez: -635
        createSimple(user, "Fosfato Monoamônio", 11.0, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 30.0, -635.0);

        // Nitrato de Amônio
        // N:33, Acidez: -535
        createSimple(user, "Nitrato de Amônio", 33.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 105.0, -535.0);

        // Nitrato de Cálcio
        // N:15, Ca:20, B:0.0022, Mo:0.0001, Zn:0.0015, Acidez: +181
        createSimple(user, "Nitrato de Cálcio", 15.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0022, 0.0, 0.0, 0.0, 0.0001, 0.0015, 60.0, 181.0);

        // Nitrato de Potássio
        // N:13, K:44, Acidez: +236
        createSimple(user, "Nitrato de Potássio", 13.0, 0.0, 44.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 74.0, 236.0);

        // Nitrocálcio
        // N:22, Ca:7, Acidez: -280
        createSimple(user, "Nitrocálcio", 22.0, 0.0, 0.0, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -280.0);

        // Sulfato de Amônio
        // N:21, S:24, B:0.0006, Cu:0.0002, Mn:0.0006, Acidez: -996
        createSimple(user, "Sulfato de Amônio", 21.0, 0.0, 0.0, 0.0, 0.0, 24.0, 0.0006, 0.0002, 0.0, 0.0006, 0.0, 0.0, 69.0, -996.0);

        // Sulfonitrato de Amônio
        // N:26, S:15, Acidez: -770
        createSimple(user, "Sulfonitrato de Amônio", 26.0, 0.0, 0.0, 0.0, 0.0, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -770.0);

        // Uréia
        // N:45, Acidez: -840
        createSimple(user, "Uréia", 45.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 75.0, -840.0);

        // Superfosfato Simples
        // P:18, Ca:19, S:12, B:0.0011, Cu:0.0044, Mn:0.0011, Mo:0.0002, Zn:0.0150, Acidez: 0
        createSimple(user, "Superfosfato Simples", 0.0, 18.0, 0.0, 19.0, 0.0, 12.0, 0.0011, 0.0044, 0.0, 0.0011, 0.0002, 0.0150, 10.0, 0.0);

        // Superfosfato Triplo
        // P:45, Ca:13, S:2, Acidez: 0
        createSimple(user, "Superfosfato Triplo", 0.0, 45.0, 0.0, 13.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 0.0);

        // Fosfato Bicálcico
        // P:30, Ca:21, Acidez: 0
        createSimple(user, "Fosfato Bicálcico", 0.0, 30.0, 0.0, 21.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        // Cloreto de Potássio (Adicionado como complemento comum, embora o doc corte)
        createSimple(user, "Cloreto de Potássio", 0.0, 0.0, 60.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 116.0, 0.0);
    }

    private void createSimple(UserModel user, String name, double n, double p, double k,
                              double ca, double mg, double s,
                              double b, double cu, double fe, double mn, double mo, double zn,
                              double indiceSalino, double indiceAcidez) {

        SimpleMineralFertilizerModel model = SimpleMineralFertilizerModel.builder()
                .user(user)
                .name(name)
                // Macros Primários
                .N(n).P2O5(p).K2O(k)
                // Macros Secundários
                .Ca(ca).Mg(mg).S(s)
                // Micronutrientes
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                // Índices
                .indiceSalino(indiceSalino)
                .indiceAcidez(indiceAcidez)
                .build();

        simpleRepository.save(model);
        log.info("➕ Adubo Simples carregado: {}", name);
    }

    // ==========================================
    // 2. ADUBOS FORMULADOS (Exemplos Genéricos)
    // ==========================================
    private void loadFormulatedMineral(UserModel user) {
        if (formulatedRepository.count() > 0) return;

        createFormulated(user, 4, 14, 8, 101);
        createFormulated(user, 10, 10, 10, 201);
        createFormulated(user, 20, 5, 20, 301);
    }

    private void createFormulated(UserModel user, int n, int p, int k, int formulaNumber) {
        Formulate formulate = new Formulate();
        formulate.setN(n); formulate.setP(p); formulate.setK(k);

        NPKrelation relation = new NPKrelation();
        relation.setN(1.0); relation.setP(p > 0 ? p/n : 0); relation.setK(k > 0 ? k/n : 0);

        FormulatedMineralFertilizerModel model = FormulatedMineralFertilizerModel.builder()
                .user(user)
                .formulate(formulate)
                .relation(relation)
                .indicatedFormulaNumber(formulaNumber)
                .N(n).P2O5(p).K2O(k)
                // Valores zerados para o resto
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .build();

        formulatedRepository.save(model);
    }

    // ==========================================
    // 3. ADUBOS ORGANOMINERAIS
    // ==========================================
    private void loadOrganoMineral(UserModel user) {
        if (organoRepository.count() > 0) return;

        OrganoMineralFertilizerModel model = OrganoMineralFertilizerModel.builder()
                .user(user)
                .name("Organomineral Aves Granulado")
                .C(8.0)
                .N(6.0).P2O5(12.0).K2O(6.0)
                .Ca(2.0).Mg(1.0).S(1.0)
                .B(0.1).Cu(0.0).Fe(0.5).Mn(0.0).Mo(0.0).Zn(0.2)
                .indiceSalino(15.0).indiceAcidez(0.0)
                .build();
        organoRepository.save(model);
    }

    // ==========================================
    // 4. ADUBOS VERDES
    // ==========================================
    private void loadGreen(UserModel user) {
        if (greenRepository.count() > 0) return;

        createGreen(user, "Mucuna Preta", 40.0, 2.5);
        createGreen(user, "Crotalária Juncea", 42.0, 2.0);
    }

    private void createGreen(UserModel user, String name, double carbono, double nitrogenio) {
        GreenFertilizerModel model = GreenFertilizerModel.builder()
                .user(user)
                .name(name)
                .C(carbono)
                .N(nitrogenio)
                .P2O5(0.5).K2O(1.5)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .indiceSalino(0.0).indiceAcidez(0.0)
                .build();
        greenRepository.save(model);
    }

    // ==========================================
    // 5. ADUBOS FOLIARES (Mineral)
    // ==========================================
    private void loadFoliarMineral(UserModel user) {
        if (foliarMineralRepository.count() > 0) return;

        MineralFertilizerModel model = MineralFertilizerModel.builder()
                .user(user)
                .name("Foliar Nitro Full")
                .N(30.0).P2O5(0.0).K2O(0.0)
                .Ca(0.0).Mg(0.0).S(2.0)
                .B(0.5).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.1).Zn(1.0)
                .indiceSalino(10.0).indiceAcidez(5.0)
                .build();
        foliarMineralRepository.save(model);
    }

    // ==========================================
    // 6. ADUBOS QUELATADOS
    // ==========================================
    private void loadChelated(UserModel user) {
        if (chelatedRepository.count() > 0) return;

        ChelatedFertilizerModel model = ChelatedFertilizerModel.builder()
                .user(user)
                .name("Quelato de Ferro EDTA 13%")
                .N(0.0).P2O5(0.0).K2O(0.0)
                .Fe(13.0)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .indiceSalino(0.0).indiceAcidez(0.0)
                .build();
        chelatedRepository.save(model);
    }

    // ==========================================
    // 7. BIOFERTILIZANTES
    // ==========================================
    private void loadBio(UserModel user) {
        if (bioRepository.count() > 0) return;

        BioFertilizerModel model = BioFertilizerModel.builder()
                .user(user)
                .name("Biofertilizante Líquido Super")
                .N(2.0).P2O5(1.0).K2O(1.5)
                .Ca(0.5).Mg(0.2).S(0.3)
                .B(0.05).Cu(0.01).Fe(0.1).Mn(0.02).Mo(0.0).Zn(0.05)
                .indiceSalino(5.0).indiceAcidez(6.5)
                .build();
        bioRepository.save(model);
    }
}