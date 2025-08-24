package cognizant.org.tickets.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponseDTO<T> implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    private String message = "";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    private T data = null;

    @JsonIgnore
    @Builder.Default
    private HttpStatus httpStatus = null;

    @Serial
    private static final long serialVersionUID = -2134609817418787475L;

    public void setResponse (HttpStatus httpStatus, String message, T data) {
        this.message = message;
        this.data = data;
        this.httpStatus =httpStatus;
    }

    public void setResponse (HttpStatus httpStatus, T data) {
        this.setResponse(httpStatus, null, data);
    }

    public void setResponse (HttpStatus httpStatus, String message) {
        this.setResponse(httpStatus, message, null);
    }

}
