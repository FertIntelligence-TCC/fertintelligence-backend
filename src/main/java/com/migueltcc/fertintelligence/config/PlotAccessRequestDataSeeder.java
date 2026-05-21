package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
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
@Order(7)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PlotAccessRequestDataSeeder implements CommandLineRunner {

    private static final String MIGUEL_EMAIL = "miguel@email.com";
    private static final String MATEUS_EMAIL = "mateus@email.com";
    private static final String MARCOS_EMAIL = "marcos@email.com";
    private static final String REBECA_EMAIL = "rebeca@email.com";
    private static final String GILVAN_EMAIL = "gilvan@email.com";

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Override
    @Transactional
    public void run(String... args) {
        UserModel miguel = findUserByEmail(MIGUEL_EMAIL);
        UserModel mateus = findUserByEmail(MATEUS_EMAIL);
        UserModel marcos = findUserByEmail(MARCOS_EMAIL);
        UserModel rebeca = findUserByEmail(REBECA_EMAIL);
        UserModel gilvan = findUserByEmail(GILVAN_EMAIL);

        List<PlotModel> plots = plotRepository.findAll().stream()
                .sorted(Comparator.comparing(PlotModel::getId))
                .toList();

        if (plots.isEmpty()) {
            log.warn("⚠️ Nenhum talhão encontrado. PlotAccessRequestDataSeeder ignorado.");
            return;
        }

        for (int index = 0; index < plots.size(); index++) {
            PlotModel plot = plots.get(index);

            createIfNotExists(miguel, plot, statusForMiguel(index), PermissionType.EDIT_ANALYSES_AND_CROPS);
            createIfNotExists(mateus, plot, statusForMateus(index), PermissionType.EDIT_ANALYSES_AND_CROPS);
            createIfNotExists(marcos, plot, statusForMarcos(index), PermissionType.EDIT_ANALYSES);
            createIfNotExists(rebeca, plot, statusForRebeca(index), PermissionType.EDIT_ANALYSES);

            if (gilvan != null && index % 11 == 0) {
                createIfNotExists(gilvan, plot, AccessRequestStatus.APPROVED, PermissionType.EDIT_ANALYSES_AND_CROPS);
            }
        }
    }

    private AccessRequestStatus statusForMiguel(int index) {
        int mod = index % 10;
        if (mod < 8) return AccessRequestStatus.APPROVED;
        if (mod == 8) return AccessRequestStatus.PENDING;
        return AccessRequestStatus.REVOKED;
    }

    private AccessRequestStatus statusForMateus(int index) {
        int mod = index % 10;
        if (index % 3 == 0) return AccessRequestStatus.APPROVED;
        if (mod == 4) return AccessRequestStatus.PENDING;
        if (mod == 5) return AccessRequestStatus.REJECTED;
        if (mod == 6) return AccessRequestStatus.REVOKED;
        return null;
    }

    private AccessRequestStatus statusForMarcos(int index) {
        int mod = index % 10;
        if (index % 4 == 0 || index % 4 == 1) return AccessRequestStatus.APPROVED;
        if (mod == 7) return AccessRequestStatus.PENDING;
        if (mod == 8) return AccessRequestStatus.REJECTED;
        return null;
    }

    private AccessRequestStatus statusForRebeca(int index) {
        int mod = index % 10;
        if (index % 5 == 0) return AccessRequestStatus.APPROVED;
        if (mod == 2) return AccessRequestStatus.PENDING;
        if (mod == 3) return AccessRequestStatus.REJECTED;
        if (mod == 4) return AccessRequestStatus.REVOKED;
        return null;
    }

    private UserModel findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.warn("⚠️ Usuário não encontrado: {}", email);
                    return null;
                });
    }

    private void createIfNotExists(
            UserModel requester,
            PlotModel plot,
            AccessRequestStatus status,
            PermissionType permissionType
    ) {
        if (requester == null || status == null || permissionType == null) {
            return;
        }

        if (plot == null) {
            return;
        }

        PropertyModel property = plot.getProperty();
        if (property == null || property.getId() == null) {
            log.warn("⚠️ Talhão sem propriedade associada. Pulando usuário={} talhão={}",
                    requester.getEmail(), plot.getIdentification());
            return;
        }

        if (!propertyRepository.existsById(property.getId())) {
            log.warn("⚠️ Propriedade não encontrada para o talhão. Pulando usuário={} talhão={}",
                    requester.getEmail(), plot.getIdentification());
            return;
        }

        if (status == AccessRequestStatus.APPROVED
                && propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                property,
                requester,
                AccessRequestStatus.APPROVED
        ).isEmpty()) {
            log.warn("⚠️ Acesso de propriedade aprovado não encontrado. Pulando usuário={} talhão={}",
                    requester.getEmail(), plot.getIdentification());
            return;
        }

        if (plotAccessRequestRepository.existsByPropertyAndPlotAndRequester(property, plot, requester)) {
            log.info("↩️ Permissão de talhão já existe: usuário={} talhão={}", requester.getEmail(), plot.getIdentification());
            return;
        }

        PlotAccessRequestModel request = PlotAccessRequestModel.builder()
                .requester(requester)
                .property(property)
                .plot(plot)
                .scope(PermissionScope.PLOT)
                .permissionType(permissionType)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        plotAccessRequestRepository.save(request);
        log.info("✅ Permissão de talhão criada: usuário={} talhão={} status={} tipo={}",
                requester.getEmail(), plot.getIdentification(), status, permissionType);
    }
}
