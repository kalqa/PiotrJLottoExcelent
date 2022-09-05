package pl.lotto.numberreceiver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.lotto.numberreceiver.dto.CouponDTO;
import pl.lotto.timegenerator.SampleClock;
import pl.lotto.timegenerator.TimeGeneratorConfiguration;
import pl.lotto.timegenerator.TimeGeneratorFacade;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NumberReceiverFacadeTest implements SampleClock {

    final private TimeGeneratorConfiguration timeGeneratorConfig = new TimeGeneratorConfiguration();
    final private NumberReceiverConfiguration numberReceiverConfig = new NumberReceiverConfiguration();
    private final UUIDGenerator uuidGenerator = new UUIDGenerator();
    private final DayOfWeek dayOfWeek = DayOfWeek.SATURDAY;
    private final LocalTime drawTime = LocalTime.of(20, 15);
    private final Duration couponExpirationInDays = Duration.ofDays(365 * 2);
    private final Clock defaultClockForTests = sampleClock(LocalDate.of(2022, 8, 10), LocalTime.of(8, 0));
    private final TimeGeneratorFacade timeGeneratorFacade = timeGeneratorConfig.createForTest(dayOfWeek, drawTime, couponExpirationInDays, defaultClockForTests);
    private Repository receiverRepository = new NumberReceiverRepositoryRepository();
    private final NumberReceiverFacade numberReceiverFacade = numberReceiverConfig.createForTests(uuidGenerator, receiverRepository, timeGeneratorFacade);
    private final UUID uuidForMocks = UUID.fromString("ef241277-64a2-457a-beea-a4c589803a26");

    @AfterEach
    void tearDown() {
        receiverRepository = new NumberReceiverRepositoryRepository();
    }

    @Test
    @DisplayName("input user numbers and returns dto with correct user numbers")
    void inputNumbers_givenInputNumbers_returnsResultDTOWithUserNumbers() {
        // given
        List<Integer> numbersFromUser = List.of(1, 2, 3, 4, 5, 6);

        // when
        CouponDTO result = numberReceiverFacade.inputNumbers(numbersFromUser);
        List<Integer> actualUserNumbers = result.typedNumbers();

        // then
        assertThat(actualUserNumbers).isEqualTo(numbersFromUser);
    }

    @Test
    @DisplayName("returns list of all coupons for provided draw date time")
    void getUserCouponsListForDrawDate_givenDrawDate_returnsUserCouponForExpectedDrawDate() {
        // given
        seedSomeCouponsToTestDB(10);
        // Advancing in time from 08.10 to 08.13
        Clock clock3DaysLater = sampleClock(LocalDate.of(2022, 8, 14), LocalTime.of(8, 0));
        TimeGeneratorFacade timeGeneratorFacade = timeGeneratorConfig.createForTest(dayOfWeek, drawTime, couponExpirationInDays, clock3DaysLater);
        NumberReceiverFacade numberReceiverFacade = numberReceiverConfig.createForTests(uuidGenerator, receiverRepository, timeGeneratorFacade);
        LocalDateTime laterDrawDate = timeGeneratorFacade.getDrawDateAndTime();
        CouponDTO expectedCoupon = numberReceiverFacade.inputNumbers(List.of(13, 14, 15, 16, 17, 18));

        // when
        List<CouponDTO> actualCouponLists = numberReceiverFacade.getUserCouponListForDrawDate(laterDrawDate);

        // then
        assertThat(actualCouponLists).contains(expectedCoupon);
        assertThat(actualCouponLists.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("returns expected user coupon when its UUID is provided")
    void getUserCouponByUUID_givenUUID_returnsUserCouponForExpectedUUID() {
        // given
        NumberReceiverFacade numberReceiverFacade = getNumberReceiverFacadeWithMockedUUID(uuidForMocks);
        numberReceiverFacade.inputNumbers(List.of(1, 2, 3, 4, 5, 6));

        // when
        Optional<CouponDTO> actualUserCouponDTO = numberReceiverFacade.getUserCouponByUUID(uuidForMocks);
        UUID actualUUID = actualUserCouponDTO.map(CouponDTO::uuid).orElse(null);

        // then
        assertThat(actualUUID).isEqualTo(uuidForMocks);
    }

    @Test
    @DisplayName("deletes expected user coupon when its UUID is provided")
    void deleteUserCouponByUUID_givenUUID_returnDeletedCouponForExpectedUUID() {
        // given
        NumberReceiverFacade numberReceiverFacade = getNumberReceiverFacadeWithMockedUUID(uuidForMocks);
        numberReceiverFacade.inputNumbers(List.of(1, 2, 3, 4, 5, 6));

        // when
        Optional<CouponDTO> actualUserCouponDTO = numberReceiverFacade.deleteUserCouponByUUID(uuidForMocks);
        UUID actualUUID = actualUserCouponDTO.map(CouponDTO::uuid).orElse(null);
        Optional<CouponDTO> optionalCoupon = numberReceiverFacade.getUserCouponByUUID(uuidForMocks);

        // then
        assertThat(actualUUID).isEqualTo(uuidForMocks);
        assertThat(optionalCoupon).isEmpty();
    }

    @Test
    @DisplayName("deletes all expired coupons from repository")
    void deleteAllExpiredCoupons_givenCurrentTime_allExpiredCouponsAreRemoved() {
        // given
        CouponDTO expectedExpiredCoupon1 = numberReceiverFacade.inputNumbers(List.of(1, 2, 3, 4, 5, 6));
        CouponDTO expectedExpiredCoupon2 = numberReceiverFacade.inputNumbers(List.of(7, 8, 9, 10, 11, 12));
        LocalDateTime timeAfterExpirationDuration = timeGeneratorFacade.getDrawDateAndTime().plusDays(couponExpirationInDays.toDays() + 1);
        Clock clockAfterExpiration = sampleClock(timeAfterExpirationDuration.toLocalDate(), timeAfterExpirationDuration.toLocalTime());
        NumberReceiverFacade numberReceiverFacade = getNumberReceiverFacadeWithClock(clockAfterExpiration);
        seedSomeCouponsToTestDB(2);

        // when
        List<CouponDTO> deletedCoupons = numberReceiverFacade.deleteAllExpiredCoupons();

        // then
        assertThat(deletedCoupons).contains(expectedExpiredCoupon1, expectedExpiredCoupon2);

    }

    private void seedSomeCouponsToTestDB(int amount) {
        for (int i = 0; i < amount; i++) {
            numberReceiverFacade.inputNumbers(List.of(1, 2, 3, 4, 5, 6));
        }
    }

    private NumberReceiverFacade getNumberReceiverFacadeWithClock(Clock clock) {
        TimeGeneratorFacade timeGeneratorFacade = timeGeneratorConfig.createForTest(dayOfWeek, drawTime, couponExpirationInDays, clock);
        return numberReceiverConfig.createForTests(uuidGenerator, receiverRepository, timeGeneratorFacade);
    }

    private NumberReceiverFacade getNumberReceiverFacadeWithMockedUUID(UUID uuid) {
        UUIDGenerator mockedUuidGenerator = getMockedUUIDGenerator(uuid);
        return numberReceiverConfig.createForTests(mockedUuidGenerator, receiverRepository, timeGeneratorFacade);
    }

    private UUIDGenerator getMockedUUIDGenerator(UUID uuid) {
        UUIDGenerator mockedUuidGenerator = Mockito.mock(UUIDGenerator.class);
        Mockito.when(mockedUuidGenerator.generateRandomUUID()).thenReturn(uuid);
        return mockedUuidGenerator;
    }

}
