package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeCientifico;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFertilizationTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CropFertilizationTableServiceImpl implements CropFertilizationTableService {

    @Autowired
    private CropFertilizationTableRepository cropFertilizationTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CropFertilizationTableResponseDto createCropFertilizationTable(
            CropFertilizationTableCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);

        validateCropNames(createRequestDto.getCrop_common_name(), createRequestDto.getCrop_scientific_nome());

        CropFertilizationTableModel table = CropFertilizationTableModel.builder()
                .creator(owner)
                .region(createRequestDto.getRegion())
                .crop_common_name(createRequestDto.getCrop_common_name())
                .crop_scientific_nome(createRequestDto.getCrop_scientific_nome())
                .cultivares(createRequestDto.getCultivares())
                .suggested_spacing(createRequestDto.getSuggested_spacing())
                .initial_value(createRequestDto.getInitial_value())
                .final_value(createRequestDto.getFinal_value())
                .used_spacing(createRequestDto.getUsed_spacing())
                .used_spacing_value(createRequestDto.getUsed_spacing_value())
                .regional_productivity(createRequestDto.getRegional_productivity())
                .expected_productivity(createRequestDto.getExpected_productivity())
                .criteria(createRequestDto.getCriteria())
                .manure(createRequestDto.getManure())
                .manure_qtd(createRequestDto.getManure_qtd())
                .gessing(createRequestDto.getGessing())
                .micronutrients(createRequestDto.getMicronutrients())
                .npk(createRequestDto.getNpk())
                .observations(createRequestDto.getObservations())
                .build();

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropFertilizationTableResponseDto getCropFertilizationTableById(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertIsCreator(table, requester);
        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        return cropFertilizationTableRepository.findAllByCreator(owner)
                .stream()
                .map(CropFertilizationTableModel::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CropFertilizationTableResponseDto updateCropFertilizationTable(
            Long tableId,
            CropFertilizationTablePostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertIsCreator(table, requester);

        // Se o update alterar nome comum e/ou científico, valide a combinação final
        if (updateRequestDto.getCrop_common_name() != null || updateRequestDto.getCrop_scientific_nome() != null) {
            NomeComum common = updateRequestDto.getCrop_common_name() != null
                    ? updateRequestDto.getCrop_common_name()
                    : table.getCrop_common_name();

            NomeCientifico scientific = updateRequestDto.getCrop_scientific_nome() != null
                    ? updateRequestDto.getCrop_scientific_nome()
                    : table.getCrop_scientific_nome();

            validateCropNames(common, scientific);
        }

        if (updateRequestDto.getRegion() != null) {
            table.setRegion(updateRequestDto.getRegion());
        }

        if (updateRequestDto.getCrop_common_name() != null) {
            table.setCrop_common_name(updateRequestDto.getCrop_common_name());
        }

        if (updateRequestDto.getCrop_scientific_nome() != null) {
            table.setCrop_scientific_nome(updateRequestDto.getCrop_scientific_nome());
        }

        if (updateRequestDto.getCultivares() != null) {
            table.setCultivares(updateRequestDto.getCultivares());
        }

        if (updateRequestDto.getSuggested_spacing() != null) {
            table.setSuggested_spacing(updateRequestDto.getSuggested_spacing());
        }

        if (updateRequestDto.getInitial_value() != null) {
            table.setInitial_value(updateRequestDto.getInitial_value());
        }

        if (updateRequestDto.getFinal_value() != null) {
            table.setFinal_value(updateRequestDto.getFinal_value());
        }

        if (updateRequestDto.getUsed_spacing() != null) {
            table.setUsed_spacing(updateRequestDto.getUsed_spacing());
        }

        if (updateRequestDto.getUsed_spacing_value() != null) {
            table.setUsed_spacing_value(updateRequestDto.getUsed_spacing_value());
        }

        if (updateRequestDto.getRegional_productivity() != null) {
            table.setRegional_productivity(updateRequestDto.getRegional_productivity());
        }

        if (updateRequestDto.getExpected_productivity() != null) {
            table.setExpected_productivity(updateRequestDto.getExpected_productivity());
        }

        if (updateRequestDto.getCriteria() != null) {
            table.setCriteria(updateRequestDto.getCriteria());
        }

        if (updateRequestDto.getManure() != null) {
            table.setManure(updateRequestDto.getManure());
        }

        if (updateRequestDto.getManure_qtd() != null) {
            table.setManure_qtd(updateRequestDto.getManure_qtd());
        }

        if (updateRequestDto.getGessing() != null) {
            table.setGessing(updateRequestDto.getGessing());
        }

        if (updateRequestDto.getMicronutrients() != null) {
            table.setMicronutrients(updateRequestDto.getMicronutrients());
        }

        if (updateRequestDto.getNpk() != null) {
            table.setNpk(updateRequestDto.getNpk());
        }

        if (updateRequestDto.getObservations() != null) {
            table.setObservations(updateRequestDto.getObservations());
        }

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return saved.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFertilizationTable(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertIsCreator(table, requester);

        cropFertilizationTableRepository.delete(table);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        // Ajuste o método abaixo se o seu UserRepository usar outro nome (ex.: findByEmail).
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    private CropFertilizationTableModel findTableByIdOrThrow(Long tableId) {
        return cropFertilizationTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de adubação não encontrada."));
    }

    private void assertIsCreator(CropFertilizationTableModel table, UserModel requester) {
        if (table.getCreator() == null || requester == null || table.getCreator().getId() == null) {
            throw new AccessDeniedException("Acesso negado.");
        }
        if (!table.getCreator().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta tabela.");
        }
    }

    private void validateCropNames(NomeComum commonName, NomeCientifico scientificName) {
        if (commonName == null || scientificName == null) {
            throw new IllegalArgumentException("Nome comum e nome científico são obrigatórios.");
        }

        NomeCientifico expectedScientificName;
        switch (commonName) {
            case FEIJAO_CAUPI -> expectedScientificName = NomeCientifico.Vigna_unguiculata;
            case FEIJAO_COMUM -> expectedScientificName = NomeCientifico.Phaseolus_vulgaris;
            case GERGELIM -> expectedScientificName = NomeCientifico.Sesamum_indicum;
            case MAMONA -> expectedScientificName = NomeCientifico.Ricinus_communis;
            case MILHO -> expectedScientificName = NomeCientifico.Zea_mays;
            case SISAL -> expectedScientificName = NomeCientifico.Agave_sisalana;
            case SOJA -> expectedScientificName = NomeCientifico.Glycine_max;
            default -> throw new IllegalArgumentException("Nome comum da cultura inválido.");
        }

        if (scientificName != expectedScientificName) {
            throw new IllegalArgumentException("O nome científico informado não corresponde ao nome comum da cultura.");
        }
    }
}
