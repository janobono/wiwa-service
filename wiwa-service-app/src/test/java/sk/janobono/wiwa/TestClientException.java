package sk.janobono.wiwa;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class TestClientException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;

    public TestClientException(final HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
