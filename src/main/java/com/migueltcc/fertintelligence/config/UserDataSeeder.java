package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.*;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Order(1)
@Profile("!test")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UserDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static final String ADMIN_USER = "admin@fertintelligence.com";
    private static final String MIGUEL_USERNAME = "miguel@email.com";
    private static final String MIGUEL_EMAIL = "miguel@email.com";
    private static final String MIGUEL_CPF = "22222222222";
    private static final String SUPREME_USERNAME = "G&MSupremos";
    private static final String SUPREME_EMAIL = "miguel_macedo18@hotmail.com";
    private static final String SUPREME_CPF = "13600319442";

    public UserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedDefaultUsers();
        seedSupremeUser();
    }

    private void seedDefaultUsers() {
        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
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
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
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
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
                .name("Miguel Macedo Ferreira")
                .username(MIGUEL_USERNAME)
                .email(MIGUEL_EMAIL)
                .password("miguel123")
                .cpf(MIGUEL_CPF)
                .profissao("Engenheiro de Software")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO)
                .telefone(new Telefone("55", "83", "991214231"))
                .formacao(Formacao.GRADUACAO)
                .cargo(Cargo.AGRONOMO_RESIDENTE)
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
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
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
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
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
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
                .build());
    }

    private void seedSupremeUser() {
        normalizeDefaultSupremeUsers();

        Optional<UserModel> existingSupreme = findExistingUser(SUPREME_USERNAME, SUPREME_EMAIL, SUPREME_CPF);
        if (existingSupreme.isPresent()) {
            UserModel supremeUser = existingSupreme.get();
            applySupremeUserFields(supremeUser);
            userRepository.save(supremeUser);
            System.out.println("UserDataSeeder: Usuário supremo atualizado/verificado.");
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

    private void createOrUpdateDefaultUser(UserCreateRequestDto dto) {
        Optional<UserModel> existingUser = findExistingUser(dto.getUsername(), dto.getEmail(), dto.getCpf());

        if (existingUser.isPresent()) {
            UserModel user = existingUser.get();
            applyDefaultUserFields(user, dto);
            userRepository.save(user);
            System.out.println("UserDataSeeder: Usuário padrão atualizado/verificado: " + dto.getUsername());
            return;
        }

        UserModel user = UserModel.builder()
                .username(dto.getUsername())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .datanasc(dto.getDatanasc())
                .genero(dto.getGenero())
                .telefone(dto.getTelefone())
                .formacao(dto.getFormacao())
                .profissao(dto.getProfissao())
                .cargo(dto.getCargo())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .idfoto(dto.getIdfoto())
                .build();

        userRepository.save(user);
        System.out.println("UserDataSeeder: Usuário padrão criado: " + dto.getUsername());
    }

    private Optional<UserModel> findExistingUser(String username, String email, String cpf) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(email))
                .or(() -> userRepository.findByCpf(cpf));
    }

    private void applyDefaultUserFields(UserModel user, UserCreateRequestDto dto) {
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setDatanasc(dto.getDatanasc());
        user.setGenero(dto.getGenero());
        user.setTelefone(dto.getTelefone());
        user.setFormacao(dto.getFormacao());
        user.setProfissao(dto.getProfissao());
        user.setCargo(dto.getCargo());
        user.setIdfoto(dto.getIdfoto());
    }

    private void applySupremeUserFields(UserModel user) {
        user.setName("Gilvan e Miguel");
        user.setUsername(SUPREME_USERNAME);
        user.setEmail(SUPREME_EMAIL);
        user.setCpf(SUPREME_CPF);
        user.setProfissao("Engenheiro de Software");
        user.setDatanasc(new DataNasc(8, 5, 2001));
        user.setGenero(Genero.MASCULINO);
        user.setTelefone(new Telefone("55", "83", "991214231"));
        user.setFormacao(Formacao.GRADUACAO);
        user.setCargo(Cargo.USUARIO_SUPREMO);
    }

    private void normalizeDefaultSupremeUsers() {
        userRepository.findAllByCargo(Cargo.USUARIO_SUPREMO).stream()
                .filter(user -> isMiguelDefaultUser(user) && !SUPREME_USERNAME.equals(user.getUsername()))
                .forEach(user -> {
                    user.setUsername(MIGUEL_USERNAME);
                    user.setEmail(MIGUEL_EMAIL);
                    user.setCpf(MIGUEL_CPF);
                    user.setCargo(Cargo.AGRONOMO_RESIDENTE);
                    userRepository.save(user);
                    System.out.println("UserDataSeeder: Miguel normalizado como AGRONOMO_RESIDENTE.");
                });
    }

    private boolean isMiguelDefaultUser(UserModel user) {
        return MIGUEL_USERNAME.equals(user.getUsername())
                || MIGUEL_EMAIL.equals(user.getEmail())
                || MIGUEL_CPF.equals(user.getCpf())
                || "Miguel Macedo Ferreira".equals(user.getName());
    }
}
