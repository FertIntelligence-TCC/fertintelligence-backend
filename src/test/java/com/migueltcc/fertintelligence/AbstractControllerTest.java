package com.migueltcc.fertintelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.repository.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(resolver = TestProfileResolver.class)
// @MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestTemplate restTemplate;

    @MockitoBean
    protected AnnualCropFolderRepository annualCropFolderRepository;

    @MockitoBean
    protected CropRepository cropRepository;

    @MockitoBean
    protected FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;

    @MockitoBean
    protected FoliarAnalysisRepository foliarAnalysisRepository;

    @MockitoBean
    protected LayerExtractRepository layerExtractRepository;

    @MockitoBean
    protected LiquidSourceRepository liquidSourceRepository;

    @MockitoBean
    protected PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;

    @MockitoBean
    protected PlotRepository plotRepository;

    @MockitoBean
    protected PropertyRepository propertyRepository;

    @MockitoBean
    protected RangeExtractRepository rangeExtractRepository;

    @MockitoBean
    protected SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;

    @MockitoBean
    protected SoilAnalysisRepository soilAnalysisRepository;

    @MockitoBean
    protected SolidSourceRepository solidSourceRepository;

    @MockitoBean
    protected TopDressingFertilizationRepository topDressingFertilizationRepository;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected CropFertilizationTableRepository cropFertilizationTableRepository;

    @MockitoBean
    protected ContentRangeRepository contentRangeRepository;

    @MockitoBean
    protected CoverageRepository coverageRepository;

    @MockitoBean
    protected CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository;

    @MockitoBean
    protected CropFoliarAnalysisInterpretationTableLineRepository cropFoliarAnalysisInterpretationTableLineRepository;

    @MockitoBean
    protected SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @MockitoBean
    protected AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;

    @MockitoBean
    protected AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;

    @MockitoBean
    protected AvailableSRepository availableSRepository;

    @MockitoBean
    protected DiverseContentRangeRepository diverseContentRangeRepository;

    @MockitoBean
    protected KExchangeableContentRepository kExchangeableContentRepository;

    @MockitoBean
    protected SalinityInterpretationRepository salinityInterpretationRepository;

    @MockitoBean
    protected PropertyAccessRequestRepository propertyAccessRequestRepository;

    @MockitoBean
    protected PlotAccessRequestRepository plotAccessRequestRepository;

    @MockitoBean
    protected SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    @MockitoBean
    protected SimpleMineralFertilizerPhotoRepository simpleMineralFertilizerPhotoRepository;

    @MockitoBean
    protected FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;

    @MockitoBean
    protected OrganoMineralFertilizerRepository organoMineralFertilizerRepository;

    @MockitoBean
    protected OrganicFertilizerRepository organicFertilizerRepository;

    @MockitoBean
    protected OrganicFertilizerPhotoRepository organicFertilizerPhotoRepository;

    @MockitoBean
    protected GreenFertilizerRepository greenFertilizerRepository;

    @MockitoBean
    protected BioFertilizerRepository bioFertilizerRepository;

    @MockitoBean
    protected BioFertilizerPhotoRepository bioFertilizerPhotoRepository;

    @MockitoBean
    protected MineralFertilizerRepository mineralFertilizerRepository;

    @MockitoBean
    protected MineralFertilizerPhotoRepository mineralFertilizerPhotoRepository;

    @MockitoBean
    protected ChelatedFertilizerRepository chelatedFertilizerRepository;

    @MockitoBean
    protected ChelatedFertilizerPhotoRepository chelatedFertilizerPhotoRepository;

    @MockitoBean
    protected RecommendationRepository recommendationRepository;

    @MockitoBean
    protected GeneralRecommendationRepository generalRecommendationRepository;

    @MockitoBean
    protected DirectRecommendationRepository directRecommendationRepository;

    @MockitoBean
    protected DirectRecommendationMicronutrientFertilizerLineRepository directRecommendationMicronutrientFertilizerLineRepository;

    @MockitoBean
    protected DirectRecommendationPlantingFormulatedFertilizerLineRepository directRecommendationPlantingFormulatedFertilizerLineRepository;

    @MockitoBean
    protected DirectRecommendationCoverageFormulatedFertilizerLineRepository directRecommendationCoverageFormulatedFertilizerLineRepository;

    @MockitoBean
    protected SummaryRecommendationRepository summaryRecommendationRepository;

    @MockitoBean
    protected ShoppingListRepository shoppingListRepository;

    @MockitoBean
    protected FertigramRepository fertigramRepository;

    @MockitoBean
    protected FertigramNutrientRepository fertigramNutrientRepository;

    // Mocks do UserControllerImplTest
    @MockitoBean
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JwtEncoder jwtEncoder;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

}
