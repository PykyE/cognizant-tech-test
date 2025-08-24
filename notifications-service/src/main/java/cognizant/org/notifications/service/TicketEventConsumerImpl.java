package cognizant.org.notifications.service;

import cognizant.org.notifications.dto.EventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class TicketEventConsumerImpl implements TicketEventConsumer {

    @KafkaListener(topics = "tickets-events")
    public void consume(String jsonMessage) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        EventDTO dto = om.readValue(jsonMessage, EventDTO.class);
        log.info("Received event -> {}", dto);
    }

}