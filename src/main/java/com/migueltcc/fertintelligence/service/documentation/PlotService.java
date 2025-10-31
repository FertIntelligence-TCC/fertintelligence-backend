package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;

import java.util.List;

public interface PlotService {

    PlotResponseDto createPlot(Long propertyId, PlotCreateRequestDto createRequestDto, String username);
    PlotResponseDto getPlotById(Long plotId, String username);
    List<PlotResponseDto> getAllPlotsByProperty(Long propertyId, String username);
    PlotResponseDto updatePlot(Long plotId, PlotPostRequestDto updateRequestDto, String username);
    void deletePlot(Long plotId, String username);

}