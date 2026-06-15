package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableLineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CropFoliarAnalysisInterpretationTableLineServiceImpl
        implements CropFoliarAnalysisInterpretationTableLineService {

    private final CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;
    private final CropFoliarAnalysisInterpretationTableRepository tableRepository;
    private final UserRepository userRepository;

    public CropFoliarAnalysisInterpretationTableLineServiceImpl(
            CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository,
            CropFoliarAnalysisInterpretationTableRepository tableRepository,
            UserRepository userRepository) {
        this.tableLineRepository = tableLineRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableLineResponseDto createCropFoliarAnalysisInterpretationTableLine(
            Long tableId,
            CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            String username) {

        UserModel requestUser = findUserByUsernameOrThrow(username);
        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, requestUser);

        validateUniqueCropInTable(table, createRequestDto.getCrop(), null);

        CropFoliarAnalysisInterpretationTableLineModel line = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .table(table)
                .crop(createRequestDto.getCrop())
                .n_content(asMacronutrientRange(createRequestDto.getN_content()))
                .p_content(asMacronutrientRange(createRequestDto.getP_content()))
                .k_content(asMacronutrientRange(createRequestDto.getK_content()))
                .ca_content(asMacronutrientRange(createRequestDto.getCa_content()))
                .mg_content(asMacronutrientRange(createRequestDto.getMg_content()))
                .s_content(asMacronutrientRange(createRequestDto.getS_content()))
                .b_content(createRequestDto.getB_content())
                .cu_content(createRequestDto.getCu_content())
                .fe_content(createRequestDto.getFe_content())
                .mn_content(createRequestDto.getMn_content())
                .mo_content(createRequestDto.getMo_content())
                .zn_content(createRequestDto.getZn_content())
                .build();

        CropFoliarAnalysisInterpretationTableLineModel savedLine = tableLineRepository.save(line);
        return savedLine.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropFoliarAnalysisInterpretationTableLineResponseDto getCropFoliarAnalysisInterpretationTableLineById(
            Long lineId,
            String username) {

        UserModel requestUser = findUserByUsernameOrThrow(username);
        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        checkCreatorPermission(line.getTable(), requestUser);

        return line.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableLineResponseDto> getAllCropFoliarAnalysisInterpretationTableLinesByTable(
            Long tableId,
            String username) {

        UserModel requestUser = findUserByUsernameOrThrow(username);
        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkReadPermission(table, requestUser);

        List<CropFoliarAnalysisInterpretationTableLineModel> lines = tableLineRepository.findAllByTableOrderByIdAsc(table);

        return (lines == null ? Collections.<CropFoliarAnalysisInterpretationTableLineModel>emptyList() : lines).stream()
                .map(CropFoliarAnalysisInterpretationTableLineModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableLineResponseDto updateCropFoliarAnalysisInterpretationTableLine(
            Long lineId,
            CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            String username) {

        UserModel requestUser = findUserByUsernameOrThrow(username);
        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        checkCreatorPermission(line.getTable(), requestUser);

        if (updateRequestDto.getCrop() != null && !updateRequestDto.getCrop().equals(line.getCrop())) {
            validateUniqueCropInTable(line.getTable(), updateRequestDto.getCrop(), line.getId());
            line.setCrop(updateRequestDto.getCrop());
        }

        // Atualização de nutrientes (se presentes no DTO)
        if (updateRequestDto.getN_content() != null) line.setN_content(asMacronutrientRange(updateRequestDto.getN_content()));
        if (updateRequestDto.getP_content() != null) line.setP_content(asMacronutrientRange(updateRequestDto.getP_content()));
        if (updateRequestDto.getK_content() != null) line.setK_content(asMacronutrientRange(updateRequestDto.getK_content()));
        if (updateRequestDto.getCa_content() != null) line.setCa_content(asMacronutrientRange(updateRequestDto.getCa_content()));
        if (updateRequestDto.getMg_content() != null) line.setMg_content(asMacronutrientRange(updateRequestDto.getMg_content()));
        if (updateRequestDto.getS_content() != null) line.setS_content(asMacronutrientRange(updateRequestDto.getS_content()));

        if (updateRequestDto.getB_content() != null) line.setB_content(updateRequestDto.getB_content());
        if (updateRequestDto.getCu_content() != null) line.setCu_content(updateRequestDto.getCu_content());
        if (updateRequestDto.getFe_content() != null) line.setFe_content(updateRequestDto.getFe_content());
        if (updateRequestDto.getMn_content() != null) line.setMn_content(updateRequestDto.getMn_content());
        if (updateRequestDto.getMo_content() != null) line.setMo_content(updateRequestDto.getMo_content());
        if (updateRequestDto.getZn_content() != null) line.setZn_content(updateRequestDto.getZn_content());

        CropFoliarAnalysisInterpretationTableLineModel updatedLine = tableLineRepository.save(line);
        return updatedLine.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFoliarAnalysisInterpretationTableLine(Long lineId, String username) {
        UserModel requestUser = findUserByUsernameOrThrow(username);
        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        checkCreatorPermission(line.getTable(), requestUser);

        tableLineRepository.delete(line);
    }

    private void validateUniqueCropInTable(CropFoliarAnalysisInterpretationTableModel table, NomeComum crop, Long currentLineId) {
        tableLineRepository.findByTableAndCrop(table, crop).ifPresent(existingLine -> {
            if (!Objects.equals(existingLine.getId(), currentLineId)) {
                throw new IllegalArgumentException(
                        "Já existe uma linha cadastrada para a cultura informada nesta tabela.");
            }
        });
    }

    private MenorMaiorTeores asMacronutrientRange(MenorMaiorTeores range) {
        if (range == null) {
            return null;
        }
        return new MenorMaiorTeores(range.getMenor(), range.getMaior(), UnidadeTeor.g_per_kg);
    }

    private CropFoliarAnalysisInterpretationTableModel findTableByIdOrThrow(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de interpretação de análise foliar não encontrada com o ID: " + tableId));
    }

    private CropFoliarAnalysisInterpretationTableLineModel findLineByIdOrThrow(Long lineId) {
        return tableLineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Linha da tabela de interpretação de análise foliar não encontrada com o ID: " + lineId));
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private void checkCreatorPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (!table.getCreator().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }
    }

    private void checkReadPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (table.isPublicTable()) {
            return;
        }

        UserModel tableCreator = table.getCreator();
        if (tableCreator == null || tableCreator.getId() == null || !tableCreator.getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }
    }
}
