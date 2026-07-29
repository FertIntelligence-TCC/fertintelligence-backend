package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.ActiveCargoUpdateResponseDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import com.migueltcc.fertintelligence.service.documentation.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public String createUser(UserCreateRequestDto userDTO) {
        rejectSupremeUserCargo(userDTO.getCargo());
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Name already exists!");
        }
        UserModel user = UserModel.builder()
                .username(userDTO.getUsername())
                .cpf(userDTO.getCpf())
                .email(userDTO.getEmail())
                .datanasc(userDTO.getDatanasc())
                .genero(userDTO.getGenero())
                .telefone(userDTO.getTelefone())
                .formacao(userDTO.getFormacao())
                .profissao(userDTO.getProfissao())
                .cargo(userDTO.getCargo())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .name(userDTO.getName())
                .idfoto(userDTO.getIdfoto())
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    @Transactional
    public String updateUser(String Username, UserPostRequestDto userDTO) {
        UserModel user = userRepository.findByUsername(Username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + Username));
        if (userDTO.getCargo() != null) {
            throw new IllegalArgumentException(
                    "O cargo não pode ser alterado pela edição de perfil. Use o endpoint de cargo ativo.");
        }
        user.setName(userDTO.getNome() == null ? user.getName() : userDTO.getNome());
        user.setUsername(userDTO.getUsername() == null ? user.getUsername() : userDTO.getUsername());
        user.setCpf(userDTO.getCpf() == null ? user.getCpf() : userDTO.getCpf());
        user.setEmail(userDTO.getEmail() == null ? user.getEmail() : userDTO.getEmail());
        user.setDatanasc(userDTO.getDatanasc() == null ? user.getDatanasc() : userDTO.getDatanasc());
        user.setGenero(userDTO.getGenero() == null ? user.getGenero() : userDTO.getGenero());
        user.setTelefone(userDTO.getTelefone() == null ? user.getTelefone() : userDTO.getTelefone());
        user.setFormacao(userDTO.getFormacao() == null ? user.getFormacao() : userDTO.getFormacao());
        user.setProfissao(userDTO.getProfissao() == null ? user.getProfissao() : userDTO.getProfissao());
        user.setPassword(userDTO.getPassword() == null ? user.getPassword() : passwordEncoder.encode(userDTO.getPassword()));
        user.setIdfoto(userDTO.getIdfoto() == null ? user.getIdfoto() : userDTO.getIdfoto());

        userRepository.save(user);
        return "User updated successfully!";
    }

    @Override
    @Transactional
    public ActiveCargoUpdateResponseDto updateActiveCargo(String userName, Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo ativo é obrigatório.");
        }
        rejectSupremeUserCargo(cargo);

        UserModel user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));
        if (user.getCargo() == Cargo.USUARIO_SUPREMO) {
            throw new IllegalArgumentException("O cargo USUARIO_SUPREMO é reservado e não pode ser alterado.");
        }

        user.setCargo(cargo);
        userRepository.save(user);
        return ActiveCargoUpdateResponseDto.builder()
                .cargo(cargo)
                .token(jwtService.generateToken(user.getUsername(), cargo))
                .build();
    }

    @Override
    @Transactional
    public String deleteUser(String userName) {
        UserModel user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        userRepository.delete(user);
        return "User deleted successfully!";
    }

    @Override
    public UserResponseDto getUser(String userName) {
        UserModel user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        return user.toDto();
    }

    private void rejectSupremeUserCargo(Cargo cargo) {
        if (cargo == Cargo.USUARIO_SUPREMO) {
            throw new IllegalArgumentException("Cargo USUARIO_SUPREMO is reserved for system bootstrap.");
        }
    }

}
