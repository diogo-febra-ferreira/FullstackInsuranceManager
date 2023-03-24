package pt.ipleiria.estg.dei.ei.dae.project.ws;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.Auth;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.ClientDTO;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ClientBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.UserBean;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;
import pt.ipleiria.estg.dei.ei.dae.project.entities.User;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;
import pt.ipleiria.estg.dei.ei.dae.project.security.TokenIssuer;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("auth")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class AuthService {
    @Inject
    private TokenIssuer issuer;
    @EJB
    private UserBean userBean;

    @EJB
    private ClientBean clientBean;

    @Context
    private SecurityContext securityContext;


    @POST
    @Path("/login")
    public Response authenticate(@Valid Auth auth) {
        if (userBean.canLogin(auth.getUsername(), auth.getPassword())) {
            String token = issuer.issue(auth.getUsername());
            return Response.ok(token).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/register")
    public Response create(ClientDTO clientDTO)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {
        clientBean.create(
                clientDTO.getUsername(),
                clientDTO.getPassword(),
                clientDTO.getName(),
                clientDTO.getEmail()
        );
        Client client = clientBean.find(clientDTO.getUsername());
        if(client!=null){
            return Response.ok("Client registered successfully").build();
        }else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error registering client").build();
        }

    }


    @GET
    @Authenticated
    @Path("/user")
    public Response getAuthenticatedUser() {
        String username = securityContext.getUserPrincipal().getName();
        User user = userBean.findOrFail(username);
        return Response.ok(UserDTO.from(user)).build();
    }
}
