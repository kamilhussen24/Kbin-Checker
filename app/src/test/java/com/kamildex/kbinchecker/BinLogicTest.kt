package com.kamildex.kbinchecker

import com.kamildex.kbinchecker.data.BinInput
import com.kamildex.kbinchecker.data.CardScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BinLogicTest {

    @Test
    fun detectsMajorNetworks() {
        assertEquals(CardScheme.VISA, CardScheme.detect("414720"))
        assertEquals(CardScheme.MASTERCARD, CardScheme.detect("516706"))
        assertEquals(CardScheme.MASTERCARD, CardScheme.detect("222100"))
        assertEquals(CardScheme.AMEX, CardScheme.detect("371449"))
        assertEquals(CardScheme.DISCOVER, CardScheme.detect("601100"))
        assertEquals(CardScheme.JCB, CardScheme.detect("352800"))
        assertEquals(CardScheme.DINERS, CardScheme.detect("360000"))
        assertEquals(CardScheme.UNIONPAY, CardScheme.detect("622202"))
        assertEquals(CardScheme.RUPAY, CardScheme.detect("607000"))
        assertEquals(CardScheme.MAESTRO, CardScheme.detect("501800"))
        assertEquals(CardScheme.MIR, CardScheme.detect("220012"))
        assertEquals(CardScheme.UNKNOWN, CardScheme.detect(""))
        assertEquals(CardScheme.UNKNOWN, CardScheme.detect("9"))
    }

    @Test
    fun inputIsLimitedToEightDigits() {
        assertEquals("51670612", BinInput.sanitize("5167 0612 3456 7890"))
        assertEquals("", BinInput.sanitize("abc"))
        assertTrue(BinInput.isValid("516706"))
        assertTrue(BinInput.isValid("51670612"))
        assertFalse(BinInput.isValid("51670"))
        assertFalse(BinInput.isValid("516706123"))
    }

    @Test
    fun formatsPreviewAndLabels() {
        assertEquals("5167 06•• •••• ••••", BinInput.preview("516706"))
        assertEquals("Credit Card", BinInput.pretty("CREDIT CARD"))
        assertEquals("—", BinInput.pretty("  "))
    }
}
