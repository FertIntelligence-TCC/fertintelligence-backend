package com.migueltcc.fertintelligence.model.fertintelligence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "RECOMENDACOES_DIRETAS")
@EqualsAndHashCode(exclude = "recommendation")
public class DirectRecommendationModel {

    public static final String DOCUMENT_NAME = "Recomendação Direta";
    public static final String PENDING_TECHNICAL_REPORT = "Documento ainda não gerado pelo motor técnico definitivo.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "ID_RECOMMENDATION", nullable = false, unique = true)
    RecommendationModel recommendation;

    @Column(name = "DOCUMENT_NAME", nullable = false)
    String documentName;

    @Lob
    @Column(name = "TECHNICAL_REPORT", nullable = false)
    String technicalReport;

    @Column(name = "CREATED_AT", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        if (this.documentName == null || this.documentName.isBlank()) {
            this.documentName = DOCUMENT_NAME;
        }
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        if (this.documentName == null || this.documentName.isBlank()) {
            this.documentName = DOCUMENT_NAME;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
