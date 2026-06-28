package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.GeneralRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ShoppingListModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SummaryRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import com.migueltcc.fertintelligence.repository.GeneralRecommendationRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.ShoppingListRepository;
import com.migueltcc.fertintelligence.repository.SummaryRecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.GeneralRecommendationService;
import com.migueltcc.fertintelligence.service.documentation.RecommendationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationNarrativeService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final GeneralRecommendationRepository generalRecommendationRepository;
    private final SummaryRecommendationRepository summaryRecommendationRepository;
    private final DirectRecommendationRepository directRecommendationRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;
    private final RecommendationCalculationService recommendationCalculationService;
    private final RecommendationReportService recommendationReportService;
    private final RecommendationNarrativeService recommendationNarrativeService;
    private final GeneralRecommendationService generalRecommendationService;
    private final PermissionManager permissionManager;

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
        String improvedReport = recommendationNarrativeService.improveNarrative(technicalReport);

        RecommendationModel recommendation = RecommendationModel.builder()
                .creator(user)
                .property(property)
                .plot(plot)
                .recommendationFolderName(dto.getRecommendationFolderName())
                .recommendationType(dto.getRecommendationType())
                .cropName(resolveCropName(calculationResult))
                .cropYear(calculationResult.getAnnualCropFolderYear())
                .limingCriteria(dto.getLimingCriteria())
                .texturalClassification(resolveTexturalClassification(dto.getTexturalClassification()))
                .origemAdubos(dto.getOrigemAdubos() != null ? dto.getOrigemAdubos() : FertilizerSourceOption.BOTH)
                .cropFertilizationTableId(dto.getCropFertilizationTableId())
                .cropFertilizationTableGroup(dto.getCropFertilizationTableGroup())
                .soilFertilityInterpretationCriteriaTableId(dto.getSoilFertilityInterpretationCriteriaTableId())
                .soilFertilityInterpretationCriteriaTableGroup(dto.getSoilFertilityInterpretationCriteriaTableGroup())
                .cropFoliarAnalysisInterpretationTableId(dto.getCropFoliarAnalysisInterpretationTableId())
                .cropFoliarAnalysisInterpretationTableGroup(dto.getCropFoliarAnalysisInterpretationTableGroup())
                .technicalReport(improvedReport)
                .build();

        RecommendationModel savedRecommendation = recommendationRepository.save(recommendation);
        generalRecommendationService.createInitial(savedRecommendation, improvedReport);

        return toDto(savedRecommendation);
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

        GeneralRecommendationModel generalRecommendation = resolveGeneralRecommendation(recommendation);
        String sourceReport = generalRecommendation != null
                ? generalRecommendation.getTechnicalReport()
                : recommendation.getTechnicalReport();
        String improvedNarrative = recommendationNarrativeService.improveNarrative(sourceReport);
        recommendation.setTechnicalReport(improvedNarrative);
        if (generalRecommendation != null) {
            generalRecommendation.setTechnicalReport(improvedNarrative);
            generalRecommendationRepository.save(generalRecommendation);
        }

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
        GeneralRecommendationModel generalRecommendation = resolveGeneralRecommendation(model);
        SummaryRecommendationModel summaryRecommendation = resolveSummaryRecommendation(model);
        DirectRecommendationModel directRecommendation = resolveDirectRecommendation(model);
        ShoppingListModel shoppingList = resolveShoppingList(model);
        return RecommendationResponseDto.builder()
                .id(model.getId())
                .creatorUserId(model.getCreator().getId())
                .creatorUserName(model.getCreator().getName())
                .propertyId(model.getProperty().getId())
                .propertyName(model.getProperty().getNome())
                .plotId(model.getPlot().getId())
                .plotIdentification(model.getPlot().getIdentification())
                .recommendationFolderName(model.getRecommendationFolderName())
                .recommendationType(model.getRecommendationType())
                .cropName(model.getCropName())
                .cropYear(model.getCropYear())
                .limingCriteria(model.getLimingCriteria())
                .texturalClassification(resolveTexturalClassification(model.getTexturalClassification()))
                .origemAdubos(model.getOrigemAdubos() != null ? model.getOrigemAdubos() : FertilizerSourceOption.BOTH)
                .cropFertilizationTableId(model.getCropFertilizationTableId())
                .cropFertilizationTableGroup(model.getCropFertilizationTableGroup())
                .soilFertilityInterpretationCriteriaTableId(model.getSoilFertilityInterpretationCriteriaTableId())
                .soilFertilityInterpretationCriteriaTableGroup(model.getSoilFertilityInterpretationCriteriaTableGroup())
                .cropFoliarAnalysisInterpretationTableId(model.getCropFoliarAnalysisInterpretationTableId())
                .cropFoliarAnalysisInterpretationTableGroup(model.getCropFoliarAnalysisInterpretationTableGroup())
                .technicalReport(model.getTechnicalReport())
                .generalRecommendationId(generalRecommendation != null ? generalRecommendation.getId() : null)
                .generalRecommendationGenerated(generalRecommendation != null || hasLegacyTechnicalReport(model))
                .generalRecommendationDocumentName(GeneralRecommendationModel.DOCUMENT_NAME)
                .summaryRecommendationId(summaryRecommendation != null ? summaryRecommendation.getId() : null)
                .summaryRecommendationGenerated(summaryRecommendation != null)
                .summaryRecommendationDocumentName(SummaryRecommendationModel.DOCUMENT_NAME)
                .directRecommendationId(directRecommendation != null ? directRecommendation.getId() : null)
                .directRecommendationGenerated(directRecommendation != null)
                .directRecommendationDocumentName(DirectRecommendationModel.DOCUMENT_NAME)
                .shoppingListId(shoppingList != null ? shoppingList.getId() : null)
                .shoppingListGenerated(shoppingList != null)
                .shoppingListDocumentName(ShoppingListModel.DOCUMENT_NAME)
                .printable(RecommendationResponseDto.isPrintableForRole(authenticatedUser.getCargo()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private GeneralRecommendationModel resolveGeneralRecommendation(RecommendationModel model) {
        if (model.getGeneralRecommendation() != null) {
            return model.getGeneralRecommendation();
        }
        if (model.getId() == null) {
            return null;
        }
        return generalRecommendationRepository.findByRecommendation(model).orElse(null);
    }

    private boolean hasLegacyTechnicalReport(RecommendationModel model) {
        return model.getTechnicalReport() != null && !model.getTechnicalReport().isBlank();
    }

    private SummaryRecommendationModel resolveSummaryRecommendation(RecommendationModel model) {
        if (model.getSummaryRecommendation() != null) {
            return model.getSummaryRecommendation();
        }
        if (model.getId() == null) {
            return null;
        }
        return summaryRecommendationRepository.findByRecommendation(model).orElse(null);
    }

    private DirectRecommendationModel resolveDirectRecommendation(RecommendationModel model) {
        if (model.getDirectRecommendation() != null) {
            return model.getDirectRecommendation();
        }
        if (model.getId() == null) {
            return null;
        }
        return directRecommendationRepository.findByRecommendation(model).orElse(null);
    }

    private ShoppingListModel resolveShoppingList(RecommendationModel model) {
        if (model.getShoppingList() != null) {
            return model.getShoppingList();
        }
        if (model.getId() == null) {
            return null;
        }
        return shoppingListRepository.findByRecommendation(model).orElse(null);
    }

    private NomeComum resolveCropName(RecommendationCalculationService.RecommendationCalculationResult calculationResult) {
        if (calculationResult.getCropName() == null) {
            throw new IllegalArgumentException("A cultura selecionada não possui nome informado.");
        }
        return NomeComum.valueOf(calculationResult.getCropName());
    }

    private TexturalClassification resolveTexturalClassification(TexturalClassification texturalClassification) {
        return texturalClassification != null ? texturalClassification : TexturalClassification.BRASILEIRO;
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
