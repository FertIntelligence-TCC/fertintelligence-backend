package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;

import java.util.List;

public interface PropertyService {

    PropertyResponseDto createProperty(PropertyCreateRequestDto createRequestDto, String username);
    PropertyResponseDto getPropertyById(Long propertyId, String username);
    List<PropertyResponseDto> getAllPropertiesByOwner(String username);
    PropertyResponseDto updateProperty(Long propertyId, PropertyPostRequestDto updateRequestDto, String username);
    void deleteProperty(Long propertyId, String username);
    List<PropertyResponseDto> searchPropertiesByName(String nome, String username);
    List<PropertyResponseDto> getManageableProperties(String username);

}