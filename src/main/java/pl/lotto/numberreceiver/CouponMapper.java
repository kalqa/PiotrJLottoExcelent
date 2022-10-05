package pl.lotto.numberreceiver;

import pl.lotto.numberreceiver.dto.InputStatus;
import pl.lotto.numberreceiver.dto.ReceiverResponseDto;

import java.util.List;
import java.util.stream.Collectors;

class CouponMapper {

    static ReceiverResponseDto toDto(Coupon coupon, InputStatus status) {
        return new ReceiverResponseDto(coupon.uuid(), coupon.creationDate(), coupon.drawDate(),
                coupon.expirationDate(), coupon.typedNumbers(), status);
    }

    static List<ReceiverResponseDto> toDtoList(List<Coupon> coupon, InputStatus status) {
        return coupon.stream()
                .map(tempCoupon -> toDto(tempCoupon,status))
                .collect(Collectors.toList());
    }


}