package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SoilFertilityInterpretationCriteriaTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SoilFertilityInterpretationCriteriaTableServiceImpl implements SoilFertilityInterpretationCriteriaTableService {

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SoilFertilityInterpretationCriteriaTableResponseDto createSoilFertilityInterpretationCriteriaTable(
            SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .creator(owner)
                .region(createRequestDto.getRegion())
                .build();

        SoilFertilityInterpretationCriteriaTableModel savedTable = soilFertilityInterpretationCriteriaTableRepository.save(table);
        return savedTable.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SoilFertilityInterpretationCriteriaTableResponseDto getSoilFertilityInterpretationCriteriaTableById(
            Long tableId,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllSoilFertilityInterpretationCriteriaTablesByCreator(
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        return soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(owner).stream()
                .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SoilFertilityInterpretationCriteriaTableResponseDto updateSoilFertilityInterpretationCriteriaTable(
            Long tableId,
            SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        if (updateRequestDto.getRegion() != null) {
            table.setRegion(updateRequestDto.getRegion());
        }

        SoilFertilityInterpretationCriteriaTableModel updatedTable = soilFertilityInterpretationCriteriaTableRepository.save(table);
        return updatedTable.toDto();
    }

    @Override
    @Transactional
    public void deleteSoilFertilityInterpretationCriteriaTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        soilFertilityInterpretationCriteriaTableRepository.delete(table);
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar tabelas de critérios de interpretação da fertilidade do solo.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return soilFertilityInterpretationCriteriaTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de critérios de interpretação da fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private void checkCreatorPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel requestingUser) {
        if (!Objects.equals(table.getCreator().getId(), requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela de critérios de interpretação da fertilidade do solo.");
        }
    }
}
