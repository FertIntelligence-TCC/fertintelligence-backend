package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String createUser(UserCreateRequestDto userDTO) {
        if (userRepository.existsByNome(userDTO.getNome())) {
            throw new RuntimeException("Name already exists!");
        }
        UserModel user = UserModel.builder()
                .nome(userDTO.getNome())
                .cpf(userDTO.getCpf())
                .email(userDTO.getEmail())
                .datanasc(userDTO.getDatanasc())
                .genero(userDTO.getGenero())
                .telefone(userDTO.getTelefone())
                .formacao(userDTO.getFormacao())
                .profissao(userDTO.getProfissao())
                .cargo(userDTO.getCargo())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    @Transactional
    public String postUser(String Username, UserPostRequestDto userDTO) {
        UserModel user = userRepository.findByNome(Username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + Username));
        user.setNome(userDTO.getNome() == null ? user.getNome() : userDTO.getNome());
        user.setCpf(userDTO.getCpf() == null ? user.getCpf() : userDTO.getCpf());
        user.setEmail(userDTO.getEmail() == null ? user.getEmail() : userDTO.getEmail());
        user.setDatanasc(userDTO.getDatanasc() == null ? user.getDatanasc() : userDTO.getDatanasc());
        user.setGenero(userDTO.getGenero() == null ? user.getGenero() : userDTO.getGenero());
        user.setTelefone(userDTO.getTelefone() == null ? user.getTelefone() : userDTO.getTelefone());
        user.setFormacao(userDTO.getFormacao() == null ? user.getFormacao() : userDTO.getFormacao());
        user.setProfissao(userDTO.getProfissao() == null ? user.getProfissao() : userDTO.getProfissao());
        user.setCargo(userDTO.getCargo() == null ? user.getCargo() : userDTO.getCargo());
        user.setPassword(userDTO.getPassword() == null ? user.getPassword() : passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return "User updated successfully!";
    }

    @Override
    @Transactional
    public String deleteUser(String userName) {
        UserModel user = userRepository.findByNome(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        userRepository.delete(user);
        return "User deleted successfully!";
    }

    @Override
    public UserResponseDto getUser(String userName) {
        UserModel user = userRepository.findByNome(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        return user.toDto();
    }

}