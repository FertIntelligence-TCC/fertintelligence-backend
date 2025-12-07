package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CropFoliarAnalysisInterpretationTableLineServiceImpl
        implements CropFoliarAnalysisInterpretationTableLineService {

    @Autowired
    private CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;

    @Autowired
    private CropFoliarAnalysisInterpretationTableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableLineResponseDto createCropFoliarAnalysisInterpretationTableLine(
            Long tableId,
            CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        NomeComum crop = createRequestDto.getCrop();
        ensureUniqueCropForTable(table, crop, null);

        CropFoliarAnalysisInterpretationTableLineModel line = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .table(table)
                .crop(crop)
                .n_content(createRequestDto.getN_content())
                .p_content(createRequestDto.getP_content())
                .k_content(createRequestDto.getK_content())
                .mg_content(createRequestDto.getMg_content())
                .s_content(createRequestDto.getS_content())
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

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        checkCreatorPermission(line.getTable(), owner);

        return line.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableLineResponseDto>
    getAllCropFoliarAnalysisInterpretationTableLinesByTable(Long tableId, String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        return tableLineRepository.findAllByTable(table).stream()
                .map(CropFoliarAnalysisInterpretationTableLineModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableLineResponseDto updateCropFoliarAnalysisInterpretationTableLine(
            Long lineId,
            CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        CropFoliarAnalysisInterpretationTableModel table = line.getTable();
        checkCreatorPermission(table, owner);

        if (updateRequestDto.getCrop() != null) {
            ensureUniqueCropForTable(table, updateRequestDto.getCrop(), line.getId());
            line.setCrop(updateRequestDto.getCrop());
        }

        if (updateRequestDto.getN_content() != null) {
            line.setN_content(updateRequestDto.getN_content());
        }

        if (updateRequestDto.getP_content() != null) {
            line.setP_content(updateRequestDto.getP_content());
        }

        if (updateRequestDto.getK_content() != null) {
            line.setK_content(updateRequestDto.getK_content());
        }

        if (updateRequestDto.getMg_content() != null) {
            line.setMg_content(updateRequestDto.getMg_content());
        }

        if (updateRequestDto.getS_content() != null) {
            line.setS_content(updateRequestDto.getS_content());
        }

        if (updateRequestDto.getB_content() != null) {
            line.setB_content(updateRequestDto.getB_content());
        }

        if (updateRequestDto.getCu_content() != null) {
            line.setCu_content(updateRequestDto.getCu_content());
        }

        if (updateRequestDto.getFe_content() != null) {
            line.setFe_content(updateRequestDto.getFe_content());
        }

        if (updateRequestDto.getMn_content() != null) {
            line.setMn_content(updateRequestDto.getMn_content());
        }

        if (updateRequestDto.getMo_content() != null) {
            line.setMo_content(updateRequestDto.getMo_content());
        }

        if (updateRequestDto.getZn_content() != null) {
            line.setZn_content(updateRequestDto.getZn_content());
        }

        CropFoliarAnalysisInterpretationTableLineModel updatedLine = tableLineRepository.save(line);
        return updatedLine.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFoliarAnalysisInterpretationTableLine(Long lineId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableLineModel line = findLineByIdOrThrow(lineId);
        CropFoliarAnalysisInterpretationTableModel table = line.getTable();
        checkCreatorPermission(table, owner);

        long totalLines = tableLineRepository.countByTable(table);
        if (totalLines <= 1) {
            throw new IllegalArgumentException(
                    "A tabela de interpretação deve possuir ao menos uma linha. Não é possível remover a última linha.");
        }

        tableLineRepository.delete(line);
    }

    private void ensureUniqueCropForTable(CropFoliarAnalysisInterpretationTableModel table, NomeComum crop, Long currentLineId) {
        if (crop == null) {
            throw new IllegalArgumentException("O nome da cultura é obrigatório para criar ou atualizar a linha.");
        }

        tableLineRepository.findByTableAndCrop(table, crop).ifPresent(existingLine -> {
            if (!Objects.equals(existingLine.getId(), currentLineId)) {
                throw new IllegalArgumentException(
                        "Já existe uma linha cadastrada para a cultura informada nesta tabela.");
            }
        });
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
}