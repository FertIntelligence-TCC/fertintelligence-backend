package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<PropertyModel, Long> {

    Optional<PropertyModel> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    Optional<PropertyModel> findByNome(String nome);
    List<PropertyModel> findAllByOwner(UserModel owner);
    List<PropertyModel> findByNomeContainingIgnoreCase(String nome);

}