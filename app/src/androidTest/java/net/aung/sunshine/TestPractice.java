package net.aung.sunshine;

import android.test.AndroidTestCase;

/**
 * Created by aung on 2/9/16.
 */
public class TestPractice extends AndroidTestCase {

    public void testAssertions() throws Exception {
        int a = 5, b = 3 , c = 5, d = 10;

        assertEquals("X should be equal", a, c);
        assertTrue("Y should be true", d > a);
        assertFalse("Z should be false", a == b);

        if(b > d) {
            fail("XX should never happen");
        }
    }
}
