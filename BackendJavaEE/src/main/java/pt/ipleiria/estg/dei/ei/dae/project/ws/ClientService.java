package pt.ipleiria.estg.dei.ei.dae.project.ws;

import pt.ipleiria.estg.dei.ei.dae.project.dtos.ClientDTO;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.EmailDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ClientBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.EmailBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.OccurrenceBean;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;

import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("clients")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class ClientService {
    @Context
    private SecurityContext securityContext;

    @EJB
    ClientBean clientBean;

    @EJB
    EmailBean emailBean;

    @EJB
    OccurrenceBean occurrenceBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/clients/”
    public List<ClientDTO> getAllClientsWS() {
        return toDTOs(clientBean.getAllClients());
    }

    private ClientDTO toDTOnoPassword(Client client) {
        return new ClientDTO(
                client.getUsername(),
                client.getEmail(),
                client.getName()
        );
    }

    private List<ClientDTO> toDTOs(List<Client> clients) {
        return clients.stream().map(this::toDTOnoPassword).collect(Collectors.toList());
    }


    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{username}")
    public Response getClientDetails(@PathParam("username") String username) {
        java.security.Principal principal = securityContext.getUserPrincipal();
        Client client = clientBean.find(username);

        if(!principal.getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("ERROR_FINDING_CLIENT")
                    .build();
        }
        return Response.ok(toDTOnoPassword(client)).build();
    }


    @DELETE
    @Path("{username}")
    public Response deleteClient(@PathParam("username") String username) throws MyEntityNotFoundException {
        Client client = clientBean.find(username);
        if (client != null) {
            clientBean.delete(username);
            return Response.ok("STUDENT DELETED SUCCESSFULLY").build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_STUDENT")
                .build();
    }

    @PUT
    @Path("/")
    public Response updateClient(ClientDTO clientDTO) throws MyEntityNotFoundException {
        System.out.println("UPDATE CLIENT");
        Client client = clientBean.find(clientDTO.getUsername());
        if (client != null) {
            System.out.println("FOUND CLIENT");
            System.out.println(toDTOnoPassword(client).toString());
            clientBean.update(clientDTO.getUsername(), clientDTO.getPassword(), clientDTO.getName(), clientDTO.getEmail());
            return Response.ok(toDTOnoPassword(client)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("ERROR_FINDING_CLIENT")
                .build();
    }

    @POST
    @RolesAllowed({"Expert", "Client"})
    @Path("/{username}/email/send")
    public Response sendEmail(@PathParam("username") String username, EmailDTO email)
            throws MyEntityNotFoundException, MessagingException {
        Client client = clientBean.find(username);
        Occurrence occurrence = occurrenceBean.find(Long.parseLong(email.getOccurrence()));
        if (client == null) {
            throw new MyEntityNotFoundException("Client with username '" + username
                    + "' not found in our records.");
        }
       // emailBean.send(client.getEmail(), email.getOccurrence() , email.getMessage());

        emailBean.send(client.getEmail(), "Occurrence " + occurrence.getCode() + " status updated to 'Valid'",
                "Occurrence " + occurrence.getCode() + " status was updated to 'Valid'. You can now choose a repairman." + "\n" + "Here is the link where you can view your occurrence details: localhost:3000/clients/" + client.getUsername()+"/"+occurrence.getCode());


        return Response.status(Response.Status.OK).entity("E-mail sent").build();
    }
}
