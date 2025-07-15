-- 🔷 Add assigned_to column to main table if not exists
ALTER TABLE sb_55313_116.drm.rcsa_alert_management
ADD COLUMN IF NOT EXISTS assigned_to character varying(100);

-- 🔷 Add assigned_to column to audit table if not exists
ALTER TABLE sb_55313_116.drm.rcsa_alert_management_audit
ADD COLUMN IF NOT EXISTS assigned_to character varying(100);

-- 🔷 Create/Replace trigger function
CREATE OR REPLACE FUNCTION sb_55313_116.fn_rcsa_alert_management_audit()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        INSERT INTO sb_55313_116.drm.rcsa_alert_management_audit (
            audit_id,
            alert_id,
            rule_id,
            rule_version,
            l1_risk_id,
            l2_risk_id,
            l3_risk_id,
            business_function_l1_id,
            business_function_l2_id,
            business_function_l3_id,
            country,
            legal_entity,
            stage,
            alert_date,
            type_of_alert,
            rule_name,
            process_name,
            inherent_risk_rating,
            control_effectiveness,
            last_approved_final_rr_rating,
            latest_risk_rating,
            assigned_role,
            assigned_to,
            alert_status,
            due_date,
            created_date,
            updated_date,
            updated_by,
            last_action_taken,
            last_action_date,
            latest_reminder_date,
            escalation_date,
            atom_case_id,
            country_id,
            legal_entity_id,
            inherent_risk_value,
            last_approved_final_rr_value,
            latest_risk_rating_value,
            created_by,
            l1_risk_name,
            l2_risk_name,
            l3_risk_name,
            business_function_l1_name,
            business_function_l2_name,
            business_function_l3_name,
            process_id
        )
        VALUES (
            nextval('sb_55313_116.drm.rcsa_alert_management_audit_audit_id_seq'),
            NEW.alert_id,
            NEW.rule_id,
            NEW.rule_version,
            NEW.l1_risk_id,
            NEW.l2_risk_id,
            NEW.l3_risk_id,
            NEW.business_function_l1_id,
            NEW.business_function_l2_id,
            NEW.business_function_l3_id,
            NEW.country,
            NEW.legal_entity,
            NEW.stage,
            NEW.alert_date,
            NEW.type_of_alert,
            NEW.rule_name,
            NEW.process_name,
            NEW.inherent_risk_rating,
            NEW.control_effectiveness,
            NEW.last_approved_final_rr_rating,
            NEW.latest_risk_rating,
            NEW.assigned_role,
            NEW.assigned_to,
            NEW.alert_status,
            NEW.due_date,
            NEW.created_date,
            NEW.updated_date,
            NEW.updated_by,
            NEW.last_action_taken,
            NEW.last_action_date,
            NEW.latest_reminder_date,
            NEW.escalation_date,
            NEW.atom_case_id,
            NEW.country_id,
            NEW.legal_entity_id,
            NEW.inherent_risk_value,
            NEW.last_approved_final_rr_value,
            NEW.latest_risk_rating_value,
            NEW.created_by,
            NEW.l1_risk_name,
            NEW.l2_risk_name,
            NEW.l3_risk_name,
            NEW.business_function_l1_name,
            NEW.business_function_l2_name,
            NEW.business_function_l3_name,
            NEW.process_id
        );

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO sb_55313_116.drm.rcsa_alert_management_audit (
            audit_id,
            alert_id,
            rule_id,
            rule_version,
            l1_risk_id,
            l2_risk_id,
            l3_risk_id,
            business_function_l1_id,
            business_function_l2_id,
            business_function_l3_id,
            country,
            legal_entity,
            stage,
            alert_date,
            type_of_alert,
            rule_name,
            process_name,
            inherent_risk_rating,
            control_effectiveness,
            last_approved_final_rr_rating,
            latest_risk_rating,
            assigned_role,
            assigned_to,
            alert_status,
            due_date,
            created_date,
            updated_date,
            updated_by,
            last_action_taken,
            last_action_date,
            latest_reminder_date,
            escalation_date,
            atom_case_id,
            country_id,
            legal_entity_id,
            inherent_risk_value,
            last_approved_final_rr_value,
            latest_risk_rating_value,
            created_by,
            l1_risk_name,
            l2_risk_name,
            l3_risk_name,
            business_function_l1_name,
            business_function_l2_name,
            business_function_l3_name,
            process_id
        )
        VALUES (
            nextval('sb_55313_116.drm.rcsa_alert_management_audit_audit_id_seq'),
            OLD.alert_id,
            OLD.rule_id,
            OLD.rule_version,
            OLD.l1_risk_id,
            OLD.l2_risk_id,
            OLD.l3_risk_id,
            OLD.business_function_l1_id,
            OLD.business_function_l2_id,
            OLD.business_function_l3_id,
            OLD.country,
            OLD.legal_entity,
            OLD.stage,
            OLD.alert_date,
            OLD.type_of_alert,
            OLD.rule_name,
            OLD.process_name,
            OLD.inherent_risk_rating,
            OLD.control_effectiveness,
            OLD.last_approved_final_rr_rating,
            OLD.latest_risk_rating,
            OLD.assigned_role,
            OLD.assigned_to,
            OLD.alert_status,
            OLD.due_date,
            OLD.created_date,
            OLD.updated_date,
            OLD.updated_by,
            OLD.last_action_taken,
            OLD.last_action_date,
            OLD.latest_reminder_date,
            OLD.escalation_date,
            OLD.atom_case_id,
            OLD.country_id,
            OLD.legal_entity_id,
            OLD.inherent_risk_value,
            OLD.last_approved_final_rr_value,
            OLD.latest_risk_rating_value,
            OLD.created_by,
            OLD.l1_risk_name,
            OLD.l2_risk_name,
            OLD.l3_risk_name,
            OLD.business_function_l1_name,
            OLD.business_function_l2_name,
            OLD.business_function_l3_name,
            OLD.process_id
        );
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- 🔷 Drop existing trigger if any
DROP TRIGGER IF EXISTS trg_rcsa_alert_management_audit 
ON sb_55313_116.drm.rcsa_alert_management;

-- 🔷 Create the trigger
CREATE TRIGGER trg_rcsa_alert_management_audit
AFTER INSERT OR UPDATE OR DELETE
ON sb_55313_116.drm.rcsa_alert_management
FOR EACH ROW
EXECUTE FUNCTION sb_55313_116.fn_rcsa_alert_management_audit();