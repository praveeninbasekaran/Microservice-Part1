@Override
public StageInfoDto getStageInfo(Long requestId, String whichStage) {
    Objects.requireNonNull(requestId, "requestId is required");
    Objects.requireNonNull(whichStage, "whichStage is required");

    whichStage = whichStage.trim().toLowerCase();

    final String getCurrentStageSql = """
        SELECT current_stage_id
        FROM workflow_manager.workflow_request
        WHERE request_id = ?
        """;

    final String getPreviousStageSql = """
        SELECT stage_id
        FROM workflow_manager.workflow_audit
        WHERE request_id = ?
        ORDER BY action_at DESC
        LIMIT 1
        """;

    final String getStageDetailsSql = """
        SELECT ws.stage_id, ws.name AS stage_name, ra.role
        FROM workflow_manager.workflow_stage ws
        LEFT JOIN workflow_manager.workflow_stage_role_action ra
        ON ws.stage_id = ra.stage_id
        WHERE ws.stage_id = ?
        """;

    final String getNextStageSql = """
        SELECT DISTINCT a.target_stage_id, ws.name AS stage_name, ra.role
        FROM workflow_manager.workflow_stage_role_action ra
        JOIN workflow_manager.workflow_action a ON ra.action_id = a.action_id
        JOIN workflow_manager.workflow_stage ws ON a.target_stage_id = ws.stage_id
        WHERE ra.stage_id = ?
        """;

    try (var conn = dataSource.getConnection()) {
        Long stageId = null;

        if ("current".equals(whichStage)) {
            try (var ps = conn.prepareStatement(getCurrentStageSql)) {
                ps.setLong(1, requestId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) stageId = rs.getLong("current_stage_id");
                }
            }

        } else if ("previous".equals(whichStage)) {
            try (var ps = conn.prepareStatement(getPreviousStageSql)) {
                ps.setLong(1, requestId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) stageId = rs.getLong("stage_id");
                }
            }

        } else if ("next".equals(whichStage)) {
            // get current first
            Long currentStageId = null;
            try (var ps = conn.prepareStatement(getCurrentStageSql)) {
                ps.setLong(1, requestId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) currentStageId = rs.getLong("current_stage_id");
                }
            }
            if (currentStageId == null) {
                throw new IllegalStateException("No current stage found for request: " + requestId);
            }

            try (var ps = conn.prepareStatement(getNextStageSql)) {
                ps.setLong(1, currentStageId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) stageId = rs.getLong("target_stage_id");
                }
            }
        }

        if (stageId == null) {
            throw new IllegalStateException("Could not determine stage for " + whichStage + " on request: " + requestId);
        }

        StageInfoDto dto = new StageInfoDto();
        dto.setStageId(stageId);

        List<String> roles = new ArrayList<>();
        try (var ps = conn.prepareStatement(getStageDetailsSql)) {
            ps.setLong(1, stageId);
            try (var rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    if (!found) {
                        dto.setStageName(rs.getString("stage_name"));
                        found = true;
                    }
                    String role = rs.getString("role");
                    if (role != null) roles.add(role);
                }
            }
        }

        dto.setEligibleRoles(roles);

        return dto;

    } catch (SQLException e) {
        throw new RuntimeException("Error getting stage info: " + e.getMessage(), e);
    }
}