package com.migueltcc.fertintelligence.dto.property;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PropertyCreateRequestDto {
    private PropertyPostRequestDto postRequestDto;
    private UserModel owner;
}