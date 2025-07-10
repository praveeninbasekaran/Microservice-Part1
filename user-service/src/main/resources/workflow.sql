CREATE SCHEMA IF NOT EXISTS drm_sit;

CREATE TABLE drm_sit.workflow_definition (
    workflow_id BIGSERIAL PRIMARY KEY,
    project VARCHAR(100) NOT NULL,
    module VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200),
    description TEXT
);

CREATE TABLE drm_sit.workflow_stage (
    stage_id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT REFERENCES drm_sit.workflow_definition(workflow_id),
    stage_order INT NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200)
);

CREATE TABLE drm_sit.workflow_action (
    action_id BIGSERIAL PRIMARY KEY,
    stage_id BIGINT REFERENCES drm_sit.workflow_stage(stage_id),
    action VARCHAR(50) NOT NULL,
    target_stage_id BIGINT REFERENCES drm_sit.workflow_stage(stage_id),
    is_terminal BOOLEAN DEFAULT FALSE
);

CREATE TABLE drm_sit.workflow_stage_role_action (
    role_action_id BIGSERIAL PRIMARY KEY,
    stage_id BIGINT REFERENCES drm_sit.workflow_stage(stage_id),
    role VARCHAR(50) NOT NULL,
    action_id BIGINT REFERENCES drm_sit.workflow_action(action_id)
);

-------------------------------
-- Insert workflow definition
INSERT INTO drm_sit.workflow_definition (project, module, code, name, description)
VALUES ('Risk Management', 'Alert Workflow', 'ALERT_WF', 'Alert Handling Workflow', 'Workflow to manage alert states.');

-- Insert workflow stages
INSERT INTO drm_sit.workflow_stage (workflow_id, stage_order, code, name)
VALUES 
    (1, 1, 'CREATED', 'Alert Created'),
    (1, 2, 'REVIEW', 'Review Alert'),
    (1, 3, 'RESOLVED', 'Resolve Alert');

-- Insert workflow actions
INSERT INTO drm_sit.workflow_action (stage_id, action, target_stage_id, is_terminal)
VALUES 
    (1, 'Submit for Review', 2, FALSE),
    (2, 'Approve', 3, TRUE),
    (2, 'Reject', 1, FALSE);

-- Insert stage role actions
INSERT INTO drm_sit.workflow_stage_role_action (stage_id, role, action_id)
VALUES 
    (1, 'Analyst', 1),
    (2, 'Reviewer', 2),
    (2, 'Reviewer', 3);
--------------------------------------------------------------------------------
select * from drm_sit.workflow_definition;
select * from drm_sit.workflow_stage;
select * from drm_sit.workflow_action;
select * from drm_sit.workflow_stage_role_action;
--------------------------------------------------------------------------------

-- DELETE existing records (maintaining referential integrity order)
DELETE FROM drm_sit.workflow_stage_role_action;
DELETE FROM drm_sit.workflow_action;
DELETE FROM drm_sit.workflow_stage;
DELETE FROM drm_sit.workflow_definition;

-- RESET sequences (optional, recommended for clean IDs)
ALTER SEQUENCE drm_sit.workflow_definition_workflow_id_seq RESTART WITH 1;
ALTER SEQUENCE drm_sit.workflow_stage_stage_id_seq RESTART WITH 1;
ALTER SEQUENCE drm_sit.workflow_action_action_id_seq RESTART WITH 1;
ALTER SEQUENCE drm_sit.workflow_stage_role_action_role_action_id_seq RESTART WITH 1;

-- INSERT fresh workflow definition
INSERT INTO drm_sit.workflow_definition (project, module, code, name, description)
VALUES ('Risk Management', 'Alert Workflow', 'ALERT_WF', 'Alert Handling Workflow', 'Workflow to manage alert states.');

-- INSERT fresh workflow stages
INSERT INTO drm_sit.workflow_stage (workflow_id, stage_order, code, name)
VALUES 
    (1, 1, 'CREATED', 'Alert Created'),
    (1, 2, 'UNDER_REVIEW', 'Under Review'),
    (1, 3, 'PENDING_APPROVAL', 'Pending Approval'),
    (1, 4, 'CLOSED', 'Closed');

-- INSERT fresh workflow actions
INSERT INTO drm_sit.workflow_action (stage_id, action, target_stage_id, is_terminal)
VALUES 
    -- CREATED stage actions
    (1, 'Submit for Review', 2, FALSE),
    (1, 'Close Directly', 4, TRUE),
    
    -- UNDER_REVIEW stage actions
    (2, 'Refer Back', 1, FALSE),
    (2, 'Submit for Approval', 3, FALSE),
    (2, 'Close as Invalid', 4, TRUE),
    
    -- PENDING_APPROVAL stage actions
    (3, 'Approved', 4, TRUE),
    (3, 'Rejected - Refer back', 2, FALSE),
    (3, 'Request More Info', 1, FALSE),

    -- CLOSED stage actions
    (4, 'Reopen', 1, FALSE);

-- INSERT fresh workflow stage role actions (2 per role)
INSERT INTO drm_sit.workflow_stage_role_action (stage_id, role, action_id)
VALUES 
    -- Analyst (CREATED stage)
    (1, 'Analyst', 1), -- Submit for Review
    (1, 'Analyst', 2), -- Close Directly

    -- Reviewer (UNDER_REVIEW stage)
    (2, 'Reviewer', 3), -- Refer Back
    (2, 'Reviewer', 4), -- Submit for Approval

    -- Approver (PENDING_APPROVAL stage)
    (3, 'Approver', 6), -- Approved
    (3, 'Approver', 7), -- Rejected - Refer back

    -- Manager (CLOSED stage)
    (4, 'Manager', 9); -- Reopen
