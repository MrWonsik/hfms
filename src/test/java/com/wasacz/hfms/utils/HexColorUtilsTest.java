package com.wasacz.hfms.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HexColorUtilsTest {

    @ParameterizedTest
    @MethodSource("hexStringAndBooleans")
    public void shouldCheckIsHexIsCorrect(String hex, boolean isHex) {

        //when
        boolean correctHexColor = HexColorUtils.isCorrectHexColor(hex);

        assertEquals(correctHexColor, isHex);
    }

    private static Stream<Arguments> hexStringAndBooleans() {
        return Stream.of(
                Arguments.of("#000", true),
                Arguments.of("#FFF", true),
                Arguments.of("#XXX", false),
                Arguments.of("#123123", true),
                Arguments.of("#aabbCC", true),
                Arguments.of("#ab0", true),
                Arguments.of("#0b0", true),
                Arguments.of("#aaa", true),
                Arguments.of("", false),
                Arguments.of("#", false),
                Arguments.of("#aa", false),
                Arguments.of("#11", false),
                Arguments.of("#112233a", false),
                Arguments.of("#abac", false)

        );
    }

    @Test
    public void whenGetRandomHexColor_thenReturnRandomHexColor() {

        //when
        String randomHexColor = HexColorUtils.getRandomHexColor();

        //then
        assertTrue(HexColorUtils.isCorrectHexColor(randomHexColor));
    }

}