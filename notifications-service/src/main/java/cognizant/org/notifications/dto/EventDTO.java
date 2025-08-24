package cognizant.org.notifications.dto;

import cognizant.org.notifications.util.Constants.Operation;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
public class EventDTO implements Serializable {

    private Operation eventType;

    private UUID ticketId;

    private String timestamp;

    private SupportTicket payload;

    @Serial
    private static final long serialVersionUID = 7387469412502649193L;

}
