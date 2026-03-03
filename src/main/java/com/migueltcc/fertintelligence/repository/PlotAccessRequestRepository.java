package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlotAccessRequestRepository extends JpaRepository<PlotAccessRequestModel, Long> {

    /* ======================================================
       FIND (compatibilidade) - mantém seus métodos atuais
    ====================================================== */

    // Consultor/Secretário: escopo por talhão
    Optional<PlotAccessRequestModel> findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
            PropertyModel property,
            PlotModel plot,
            UserModel requester,
            PermissionScope scope,
            PermissionType permissionType,
            AccessRequestStatus status
    );

    // Residente: escopo na propriedade (plot = null)
    Optional<PlotAccessRequestModel> findByPropertyAndRequesterAndScopeAndPermissionTypeAndStatus(
            PropertyModel property,
            UserModel requester,
            PermissionScope scope,
            PermissionType permissionType,
            AccessRequestStatus status
    );

    // Lista geral pro gerente
    List<PlotAccessRequestModel> findAllByProperty(PropertyModel property);

    // Lista por status (pra abas pendentes/aprovadas)
    List<PlotAccessRequestModel> findAllByPropertyAndStatus(PropertyModel property, AccessRequestStatus status);

    // Lista do usuário em uma propriedade
    List<PlotAccessRequestModel> findAllByRequesterAndProperty(UserModel requester, PropertyModel property);

    /* ======================================================
       EXISTS (melhoria) - evita N queries no PermissionManager
       - PermissionType IN (collection)
    ====================================================== */

    /**
     * Escopo PROPERTY (plot = null): existe permissão aprovada para qualquer PermissionType permitido?
     */
    boolean existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
            PropertyModel property,
            UserModel requester,
            PermissionScope scope,
            Collection<PermissionType> permissionTypes,
            AccessRequestStatus status
    );

    /**
     * Escopo PLOT: existe permissão aprovada para qualquer PermissionType permitido nesse plot?
     */
    boolean existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
            PropertyModel property,
            PlotModel plot,
            UserModel requester,
            PermissionScope scope,
            Collection<PermissionType> permissionTypes,
            AccessRequestStatus status
    );

    boolean existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
            PropertyModel property,
            UserModel requester,
            PermissionScope scope,
            Set<PermissionType> permissionTypes,
            AccessRequestStatus status
    );

    boolean existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
            PropertyModel property,
            PlotModel plot,
            UserModel requester,
            PermissionScope scope,
            Set<PermissionType> permissionTypes,
            AccessRequestStatus status
    );

    // Adicione este método para resolver o erro "cannot find symbol"
    Optional<PlotAccessRequestModel> findByPropertyAndPlotAndRequesterAndStatus(
            PropertyModel property,
            PlotModel plot,
            UserModel requester,
            AccessRequestStatus status
    );

}