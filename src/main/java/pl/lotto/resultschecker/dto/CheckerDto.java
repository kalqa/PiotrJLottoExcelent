package pl.lotto.resultschecker.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CheckerDto(UUID uuid,
                         LocalDateTime drawDate,
                         List<Integer> typedNumbers,
                         List<Integer> winningNumbers,
                         List<Integer> matchedNumbers,
                         boolean isWinner,
                         CheckerStatus status) {
}
