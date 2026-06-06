package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.*;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Profile("!test")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String ADMIN_USER = "admin@fertintelligence.com";
    private static final String SUPREME_USERNAME = "G&MSupremos";
    private static final String SUPREME_EMAIL = "miguel_macedo18@hotmail.com";
    private static final String SUPREME_CPF = "13600319442";

    @Override
    public void run(String... args) throws Exception {
        createSystemUserIfNotFound();
        createSupremeUserIfNotFound();
    }

    private void createSystemUserIfNotFound() {
        try {
            userService.getUser(ADMIN_USER);
            System.out.println("UserDataSeeder: Usuário admin já existe.");
        } catch (Exception e) {
            System.out.println("UserDataSeeder: Criando usuário admin...");

            UserCreateRequestDto adminUser = UserCreateRequestDto.builder()
                    .name("Administrador do Sistema")
                    .username(ADMIN_USER)
                    .email(ADMIN_USER)
                    .password("admin123")
                    .cpf("00000000000")
                    .profissao("System Admin")
                    .datanasc(new DataNasc(1, 1, 2000))
                    .genero(Genero.OUTRO)
                    .telefone(new Telefone("55", "11", "999999999"))
                    .formacao(Formacao.DOUTORADO)
                    .cargo(Cargo.PROPRIETARIO)
                    .build();

            UserCreateRequestDto gilvanUser = UserCreateRequestDto.builder()
                    .name("Gilvan Barbosa Ferreira")
                    .username("gilvan@email.com")
                    .email("gilvan@email.com")
                    .password("gilvan123")
                    .cpf("11111111111")
                    .profissao("Engenheiro Agrônomo")
                    .datanasc(new DataNasc(30, 05, 1972))
                    .genero(Genero.MASCULINO)
                    .telefone(new Telefone("55", "83", "991070613"))
                    .formacao(Formacao.DOUTORADO)
                    .cargo(Cargo.GERENTE)
                    .build();

            UserCreateRequestDto miguelUser = UserCreateRequestDto.builder()
                    .name("Miguel Macedo Ferreira")
                    .username("miguel@email.com")
                    .email("miguel@email.com")
                    .password("miguel123")
                    .cpf("22222222222")
                    .profissao("Engenheiro de Software")
                    .datanasc(new DataNasc(8, 5, 2001))
                    .genero(Genero.MASCULINO)
                    .telefone(new Telefone("55", "83", "991214231"))
                    .formacao(Formacao.GRADUACAO)
                    .cargo(Cargo.AGRONOMO_RESIDENTE)
                    .build();

            UserCreateRequestDto mateusUser = UserCreateRequestDto.builder()
                    .name("Mateus Macedo Ferreira")
                    .username("mateus@email.com")
                    .email("mateus@email.com")
                    .password("mateus123")
                    .cpf("33333333333")
                    .profissao("Engenheiro de Software")
                    .datanasc(new DataNasc(3, 11, 2002))
                    .genero(Genero.MASCULINO)
                    .telefone(new Telefone("55", "83", "993810404"))
                    .formacao(Formacao.GRADUACAO)
                    .cargo(Cargo.AGRONOMO_CONSULTOR)
                    .build();

            UserCreateRequestDto marcosUser = UserCreateRequestDto.builder()
                    .name("Marcos Macedo Ferreira")
                    .username("marcos@email.com")
                    .email("marcos@email.com")
                    .password("marcos123")
                    .cpf("44444444444")
                    .profissao("Engenheiro de Finanças")
                    .datanasc(new DataNasc(25, 4, 2005))
                    .genero(Genero.MASCULINO)
                    .telefone(new Telefone("55", "83", "987071130"))
                    .formacao(Formacao.ENSINO_MEDIO)
                    .cargo(Cargo.SECRETARIO)
                    .build();

            UserCreateRequestDto rebecaUser = UserCreateRequestDto.builder()
                    .name("Rebeca dos Santos Ferreira")
                    .username("rebeca@email.com")
                    .email("rebeca@email.com")
                    .password("rebeca123")
                    .cpf("55555555555")
                    .profissao("Médica & Veterinária")
                    .datanasc(new DataNasc(24, 11, 2014))
                    .genero(Genero.FEMININO)
                    .telefone(new Telefone("55", "83", "991518440"))
                    .formacao(Formacao.ENSINO_MEDIO)
                    .cargo(Cargo.SUPERVISOR_DE_AREA)
                    .build();

            try {
                userService.createUser(adminUser);
                System.out.println("UserDataSeeder: Admin criado com sucesso.");
            } catch (Exception ex) {
                System.err.println("Erro ao criar admin: " + ex.getMessage());
            }

            try {
                System.out.println("UserDataSeeder: Criando usuários de Gilvan e filhos...");
                userService.createUser(gilvanUser);
                System.out.println("UserDataSeeder: Usuário de Gilvan criado com sucesso.");
                userService.createUser(miguelUser);
                System.out.println("UserDataSeeder: Usuário de Miguel criado com sucesso.");
                userService.createUser(mateusUser);
                System.out.println("UserDataSeeder: Usuário de Mateus criado com sucesso.");
                userService.createUser(marcosUser);
                System.out.println("UserDataSeeder: Usuário de Marcos criado com sucesso.");
                userService.createUser(rebecaUser);
                System.out.println("UserDataSeeder: Usuário de Rebeca criado com sucesso.");
            } catch (Exception gm) {
                System.err.println("Erro ao criar Usuários de Gilvan e filhos: " + gm.getMessage());
            }
        }
    }

    private void createSupremeUserIfNotFound() {
        if (userRepository.existsByCargo(Cargo.USUARIO_SUPREMO)
                || userRepository.existsByUsername(SUPREME_USERNAME)
                || userRepository.existsByEmail(SUPREME_EMAIL)
                || userRepository.existsByCpf(SUPREME_CPF)) {
            System.out.println("UserDataSeeder: Usuário supremo já existe.");
            return;
        }

        System.out.println("UserDataSeeder: Criando usuário supremo...");

        UserModel supremeUser = UserModel.builder()
                .name("Gilvan e Miguel")
                .username(SUPREME_USERNAME)
                .email(SUPREME_EMAIL)
                .password(passwordEncoder.encode("miniprojetofosforico"))
                .cpf(SUPREME_CPF)
                .profissao("Engenheiro de Software")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO)
                .telefone(new Telefone("55", "83", "991214231"))
                .formacao(Formacao.GRADUACAO)
                .cargo(Cargo.USUARIO_SUPREMO)
                .build();

        userRepository.save(supremeUser);
        System.out.println("UserDataSeeder: Usuário supremo criado com sucesso.");
    }
}
