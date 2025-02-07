package keycloak.otpLogin;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import keycloak.otpLogin.sender.SmsSender;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.time.Instant;
import java.util.List;

public class OtpLoginAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {
    private static final String ENTER_OTP_CODE_FORM_TMPL = "enter-code.ftl";
    private static final String AUTH_NOTE_USER_PHONE_NUMBER = "user-phone-number";
    private static final String AUTH_NOTE_PHONE_NUMBER_OTP_CODE = "phone-number-otp-code";
    private static final String AUTH_NOTE_TIMESTAMP = "timestamp";
    private static final String AUTH_OTP_SECRET_CREDENTIAL_NAME = "AUTH_OTP_SECRET_CREDENTIAL_NAME";

    private final Random rand = new Random();
    private static final Logger LOG = Logger.getLogger(OtpLoginAuthenticator.class);


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        if (user == null) {
            context.failure(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR);
            return;
        }
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        String phoneNumber = user.getUsername();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();

        String otpCode = "35123";
        if (otpCode == null) {
            context.failure(AuthenticationFlowError.INTERNAL_ERROR);
            return;
        }

        authSession.setAuthNote(AUTH_NOTE_PHONE_NUMBER_OTP_CODE, otpCode);
        authSession.setAuthNote(AUTH_NOTE_USER_PHONE_NUMBER, phoneNumber);
        authSession.setAuthNote(AUTH_NOTE_TIMESTAMP, Instant.now().toString());
        new SmsSender(config.getConfig().get("apiUrl")).sendSms(otpCode, phoneNumber);
        Response challenge = context.form().createForm(ENTER_OTP_CODE_FORM_TMPL);
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        UserModel user = context.getUser();
        String phoneNumber = user.getUsername();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String inputOtpCode = formData.getFirst("code-input");
        String savedOtpCode = (String) authSession.getAuthNote(AUTH_NOTE_PHONE_NUMBER_OTP_CODE);

        if (inputOtpCode.equals(savedOtpCode)) {
            context.success();
            return;
        }

        Response challenge = context.form()
                .setError("wrongOtp")
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

    public static CredentialModel getUserSecretForOtp(KeycloakSession session, RealmModel realm, UserModel user) {
        return user.credentialManager().getStoredCredentialByNameAndType(AUTH_OTP_SECRET_CREDENTIAL_NAME, CredentialModel.SECRET);
    }

    @Override
    public void close() {
        // not used for current version
    }
}
