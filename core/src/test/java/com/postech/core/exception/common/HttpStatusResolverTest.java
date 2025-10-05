package com.postech.core.exception.common;

import com.postech.core.account.domain.exception.InvalidEmailException;
import com.postech.core.common.exception.BusinessException;
import com.postech.core.common.exception.EntityAlreadyExistsException;
import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.common.exception.HttpStatusResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpStatusResolverTest {
    @Test
    void shouldReturnUnprocessableEntityForInvalidEmailException() {
        // Given
        BusinessException exception = new InvalidEmailException();

        // When
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);

        // Then
        assertThat(httpStatus).isEqualTo(422);
    }

    @Test
    void shouldReturnNotFoundForEntityNotFoundException() {
        // Given
        BusinessException exception = new EntityNotFoundException("Test");

        // When
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);

        // Then
        assertThat(httpStatus).isEqualTo(404);
    }

    @Test
    void shouldReturnConflictForEntityAlreadyExistException() {
        // Given
        BusinessException exception = new EntityAlreadyExistsException("Test");

        // When
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);

        // Then
        assertThat(httpStatus).isEqualTo(409);
    }

    @Test
    void shouldReturnBadRequestForGenericBusinessException() {
        // Given
        BusinessException exception = new BusinessException("A generic business error occurred");

        // When
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);

        // Then
        assertThat(httpStatus).isEqualTo(400);
    }
}
