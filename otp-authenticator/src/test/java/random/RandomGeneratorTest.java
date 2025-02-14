package random;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import random.impl.RandomNumberGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomGeneratorTest {
    public static boolean isFiveDigits(int num) {
        return num >= 10000 && num <= 99999;
    }

    @Test
    @DisplayName("Simple rng should work")
    public void testMultiply() {
        int randomInt = new RandomNumberGenerator().generate(5);
        System.out.println("randomInt: " + randomInt);
        System.out.println("isFiveDigits: " + String.valueOf(randomInt));
        assertTrue("Regular multiplication should work", isFiveDigits(randomInt));
    }
}