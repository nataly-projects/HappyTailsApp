package utils;

public enum AnimalStatus {
    ACTIVE("Active"),
    HIDDEN("Hidden");

    private final String displayName;

    AnimalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
