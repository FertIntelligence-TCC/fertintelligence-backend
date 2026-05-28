package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private PlotRepository plotRepository;
    @Autowired
    private RecommendationCalculationService recommendationCalculationService;
    @Autowired
    private RecommendationReportService recommendationReportService;
    @Autowired
    private RecommendationNarrativeService recommendationNarrativeService;
    @Autowired
    private PermissionManager permissionManager;

    @Override
    @Transactional
    public RecommendationResponseDto generate(RecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(dto.getPropertyId());
        PlotModel plot = findPlotByIdOrThrow(dto.getPlotId());

        if (!plot.getProperty().getId().equals(property.getId())) {
            throw new IllegalArgumentException("O talhão informado não pertence à propriedade informada.");
        }
        permissionManager.assertCanGenerateRecommendation(property, plot, user);

        RecommendationCalculationService.RecommendationCalculationResult calculationResult =
                recommendationCalculationService.calculate(dto, user, property, plot);
        String technicalReport = recommendationReportService.buildTechnicalReport(calculationResult);

        RecommendationModel recommendation = RecommendationModel.builder()
                .creator(user)
                .property(property)
                .plot(plot)
                .recommendationType(dto.getRecommendationType())
                .cropName(dto.getCropName())
                .cropYear(dto.getCropYear())
                .limingCriteria(dto.getLimingCriteria())
                .origemAdubos(dto.getOrigemAdubos())
                .cropFertilizationTableId(dto.getCropFertilizationTableId())
                .soilFertilityInterpretationCriteriaTableId(dto.getSoilFertilityInterpretationCriteriaTableId())
                .cropFoliarAnalysisInterpretationTableId(dto.getCropFoliarAnalysisInterpretationTableId())
                .technicalReport(technicalReport)
                .build();

        return toDto(recommendationRepository.save(recommendation));
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationResponseDto preparePrint(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        permissionManager.assertCanPrintRecommendation(user);
        return toDto(recommendation, user);
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return toDto(recommendation, user);
    }

    @Override
    @Transactional
    public RecommendationResponseDto improveNarrative(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);

        String improvedNarrative = recommendationNarrativeService.improveNarrative(recommendation.getTechnicalReport());
        recommendation.setTechnicalReport(improvedNarrative);

        RecommendationModel savedRecommendation = recommendationRepository.save(recommendation);
        return toDto(savedRecommendation, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponseDto> getMine(String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        return recommendationRepository.findAllByCreatorOrderByCreatedAtDesc(user).stream().map(model -> toDto(model, user)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponseDto> getByProperty(Long propertyId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        return recommendationRepository.findAllByProperty(property).stream()
                .filter(model -> permissionManager.canReadPlot(model.getPlot(), user))
                .map(model -> toDto(model, user)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponseDto> getByPlot(Long plotId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        PlotModel plot = findPlotByIdOrThrow(plotId);
        permissionManager.assertCanReadPlot(plot, user);
        return recommendationRepository.findAllByPlot(plot).stream().map(model -> toDto(model, user)).toList();
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        recommendationRepository.delete(recommendation);
    }

    private RecommendationResponseDto toDto(RecommendationModel model) {
        return toDto(model, model.getCreator());
    }

    private RecommendationResponseDto toDto(RecommendationModel model, UserModel authenticatedUser) {
        return RecommendationResponseDto.builder()
                .id(model.getId())
                .creatorUserId(model.getCreator().getId())
                .creatorUserName(model.getCreator().getName())
                .propertyId(model.getProperty().getId())
                .propertyName(model.getProperty().getNome())
                .plotId(model.getPlot().getId())
                .plotIdentification(model.getPlot().getIdentification())
                .recommendationType(model.getRecommendationType())
                .cropName(model.getCropName())
                .cropYear(model.getCropYear())
                .limingCriteria(model.getLimingCriteria())
                .origemAdubos(model.getOrigemAdubos() != null ? model.getOrigemAdubos() : FertilizerSourceOption.BOTH)
                .cropFertilizationTableId(model.getCropFertilizationTableId())
                .soilFertilityInterpretationCriteriaTableId(model.getSoilFertilityInterpretationCriteriaTableId())
                .cropFoliarAnalysisInterpretationTableId(model.getCropFoliarAnalysisInterpretationTableId())
                .technicalReport(model.getTechnicalReport())
                .printable(RecommendationResponseDto.isPrintableForRole(authenticatedUser.getCargo()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private UserModel findUserByUsernameOrEmailOrThrow(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usernameOrEmail));
    }

    private PropertyModel findPropertyByIdOrThrow(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada com o ID: " + id));
    }

    private PlotModel findPlotByIdOrThrow(Long id) {
        return plotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + id));
    }

    private RecommendationModel findRecommendationByIdOrThrow(Long id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }
}
