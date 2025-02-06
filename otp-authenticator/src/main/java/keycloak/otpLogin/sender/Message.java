package keycloak.otpLogin.sender;

import lombok.Data;

@Data
public class Message {
    private final String otpCode;
    private final String phoneNumber;
}
