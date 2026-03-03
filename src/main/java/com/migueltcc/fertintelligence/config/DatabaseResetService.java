package com.migueltcc.fertintelligence.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DatabaseResetService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String TBL_PROPERTY_ACCESS_REQUEST = "solitacoes_de_acesso_a_propriedades";
    private static final String TBL_PROPERTIES = "propriedades";

    @Value("${app.seed.allow-reset:false}")
    private boolean allowReset;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetProperties() {
        if (!allowReset) {
            // Em testes (e por padrão), não reseta nada.
            return;
        }

        try {
            entityManager.createNativeQuery("""
                TRUNCATE TABLE
                    %s,
                    %s
                RESTART IDENTITY
                CASCADE
            """.formatted(TBL_PROPERTY_ACCESS_REQUEST, TBL_PROPERTIES)).executeUpdate();

            entityManager.flush();
        } catch (Exception e) {
            entityManager.createNativeQuery("DELETE FROM " + TBL_PROPERTY_ACCESS_REQUEST).executeUpdate();
            entityManager.createNativeQuery("DELETE FROM " + TBL_PROPERTIES).executeUpdate();
            entityManager.flush();
        }
    }
}