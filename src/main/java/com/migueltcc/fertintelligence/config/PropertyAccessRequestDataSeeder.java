package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@Order(5)
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PropertyAccessRequestDataSeeder implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@fertintelligence.com";
    private static final String GILVAN_EMAIL = "gilvan@email.com";
    private static final String MIGUEL_EMAIL = "miguel@email.com";
    private static final String MATEUS_EMAIL = "mateus@email.com";
    private static final String MARCOS_EMAIL = "marcos@email.com";
    private static final String REBECA_EMAIL = "rebeca@email.com";

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Override
    @Transactional
    public void run(String... args) {
        UserModel admin = findUserByEmail(ADMIN_EMAIL);
        UserModel gilvan = findUserByEmail(GILVAN_EMAIL);
        UserModel miguel = findUserByEmail(MIGUEL_EMAIL);
        UserModel mateus = findUserByEmail(MATEUS_EMAIL);
        UserModel marcos = findUserByEmail(MARCOS_EMAIL);
        UserModel rebeca = findUserByEmail(REBECA_EMAIL);

        List<PropertyModel> properties = propertyRepository.findAll().stream()
                .sorted(Comparator.comparing(PropertyModel::getId))
                .toList();

        if (properties.isEmpty()) {
            log.warn("⚠️ nenhuma propriedade encontrada para criar acessos.");
            return;
        }

        if (admin == null) {
            log.warn("⚠️ usuário não encontrado: {}", ADMIN_EMAIL);
        }

        for (int index = 0; index < properties.size(); index++) {
            PropertyModel property = properties.get(index);

            createIfNotExists(gilvan, property, AccessRequestStatus.APPROVED);

            if (index % 10 < 7) {
                createIfNotExists(miguel, property, AccessRequestStatus.APPROVED);
            }

            if (index % 2 == 0) {
                createIfNotExists(mateus, property, AccessRequestStatus.APPROVED);
            } else if (index % 10 == 7) {
                createIfNotExists(mateus, property, AccessRequestStatus.PENDING);
            } else if (index % 10 == 8) {
                createIfNotExists(mateus, property, AccessRequestStatus.REJECTED);
            } else if (index % 10 == 9) {
                createIfNotExists(mateus, property, AccessRequestStatus.REVOKED);
            }

            if (index % 5 == 0 || index % 5 == 1) {
                createIfNotExists(marcos, property, AccessRequestStatus.APPROVED);
            } else if (index % 10 == 6) {
                createIfNotExists(marcos, property, AccessRequestStatus.PENDING);
            } else if (index % 10 == 7) {
                createIfNotExists(marcos, property, AccessRequestStatus.REJECTED);
            }

            if (index % 10 == 0 || index % 10 == 1 || index % 10 == 2) {
                createIfNotExists(rebeca, property, AccessRequestStatus.APPROVED);
            } else if (index % 10 == 5) {
                createIfNotExists(rebeca, property, AccessRequestStatus.PENDING);
            } else if (index % 10 == 6) {
                createIfNotExists(rebeca, property, AccessRequestStatus.REJECTED);
            } else if (index % 10 == 7) {
                createIfNotExists(rebeca, property, AccessRequestStatus.REVOKED);
            }
        }
    }

    private UserModel findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.warn("⚠️ usuário não encontrado: {}", email);
                    return null;
                });
    }

    private void createIfNotExists(UserModel requester, PropertyModel property, AccessRequestStatus status) {
        if (requester == null) {
            log.warn("⚠️ usuário não encontrado, acesso não criado para propriedade id={}",
                    property != null ? property.getId() : null);
            return;
        }

        if (property == null) {
            log.error("property nula ao tentar criar acesso para requester={}", requester.getEmail());
            return;
        }

        if (propertyAccessRequestRepository.existsByRequesterAndProperty(requester, property)) {
            log.info("↩️ acesso já existente requester={} propertyId={}", requester.getEmail(), property.getId());
            return;
        }

        PropertyAccessRequestModel accessRequest = PropertyAccessRequestModel.builder()
                .requester(requester)
                .property(property)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        propertyAccessRequestRepository.save(accessRequest);
        log.info("✅ acesso criado requester={} propertyId={} status={}", requester.getEmail(), property.getId(), status);
    }
}
