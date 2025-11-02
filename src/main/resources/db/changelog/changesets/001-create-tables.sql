--liquibase formatted sql

--changeset digital-health-team:001-create-patients-table
CREATE TABLE IF NOT EXISTS tb_patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier BIGINT NOT NULL UNIQUE,
    given_name VARCHAR(100) NOT NULL,
    family_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    birth_date DATE,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATE,
    soft_delete BOOLEAN DEFAULT FALSE NOT NULL,
    
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE')),
    CONSTRAINT chk_identifier_range CHECK (identifier >= 1000000 AND identifier <= 99999999)
);
CREATE INDEX IF NOT EXISTS idx_patients_identifier ON tb_patients(identifier);
CREATE INDEX IF NOT EXISTS idx_patients_family_name ON tb_patients(family_name);
CREATE INDEX IF NOT EXISTS idx_patients_given_name ON tb_patients(given_name);
CREATE INDEX IF NOT EXISTS idx_patients_birth_date ON tb_patients(birth_date);
CREATE INDEX IF NOT EXISTS idx_patients_soft_delete ON tb_patients(soft_delete);
CREATE INDEX IF NOT EXISTS idx_patients_search ON tb_patients(family_name, given_name, birth_date);

--changeset digital-health-team:002-create-encounters-table
CREATE TABLE IF NOT EXISTS tb_encounters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    encounter_start TIMESTAMP NOT NULL,
    encounter_end TIMESTAMP,
    encounter_date DATE NOT NULL,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATE,
    soft_delete BOOLEAN DEFAULT FALSE NOT NULL,
    
    CONSTRAINT fk_encounters_patient FOREIGN KEY (patient_id) 
        REFERENCES tb_patients(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_encounters_patient_id ON tb_encounters(patient_id);
CREATE INDEX IF NOT EXISTS idx_encounters_start ON tb_encounters(encounter_start);
CREATE INDEX IF NOT EXISTS idx_encounters_date ON tb_encounters(encounter_date);
CREATE INDEX IF NOT EXISTS idx_encounters_soft_delete ON tb_encounters(soft_delete);

--changeset digital-health-team:003-create-observation-table
CREATE TABLE IF NOT EXISTS observation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    encounter_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL,
    observation_value VARCHAR(500) NOT NULL,
    effective_date_time TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATE,
    soft_delete BOOLEAN DEFAULT FALSE NOT NULL,
    
    CONSTRAINT fk_observation_patient FOREIGN KEY (patient_id) 
        REFERENCES tb_patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_observation_encounter FOREIGN KEY (encounter_id) 
        REFERENCES tb_encounters(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_observation_patient_id ON observation(patient_id);
CREATE INDEX IF NOT EXISTS idx_observation_encounter_id ON observation(encounter_id);
CREATE INDEX IF NOT EXISTS idx_observation_code ON observation(code);
CREATE INDEX IF NOT EXISTS idx_observation_effective_date ON observation(effective_date_time);
CREATE INDEX IF NOT EXISTS idx_observation_soft_delete ON observation(soft_delete);
