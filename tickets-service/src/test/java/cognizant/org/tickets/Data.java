package cognizant.org.tickets;

import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.util.Constants.Status;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class Data {

    public static Optional<SupportTicket> createSupportTicketMock() {
        Timestamp timestamp = Timestamp.from(Instant.now());
        return Optional.of(new SupportTicket(UUID.randomUUID(), "mock ticket title", "mock ticket description", Status.OPEN, timestamp, timestamp));
    }

    public static Optional<CreateTicketRequestDTO> createTicketRequestMock() {
        return Optional.of(new CreateTicketRequestDTO("mock ticket title", "mock ticket description", "OPEN"));
    }

    public static Optional<SupportTicket> createTicketResponseMock(CreateTicketRequestDTO request) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        return Optional.of(new SupportTicket(UUID.randomUUID(), request.getTitle(), request.getDescription(), Status.valueOf(request.getStatus()), timestamp, timestamp));
    }

    public static Optional<UpdateTicketRequestDTO> updateTicketRequestMock() {
        return Optional.of(new UpdateTicketRequestDTO("mock ticket title", "mock ticket description", "OPEN"));
    }

    public static Optional<SupportTicket> updateTicketResponseMock(UpdateTicketRequestDTO request, UUID ticketId) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        return Optional.of(new SupportTicket(ticketId, request.getTitle(), request.getDescription(), Status.valueOf(request.getStatus()), timestamp, timestamp));
    }

}
