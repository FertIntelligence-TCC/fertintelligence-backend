package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PlotService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlotServiceImpl implements PlotService {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PlotResponseDto createPlot(Long propertyId, PlotCreateRequestDto createRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkPermission(property, null, requestingUser, true);

        plotRepository.findByIdentificationAndProperty(createRequestDto.getIdentification(), property)
                .ifPresent(existing -> {
                    throw new EntityExistsException("Um talhão com esta identificação já existe nesta propriedade: "
                            + createRequestDto.getIdentification());
                });

        PlotModel plot = PlotModel.builder()
                .property(property)
                .identification(createRequestDto.getIdentification())
                .area(createRequestDto.getArea())
                .soilClass(createRequestDto.getSoilClass())
                .soilTexture(createRequestDto.getSoilTexture())
                .cropIncorporationYear(createRequestDto.getCropIncorporationYear())
                .irrigatedArea(createRequestDto.getIrrigatedArea())
                .declivity(createRequestDto.getDeclivity())
                .monthlyPluviosity(createRequestDto.getMonthlyPluviosity())
                .annualPluviosity(createRequestDto.getAnnualPluviosity())
                .build();

        PlotModel savedPlot = plotRepository.save(plot);
        return savedPlot.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PlotResponseDto getPlotById(Long plotId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPermission(plot.getProperty(), plot, requestingUser, false);

        return plot.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotResponseDto> getAllPlotsByProperty(Long propertyId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkPermission(property, null, requestingUser, false);

        return plotRepository.findAllByProperty(property).stream()
                .map(PlotModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlotResponseDto updatePlot(Long plotId, PlotPostRequestDto updateRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPermission(plot.getProperty(), plot, requestingUser, true);

        if (updateRequestDto.getIdentification() != null
                && !updateRequestDto.getIdentification().equals(plot.getIdentification())) {
            plotRepository.findByIdentificationAndProperty(updateRequestDto.getIdentification(), plot.getProperty())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(plotId)) {
                            throw new EntityExistsException("A identificação '" + updateRequestDto.getIdentification()
                                    + "' já está em uso nesta propriedade.");
                        }
                    });
            plot.setIdentification(updateRequestDto.getIdentification());
        }

        if (updateRequestDto.getArea() != null) {
            plot.setArea(updateRequestDto.getArea());
        }

        if (updateRequestDto.getSoilClass() != null) {
            plot.setSoilClass(updateRequestDto.getSoilClass());
        }

        if (updateRequestDto.getSoilTexture() != null) {
            plot.setSoilTexture(updateRequestDto.getSoilTexture());
        }

        if (updateRequestDto.getCropIncorporationYear() != null) {
            plot.setCropIncorporationYear(updateRequestDto.getCropIncorporationYear());
        }

        if (updateRequestDto.getIrrigatedArea() != null) {
            plot.setIrrigatedArea(updateRequestDto.getIrrigatedArea());
        }

        if (updateRequestDto.getDeclivity() != null) {
            plot.setDeclivity(updateRequestDto.getDeclivity());
        }

        if (updateRequestDto.getMonthlyPluviosity() != null) {
            plot.setMonthlyPluviosity(updateRequestDto.getMonthlyPluviosity());
        }

        if (updateRequestDto.getAnnualPluviosity() != null) {
            plot.setAnnualPluviosity(updateRequestDto.getAnnualPluviosity());
        }

        PlotModel updatedPlot = plotRepository.save(plot);
        return updatedPlot.toDto();
    }

    @Override
    @Transactional
    public void deletePlot(Long plotId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPermission(plot.getProperty(), plot, requestingUser, true);

        plotRepository.delete(plot);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findPropertyByIdOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada com o ID: " + propertyId));
    }

    private PlotModel findPlotByIdOrThrow(Long plotId) {
        return plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + plotId));
    }

    /**
     * Regras (refatorado):
     * - Dono e gerente: sempre liberados (read/edit)
     * - Usuário aprovado na propriedade (PropertyAccessRequest APPROVED): liberado para READ (listar/ver)
     * - Edit (create/update/delete): restrito a dono, gerente e agrônomo residente (desde que aprovado na propriedade)
     * - PlotAccessRequest permanece útil caso você queira permissões por talhão no futuro (ou para cargos específicos),
     *   mas NÃO deve bloquear a listagem de talhões se o usuário já foi aceito na propriedade.
     */
    private void checkPermission(PropertyModel property, PlotModel plot, UserModel requestingUser, boolean requireEdit) {
        if (requestingUser.getCargo() != Cargo.PROPRIETARIO
                && requestingUser.getCargo() != Cargo.GERENTE
                && requestingUser.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && requestingUser.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && requestingUser.getCargo() != Cargo.SECRETARIO
                && requestingUser.getCargo() != Cargo.SUPERVISOR_DE_AREA
        ) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }

        // dono
        if (property.getOwner() != null
                && property.getOwner().getUsername() != null
                && property.getOwner().getUsername().equals(requestingUser.getUsername())) {
            return;
        }

        // gerente da propriedade (comparar por username é mais robusto)
        if (property.getManager() != null
                && property.getManager().getUsername() != null
                && property.getManager().getUsername().equals(requestingUser.getUsername())) {
            return;
        }

        // A partir daqui: não é dono/gerente
        boolean hasPropertyApproval = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                property,
                requestingUser,
                AccessRequestStatus.APPROVED
        ).isPresent();

        if (!hasPropertyApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }

        // leitura: aprovação na propriedade basta
        if (!requireEdit) return;

        // edição: permitir GERENTE
        if (requestingUser.getCargo() == Cargo.GERENTE) {
            return;
        }

        throw new AccessDeniedException("Você não tem permissão para modificar este recurso.");
    }
}