package cognizant.org.tickets.repository;

import cognizant.org.tickets.entity.SupportTicket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends CrudRepository<SupportTicket, UUID> {
}
