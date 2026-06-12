package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeCientifico;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFertilizationTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CropFertilizationTableServiceImpl implements CropFertilizationTableService {

    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final UserRepository userRepository;

    // Novos repositórios para gerenciar a exclusão dos filhos
    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;

    public CropFertilizationTableServiceImpl(
            CropFertilizationTableRepository cropFertilizationTableRepository,
            UserRepository userRepository,
            ContentRangeRepository contentRangeRepository,
            CoverageRepository coverageRepository) {
        this.cropFertilizationTableRepository = cropFertilizationTableRepository;
        this.userRepository = userRepository;
        this.contentRangeRepository = contentRangeRepository;
        this.coverageRepository = coverageRepository;
    }

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
                .sources(createRequestDto.getSources())
                .publicTable(Boolean.TRUE.equals(createRequestDto.getPublic_table()))
                .build();

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropFertilizationTableResponseDto getCropFertilizationTableById(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertCanView(table, requester);
        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username) {
        return getAllCropFertilizationTables(username, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username, TechnicalTableGroup group) {
        UserModel owner = findUserByUsernameOrThrow(username);

        if (group != null) {
            return findTablesByGroup(owner, group).stream()
                    .map(CropFertilizationTableModel::toDto)
                    .toList();
        }

        if (isSupremeUser(owner)) {
            return cropFertilizationTableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO)
                    .stream()
                    .map(CropFertilizationTableModel::toDto)
                    .toList();
        }

        return mergeTables(
                cropFertilizationTableRepository.findAllByCreator(owner),
                cropFertilizationTableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO),
                cropFertilizationTableRepository.findAllByPublicTableTrue()
        )
                .stream()
                .map(CropFertilizationTableModel::toDto)
                .toList();
    }

    private List<CropFertilizationTableModel> findTablesByGroup(UserModel owner, TechnicalTableGroup group) {
        return switch (group) {
            case MINHAS -> cropFertilizationTableRepository.findAllByCreator(owner);
            case PRIVADAS -> {
                // Retorna tabelas do usuário logado que não são públicas (privadas)
                List<CropFertilizationTableModel> allByOwner = cropFertilizationTableRepository.findAllByCreator(owner);
                yield allByOwner.stream()
                        .filter(t -> !t.isPublicTable())
                        .collect(Collectors.toList());
            }
            case PUBLICAS -> cropFertilizationTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO);
            case PADRAO -> cropFertilizationTableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllPublicCropFertilizationTables() {
        return cropFertilizationTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(CropFertilizationTableModel::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllDefaultCropFertilizationTables(String username) {
        findUserByUsernameOrThrow(username);
        return cropFertilizationTableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO)
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
        assertCanModify(table, requester);

        if (updateRequestDto.getCrop_common_name() != null || updateRequestDto.getCrop_scientific_nome() != null) {
            NomeComum common = updateRequestDto.getCrop_common_name() != null
                    ? updateRequestDto.getCrop_common_name()
                    : table.getCrop_common_name();

            NomeCientifico scientific = updateRequestDto.getCrop_scientific_nome() != null
                    ? updateRequestDto.getCrop_scientific_nome()
                    : table.getCrop_scientific_nome();

            validateCropNames(common, scientific);
        }

        updateTableFields(table, updateRequestDto);

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return saved.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFertilizationTable(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertCanModify(table, requester);

        // --- INÍCIO DA CORREÇÃO: Deleção Manual em Cascata ---

        // 1. Busca todos os intervalos desta tabela
        List<ContentRangeModel> ranges = contentRangeRepository.findAllByTableOrderByNutrientAscOrderAsc(table);

        // 2. Para cada intervalo, busca e deleta suas coberturas
        for (ContentRangeModel range : ranges) {
            List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
            coverageRepository.deleteAll(coverages);
        }

        // 3. Deleta os intervalos
        contentRangeRepository.deleteAll(ranges);

        // 4. Finalmente, deleta a tabela pai
        cropFertilizationTableRepository.delete(table);

        // --- FIM DA CORREÇÃO ---
    }

    // --- Métodos Privados ---

    private void updateTableFields(CropFertilizationTableModel table, CropFertilizationTablePostRequestDto dto) {
        if (dto.getRegion() != null) table.setRegion(dto.getRegion());
        if (dto.getCrop_common_name() != null) table.setCrop_common_name(dto.getCrop_common_name());
        if (dto.getCrop_scientific_nome() != null) table.setCrop_scientific_nome(dto.getCrop_scientific_nome());
        if (dto.getCultivares() != null) table.setCultivares(dto.getCultivares());
        if (dto.getSuggested_spacing() != null) table.setSuggested_spacing(dto.getSuggested_spacing());
        if (dto.getInitial_value() != null) table.setInitial_value(dto.getInitial_value());
        if (dto.getFinal_value() != null) table.setFinal_value(dto.getFinal_value());
        if (dto.getUsed_spacing() != null) table.setUsed_spacing(dto.getUsed_spacing());
        if (dto.getUsed_spacing_value() != null) table.setUsed_spacing_value(dto.getUsed_spacing_value());
        if (dto.getRegional_productivity() != null) table.setRegional_productivity(dto.getRegional_productivity());
        if (dto.getExpected_productivity() != null) table.setExpected_productivity(dto.getExpected_productivity());
        if (dto.getCriteria() != null) table.setCriteria(dto.getCriteria());
        if (dto.getManure() != null) table.setManure(dto.getManure());
        if (dto.getManure_qtd() != null) table.setManure_qtd(dto.getManure_qtd());
        if (dto.getGessing() != null) table.setGessing(dto.getGessing());
        if (dto.getMicronutrients() != null) table.setMicronutrients(dto.getMicronutrients());
        if (dto.getNpk() != null) table.setNpk(dto.getNpk());
        if (dto.getObservations() != null) table.setObservations(dto.getObservations());
        if (dto.getSources() != null) table.setSources(dto.getSources());
        if (dto.getPublic_table() != null) table.setPublicTable(dto.getPublic_table());
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropFertilizationTableModel findTableByIdOrThrow(Long tableId) {
        return cropFertilizationTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de adubação não encontrada com ID: " + tableId));
    }

    private void assertCanView(CropFertilizationTableModel table, UserModel requester) {
        if (isCreator(table, requester) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Acesso negado: Você não tem permissão para acessar esta tabela.");
    }

    private void assertCanModify(CropFertilizationTableModel table, UserModel requester) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(requester)) {
                return;
            }
            throw new AccessDeniedException("Acesso negado: Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        assertIsCreator(table, requester);
    }

    private void assertIsCreator(CropFertilizationTableModel table, UserModel requester) {
        if (table.getCreator() == null || requester == null || table.getCreator().getId() == null) {
            throw new AccessDeniedException("Acesso negado: Propriedades de criador inválidas.");
        }
        if (!table.getCreator().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Acesso negado: Você não tem permissão para modificar esta tabela.");
        }
    }

    private boolean isCreator(CropFertilizationTableModel table, UserModel requester) {
        return table.getCreator() != null
                && requester != null
                && table.getCreator().getId() != null
                && table.getCreator().getId().equals(requester.getId());
    }

    private boolean isDefaultTable(CropFertilizationTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO;
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }

    @SafeVarargs
    private List<CropFertilizationTableModel> mergeTables(List<CropFertilizationTableModel>... tableLists) {
        Map<Long, CropFertilizationTableModel> byId = Stream.of(tableLists)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(CropFertilizationTableModel::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        return List.copyOf(byId.values());
    }

    private void validateCropNames(NomeComum commonName, NomeCientifico scientificName) {
        if (commonName == null || scientificName == null) {
            throw new IllegalArgumentException("Nome comum e nome científico são obrigatórios.");
        }

        NomeCientifico expectedScientificName;
        switch (commonName) {
            case ALGODAO -> expectedScientificName = NomeCientifico.Gossypium_hirsutum;
            case AMENDOIM -> expectedScientificName = NomeCientifico.Arachis_hypogaea;
            case CANA_DE_ACUCAR -> expectedScientificName = NomeCientifico.Saccharum_officinarum;
            case FEIJAO_CAUPI -> expectedScientificName = NomeCientifico.Vigna_unguiculata;
            case FEIJAO_COMUM -> expectedScientificName = NomeCientifico.Phaseolus_vulgaris;
            case GERGELIM -> expectedScientificName = NomeCientifico.Sesamum_indicum;
            case MAMONA -> expectedScientificName = NomeCientifico.Ricinus_communis;
            case MILHO -> expectedScientificName = NomeCientifico.Zea_mays;
            case SISAL -> expectedScientificName = NomeCientifico.Agave_sisalana;
            case SOJA -> expectedScientificName = NomeCientifico.Glycine_max;
            default -> throw new IllegalArgumentException("Nome comum da cultura inválido ou não suportado: " + commonName);
        }

        if (scientificName != expectedScientificName) {
            throw new IllegalArgumentException(
                    String.format("Inconsistência: O nome científico '%s' não corresponde à cultura '%s'. Esperado: '%s'.",
                            scientificName, commonName, expectedScientificName)
            );
        }
    }
}
