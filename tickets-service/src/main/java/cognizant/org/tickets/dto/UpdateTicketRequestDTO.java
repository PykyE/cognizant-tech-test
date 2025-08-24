package cognizant.org.tickets.dto;

import cognizant.org.tickets.util.Constants.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateTicketRequestDTO extends CreateTicketRequestDTO implements Serializable {

    public UpdateTicketRequestDTO(String title, String description, String status) {
        super(title, description, status);
    }

    @Serial
    private static final long serialVersionUID = 6179423513054883655L;

}
