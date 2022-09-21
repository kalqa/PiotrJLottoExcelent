package pl.lotto;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.lotto.timegenerator.AdjustableClock;
import pl.lotto.timegenerator.TimeGeneratorFacade;

@SpringBootTest(classes = AppRunner.class)
public class BaseIntegrationSpec {

    @Autowired
    public ApplicationContext appContext;
    @Autowired
    public AdjustableClock clock;
    @Autowired
    public TimeGeneratorFacade timeGeneratorFacade;

    @Bean
    public Clock adjustableClock() {
        LocalDate sampleDate = LocalDate.of(2022, 8, 8);
        LocalTime sampleTime = LocalTime.of(8, 0);
        ZoneId sampleZone = ZoneId.systemDefault();
        return AdjustableClock.ofLocalDateAndLocalTime(sampleDate, sampleTime, sampleZone);
    }
}
