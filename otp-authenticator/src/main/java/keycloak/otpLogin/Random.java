package keycloak.otpLogin;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Random {
    public String generate(String input)  {
        try {
            MessageDigest md = MessageDigest.getInstance("SHAKE-256");

            return Hex.encodeHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
