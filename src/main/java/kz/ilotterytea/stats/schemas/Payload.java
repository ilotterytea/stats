package kz.ilotterytea.stats.schemas;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class Payload<T> {
    private final Integer status;
    private final String message;
    private final T data;

    public Payload(T data) {
        this.status = 200;
        this.message = "Success!";
        this.data = data;
    }

    public Payload(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
