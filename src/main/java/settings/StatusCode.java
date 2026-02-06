package settings;

public enum StatusCode {
    CREATED(201),
    OK(200),
    NO_CONTENT(204),
    NOT_FOUND(404);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}