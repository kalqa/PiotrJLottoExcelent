package pl.lotto.timegenerator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.*;

@Configuration
public class TimeGeneratorConfiguration {

    public TimeGeneratorFacade createForTest(Clock clockForTest, TimeConfigurable timeSpec) {
        return createForProduction(clockForTest,timeSpec);
    }

    @Bean("timeGeneratorFacade")
    @Profile("integration")
    public TimeGeneratorFacade createForProduction(Clock clock, TimeConfigurable timeSpec) {
        ExpirationDateTimeGenerator expirationDateTimeGenerator = new ExpirationDateTimeGenerator(timeSpec.getExpirationInDays());
        CurrentDateTimeGenerator currentDateTimeGenerator = new CurrentDateTimeGenerator(clock);
        DrawDateTimeGenerator drawDateTimeGenerator = new DrawDateTimeGenerator(timeSpec.getDrawDayOfWeek(), timeSpec.getDrawHour());
        TimeGenerator timeGenerator = new TimeGenerator(currentDateTimeGenerator, drawDateTimeGenerator, expirationDateTimeGenerator);
        return new TimeGeneratorFacade(timeGenerator);
    }

    @Bean("utcClock")
    @Profile("production")
    Clock productionClock(){
        return Clock.systemUTC();
    }

}
