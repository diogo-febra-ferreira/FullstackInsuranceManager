package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.PolicyDTO;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Policy;
import pt.ipleiria.estg.dei.ei.dae.project.ws.PolicyService;

import javax.ejb.Stateless;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Stateless
public class PolicyBean {

    PolicyService policyService;
    public Policy find(long code){
        String path= "https://63bed56bf5cfc0949b628c83.mockapi.io/mockAPI/policy/"+code;
        //System.out.println("QWERTY path:"+path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(path))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //System.out.println("QWERTY response body:"+response.body());
            JsonFactory factory = new JsonFactory();
            JsonParser parser  = factory.createParser(response.body());

            // Deserializes from json file to policy
            ObjectMapper mapper = new ObjectMapper();
            Policy policy = mapper.readValue(parser, Policy.class);

            return policy;


        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
