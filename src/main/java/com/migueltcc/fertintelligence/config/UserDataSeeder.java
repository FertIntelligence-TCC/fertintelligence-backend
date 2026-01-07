package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.*;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // <--- GARANTE QUE RODE PRIMEIRO
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    public static final String ADMIN_USER = "admin@fertintelligence.com";

    @Override
    public void run(String... args) throws Exception {
        createSystemUserIfNotFound();
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
            try {
                userService.createUser(adminUser);
                System.out.println("UserDataSeeder: Admin criado com sucesso.");
            } catch (Exception ex) {
                System.err.println("Erro ao criar admin: " + ex.getMessage());
            }
        }
    }
}