package keycloak.otpLogin.sender;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public interface ServicesInterface {
    @POST
    @Path("/sms")
    @Consumes({ MediaType.MEDIA_TYPE_WILDCARD })
    @Produces({ MediaType.APPLICATION_JSON })
    Response sendSms(Message msg);
}