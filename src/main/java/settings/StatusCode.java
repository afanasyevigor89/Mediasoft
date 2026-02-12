package settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    CREATED(201),
    OK(200),
    NO_CONTENT(204),
    NOT_FOUND(404),
    BAD_REQUEST(400);

    private final int code;
}