package settings;

public enum Category {
    FRUITS("FRUITS"),
    VEGETABLES("VEGETABLES");

    private final String name;

    Category(String name) {
        this.name = name;

    }
    public String getName() {
        return name;
    }
}
