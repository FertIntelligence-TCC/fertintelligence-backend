package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
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
    private UserRepository userRepository;

    @Override
    @Transactional
    public PlotResponseDto createPlot(Long propertyId, PlotCreateRequestDto createRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkManagerOrOwnerPermission(property, requestingUser);

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
        checkManagerOrOwnerPermission(plot.getProperty(), requestingUser);

        return plot.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotResponseDto> getAllPlotsByProperty(Long propertyId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkManagerOrOwnerPermission(property, requestingUser);

        return plotRepository.findAllByProperty(property).stream()
                .map(PlotModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlotResponseDto updatePlot(Long plotId, PlotPostRequestDto updateRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkManagerOrOwnerPermission(plot.getProperty(), requestingUser);

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
        checkManagerOrOwnerPermission(plot.getProperty(), requestingUser);

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

    private void checkManagerOrOwnerPermission(PropertyModel property, UserModel requestingUser) {
        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            return;
        }

        throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
    }
}