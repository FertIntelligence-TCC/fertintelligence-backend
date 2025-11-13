package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropFoliarAnalysisInterpretationTableServiceImpl
        implements CropFoliarAnalysisInterpretationTableService {

    @Autowired
    private CropFoliarAnalysisInterpretationTableRepository tableRepository;

    @Autowired
    private CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableResponseDto createCropFoliarAnalysisInterpretationTable(
            CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFoliarAnalysisInterpretationTableModel table = CropFoliarAnalysisInterpretationTableModel.builder()
                .creator(owner)
                .build();

        CropFoliarAnalysisInterpretationTableModel savedTable = tableRepository.save(table);
        return savedTable.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropFoliarAnalysisInterpretationTableResponseDto getCropFoliarAnalysisInterpretationTableById(
            Long tableId,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableResponseDto>
    getAllCropFoliarAnalysisInterpretationTablesByCreator(String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        return tableRepository.findAllByCreator(owner).stream()
                .map(CropFoliarAnalysisInterpretationTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableResponseDto updateCropFoliarAnalysisInterpretationTable(
            Long tableId,
            CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        // Atualmente, a tabela não possui campos adicionais além do criador.
        // O método foi mantido para garantir compatibilidade com futuras evoluções da API.
        CropFoliarAnalysisInterpretationTableModel updatedTable = tableRepository.save(table);
        return updatedTable.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFoliarAnalysisInterpretationTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        tableLineRepository.deleteAllByTable(table);
        tableRepository.delete(table);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropFoliarAnalysisInterpretationTableModel findTableByIdOrThrow(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de interpretação de análise foliar não encontrada com o ID: " + tableId));
    }

    private void checkCreatorPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (!table.getCreator().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException(
                    "Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar tabelas de interpretação.");
        }
    }
}
