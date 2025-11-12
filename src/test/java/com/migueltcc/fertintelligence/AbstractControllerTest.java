package com.migueltcc.fertintelligence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.repository.*;
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
@ActiveProfiles("test")
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

    // Mocks do UserControllerImplTest
    @MockitoBean
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JwtEncoder jwtEncoder;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

}