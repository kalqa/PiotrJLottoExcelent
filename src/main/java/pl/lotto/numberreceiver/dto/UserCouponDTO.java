package pl.lotto.numberreceiver.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserCouponDTO(UUID uuid, LocalDateTime tokenCreationDate, LocalDateTime resultsDrawDate,
                            LocalDateTime tokenExpirationDate, List<Integer> typedNumbers) {

}