package cognizant.org.tickets.dto;

import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.util.Constants.Operation;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class EventDTO implements Serializable {

    private Operation eventType;

    private UUID ticketId;

    private String timestamp;

    private SupportTicket payload;

    public EventDTO(Operation eventType, SupportTicket data) {
        this.eventType = eventType;
        this.ticketId = data.getId();
        this.timestamp = Instant.now().toString();
        this.payload = data;
    }

    @Serial
    private static final long serialVersionUID = 6407748994022671922L;

}
