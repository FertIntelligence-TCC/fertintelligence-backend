package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FertilizerDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final SimpleMineralFertilizerRepository simpleRepository;
    private final FormulatedMineralFertilizerRepository formulatedRepository;
    private final OrganoMineralFertilizerRepository organoRepository;
    private final GreenFertilizerRepository greenRepository;
    private final OrganicFertilizerRepository organicRepository;

    private final MineralFertilizerRepository foliarMineralRepository;
    private final ChelatedFertilizerRepository chelatedRepository;
    private final BioFertilizerRepository bioRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🧪 Iniciando o seeding de fertilizantes fictícios...");

        Optional<UserModel> creatorOpt = userRepository.findAll().stream()
                .filter(u -> u.getCargo() == Cargo.USUARIO_SUPREMO)
                .findFirst();
        if (creatorOpt.isEmpty()) {
            log.warn("⚠️ Usuário com cargo USUARIO_SUPREMO não encontrado. Seeder de fertilizantes será encerrado.");
            return;
        }
        UserModel creator = creatorOpt.get();
        log.info("✅ Usuário supremo encontrado: {} (email: {})", creator.getUsername(), creator.getEmail());

        loadSimpleMineral(creator);
        loadFormulatedMineral(creator);
        loadOrganoMineral(creator);
        loadGreen(creator);
        loadOrganic(creator);
        loadFoliarMineral(creator);
        loadChelated(creator);
        loadBio(creator);

        log.info("✅ Seeding de fertilizantes concluído.");
    }

    private void loadSimpleMineral(UserModel user) {
        createSimpleIfMissing(user, "Ureia", 45.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 75.0, -840.0);
        createSimpleIfMissing(user, "Sulfato de Amônio", 21.0, 0.0, 0.0, 0.0, 0.0, 24.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 69.0, -996.0);
        createSimpleIfMissing(user, "Superfosfato Simples", 0.0, 18.0, 0.0, 19.0, 0.0, 12.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 0.0);
        createSimpleIfMissing(user, "Superfosfato Triplo", 0.0, 45.0, 0.0, 13.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 0.0);
        createSimpleIfMissing(user, "MAP (Fosfato Monoamônico)", 11.0, 50.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 30.0, -635.0);
        createSimpleIfMissing(user, "Cloreto de Potássio", 0.0, 0.0, 60.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 116.0, 0.0);
    }

    private void createSimpleIfMissing(UserModel user, String name, double n, double p, double k,
                                       double ca, double mg, double s,
                                       double b, double cu, double fe, double mn, double mo, double zn,
                                       double indiceSalino, double indiceAcidez) {
        if (simpleRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        SimpleMineralFertilizerModel model = SimpleMineralFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .N(n).P2O5(p).K2O(k)
                .Ca(ca).Mg(mg).S(s)
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                .indiceSalino(indiceSalino)
                .indiceAcidez(indiceAcidez)
                .build();

        simpleRepository.save(model);
        log.info("➕ Adubo mineral simples carregado: {}", name);
    }

    private void loadFormulatedMineral(UserModel user) {
        createFormulatedIfMissing(user, 4, 14, 8, 101);
        createFormulatedIfMissing(user, 10, 10, 10, 201);
        createFormulatedIfMissing(user, 20, 5, 20, 301);
    }

    private void createFormulatedIfMissing(UserModel user, int n, int p, int k, int formulaNumber) {
        if (formulatedRepository.findAll().stream()
                .anyMatch(f -> f.getFormulate() != null
                        && f.getFormulate().getN() == n
                        && f.getFormulate().getP() == p
                        && f.getFormulate().getK() == k)) {
            return;
        }

        Formulate formulate = new Formulate();
        formulate.setN(n);
        formulate.setP(p);
        formulate.setK(k);

        NPKrelation relation = FormulatedMineralFertilizerModel.calculateRelation(formulate);

        FormulatedMineralFertilizerModel model = FormulatedMineralFertilizerModel.builder()
                .user(user)
                .publico(true)
                .formulate(formulate)
                .relation(relation)
                .indicatedFormulaNumber(formulaNumber)
                .N(n).P2O5(p).K2O(k)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .build();

        formulatedRepository.save(model);
        log.info("➕ Adubo formulado NPK {}-{}-{} carregado", n, p, k);
    }

    private void loadOrganoMineral(UserModel user) {
        createOrganoIfMissing(user, "Organomineral 05-10-10", 12.0, 5.0, 10.0, 10.0, 3.0, 1.5, 1.2, 0.05, 0.03, 0.30, 0.08, 0.01, 0.12, 12.0, -20.0);
    }

    private void createOrganoIfMissing(UserModel user, String name, double c, double n, double p2o5, double k2o,
                                       double ca, double mg, double s, double b, double cu, double fe,
                                       double mn, double mo, double zn, double indiceSalino, double indiceAcidez) {
        if (organoRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        OrganoMineralFertilizerModel model = OrganoMineralFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .C(c)
                .N(n).P2O5(p2o5).K2O(k2o)
                .Ca(ca).Mg(mg).S(s)
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                .indiceSalino(indiceSalino).indiceAcidez(indiceAcidez)
                .build();
        organoRepository.save(model);
        log.info("➕ Adubo organomineral carregado: {}", name);
    }

    private void loadGreen(UserModel user) {
        createGreenIfMissing(user, "Crotalária Juncea", 42.0, 2.5, 0.5, 1.8);
    }

    private void createGreenIfMissing(UserModel user, String name, double carbono, double nitrogenio, double p2o5, double k2o) {
        if (greenRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        GreenFertilizerModel model = GreenFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .C(carbono)
                .N(nitrogenio)
                .P2O5(p2o5).K2O(k2o)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .build();
        greenRepository.save(model);
        log.info("➕ Adubo verde carregado: {}", name);
    }

    private void loadOrganic(UserModel user) {
        createOrganicIfMissing(user, "Composto Orgânico", 25.0, 1.5, 1.0, 1.2, 35.0, 20.0);
    }

    private void createOrganicIfMissing(UserModel user, String name, double carbono, double nitrogenio, double p2o5,
                                        double k2o, double teorUmidade, double teorCinzas) {
        if (organicRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        OrganicFertilizerModel model = OrganicFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .C(carbono)
                .N(nitrogenio)
                .P2O5(p2o5).K2O(k2o)
                .Ca(0.0).Mg(0.0).S(0.0)
                .B(0.0).Cu(0.0).Fe(0.0).Mn(0.0).Mo(0.0).Zn(0.0)
                .teorUmidade(teorUmidade)
                .teorCinzas(teorCinzas)
                .build();
        organicRepository.save(model);
        log.info("➕ Adubo orgânico carregado: {}", name);
    }

    private void loadFoliarMineral(UserModel user) {
        createFoliarMineralIfMissing(user, "Foliar Boro 10%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 6.0);
        createFoliarMineralIfMissing(user, "Foliar Zinco 10%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 2.0, 6.0);
        createFoliarMineralIfMissing(user, "Foliar Manganês 12%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 12.0, 0.0, 0.0, 2.0, 6.0);
    }

    private void createFoliarMineralIfMissing(UserModel user, String name, double n, double p2o5, double k2o,
                                              double ca, double mg, double s, double b, double cu, double fe,
                                              double mn, double mo, double zn, double indiceSalino, double indiceAcidez) {
        if (foliarMineralRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        MineralFertilizerModel model = MineralFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .N(n).P2O5(p2o5).K2O(k2o)
                .Ca(ca).Mg(mg).S(s)
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                .indiceSalino(indiceSalino).indiceAcidez(indiceAcidez)
                .build();
        foliarMineralRepository.save(model);
        log.info("➕ Adubo foliar mineral carregado: {}", name);
    }

    private void loadChelated(UserModel user) {
        createChelatedIfMissing(user, "Quelato de Zinco EDTA 14%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 14.0, 0.0, 6.5);
        createChelatedIfMissing(user, "Quelato de Manganês EDTA 13%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 13.0, 0.0, 0.0, 0.0, 6.5);
        createChelatedIfMissing(user, "Quelato de Ferro EDTA 13%", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 13.0, 0.0, 0.0, 0.0, 0.0, 6.5);
    }

    private void createChelatedIfMissing(UserModel user, String name, double n, double p2o5, double k2o,
                                         double ca, double mg, double s, double b, double cu, double fe,
                                         double mn, double mo, double zn, double indiceSalino, double indiceAcidez) {
        if (chelatedRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        ChelatedFertilizerModel model = ChelatedFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .N(n).P2O5(p2o5).K2O(k2o)
                .Ca(ca).Mg(mg).S(s)
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                .indiceSalino(indiceSalino).indiceAcidez(indiceAcidez)
                .build();
        chelatedRepository.save(model);
        log.info("➕ Adubo quelatado carregado: {}", name);
    }

    private void loadBio(UserModel user) {
        createBioIfMissing(user, "Biofertilizante foliar genérico", 2.0, 1.0, 1.5, 0.5, 0.3, 0.2, 0.05, 0.01, 0.08, 0.03, 0.01, 0.05, 3.0, 6.0);
    }

    private void createBioIfMissing(UserModel user, String name, double n, double p2o5, double k2o,
                                    double ca, double mg, double s, double b, double cu, double fe,
                                    double mn, double mo, double zn, double indiceSalino, double indiceAcidez) {
        if (bioRepository.findAll().stream().anyMatch(f -> isEquivalentName(f.getName(), name))) return;

        BioFertilizerModel model = BioFertilizerModel.builder()
                .user(user)
                .publico(true)
                .name(name)
                .N(n).P2O5(p2o5).K2O(k2o)
                .Ca(ca).Mg(mg).S(s)
                .B(b).Cu(cu).Fe(fe).Mn(mn).Mo(mo).Zn(zn)
                .indiceSalino(indiceSalino).indiceAcidez(indiceAcidez)
                .build();
        bioRepository.save(model);
        log.info("➕ Biofertilizante carregado: {}", name);
    }

    private boolean isEquivalentName(String currentName, String expectedName) {
        return currentName != null && currentName.trim().equalsIgnoreCase(expectedName.trim());
    }
}
