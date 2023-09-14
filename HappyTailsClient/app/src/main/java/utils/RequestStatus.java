package utils;

public enum RequestStatus {
    PENDING("Pending"),
    ACCEPT("Accept"),
    DENY("Deny");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
