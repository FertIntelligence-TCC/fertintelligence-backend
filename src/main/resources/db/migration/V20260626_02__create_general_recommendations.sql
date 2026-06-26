ALTER TABLE recommendations
    ADD COLUMN IF NOT EXISTS recommendation_folder_name VARCHAR(255);

CREATE TABLE IF NOT EXISTS general_recommendations (
    id BIGSERIAL PRIMARY KEY,
    id_recommendation BIGINT NOT NULL,
    document_name VARCHAR(255) NOT NULL DEFAULT 'Recomendação Geral',
    technical_report TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_general_recommendations_recommendation UNIQUE (id_recommendation),
    CONSTRAINT fk_general_recommendations_recommendation
        FOREIGN KEY (id_recommendation)
        REFERENCES recommendations (id)
        ON DELETE CASCADE
);
