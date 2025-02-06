package keycloak.otpLogin.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Data
public class SmsSender
{
    private final String baseUrl;
    public void sendSms(String otpCode, String phoneNumber)
    {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            ObjectMapper objectMapper = new ObjectMapper();
            Message msg = new Message(phoneNumber, otpCode);
            String json = objectMapper.writeValueAsString(msg);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl+"/api/sms"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.statusCode());
            }

            System.out.println(response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
