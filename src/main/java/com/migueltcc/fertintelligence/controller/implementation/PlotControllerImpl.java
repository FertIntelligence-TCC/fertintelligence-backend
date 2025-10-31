package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.PlotController;
import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/plot")
public class PlotControllerImpl implements PlotController {

    @Autowired
    private PlotService plotService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<PlotResponseDto> createPlot(
            @RequestParam(name = "propertyId") Long propertyId,
            @Valid @RequestBody PlotCreateRequestDto createRequestDto,
            Authentication authentication) {

        PlotResponseDto createdPlot = plotService.createPlot(propertyId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/plot/get")
                .queryParam("plotId", createdPlot.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdPlot);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<PlotResponseDto> getPlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        PlotResponseDto plot = plotService.getPlotById(plotId, authentication.getName());
        return ResponseEntity.ok(plot);
    }

    @Override
    @GetMapping("/get-by-property")
    public ResponseEntity<List<PlotResponseDto>> getPlotsByProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication) {
        List<PlotResponseDto> plots = plotService.getAllPlotsByProperty(propertyId, authentication.getName());
        return ResponseEntity.ok(plots);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<PlotResponseDto> updatePlot(
            @RequestParam(name = "plotId") Long plotId,
            @Valid @RequestBody PlotPostRequestDto updateRequestDto,
            Authentication authentication) {
        PlotResponseDto updatedPlot = plotService.updatePlot(plotId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedPlot);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        plotService.deletePlot(plotId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}