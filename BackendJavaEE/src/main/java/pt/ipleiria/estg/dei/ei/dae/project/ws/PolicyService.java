package pt.ipleiria.estg.dei.ei.dae.project.ws;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.OccurrenceDTO;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.PolicyDTO;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("policies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class PolicyService {



    @GET
    @Path("{code}")
    public Response getPolicyById(@PathParam("code") Long code){
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
            JsonParser  parser  = factory.createParser(response.body());

            // Deserializes from json file to policy
            ObjectMapper mapper = new ObjectMapper();
            PolicyDTO policy = mapper.readValue(parser, PolicyDTO.class);
            //System.out.println(policy.toString());

            return Response.ok(policy).build();


        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

    @GET
    @Path("client/{clientNIF}")
    public Response getPoliciesForClient(@PathParam("clientNIF") String clientNIF){
        String path= "https://63bed56bf5cfc0949b628c83.mockapi.io/mockAPI/policy/?client="+clientNIF;
       // System.out.println("QWERTY path:"+path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(path))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

         //   System.out.println("QWERTY response body:"+response.body());
            JsonFactory factory = new JsonFactory();
            JsonParser  parser  = factory.createParser(response.body());

            // Deserializes from json file to a list of Policies
            ObjectMapper mapper = new ObjectMapper();
            List<PolicyDTO> policyList = Arrays.asList(mapper.readValue(response.body(), PolicyDTO[].class));

           // System.out.println("QWERTY policy list = " + policyList.toString());

            return Response.ok(policyList).build();



        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

        }
    }

    @POST
    @Path("/")
    public Response create(PolicyDTO policyDTO) {

        String path = "https://63bed56bf5cfc0949b628c83.mockapi.io/mockAPI/policy/";

        try {



            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper
                    .writeValueAsString(policyDTO);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

//            System.out.println("Request : "+requestBody);
//            System.out.println("Response : "+response.body());


            return Response.ok(response.body()).build();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }






    }

}