package cognizant.org.tickets.util;

import lombok.Getter;

public class Constants {

    public enum Status {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }

    public enum Operation {
        CREATED,
        UPDATED,
        DELETED
    }

    @Getter
    public enum Topics {
        TICKET_EVENTS("tickets-events");
        Topics(String name) {
            this.name = name;
        }
        private final String name;
    }

}
