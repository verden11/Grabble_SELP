package io.github.verden11.grabble;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import io.github.verden11.grabble.Helper.General;
import io.github.verden11.grabble.Helper.Validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.util.Patterns;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GrabbleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void wordScore_calculate() throws Exception {
        assertEquals(General.calculateWordValue("looking"), 70);
        assertEquals(General.calculateWordValue("bestare"), 42);
        assertEquals(General.calculateWordValue("Zyzomys"), 109);
        assertEquals(General.calculateWordValue("Anomura"), 50);
        assertEquals(General.calculateWordValue("perhaps"), 66);
        assertEquals(General.calculateWordValue(""), 0);
        assertNotEquals(General.calculateWordValue("abature"), 0);
    }

    @Test
    public void isPasswordValid() throws Exception {
        assertTrue(Validate.isPasswordValid("thePassword"));
        assertFalse(Validate.isPasswordValid("12345"));
        assertFalse(Validate.isPasswordValid("a"));
        assertFalse(Validate.isPasswordValid("aa"));
        assertFalse(Validate.isPasswordValid("aaa"));
        assertFalse(Validate.isPasswordValid("aaaa"));
        assertFalse(Validate.isPasswordValid("aaaaa"));
        assertFalse(Validate.isPasswordValid("aaaaaa"));
        assertTrue(Validate.isPasswordValid("aaaaaaa"));
    }
}

