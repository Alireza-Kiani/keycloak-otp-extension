package keycloak.otpLogin;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OtpLoginAuthenticatorFactory
        implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "single-time-otp-authenticator";
    private static final OtpLoginAuthenticator SINGLETON = new OtpLoginAuthenticator();
    private static final String TITLE = "Single Time OTP Authenticator";

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
        return true;
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
        List<ProviderConfigProperty> configs = new ArrayList<ProviderConfigProperty>();
        configs.add(new ProviderConfigProperty("ttl", "Time-to-live", "The time to live in seconds for the code to be valid.", ProviderConfigProperty.STRING_TYPE, "300"));
        configs.add(new ProviderConfigProperty("apiUrl", "SMS API URL", "The path to the API that receives an HTTP request.", ProviderConfigProperty.STRING_TYPE, "https://example.com/api/sms/send"));
        configs.add(new ProviderConfigProperty("otpLength", "OTP Length", "Length of OTP", ProviderConfigProperty.STRING_TYPE, "verysecret"));

        return configs;
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
