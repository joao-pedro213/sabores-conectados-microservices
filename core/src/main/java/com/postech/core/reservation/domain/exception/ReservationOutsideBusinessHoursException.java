package com.postech.core.reservation.domain.exception;

import com.postech.core.common.exception.BusinessException;

public class ReservationOutsideBusinessHoursException extends BusinessException {
    private static final String MESSAGE = "Reservation time is outside of business hours";

    public ReservationOutsideBusinessHoursException() {
        super(MESSAGE);
    }
}
