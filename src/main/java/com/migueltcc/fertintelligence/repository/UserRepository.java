package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserModel> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByCargo(Cargo cargo);
    List<UserModel> findAllByCargo(Cargo cargo);

}
