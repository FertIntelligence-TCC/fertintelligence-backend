CREATE TABLE IF NOT EXISTS summary_recommendations (
    id BIGSERIAL PRIMARY KEY,
    id_recommendation BIGINT NOT NULL,
    document_name VARCHAR(255) NOT NULL DEFAULT 'Recomendação Resumida',
    technical_report TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_summary_recommendations_recommendation UNIQUE (id_recommendation),
    CONSTRAINT fk_summary_recommendations_recommendation
        FOREIGN KEY (id_recommendation)
        REFERENCES recommendations (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS direct_recommendations (
    id BIGSERIAL PRIMARY KEY,
    id_recommendation BIGINT NOT NULL,
    document_name VARCHAR(255) NOT NULL DEFAULT 'Recomendação Direta',
    technical_report TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_direct_recommendations_recommendation UNIQUE (id_recommendation),
    CONSTRAINT fk_direct_recommendations_recommendation
        FOREIGN KEY (id_recommendation)
        REFERENCES recommendations (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shopping_lists (
    id BIGSERIAL PRIMARY KEY,
    id_recommendation BIGINT NOT NULL,
    document_name VARCHAR(255) NOT NULL DEFAULT 'Lista de Compras',
    technical_report TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_shopping_lists_recommendation UNIQUE (id_recommendation),
    CONSTRAINT fk_shopping_lists_recommendation
        FOREIGN KEY (id_recommendation)
        REFERENCES recommendations (id)
        ON DELETE CASCADE
);
