package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_INTERVALOS")
@EqualsAndHashCode
public class RangeExtractModel extends ExtractTemplateModel {
}
