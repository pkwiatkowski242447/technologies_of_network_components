package pl.tks.gr3.cinema.adapters.rabbitmq.messages;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClientCreateMessage {

    private UUID clientId;
    private String clientLogin;
}
