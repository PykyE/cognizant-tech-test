package cognizant.org.tickets.service;

import cognizant.org.tickets.dto.APIResponseDTO;
import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;

import java.util.List;

public interface TicketService {

    APIResponseDTO<List<SupportTicket>> getAllTickets();

    APIResponseDTO<SupportTicket> getTicketById(String ticketId);

    APIResponseDTO<SupportTicket> createTicket(CreateTicketRequestDTO request);

    APIResponseDTO<SupportTicket> updateTicket(UpdateTicketRequestDTO request, String ticketId);

    APIResponseDTO<Void> deleteTicket(String ticketId);

}
