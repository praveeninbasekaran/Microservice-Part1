-- 1️⃣ Add primary key to audit table
ALTER TABLE sb_55313_i16.drm.rcsa_alert_management_audit
ADD COLUMN audit_id SERIAL PRIMARY KEY;

-- 2️⃣ Add missing columns
ALTER TABLE sb_55313_i16.drm.rcsa_alert_management_audit
ADD COLUMN latest_risk_rating character varying(255) COLLATE pg_catalog."default",
ADD COLUMN latest_approved_final_rr_rating character varying(100) COLLATE pg_catalog."default",
ADD COLUMN country_id character varying(50) COLLATE pg_catalog."default",
ADD COLUMN legal_entity_id character varying(50) COLLATE pg_catalog."default",
ADD COLUMN inherent_risk_value character varying(255) COLLATE pg_catalog."default",
ADD COLUMN latest_approved_final_rr_value character varying(255) COLLATE pg_catalog."default",
ADD COLUMN business_function_11_name character varying(255) COLLATE pg_catalog."default",
ADD COLUMN business_function_12_name character varying(255) COLLATE pg_catalog."default",
ADD COLUMN business_function_13_name character varying(255) COLLATE pg_catalog."default",
ADD COLUMN process_id character varying(255) COLLATE pg_catalog."default";

-- 3️⃣ Add foreign key constraint on alert_id
ALTER TABLE sb_55313_i16.drm.rcsa_alert_management_audit
ADD CONSTRAINT fk_alert_id_audit FOREIGN KEY (alert_id)
REFERENCES sb_55313_i16.drm.rcsa_alert_management (alert_id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;