package keycloak.phoneNumberLogin;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class PhoneNumberOnlyLoginAuthenticatorFactory
        implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "phone-number-only-authenticator";
    private static final PhoneNumberOnlyLoginAuthenticator SINGLETON = new PhoneNumberOnlyLoginAuthenticator();
    private static final String TITLE = "Login with only phone number";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED,
            AuthenticationExecutionModel.Requirement.CONDITIONAL
    };

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return TITLE;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return TITLE;
    }

    @Override
    public String getHelpText() {
        return TITLE;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
//        return List.of(
//        );
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope config) {
        // not needed for current version
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // not needed for current version
    }

    @Override
    public void close() {
        // not used for current version
    }

}
