package com.postech.core.reservation.domain.exception;

import com.postech.core.common.exception.BusinessException;

public class InvalidReservationDateException extends BusinessException {
    private static final String MESSAGE = "Reservation date must be in the future";

    public InvalidReservationDateException() {
        super(MESSAGE);
    }
}
