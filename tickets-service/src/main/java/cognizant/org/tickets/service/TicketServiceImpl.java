package cognizant.org.tickets.service;

import cognizant.org.tickets.dto.APIResponseDTO;
import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.EventDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.kafka.TicketEventProducer;
import cognizant.org.tickets.repository.TicketRepository;
import cognizant.org.tickets.util.Constants.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@Log4j2
public class TicketServiceImpl implements TicketService {

    private final TicketRepository tr;
    private final TicketEventProducer tep;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketEventProducer tep) {
        this.tr = ticketRepository;
        this.tep = tep;
    }

    @Override
    public APIResponseDTO<List<SupportTicket>> getAllTickets() {
        APIResponseDTO<List<SupportTicket>> apiResponse = new APIResponseDTO<>();
        List<SupportTicket> tickets;
        try {
            tickets = StreamSupport
                    .stream(tr.findAll().spliterator(), false)
                    .toList();
            apiResponse.setResponse(tickets.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK, tickets);
        } catch (Error e) {
            log.error("Error fetching tickets -> {}", e.getMessage());
            apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return apiResponse;
    }

    @Override
    public APIResponseDTO<SupportTicket> getTicketById(String ticketId) {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        SupportTicket ticket;
        try {
            Optional<SupportTicket> optTicket = tr.findById(UUID.fromString(ticketId));
            ticket = optTicket.orElse(null);
            apiResponse.setResponse(Objects.isNull(ticket) ? HttpStatus.NO_CONTENT : HttpStatus.OK, ticket);
        } catch (Exception e) {
            log.error("Error fetching ticket by id {} -> {}", ticketId, e.getMessage());
            apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return apiResponse;
    }

    @Override
    public APIResponseDTO<SupportTicket> createTicket(CreateTicketRequestDTO request) {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        SupportTicket toSaveTicket = SupportTicket
                .builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(Status.valueOf(request.getStatus()))
                .build();
        try {
            SupportTicket savedTicket = tr.save(toSaveTicket);
            apiResponse.setResponse(HttpStatus.CREATED, savedTicket);
            tep.publishTicketEvent(new EventDTO(Operation.CREATED, savedTicket));
        } catch (Exception e) {
            log.error("Error creating ticket -> {}", e.getMessage());
            apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return apiResponse;
    }

    @Override
    public APIResponseDTO<SupportTicket> updateTicket(UpdateTicketRequestDTO request, String ticketId) {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        try {
            UUID uuid = UUID.fromString(ticketId);
            Optional<SupportTicket> ticketOpt = tr.findById(uuid);
            SupportTicket toUpdateTicket = SupportTicket
                    .builder()
                    .id(UUID.fromString(ticketId))
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(Status.valueOf(request.getStatus()))
                    .build();
            SupportTicket updatedTicket = tr.save(toUpdateTicket);
            apiResponse.setResponse(ticketOpt.isEmpty() ? HttpStatus.CREATED : HttpStatus.OK, updatedTicket);
            tep.publishTicketEvent(new EventDTO(Operation.UPDATED, updatedTicket));
        } catch (Exception e) {
            log.error("Error updating ticket -> {}", e.getMessage());
            apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return apiResponse;
    }

    @Override
    public APIResponseDTO<Void> deleteTicket(String ticketId) {
        APIResponseDTO<Void> apiResponse = new APIResponseDTO<>();
        try {
            UUID uuid = UUID.fromString(ticketId);
            Optional<SupportTicket> ticketOpt = tr.findById(uuid);
            if (ticketOpt.isEmpty()) {
                apiResponse.setResponse(HttpStatus.NOT_FOUND, (Void) null);
            } else {
                tr.deleteById(uuid);
                apiResponse.setResponse(HttpStatus.OK, (Void) null);
                tep.publishTicketEvent(new EventDTO(Operation.DELETED, ticketOpt.get()));
            }
        } catch (Exception e) {
            log.error("Error deleting ticket by id {} -> {}", ticketId, e.getMessage());
            apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return apiResponse;
    }

}
