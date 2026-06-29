package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SummaryRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.SummaryRecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SummaryRecommendationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationStructuredDataAssembler;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.SummaryRecommendationReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SummaryRecommendationServiceImpl implements SummaryRecommendationService {

    private final SummaryRecommendationRepository summaryRecommendationRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;
    private final SummaryRecommendationReportService summaryRecommendationReportService;
    private final RecommendationStructuredDataAssembler structuredDataAssembler;

    @Override
    @Transactional
    public SummaryRecommendationResponseDto create(SummaryRecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        SummaryRecommendationModel summaryRecommendation = createInitial(recommendation, dto.getTechnicalReport());
        return toDto(summaryRecommendation);
    }

    @Override
    @Transactional
    public SummaryRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Recomendação Resumida.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Recomendação Resumida não pode ser vazio.");
        }
        return summaryRecommendationRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
    }

    private SummaryRecommendationModel saveNew(RecommendationModel recommendation, String technicalReport) {
        SummaryRecommendationModel summaryRecommendation = SummaryRecommendationModel.builder()
                .recommendation(recommendation)
                .documentName(SummaryRecommendationModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .build();

        try {
            SummaryRecommendationModel saved = summaryRecommendationRepository.saveAndFlush(summaryRecommendation);
            recommendation.setSummaryRecommendation(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            return summaryRecommendationRepository.findByRecommendation(recommendation)
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryRecommendationResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        SummaryRecommendationModel summaryRecommendation = findSummaryRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(summaryRecommendation.getRecommendation().getPlot(), user);
        return toDto(summaryRecommendation);
    }

    @Override
    @Transactional
    public SummaryRecommendationResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return summaryRecommendationRepository.findByRecommendation(recommendation)
                .map(this::toDto)
                .orElseGet(() -> toDto(createInitial(recommendation, summaryRecommendationReportService.build(recommendation))));
    }

    @Override
    @Transactional
    public SummaryRecommendationResponseDto update(Long id, SummaryRecommendationPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        SummaryRecommendationModel summaryRecommendation = findSummaryRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(summaryRecommendation.getRecommendation().getPlot(), user);

        if (dto.getNewTechnicalReport() != null && !dto.getNewTechnicalReport().isBlank()) {
            summaryRecommendation.setTechnicalReport(dto.getNewTechnicalReport());
        }

        return toDto(summaryRecommendationRepository.save(summaryRecommendation));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        SummaryRecommendationModel summaryRecommendation = findSummaryRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(summaryRecommendation.getRecommendation().getPlot(), user);
        summaryRecommendation.getRecommendation().setSummaryRecommendation(null);
        summaryRecommendationRepository.delete(summaryRecommendation);
    }

    private SummaryRecommendationResponseDto toDto(SummaryRecommendationModel model) {
        return SummaryRecommendationResponseDto.builder()
                .id(model.getId())
                .recommendationId(model.getRecommendation().getId())
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : SummaryRecommendationModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .structuredTables(structuredDataAssembler.summarySections(model.getRecommendation()))
                .fertigramas(structuredDataAssembler.summaryFertigramas(model.getRecommendation()))
                .technicalObservations(structuredDataAssembler.observations(model.getTechnicalReport()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private UserModel findUserByUsernameOrEmailOrThrow(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usernameOrEmail));
    }

    private RecommendationModel findRecommendationByIdOrThrow(Long id) {
        return recommendationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }

    private SummaryRecommendationModel findSummaryRecommendationByIdOrThrow(Long id) {
        return summaryRecommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação Resumida não encontrada com o ID: " + id));
    }
}
