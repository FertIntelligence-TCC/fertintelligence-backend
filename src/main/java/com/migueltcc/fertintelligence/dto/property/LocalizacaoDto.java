package com.migueltcc.fertintelligence.dto.property;

import com.migueltcc.fertintelligence.composedAtributes.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAtributes.LongitudeDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoDto {
    private Double latitude;
    private LatitudeDirection latitudeDirection;
    private Double longitude;
    private LongitudeDirection longitudeDirection;
    private Double altitude;
}