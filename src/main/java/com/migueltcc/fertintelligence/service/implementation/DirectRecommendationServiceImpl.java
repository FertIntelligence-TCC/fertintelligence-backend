package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.DirectRecommendationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DirectRecommendationServiceImpl implements DirectRecommendationService {

    private final DirectRecommendationRepository directRecommendationRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;
    private final DirectRecommendationReportService directRecommendationReportService;

    @Override
    @Transactional
    public DirectRecommendationResponseDto create(DirectRecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        DirectRecommendationModel directRecommendation = createInitial(recommendation, dto.getTechnicalReport());
        return toDto(directRecommendation);
    }

    @Override
    @Transactional
    public DirectRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Recomendação Direta.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Recomendação Direta não pode ser vazio.");
        }
        return directRecommendationRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
    }

    private DirectRecommendationModel saveNew(RecommendationModel recommendation, String technicalReport) {
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .build();

        try {
            DirectRecommendationModel saved = directRecommendationRepository.saveAndFlush(directRecommendation);
            recommendation.setDirectRecommendation(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            return directRecommendationRepository.findByRecommendation(recommendation)
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DirectRecommendationResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);
        return toDto(directRecommendation);
    }

    @Override
    @Transactional
    public DirectRecommendationResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return directRecommendationRepository.findByRecommendation(recommendation)
                .map(this::toDto)
                .orElseGet(() -> toDto(createInitial(recommendation, directRecommendationReportService.build(recommendation))));
    }

    @Override
    @Transactional
    public DirectRecommendationResponseDto update(Long id, DirectRecommendationPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);

        if (dto.getNewTechnicalReport() != null && !dto.getNewTechnicalReport().isBlank()) {
            directRecommendation.setTechnicalReport(dto.getNewTechnicalReport());
        }

        return toDto(directRecommendationRepository.save(directRecommendation));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);
        directRecommendation.getRecommendation().setDirectRecommendation(null);
        directRecommendationRepository.delete(directRecommendation);
    }

    private DirectRecommendationResponseDto toDto(DirectRecommendationModel model) {
        DirectRecommendationReportService.DirectDoseUnitMetadata doseUnitMetadata =
                directRecommendationReportService.resolveDoseUnitMetadata(model.getRecommendation());
        return DirectRecommendationResponseDto.builder()
                .id(model.getId())
                .recommendationId(model.getRecommendation().getId())
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .applicableDoseColumn(doseUnitMetadata.applicableDoseColumn())
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

    private DirectRecommendationModel findDirectRecommendationByIdOrThrow(Long id) {
        return directRecommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação Direta não encontrada com o ID: " + id));
    }
}
