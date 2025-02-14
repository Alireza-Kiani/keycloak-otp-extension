package keycloak.phoneNumberLogin;

import jakarta.ws.rs.core.MultivaluedMap;
import keycloak.phoneNumberLogin.snowflake.Snowflake;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Optional;
import java.util.stream.Stream;

public class PhoneNumberOnlyLoginAuthenticator extends AbstractUsernameFormAuthenticator implements Authenticator {

    private static final String ENTER_PHONE_NUMBER_FORM_TMPL = "enter-phone-number.ftl";
    private static final Logger LOG = Logger.getLogger(PhoneNumberOnlyLoginAuthenticator.class);
    private static final Snowflake snowflake = new Snowflake();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        LOG.debugf("authenticate");
        context.challenge(context.form().createForm(ENTER_PHONE_NUMBER_FORM_TMPL));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Using phone number as username
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String phoneNumber = formData.getFirst("phone-number");
        Stream<UserModel> userStream = context.getSession().users().searchForUserByUserAttributeStream(context.getRealm(), "phoneNumber",phoneNumber);
        Optional<UserModel> user = userStream.findFirst();
        if (!user.isPresent()) {
            user = Optional.ofNullable(context.getSession().users().addUser(context.getRealm(), Long.toString(snowflake.nextId())));
            if (user.isPresent()) {
                context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, context.form().setError("wrongOtp").createForm(ENTER_PHONE_NUMBER_FORM_TMPL));
                return;
            }

            user.get().setSingleAttribute("phoneNumber", phoneNumber);
            user.get().setEnabled(true);
        }

        context.setUser(user.get());
        context.success();
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
