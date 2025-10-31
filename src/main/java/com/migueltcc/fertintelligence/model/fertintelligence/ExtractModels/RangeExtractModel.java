package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
// @AllArgsConstructor
@SuperBuilder
@Entity
@Data
// @Table(name = "EXTRATOS_INTERVALOS")
@EqualsAndHashCode(callSuper = true)
public class RangeExtractModel extends ExtractTemplateModel {
}
