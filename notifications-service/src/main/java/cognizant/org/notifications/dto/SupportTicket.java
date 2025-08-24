package cognizant.org.notifications.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
public class SupportTicket implements Serializable {

    private UUID id;

    private String title;

    private String description;

    private String status;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @Serial
    private static final long serialVersionUID = -3411211603040994628L;

}
