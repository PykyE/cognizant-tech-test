package cognizant.org.tickets.controller;

import cognizant.org.tickets.dto.APIResponseDTO;
import cognizant.org.tickets.dto.CreateTicketRequestDTO;
import cognizant.org.tickets.dto.UpdateTicketRequestDTO;
import cognizant.org.tickets.entity.SupportTicket;
import cognizant.org.tickets.service.TicketService;
import cognizant.org.tickets.util.Util;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(KafkaTemplate<String, String> kafkaTemplate, TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping()
    public ResponseEntity<APIResponseDTO<List<SupportTicket>>> getAllTickets() {
        APIResponseDTO<List<SupportTicket>> apiResponse = ticketService.getAllTickets();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<APIResponseDTO<SupportTicket>> getTicketById(@PathVariable(value = "id") String ticketId) {
        APIResponseDTO<SupportTicket> apiResponse = ticketService.getTicketById(ticketId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping()
    public ResponseEntity<APIResponseDTO<SupportTicket>> createTicket(@Valid @RequestBody CreateTicketRequestDTO request, BindingResult result) {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        if(result.hasErrors()) {
            List<String> errors = Util.getRequestFieldErrors(result);
            apiResponse.setResponse(HttpStatus.BAD_REQUEST, Util.objectToJson(errors));
            log.error("Request errors: {}", Util.objectToJson(errors));
            return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
        }
        apiResponse = ticketService.createTicket(request);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<APIResponseDTO<SupportTicket>> updateTicket(@Valid @RequestBody UpdateTicketRequestDTO request, BindingResult result, @PathVariable(value = "id") String ticketId) {
        APIResponseDTO<SupportTicket> apiResponse = new APIResponseDTO<>();
        if(result.hasErrors()) {
            List<String> errors = Util.getRequestFieldErrors(result);
            apiResponse.setResponse(HttpStatus.BAD_REQUEST, Util.objectToJson(errors));
            log.error("Request errors: {}", Util.objectToJson(errors));
            return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
        }
        apiResponse = ticketService.updateTicket(request, ticketId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<APIResponseDTO<Void>> deleteTicket(@PathVariable(value = "id") String ticketId) {
        APIResponseDTO<Void> apiResponse = ticketService.deleteTicket(ticketId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

}
