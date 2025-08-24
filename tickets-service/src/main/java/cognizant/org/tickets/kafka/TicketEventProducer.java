package cognizant.org.tickets.kafka;

import cognizant.org.tickets.dto.EventDTO;
import cognizant.org.tickets.util.Constants.Topics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TicketEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TicketEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTicketEvent(EventDTO message) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        String jsonMessage = om.writeValueAsString(message);
        kafkaTemplate.send(Topics.TICKET_EVENTS.getName(), jsonMessage);
        log.info("Sent event -> {}", jsonMessage);
    }

}