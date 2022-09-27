package pl.lotto.numberreceiver;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class ReceiverUuidGenerator {

    UUID generateRandomUUID() {
        return UUID.randomUUID();
    }

}
