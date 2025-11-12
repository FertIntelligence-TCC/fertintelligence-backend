package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ContentRangeController;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ContentRangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/content-range")
public class ContentRangeControllerImpl implements ContentRangeController {

    @Autowired
    private ContentRangeService contentRangeService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ContentRangeResponseDto> createContentRange(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody ContentRangeCreateRequestDto createRequestDto,
            Authentication authentication) {

        ContentRangeResponseDto createdRange = contentRangeService
                .createContentRange(tableId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/content-range/get")
                .queryParam("contentRangeId", createdRange.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdRange);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ContentRangeResponseDto> getContentRange(
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            Authentication authentication) {
        ContentRangeResponseDto range = contentRangeService
                .getContentRangeById(contentRangeId, authentication.getName());
        return ResponseEntity.ok(range);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<List<ContentRangeResponseDto>> getContentRangesByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        List<ContentRangeResponseDto> ranges = contentRangeService
                .getAllContentRangesByTable(tableId, authentication.getName());
        return ResponseEntity.ok(ranges);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ContentRangeResponseDto> updateContentRange(
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Valid @RequestBody ContentRangePostRequestDto updateRequestDto,
            Authentication authentication) {
        ContentRangeResponseDto updatedRange = contentRangeService
                .updateContentRange(contentRangeId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedRange);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteContentRange(
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            Authentication authentication) {
        contentRangeService.deleteContentRange(contentRangeId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}