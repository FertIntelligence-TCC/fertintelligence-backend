package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoveragePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CoverageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CoverageServiceImpl implements CoverageService {

    @Autowired
    private CoverageRepository coverageRepository;

    @Autowired
    private ContentRangeRepository contentRangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CoverageResponseDto createCoverage(Long contentRangeId, CoverageCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        ContentRangeModel range = findContentRangeByIdOrThrow(contentRangeId);

        List<CoverageModel> existingCoverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
        int expectedOrder = existingCoverages.size() + 1;

        if (createRequestDto.getOrder() == null || createRequestDto.getOrder() != expectedOrder) {
            throw new IllegalArgumentException("A ordem da cobertura deve ser sequencial e iniciar em 1.");
        }

        List<CoverageModel> siblingPlaceholders = createSiblingPlaceholderCoverages(range, existingCoverages.size(), expectedOrder);

        CoverageModel coverage = CoverageModel.builder()
                .range(range)
                .order(createRequestDto.getOrder())
                .application(createRequestDto.getApplication())
                .build();

        CoverageModel savedCoverage = coverageRepository.save(coverage);

        if (!siblingPlaceholders.isEmpty()) {
            coverageRepository.saveAll(siblingPlaceholders);
        }

        return savedCoverage.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CoverageResponseDto getCoverageById(Long coverageId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        CoverageModel coverage = findCoverageByIdOrThrow(coverageId);
        checkReadPermission(coverage.getRange().getTable(), owner);

        return coverage.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoverageResponseDto> getAllCoveragesByContentRange(Long contentRangeId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        ContentRangeModel range = findContentRangeByIdOrThrow(contentRangeId);
        checkReadPermission(range.getTable(), owner);

        return coverageRepository.findAllByRangeOrderByOrderAsc(range).stream()
                .map(CoverageModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CoverageResponseDto updateCoverage(Long coverageId, CoveragePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        CoverageModel coverage = findCoverageByIdOrThrow(coverageId);
        ContentRangeModel range = coverage.getRange();

        Integer updatedOrder = updateRequestDto.getOrder() != null
                ? updateRequestDto.getOrder()
                : coverage.getOrder();

        Double updatedApplication = updateRequestDto.getApplication() != null
                ? updateRequestDto.getApplication()
                : coverage.getApplication();

        List<CoverageModel> existingCoverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
        List<CoverageModel> adjustedCoverages = existingCoverages.stream()
                .map(current -> Objects.equals(current.getId(), coverage.getId())
                        ? CoverageModel.builder()
                        .id(coverage.getId())
                        .range(range)
                        .order(updatedOrder)
                        .application(updatedApplication)
                        .build()
                        : current)
                .collect(Collectors.toList());

        validateCoverageOrders(adjustedCoverages);

        coverage.setOrder(updatedOrder);
        coverage.setApplication(updatedApplication);

        CoverageModel savedCoverage = coverageRepository.save(coverage);
        return savedCoverage.toDto();
    }

    @Override
    @Transactional
    public void deleteCoverage(Long coverageId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        CoverageModel coverage = findCoverageByIdOrThrow(coverageId);
        ContentRangeModel range = coverage.getRange();

        List<CoverageModel> existingCoverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
        if (!existingCoverages.isEmpty()) {
            CoverageModel lastCoverage = existingCoverages.get(existingCoverages.size() - 1);
            if (!Objects.equals(lastCoverage.getId(), coverage.getId())) {
                throw new IllegalArgumentException("Somente a última cobertura cadastrada pode ser removida.");
            }
        }

        List<CoverageModel> siblingCoveragesToDelete = collectSiblingCoveragesForDeletion(range,
                existingCoverages.size());

        coverageRepository.delete(coverage);

        if (!siblingCoveragesToDelete.isEmpty()) {
            coverageRepository.deleteAll(siblingCoveragesToDelete);
        }
    }

    private void validateCoverageOrders(List<CoverageModel> coverages) {
        List<CoverageModel> sortedCoverages = coverages.stream()
                .sorted(Comparator.comparing(CoverageModel::getOrder))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedCoverages.size(); i++) {
            CoverageModel current = sortedCoverages.get(i);
            Integer order = current.getOrder();

            if (order == null || order <= 0) {
                throw new IllegalArgumentException("A ordem das coberturas deve ser positiva.");
            }

            if (!order.equals(i + 1)) {
                throw new IllegalArgumentException("As coberturas devem ser ordenadas sequencialmente sem lacunas.");
            }
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private ContentRangeModel findContentRangeByIdOrThrow(Long contentRangeId) {
        return contentRangeRepository.findById(contentRangeId)
                .orElseThrow(() -> new EntityNotFoundException("Intervalo de teor não encontrado com o ID: " + contentRangeId));
    }

    private CoverageModel findCoverageByIdOrThrow(Long coverageId) {
        return coverageRepository.findById(coverageId)
                .orElseThrow(() -> new EntityNotFoundException("Cobertura não encontrada com o ID: " + coverageId));
    }

    private void checkReadPermission(CropFertilizationTableModel table, UserModel requestingUser) {
        if (table.isPublicTable()) {
            return;
        }

        checkCreatorPermission(table, requestingUser);
    }

    private void checkCreatorPermission(CropFertilizationTableModel table, UserModel requestingUser) {
        StandardEntityAuthorization.assertCanRead(table.getCreator(), table.isPublicTable(), requestingUser);
    }

    private List<CoverageModel> createSiblingPlaceholderCoverages(ContentRangeModel range, int currentCoverageCount,
                                                                  int newOrder) {
        List<ContentRangeModel> siblingRanges = contentRangeRepository
                .findAllByTableAndNutrientOrderByOrderAsc(range.getTable(), range.getNutrient());

        List<CoverageModel> placeholders = new ArrayList<>();

        for (ContentRangeModel sibling : siblingRanges) {
            if (Objects.equals(sibling.getId(), range.getId())) {
                continue;
            }

            List<CoverageModel> siblingCoverages = coverageRepository.findAllByRangeOrderByOrderAsc(sibling);
            if (siblingCoverages.size() != currentCoverageCount) {
                throw new IllegalStateException(
                        "Todos os intervalos do nutriente devem possuir a mesma quantidade de coberturas cadastradas.");
            }

            placeholders.add(CoverageModel.builder()
                    .range(sibling)
                    .order(newOrder)
                    .application(null)
                    .build());
        }

        return placeholders;
    }

    private List<CoverageModel> collectSiblingCoveragesForDeletion(ContentRangeModel range, int currentCoverageCount) {
        List<ContentRangeModel> siblingRanges = contentRangeRepository
                .findAllByTableAndNutrientOrderByOrderAsc(range.getTable(), range.getNutrient());

        List<CoverageModel> coveragesToDelete = new ArrayList<>();

        for (ContentRangeModel sibling : siblingRanges) {
            if (Objects.equals(sibling.getId(), range.getId())) {
                continue;
            }

            List<CoverageModel> siblingCoverages = coverageRepository.findAllByRangeOrderByOrderAsc(sibling);
            if (siblingCoverages.size() != currentCoverageCount) {
                throw new IllegalStateException(
                        "Todos os intervalos do nutriente devem possuir a mesma quantidade de coberturas cadastradas.");
            }

            if (currentCoverageCount > 0) {
                coveragesToDelete.add(siblingCoverages.get(currentCoverageCount - 1));
            }
        }

        return coveragesToDelete;
    }

}
