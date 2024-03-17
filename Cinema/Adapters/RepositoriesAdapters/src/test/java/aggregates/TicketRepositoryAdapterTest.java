package aggregates;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.TicketRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.TicketRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketRepositoryAdapterTest {
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketRepositoryAdapter ticketRepositoryAdapter;

    @Test
    public void ticketRepositoryAdapterCreatTicketTestPositive() {
        //when(ticketRepository.create()).thenReturn()
    }
}
