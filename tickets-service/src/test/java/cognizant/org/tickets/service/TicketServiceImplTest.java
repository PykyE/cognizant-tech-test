package cognizant.org.tickets.service;

import cognizant.org.tickets.Data;
import cognizant.org.tickets.dto.APIResponseDTO;
import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.EventDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.kafka.TicketEventProducer;
import cognizant.org.tickets.repository.TicketRepository;
import cognizant.org.tickets.util.Constants.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    private TicketRepository ticketRepository;
    private TicketEventProducer ticketEventProducer;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        ticketEventProducer = mock(TicketEventProducer.class);
        ticketService = new TicketServiceImpl(ticketRepository, ticketEventProducer);
    }

    @Test
    void getAllTickets_returnsTickets() {
        SupportTicket ticket = Data.createSupportTicketMock().orElseThrow();

        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        APIResponseDTO<List<SupportTicket>> response = ticketService.getAllTickets();

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("mock ticket title", response.getData().get(0).getTitle());
    }

    @Test
    void getAllTickets_returnsNoContent() {
        when(ticketRepository.findAll()).thenReturn(Collections.emptyList());

        APIResponseDTO<List<SupportTicket>> response = ticketService.getAllTickets();

        assertEquals(HttpStatus.NO_CONTENT, response.getHttpStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void getTicketById_returnsTicket_whenExists() {
        Optional<SupportTicket> ticket = Data.createSupportTicketMock();
        UUID id = ticket.orElseThrow().getId();

        when(ticketRepository.findById(id)).thenReturn(ticket);

        APIResponseDTO<SupportTicket> response = ticketService.getTicketById(id.toString());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals(id, response.getData().getId());
    }

    @Test
    void getTicketById_returnsNoContent_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        APIResponseDTO<SupportTicket> response = ticketService.getTicketById(id.toString());

        assertEquals(HttpStatus.NO_CONTENT, response.getHttpStatus());
        assertNull(response.getData());
    }

    @Test
    void createTicket_returnsCreatedTicket() throws JsonProcessingException {
        CreateTicketRequestDTO request = Data.createTicketRequestMock().orElseThrow();
        SupportTicket requestResponse = Data.createTicketResponseMock(request).orElseThrow();

        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(requestResponse);

        APIResponseDTO<SupportTicket> response = ticketService.createTicket(request);

        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals("mock ticket title", response.getData().getTitle());

        ArgumentCaptor<EventDTO> eventCaptor = ArgumentCaptor.forClass(EventDTO.class);
        verify(ticketEventProducer).publishTicketEvent(eventCaptor.capture());

        EventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(Operation.CREATED, capturedEvent.getEventType());
        assertEquals(requestResponse.getId(), capturedEvent.getPayload().getId());
    }

    @Test
    void updateTicket_returnsUpdatedTicket() throws JsonProcessingException {
        UUID ticketID = UUID.randomUUID();
        UpdateTicketRequestDTO request = Data.updateTicketRequestMock().orElseThrow();
        SupportTicket requestResponse = Data.updateTicketResponseMock(request, ticketID).orElseThrow();

        when(ticketRepository.findById(any())).thenReturn(Data.createSupportTicketMock());
        when(ticketRepository.save(any(SupportTicket.class))).thenReturn(requestResponse);

        APIResponseDTO<SupportTicket> response = ticketService.updateTicket(request, ticketID.toString());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());
        assertEquals("mock ticket title", response.getData().getTitle());

        ArgumentCaptor<EventDTO> eventCaptor = ArgumentCaptor.forClass(EventDTO.class);
        verify(ticketEventProducer).publishTicketEvent(eventCaptor.capture());

        EventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(Operation.UPDATED, capturedEvent.getEventType());
        assertEquals(requestResponse.getId(), capturedEvent.getPayload().getId());
    }

    @Test
    void deleteTicket_returnsOk() throws JsonProcessingException {
        SupportTicket ticket = Data.createSupportTicketMock().orElseThrow();
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        doNothing().when(ticketRepository).deleteById(ticket.getId());

        APIResponseDTO<Void> response = ticketService.deleteTicket(ticket.getId().toString());

        assertEquals(HttpStatus.OK, response.getHttpStatus());
        verify(ticketRepository).deleteById(ticket.getId());

        ArgumentCaptor<EventDTO> eventCaptor = ArgumentCaptor.forClass(EventDTO.class);
        verify(ticketEventProducer).publishTicketEvent(eventCaptor.capture());

        EventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(Operation.DELETED, capturedEvent.getEventType());
        assertEquals(ticket.getId(), capturedEvent.getPayload().getId());
    }

}