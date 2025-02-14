package random.impl;

import java.security.SecureRandom;

public class RandomNumberGenerator implements random.RandomNumberGenerator {
    public int generate(int length) {
        SecureRandom secureRandom = new SecureRandom();
        int min = (int) Math.pow(10, length - 1); // Smallest number (e.g., 100000 for 6 digits)
        int max = (int) Math.pow(10, length) - 1; // Largest number (e.g., 999999 for 6 digits)
        return secureRandom.nextInt(max - min + 1) + min;
    }
}
