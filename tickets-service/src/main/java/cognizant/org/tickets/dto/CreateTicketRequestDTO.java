package cognizant.org.tickets.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketRequestDTO implements Serializable {

    @NotEmpty
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    @Pattern(regexp = "OPEN|IN_PROGRESS|RESOLVED", message = "Accepted values: OPEN | IN_PROGRESS | RESOLVED")
    private String status;

    @Serial
    private static final long serialVersionUID = 6267080226532193704L;

}
