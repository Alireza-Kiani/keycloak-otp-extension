package keycloak.otpLogin;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import keycloak.otpLogin.sender.SmsSender;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;
import random.RandomNumberGenerator;

import java.time.Instant;

public class OtpLoginAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {
    private static final String ENTER_OTP_CODE_FORM_TMPL = "enter-code.ftl";
    private static final String AUTH_NOTE_USER_PHONE_NUMBER = "user-phone-number";
    private static final String AUTH_NOTE_PHONE_NUMBER_OTP_CODE = "phone-number-otp-code";
    private static final String AUTH_NOTE_TIMESTAMP = "timestamp";

    private final RandomNumberGenerator rand = new random.impl.RandomNumberGenerator();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        if (user == null) {
            context.failure(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR);
            return;
        }
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
//        String userId = user.getUsername();
        String phoneNumber = user.getFirstAttribute("phoneNumber");
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        int otpCode = rand.generate(Integer.parseInt(config.getConfig().get("otpLength")));
        authSession.setAuthNote(AUTH_NOTE_PHONE_NUMBER_OTP_CODE, String.valueOf(otpCode));
        authSession.setAuthNote(AUTH_NOTE_USER_PHONE_NUMBER, phoneNumber);
        authSession.setAuthNote(AUTH_NOTE_TIMESTAMP, Instant.now().toString());
        new SmsSender(config.getConfig().get("apiUrl")).sendSms(String.valueOf(otpCode), phoneNumber);
        context.form().setAttribute("userPhoneNumber", phoneNumber);
        Response challenge = context.form().createForm(ENTER_OTP_CODE_FORM_TMPL);
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        Instant otpCodeIssueTimestampPlusTtl = Instant.parse(authSession.getAuthNote(AUTH_NOTE_TIMESTAMP)).plusSeconds(Long.parseLong(config.getConfig().get("ttl")));
        String inputOtpCode = formData.getFirst("code-input");
        String savedOtpCode = authSession.getAuthNote(AUTH_NOTE_PHONE_NUMBER_OTP_CODE);

        if (validateOtp(otpCodeIssueTimestampPlusTtl, savedOtpCode, inputOtpCode)) {
            context.success();
            return;
        }

        Response challenge = context.form()
                .setError("wrongOtp or otpCode expired")
                .setAttribute("userPhoneNumber", authSession.getAuthNote(AUTH_NOTE_USER_PHONE_NUMBER))
                .createForm(ENTER_OTP_CODE_FORM_TMPL);
        context.challenge(challenge);
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // not needed for current version
    }

    public static boolean validateOtp(Instant otpCodeIssueTimestampPlusTtl, String savedOtpCode, String inputOtpCode) {
        return inputOtpCode.equals(savedOtpCode) && otpCodeIssueTimestampPlusTtl.isAfter(Instant.now());
    }

    @Override
    public void close() {
        // not used for current version
    }
}
