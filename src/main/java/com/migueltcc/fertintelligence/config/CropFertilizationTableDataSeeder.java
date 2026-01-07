package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeCientifico;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.SpacingType;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.TipoEsterco;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.user.DataNasc;
import com.migueltcc.fertintelligence.composedAttributes.user.Formacao;
import com.migueltcc.fertintelligence.composedAttributes.user.Genero;
import com.migueltcc.fertintelligence.composedAttributes.user.Telefone;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test") // mantém compatível com o original :contentReference[oaicite:0]{index=0}
public class CropFertilizationTableDataSeeder implements CommandLineRunner {

    private static final String SYSTEM_USER = "admin@fertintelligence.com";

    @Autowired
    private CropFertilizationTableRepository cropFertilizationTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) {
        // Proteção extra: evita rodar em testes mesmo se o profile "test" não estiver ativo corretamente
        if (isTestEnvironment()) {
            return;
        }

        if (cropFertilizationTableRepository.count() > 0) {
            System.out.println("Tabelas de adubação já presentes. Seed ignorado.");
            return;
        }

        UserModel creator = findOrCreateSystemUser();
        List<CropFertilizationTableModel> tables = buildTables(creator);
        cropFertilizationTableRepository.saveAll(tables);

        System.out.println("Seed de tabelas de adubação concluído: " + tables.size() + " registros criados.");
    }

    private boolean isTestEnvironment() {
        try {
            // 1) spring profiles ativos
            if (Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase("test"))) {
                return true;
            }
            // 2) fallback por propriedade (caso perfil não esteja setado corretamente)
            String active = System.getProperty("spring.profiles.active");
            return active != null && active.toLowerCase().contains("test");
        } catch (Exception ignored) {
            return false;
        }
    }

    private UserModel findOrCreateSystemUser() {
        return userRepository.findByUsername(SYSTEM_USER)
                .orElseGet(this::createSystemUser);
    }

    private UserModel createSystemUser() {
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

        userService.createUser(adminUser);

        return userRepository.findByUsername(SYSTEM_USER)
                .orElseThrow(() -> new EntityNotFoundException("Usuário admin não pôde ser criado."));
    }

    /**
     * Ajustado para ficar 100% compatível com a estrutura atual do CropFertilizationTableModel:
     * - NÃO existe suggested_spacing_value no Model (removido)
     * - used_spacing é SpacingType (enum), e used_spacing_value é Double
     *
     * Também foi ajustado para usar apenas culturas que estão na validação atual do serviço
     * (SOJA, MILHO, GERGELIM, SISAL, MAMONA, FEIJAO_COMUM, FEIJAO_CAUPI).
     */
    private List<CropFertilizationTableModel> buildTables(UserModel creator) {
        return List.of(
                // SOJA
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.SUL)
                        .crop_common_name(NomeComum.SOJA)
                        .crop_scientific_nome(NomeCientifico.Glycine_max)
                        .cultivares("TMG 7062 IPRO, BRS 583")
                        .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .initial_value(0.45)
                        .final_value(0.55)
                        .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .used_spacing_value(0.50)
                        .regional_productivity(3500.0)
                        .expected_productivity(4200.0)
                        .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                        .manure(TipoEsterco.BOVINO)
                        .manure_qtd(4.0)
                        .gessing(1.2)
                        .micronutrients(180.0)
                        .npk(130.0)
                        .observations("Tabela fictícia para soja no sul.")
                        .build(),

                // MILHO
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.SUL)
                        .crop_common_name(NomeComum.MILHO)
                        .crop_scientific_nome(NomeCientifico.Zea_mays)
                        .cultivares("AG 8025 PRO3, BRS 1055")
                        .suggested_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                        .initial_value(4.5)
                        .final_value(5.5)
                        .used_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                        .used_spacing_value(5.0)
                        .regional_productivity(9000.0)
                        .expected_productivity(10500.0)
                        .criteria(CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL)
                        .manure(TipoEsterco.GALINHA)
                        .manure_qtd(2.5)
                        .gessing(0.8)
                        .micronutrients(140.0)
                        .npk(200.0)
                        .observations("Tabela fictícia para milho com plantio adensado.")
                        .build(),

                // FEIJÃO-COMUM
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.NORDESTE)
                        .crop_common_name(NomeComum.FEIJAO_COMUM)
                        .crop_scientific_nome(NomeCientifico.Phaseolus_vulgaris)
                        .cultivares("Carioca, BRS Estilo")
                        .suggested_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                        .initial_value(0.25)
                        .final_value(0.35)
                        .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                        .used_spacing_value(0.30)
                        .regional_productivity(1800.0)
                        .expected_productivity(2200.0)
                        .criteria(CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL_MAIS_ELEVACAO_DO_TEOR_DE_CALCIO_MAIS_MAGNESIO)
                        .manure(TipoEsterco.BOVINO)
                        .manure_qtd(5.0)
                        .gessing(0.6)
                        .micronutrients(80.0)
                        .npk(110.0)
                        .observations("Tabela fictícia para feijão-comum com espaçamento entre plantas.")
                        .build(),

                // GERGELIM
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.SUL)
                        .crop_common_name(NomeComum.GERGELIM)
                        .crop_scientific_nome(NomeCientifico.Sesamum_indicum)
                        .cultivares("BRS Seda, CNPA G2")
                        .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .initial_value(0.40)
                        .final_value(0.50)
                        .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .used_spacing_value(0.45)
                        .regional_productivity(900.0)
                        .expected_productivity(1200.0)
                        .criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                        .manure(TipoEsterco.BOVINO)
                        .manure_qtd(3.0)
                        .gessing(0.4)
                        .micronutrients(60.0)
                        .npk(90.0)
                        .observations("Tabela fictícia para gergelim em linhas estreitas.")
                        .build(),

                // SISAL
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.NORDESTE)
                        .crop_common_name(NomeComum.SISAL)
                        .crop_scientific_nome(NomeCientifico.Agave_sisalana)
                        .cultivares("Agave 1")
                        .suggested_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                        .initial_value(0.60)
                        .final_value(0.80)
                        .used_spacing(SpacingType.BETWEEN_PLANTS_OR_HOLES_IN_METERS)
                        .used_spacing_value(0.70)
                        .regional_productivity(1500.0)
                        .expected_productivity(1800.0)
                        .criteria(CriterioCalagem.ELEVACAO_DO_TEOR_DE_CALCIO_MAIS_MAGNESIO)
                        .manure(TipoEsterco.TORTAS)
                        .manure_qtd(6.0)
                        .gessing(1.1)
                        .micronutrients(70.0)
                        .npk(115.0)
                        .observations("Tabela fictícia para sisal em região semiárida.")
                        .build(),

                // MAMONA
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.CENTRO_OESTE)
                        .crop_common_name(NomeComum.MAMONA)
                        .crop_scientific_nome(NomeCientifico.Ricinus_communis)
                        .cultivares("BRS Nordestina, Guarani")
                        .suggested_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .initial_value(0.70)
                        .final_value(0.80)
                        .used_spacing(SpacingType.BETWEEN_LINES_IN_METERS)
                        .used_spacing_value(0.75)
                        .regional_productivity(1400.0)
                        .expected_productivity(1800.0)
                        .criteria(CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL)
                        .manure(TipoEsterco.GALINHA)
                        .manure_qtd(2.8)
                        .gessing(0.7)
                        .micronutrients(85.0)
                        .npk(125.0)
                        .observations("Tabela fictícia para mamona em espaçamento reduzido.")
                        .build(),

                // FEIJÃO-CAUPI
                CropFertilizationTableModel.builder()
                        .creator(creator)
                        .region(Regiao.SUL)
                        .crop_common_name(NomeComum.FEIJAO_CAUPI)
                        .crop_scientific_nome(NomeCientifico.Vigna_unguiculata)
                        .cultivares("BRS Tumucumaque, BRS Novaera")
                        .suggested_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                        .initial_value(10.0)
                        .final_value(12.0)
                        .used_spacing(SpacingType.PLANTS_PER_LINEAR_METER)
                        .used_spacing_value(11.0)
                        .regional_productivity(2200.0)
                        .expected_productivity(2600.0)
                        .criteria(CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL_MAIS_ELEVACAO_DO_TEOR_DE_CALCIO_MAIS_MAGNESIO)
                        .manure(TipoEsterco.OVINO)
                        .manure_qtd(3.5)
                        .gessing(0.5)
                        .micronutrients(75.0)
                        .npk(115.0)
                        .observations("Tabela fictícia para feijão-caupi de alto rendimento.")
                        .build()
        );
    }
}
