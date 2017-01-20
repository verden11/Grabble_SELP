package io.github.verden11.grabble;

import org.junit.Test;

import io.github.verden11.grabble.Helper.General;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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

}

