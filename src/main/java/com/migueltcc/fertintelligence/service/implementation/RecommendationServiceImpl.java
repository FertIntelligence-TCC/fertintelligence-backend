package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListItemResponseDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
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
import com.migueltcc.fertintelligence.service.documentation.DirectRecommendationService;
import com.migueltcc.fertintelligence.service.documentation.RecommendationService;
import com.migueltcc.fertintelligence.service.documentation.ShoppingListService;
import com.migueltcc.fertintelligence.service.documentation.SummaryRecommendationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationFertigramaAssembler;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationNarrativeService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationStructuredDataAssembler;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.ShoppingListReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.SummaryRecommendationReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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
    private final DirectRecommendationReportService directRecommendationReportService;
    private final GeneralRecommendationService generalRecommendationService;
    private final SummaryRecommendationService summaryRecommendationService;
    private final DirectRecommendationService directRecommendationService;
    private final ShoppingListService shoppingListService;
    private final DirectRecommendationDtoMapper directRecommendationDtoMapper;
    private final SummaryRecommendationReportService summaryRecommendationReportService;
    private final ShoppingListReportService shoppingListReportService;
    private final RecommendationStructuredDataAssembler structuredDataAssembler;
    private final RecommendationFertigramaAssembler fertigramaAssembler;
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
                .cropId(calculationResult.getCropId())
                .cropUsedAreaInThePlot(calculationResult.getCropUsedAreaInThePlot())
                .cropPlantingDate(copyDate(calculationResult.getCropPlantingDate()))
                .reportClientProducer(property.getOwner() != null ? normalizeOptionalText(property.getOwner().getName()) : null)
                .reportPropertyName(normalizeOptionalText(property.getNome()))
                .reportMunicipality(normalizeOptionalText(dto.getReportMunicipality()))
                .reportState(normalizeState(dto.getReportState()))
                .reportPlotIdentification(normalizeOptionalText(plot.getIdentification()))
                .reportEvaluatedAreaHa(calculationResult.getCropUsedAreaInThePlot() != null
                        ? calculationResult.getCropUsedAreaInThePlot()
                        : plot.getArea())
                .reportTechnicalResponsible(normalizeOptionalText(user.getName()))
                .reportProfessionalRegistration(normalizeOptionalText(dto.getReportProfessionalRegistration()))
                .reportResponsiblePhone(formatPhone(user))
                .reportResponsibleEmail(normalizeOptionalText(user.getEmail()))
                .reportIssueDate(LocalDate.now())
                .reportSignatureAuthor(normalizeOptionalText(user.getName()))
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
        if (shouldGenerateFertilizationDocuments(dto)) {
            directRecommendationService.createInitial(
                    savedRecommendation,
                    directRecommendationReportService.build(savedRecommendation),
                    calculationResult.getFertilizationRecommendationRows(),
                    calculationResult.getMicronutrientFertilizerRows(),
                    calculationResult.getPlantingFormulatedFertilizerRows(),
                    calculationResult.getCoverageFormulatedFertilizerRows());
            summaryRecommendationService.createInitial(savedRecommendation, summaryRecommendationReportService.build(savedRecommendation));
            shoppingListService.createInitial(savedRecommendation, shoppingListReportService.build(savedRecommendation));
        }

        return toDto(savedRecommendation);
    }

    private boolean shouldGenerateFertilizationDocuments(RecommendationCreateRequestDto dto) {
        return dto == null || dto.getRecommendationType() != RecommendationType.ACIDITY_OR_SALINITY_CORRECTION;
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
                .cropPlantingDate(model.getCropPlantingDate())
                .reportClientProducer(model.getReportClientProducer())
                .reportPropertyName(model.getReportPropertyName())
                .reportMunicipality(model.getReportMunicipality())
                .reportState(model.getReportState())
                .reportPlotIdentification(model.getReportPlotIdentification())
                .reportEvaluatedAreaHa(model.getReportEvaluatedAreaHa())
                .reportTechnicalResponsible(model.getReportTechnicalResponsible())
                .reportProfessionalRegistration(model.getReportProfessionalRegistration())
                .reportResponsiblePhone(model.getReportResponsiblePhone())
                .reportResponsibleEmail(model.getReportResponsibleEmail())
                .reportIssueDate(model.getReportIssueDate())
                .reportSignatureAuthor(model.getReportSignatureAuthor())
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
                .generalRecommendation(toGeneralRecommendationDto(model, generalRecommendation))
                .summaryRecommendationId(summaryRecommendation != null ? summaryRecommendation.getId() : null)
                .summaryRecommendationGenerated(summaryRecommendation != null)
                .summaryRecommendationDocumentName(SummaryRecommendationModel.DOCUMENT_NAME)
                .summaryRecommendation(toSummaryRecommendationDto(summaryRecommendation))
                .directRecommendationId(directRecommendation != null ? directRecommendation.getId() : null)
                .directRecommendationGenerated(directRecommendation != null)
                .directRecommendationDocumentName(DirectRecommendationModel.DOCUMENT_NAME)
                .directRecommendation(directRecommendationDtoMapper.toDto(directRecommendation))
                .shoppingListId(shoppingList != null ? shoppingList.getId() : null)
                .shoppingListGenerated(shoppingList != null)
                .shoppingListDocumentName(ShoppingListModel.DOCUMENT_NAME)
                .shoppingList(toShoppingListDto(shoppingList))
                .purchaseList(structuredDataAssembler.purchaseList(model))
                .printable(RecommendationResponseDto.isPrintableForRole(authenticatedUser.getCargo()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private GeneralRecommendationResponseDto toGeneralRecommendationDto(
            RecommendationModel recommendation,
            GeneralRecommendationModel generalRecommendation) {
        String technicalReport = generalRecommendation != null
                ? generalRecommendation.getTechnicalReport()
                : recommendation.getTechnicalReport();
        if (technicalReport == null || technicalReport.isBlank()) {
            return null;
        }
        return GeneralRecommendationResponseDto.builder()
                .id(generalRecommendation != null ? generalRecommendation.getId() : null)
                .recommendationId(recommendation.getId())
                .documentName(generalRecommendation != null && generalRecommendation.getDocumentName() != null
                        ? generalRecommendation.getDocumentName()
                        : GeneralRecommendationModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .content(technicalReport)
                .structuredTables(structuredDataAssembler.generalSections(technicalReport))
                .fertigramas(fertigramaAssembler.generalFertigramas(technicalReport))
                .technicalObservations(structuredDataAssembler.observations(technicalReport))
                .generated(true)
                .createdAt(generalRecommendation != null ? generalRecommendation.getCreatedAt() : recommendation.getCreatedAt())
                .updatedAt(generalRecommendation != null ? generalRecommendation.getUpdatedAt() : recommendation.getUpdatedAt())
                .build();
    }

    private SummaryRecommendationResponseDto toSummaryRecommendationDto(SummaryRecommendationModel summaryRecommendation) {
        if (summaryRecommendation == null) {
            return null;
        }
        return SummaryRecommendationResponseDto.builder()
                .id(summaryRecommendation.getId())
                .recommendationId(summaryRecommendation.getRecommendation().getId())
                .documentName(summaryRecommendation.getDocumentName() != null
                        ? summaryRecommendation.getDocumentName()
                        : SummaryRecommendationModel.DOCUMENT_NAME)
                .technicalReport(summaryRecommendation.getTechnicalReport())
                .content(summaryRecommendation.getTechnicalReport())
                .structuredTables(structuredDataAssembler.summarySections(summaryRecommendation.getRecommendation()))
                .fertigramas(fertigramaAssembler.summaryFertigramas(summaryRecommendation.getRecommendation()))
                .technicalObservations(structuredDataAssembler.observations(summaryRecommendation.getTechnicalReport()))
                .createdAt(summaryRecommendation.getCreatedAt())
                .updatedAt(summaryRecommendation.getUpdatedAt())
                .build();
    }

    private ShoppingListResponseDto toShoppingListDto(ShoppingListModel shoppingList) {
        if (shoppingList == null) {
            return null;
        }
        List<ShoppingListItemResponseDto> items = structuredDataAssembler.shoppingItems(shoppingList.getRecommendation());
        int missingPrices = (int) items.stream().filter(item -> !Boolean.TRUE.equals(item.getPriceAvailable())).count();
        String costObservation = "Os valores representam apenas a estimativa dos custos dos insumos recomendados, sem incluir transporte, armazenamento ou aplicação."
                + " Os totais são segmentados por opção e alternativas mutuamente exclusivas não são somadas."
                + (missingPrices > 0 ? " Estimativa parcial: " + missingPrices + " insumo(s) não possuem preço comercial cadastrado." : "");
        return ShoppingListResponseDto.builder()
                .id(shoppingList.getId())
                .recommendationId(shoppingList.getRecommendation().getId())
                .documentName(shoppingList.getDocumentName() != null
                        ? shoppingList.getDocumentName()
                        : ShoppingListModel.DOCUMENT_NAME)
                .technicalReport(shoppingList.getTechnicalReport())
                .content(shoppingList.getTechnicalReport())
                .usedAreaInThePlot(shoppingList.getRecommendation().getCropUsedAreaInThePlot())
                .items(items)
                .blocks(structuredDataAssembler.shoppingBlocks(items))
                .purchaseList(structuredDataAssembler.purchaseList(items))
                .itemsWithoutPrice(missingPrices)
                .costEstimateObservation(costObservation)
                .technicalObservations(structuredDataAssembler.observations(shoppingList.getTechnicalReport()))
                .createdAt(shoppingList.getCreatedAt())
                .updatedAt(shoppingList.getUpdatedAt())
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

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeState(String value) {
        String normalized = normalizeOptionalText(value);
        return normalized != null ? normalized.toUpperCase(Locale.ROOT) : null;
    }

    private String formatPhone(UserModel user) {
        if (user == null || user.getTelefone() == null) {
            return null;
        }
        String phone = String.join(" ",
                normalizeOptionalText(user.getTelefone().getPais()) != null ? user.getTelefone().getPais().trim() : "",
                normalizeOptionalText(user.getTelefone().getDdd()) != null ? user.getTelefone().getDdd().trim() : "",
                normalizeOptionalText(user.getTelefone().getNumero()) != null ? user.getTelefone().getNumero().trim() : ""
        ).trim().replaceAll("\\s+", " ");
        return phone.isBlank() ? null : phone;
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
