package com.postech.core.account.domain.entity;

import com.postech.core.account.domain.exception.InvalidEmailException;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountEntityTest {
    @Mock
    private EmailValidator mockEmailValidator;

    private MockedStatic<EmailValidator> mockedStaticEmailValidator;

    @BeforeEach
    void setUp() {
        this.mockedStaticEmailValidator = mockStatic(EmailValidator.class);
        this.mockedStaticEmailValidator.when(EmailValidator::getInstance).thenReturn(this.mockEmailValidator);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid.@mail.com", "invalid", ""})
    void shouldRaiseExceptionWhenEmailIsInvalid(String invalidEmail) {
        when(this.mockEmailValidator.isValid(invalidEmail)).thenReturn(false);
        assertThatThrownBy(() -> AccountEntity
                .builder()
                .email(invalidEmail)
                .build())
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email should have a valid format");
    }

    @AfterEach
    void tearDown() {
        if (this.mockedStaticEmailValidator != null) {
            this.mockedStaticEmailValidator.close();
        }
    }
}
