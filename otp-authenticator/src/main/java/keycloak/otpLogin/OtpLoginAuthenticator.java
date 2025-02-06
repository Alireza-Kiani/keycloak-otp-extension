package keycloak.otpLogin;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import keycloak.otpLogin.sender.SmsSender;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;

import java.time.Instant;

public class OtpLoginAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {

    private static final String ENTER_OTP_CODE_FORM_TMPL = "enter-code.ftl";
    private static final String AUTH_NOTE_USER_PHONE_NUMBER = "user-phone-number";
    private static final String AUTH_NOTE_PHONE_NUMBER_OTP_CODE = "phone-number-otp-code";
    private static final String AUTH_NOTE_TIMESTAMP = "timestamp";

    private final Random rand = new Random();
    private static final Logger LOG = Logger.getLogger(OtpLoginAuthenticator.class);


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        System.out.println("XD");
        UserModel user = context.getUser();
        System.out.println(user.getUsername());
        if (user == null) {
            System.out.println("inf if 35");
            context.failure(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR);
            return;
        }
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
//        LOG.debug(config.getConfig().get("apiUrl"));
        System.out.println(config.getConfig().get("apiUrl"));

        String phoneNumber = user.getUsername();

        AuthenticationSessionModel authSession = context.getAuthenticationSession();

//        String otpCode = rand.generate(phoneNumber);
        String otpCode = "35123";
        System.out.println(otpCode);
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

        Response challenge =  context.form()
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

    @Override
    public void close() {
        // not used for current version
    }
}
