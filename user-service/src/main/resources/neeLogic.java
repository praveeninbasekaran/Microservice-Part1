public StageInfoDto getStageInfo(Long workflowInstanceId, String stage) {
    Objects.requireNonNull(workflowInstanceId, "workflowInstanceId is required");
    Objects.requireNonNull(stage, "stage is required");

    long currentStageId;

    // 1. Get current stageId
    try (Connection conn = dataSource.getConnection()) {
        String getCurrentStageSql = "SELECT current_stage_id FROM workflow_request WHERE request_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(getCurrentStageSql)) {
            ps.setLong(1, workflowInstanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentStageId = rs.getLong("current_stage_id");
                } else {
                    throw new IllegalArgumentException("Invalid workflowInstanceId: " + workflowInstanceId);
                }
            }
        }

        Long targetStageId = currentStageId;

        switch (stage.toLowerCase()) {
            case "next":
                targetStageId = getAdjacentStageId(conn, currentStageId, +1);
                break;
            case "previous":
                targetStageId = getAdjacentStageId(conn, currentStageId, -1);
                break;
            case "current":
                // keep targetStageId as is
                break;
            default:
                throw new IllegalArgumentException("Invalid stage parameter: " + stage);
        }

        // 2. Get stage details and eligible roles
        String stageQuery = """
            SELECT s.stage_id, s.stage_name, r.role
            FROM workflow_stage s
            JOIN workflow_stage_role r ON s.stage_id = r.stage_id
            WHERE s.stage_id = ?
        """;

        StageInfoDto dto = new StageInfoDto();
        dto.setStageId(targetStageId);

        Set<String> roles = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(stageQuery)) {
            ps.setLong(1, targetStageId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    if (!found) {
                        dto.setStageName(rs.getString("stage_name"));
                        found = true;
                    }
                    roles.add(rs.getString("role"));
                }

                if (!found) {
                    throw new IllegalStateException("No stage found for stageId: " + targetStageId);
                }
            }
        }

        dto.setEligibleRoles(new ArrayList<>(roles));
        return dto;

    } catch (SQLException e) {
        throw new RuntimeException("Error fetching stage info", e);
    }
}

// Helper method to get next/previous stage
private Long getAdjacentStageId(Connection conn, Long currentStageId, int direction) throws SQLException {
    String sql = """
        SELECT stage_id FROM workflow_stage
        WHERE stage_order = (
            SELECT stage_order + ? FROM workflow_stage WHERE stage_id = ?
        )
    """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, direction);
        ps.setLong(2, currentStageId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("stage_id");
            } else {
                throw new IllegalStateException("No adjacent stage found for stageId: " + currentStageId);
            }
        }
    }
}