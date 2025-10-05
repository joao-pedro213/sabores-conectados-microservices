package com.postech.core.common.exception;

import com.postech.core.account.domain.exception.InvalidEmailException;
import com.postech.core.reservation.domain.exception.InvalidReservationDateException;
import com.postech.core.reservation.domain.exception.ReservationOutsideBusinessHoursException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpStatusResolver {
    public static int resolveHttpStatusForBusinessException(BusinessException exception) {
        return switch (exception) {
            case InvalidEmailException i -> 422;
            case InvalidReservationDateException i -> 422;
            case ReservationOutsideBusinessHoursException i -> 422;
            case EntityNotFoundException i -> 404;
            case EntityAlreadyExistsException i -> 409;
            default -> 400;
        };
    }
}
