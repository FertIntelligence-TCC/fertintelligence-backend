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
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UserDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Usuário admin legado.
     * Mantido apenas para encontrar e migrar o registro antigo no banco,
     * sem trocar o ID.
     */
    private static final String LEGACY_ADMIN_USERNAME = "admin@fertintelligence.com";

    /*
     * Usuário administrador supremo atual do sistema.
     * Este login deve pertencer ao cargo USUARIO_SUPREMO.
     */
    private static final String SUPREME_ADMIN_USERNAME = "AdminUser";
    private static final String SUPREME_ADMIN_PASSWORD = "admin123";
    private static final String SUPREME_ADMIN_EMAIL = "miguel_macedo18@hotmail.com";
    private static final String SUPREME_ADMIN_CPF = "13600319442";

    /*
     * Usuária Eliane.
     * Ocupa o antigo registro do admin padrão/proprietário,
     * preservando o ID existente no banco.
     */
    private static final String ELIANE_USERNAME = "eliane@email.com";
    private static final String ELIANE_EMAIL = "eliane@email.com";
    private static final String ELIANE_PASSWORD = "eliane123";
    private static final String ELIANE_CPF = "00000000000";

    private static final String MIGUEL_USERNAME = "miguel@email.com";
    private static final String MIGUEL_EMAIL = "miguel@email.com";
    private static final String MIGUEL_CPF = "22222222222";

    public UserDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedDefaultUsers();
        seedSupremeAdminUser();
    }

    private void seedDefaultUsers() {
        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
                .name("Eliane")
                .username(ELIANE_USERNAME)
                .email(ELIANE_EMAIL)
                .password(ELIANE_PASSWORD)
                .cpf(ELIANE_CPF)
                .profissao("Odontóloga")
                .datanasc(new DataNasc(30, 5, 1981))
                .genero(Genero.OUTRO)
                .telefone(new Telefone("55", "11", "999999999"))
                .formacao(Formacao.GRADUACAO)
                .cargo(Cargo.PROPRIETARIO)
                .build());

        createOrUpdateDefaultUser(UserCreateRequestDto.builder()
                .name("Gilvan Barbosa Ferreira")
                .username("gilvan@email.com")
                .email("gilvan@email.com")
                .password("gilvan123")
                .cpf("11111111111")
                .profissao("Engenheiro Agrônomo")
                .datanasc(new DataNasc(30, 5, 1972))
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

    private void seedSupremeAdminUser() {
        normalizeMiguelIfHeWasAccidentallySupreme();

        Optional<UserModel> existingSupremeAdmin = findExistingSupremeAdminUser();
        if (existingSupremeAdmin.isPresent()) {
            UserModel supremeAdmin = existingSupremeAdmin.get();
            applySupremeAdminFields(supremeAdmin);
            userRepository.save(supremeAdmin);
            System.out.println("UserDataSeeder: Usuário admin supremo atualizado/verificado.");
            return;
        }

        UserModel supremeAdmin = UserModel.builder()
                .name("Gilvan e Miguel")
                .username(SUPREME_ADMIN_USERNAME)
                .email(SUPREME_ADMIN_EMAIL)
                .password(passwordEncoder.encode(SUPREME_ADMIN_PASSWORD))
                .cpf(SUPREME_ADMIN_CPF)
                .profissao("Engenheiro de Software")
                .datanasc(new DataNasc(8, 5, 2001))
                .genero(Genero.MASCULINO)
                .telefone(new Telefone("55", "83", "991214231"))
                .formacao(Formacao.GRADUACAO)
                .cargo(Cargo.USUARIO_SUPREMO)
                .build();

        userRepository.save(supremeAdmin);
        System.out.println("UserDataSeeder: Usuário admin supremo criado com sucesso.");
    }

    private Optional<UserModel> findExistingSupremeAdminUser() {
        return userRepository.findByUsername(SUPREME_ADMIN_USERNAME)
                .or(() -> userRepository.findByUsername(LEGACY_ADMIN_USERNAME))
                .or(() -> findExistingUser(SUPREME_ADMIN_USERNAME, SUPREME_ADMIN_EMAIL, SUPREME_ADMIN_CPF));
    }

    private void createOrUpdateDefaultUser(UserCreateRequestDto dto) {
        Optional<UserModel> existingUser = isElianeSeed(dto)
                ? findExistingElianeOrLegacyAdminUser(dto)
                : findExistingUser(dto.getUsername(), dto.getEmail(), dto.getCpf());

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

    private boolean isElianeSeed(UserCreateRequestDto dto) {
        return ELIANE_CPF.equals(dto.getCpf())
                || ELIANE_USERNAME.equals(dto.getUsername())
                || ELIANE_EMAIL.equals(dto.getEmail());
    }

    private Optional<UserModel> findExistingElianeOrLegacyAdminUser(UserCreateRequestDto dto) {
        return userRepository.findByUsername(ELIANE_USERNAME)
                .or(() -> userRepository.findByEmail(ELIANE_EMAIL))
                .or(() -> userRepository.findByCpf(ELIANE_CPF))
                .or(() -> userRepository.findByUsername(LEGACY_ADMIN_USERNAME))
                .or(() -> findExistingUser(dto.getUsername(), dto.getEmail(), dto.getCpf()));
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
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    private void applySupremeAdminFields(UserModel user) {
        user.setName("Gilvan e Miguel");
        user.setUsername(SUPREME_ADMIN_USERNAME);
        user.setEmail(SUPREME_ADMIN_EMAIL);
        user.setCpf(SUPREME_ADMIN_CPF);
        user.setProfissao("Engenheiro de Software");
        user.setDatanasc(new DataNasc(8, 5, 2001));
        user.setGenero(Genero.MASCULINO);
        user.setTelefone(new Telefone("55", "83", "991214231"));
        user.setFormacao(Formacao.GRADUACAO);
        user.setCargo(Cargo.USUARIO_SUPREMO);
        user.setPassword(passwordEncoder.encode(SUPREME_ADMIN_PASSWORD));
    }

    private void normalizeMiguelIfHeWasAccidentallySupreme() {
        userRepository.findAllByCargo(Cargo.USUARIO_SUPREMO).stream()
                .filter(user -> isMiguelDefaultUser(user) && !SUPREME_ADMIN_USERNAME.equals(user.getUsername()))
                .forEach(user -> {
                    user.setUsername(MIGUEL_USERNAME);
                    user.setEmail(MIGUEL_EMAIL);
                    user.setCpf(MIGUEL_CPF);
                    user.setCargo(Cargo.AGRONOMO_RESIDENTE);
                    user.setPassword(passwordEncoder.encode("miguel123"));
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
