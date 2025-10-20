package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.property.PropertyUpdateRequestDto;

import java.util.List;

public interface PropertyService {

    PropertyResponseDto createProperty(PropertyPostRequestDto postRequestDto, String username);
    PropertyResponseDto getPropertyById(Long propertyId, String username);
    List<PropertyResponseDto> getAllPropertiesByOwner(String username);
    PropertyResponseDto updateProperty(Long propertyId, PropertyUpdateRequestDto updateRequestDto, String username);
    void deleteProperty(Long propertyId, String username);
}