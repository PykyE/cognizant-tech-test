package cognizant.org.notifications.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface TicketEventConsumer {

    void consume(String jsonMessage) throws JsonProcessingException;

}
