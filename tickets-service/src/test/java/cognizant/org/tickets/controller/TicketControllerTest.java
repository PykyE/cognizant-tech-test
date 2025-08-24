package cognizant.org.tickets.controller;

import cognizant.org.tickets.Data;
import cognizant.org.tickets.dto.APIResponseDTO;
import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.kafka.TicketEventProducer;
import cognizant.org.tickets.service.TicketService;
import cognizant.org.tickets.util.Constants.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketEventProducer ticketEventProducer;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTickets_returnsTickets() throws Exception {
        APIResponseDTO<List<SupportTicket>> apiResponse = new APIResponseDTO<>();
        List<SupportTicket> stList = List.of(Data.createSupportTicketMock().orElseThrow());
        apiResponse.setResponse(HttpStatus.OK, stList);

        when(ticketService.getAllTickets()).thenReturn(apiResponse);

        mvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].title").value("mock ticket title"))
                .andExpect(jsonPath("$.data[0].description").value("mock ticket description"))
                .andExpect(jsonPath("$.data[0].status").value(Status.OPEN.name()))
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.data[0].updatedAt").exists());

        verify(ticketService).getAllTickets();
    }

    @Test
    void getAllTickets_returnsNoTickets() throws Exception {
        APIResponseDTO<List<SupportTicket>> apiResponse = new APIResponseDTO<>();
        List<SupportTicket> stList = null;
        apiResponse.setResponse(HttpStatus.NO_CONTENT, stList);

        when(ticketService.getAllTickets()).thenReturn(apiResponse);

        mvc.perform(get("/tickets"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(ticketService).getAllTickets();
    }

    @Test
    void getTicketById_returnsTicket() throws Exception {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        SupportTicket stMock = Data.createSupportTicketMock().orElseThrow();
        apiResponse.setResponse(HttpStatus.OK, stMock);

        when(ticketService.getTicketById(stMock.getId().toString())).thenReturn(apiResponse);

        mvc.perform(get("/tickets/" + stMock.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("mock ticket title"))
                .andExpect(jsonPath("$.data.description").value("mock ticket description"))
                .andExpect(jsonPath("$.data.status").value(Status.OPEN.name()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());

        verify(ticketService).getTicketById(stMock.getId().toString());
    }

    @Test
    void getTicketById_returnsNoTicket() throws Exception {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        SupportTicket stMock = null;
        apiResponse.setResponse(HttpStatus.NO_CONTENT, stMock);

        String ticketId = "dec845ab-90d4-4cff-92ce-e8b7b6c0724b";

        when(ticketService.getTicketById(ticketId)).thenReturn(apiResponse);

        mvc.perform(get("/tickets/" + ticketId))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(ticketService).getTicketById(ticketId);
    }

    @Test
    void getTicketById_withWrongUUID_returnsError() throws Exception {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        String wrongUUID = "123abc";
        String errMsg = "Invalid UUID string: " + wrongUUID;
        apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, errMsg);

        when(ticketService.getTicketById(wrongUUID)).thenReturn(apiResponse);

        mvc.perform(get("/tickets/" + wrongUUID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errMsg));

        verify(ticketService).getTicketById(wrongUUID);
    }

    @Test
    void createTicket_returnsCreatedTicket() throws Exception {
        CreateTicketRequestDTO request = Data.createTicketRequestMock().orElseThrow();
        SupportTicket requestResponse = Data.createTicketResponseMock(request).orElseThrow();
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        apiResponse.setResponse(HttpStatus.CREATED, requestResponse);

        when(ticketService.createTicket(request)).thenReturn(apiResponse);

        mvc.perform(post("/tickets").content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").value(requestResponse.getId().toString()))
                .andExpect(jsonPath("$.data.title").value(request.getTitle()))
                .andExpect(jsonPath("$.data.description").value(request.getDescription()))
                .andExpect(jsonPath("$.data.status").value(request.getStatus()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());

        verify(ticketService).createTicket(request);
    }

    @Test
    void updateTicket_returnsUpdatedTicket_whenTicketExists() throws Exception {
        UpdateTicketRequestDTO request = Data.updateTicketRequestMock().orElseThrow();
        String ticketId = "dec845ab-90d4-4cff-92ce-e8b7b6c0724b";
        SupportTicket requestResponse = Data.updateTicketResponseMock(request, UUID.fromString(ticketId)).orElseThrow();
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        apiResponse.setResponse(HttpStatus.OK, requestResponse);

        when(ticketService.updateTicket(request, ticketId)).thenReturn(apiResponse);

        mvc.perform(put("/tickets/" + ticketId).content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").value(ticketId))
                .andExpect(jsonPath("$.data.title").value(request.getTitle()))
                .andExpect(jsonPath("$.data.description").value(request.getDescription()))
                .andExpect(jsonPath("$.data.status").value(request.getStatus()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());

        verify(ticketService).updateTicket(request, ticketId);
    }

    @Test
    void updateTicket_returnsNewTicket_whenNoTicketExists() throws Exception {
        UpdateTicketRequestDTO request = Data.updateTicketRequestMock().orElseThrow();
        String ticketId = "dec845ab-90d4-4cff-92ce-e8b7b6c0724b";
        SupportTicket requestResponse = Data.updateTicketResponseMock(request, UUID.fromString(ticketId)).orElseThrow();
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        apiResponse.setResponse(HttpStatus.CREATED, requestResponse);

        when(ticketService.updateTicket(request, ticketId)).thenReturn(apiResponse);

        mvc.perform(put("/tickets/" + ticketId).content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.id").value(ticketId))
                .andExpect(jsonPath("$.data.title").value(request.getTitle()))
                .andExpect(jsonPath("$.data.description").value(request.getDescription()))
                .andExpect(jsonPath("$.data.status").value(request.getStatus()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());

        verify(ticketService).updateTicket(request, ticketId);
    }

    @Test
    void deleteTicket_returnsOk_whenTicketExists() throws Exception {
        String ticketId = "dec845ab-90d4-4cff-92ce-e8b7b6c0724b";
        APIResponseDTO<Void> apiResponse = new APIResponseDTO<>();
        apiResponse.setResponse(HttpStatus.OK, (String) null);

        when(ticketService.deleteTicket(ticketId)).thenReturn(apiResponse);

        mvc.perform(delete("/tickets/" + ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(ticketService).deleteTicket(ticketId);
    }

    @Test
    void deleteTicket_returnsNotFound_whenNoTicketExists() throws Exception {
        String ticketId = "dec845ab-90d4-4cff-92ce-e8b7b6c0724b";
        APIResponseDTO<Void> apiResponse = new APIResponseDTO<>();
        apiResponse.setResponse(HttpStatus.NOT_FOUND, (String) null);

        when(ticketService.deleteTicket(ticketId)).thenReturn(apiResponse);

        mvc.perform(delete("/tickets/" + ticketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").exists());

        verify(ticketService).deleteTicket(ticketId);
    }

}