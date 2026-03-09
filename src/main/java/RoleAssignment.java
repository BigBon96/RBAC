public interface RoleAssignment {
    String assignmentId();
    User user();
    Role role();
    AssignmentMetadata metadata();
    boolean isActive();
    String assignmentType();

    default String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[")
                .append(assignmentType())
                .append("] ")
                .append(role().getName())
                .append(" assigned to ")
                .append(user().username())
                .append(" by ")
                .append(metadata().assignedBy())
                .append(" at ")
                .append(metadata().assignedAt())
                .append("\n");

        sb.append("Reason ")
                .append(metadata().reason())
                .append("\n");

        sb.append("Status: ")
                .append(isActive() ? "ACTIVE" : "INACTIVE");

        return sb.toString();
    }
}

