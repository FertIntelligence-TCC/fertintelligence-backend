package com.migueltcc.fertintelligence.dto.property;

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
    private Double longitude;
    private Double altitude;
}