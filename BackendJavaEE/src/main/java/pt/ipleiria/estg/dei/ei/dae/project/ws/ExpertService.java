package pt.ipleiria.estg.dei.ei.dae.project.ws;

import pt.ipleiria.estg.dei.ei.dae.project.dtos.ExpertDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ExpertBean;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Expert;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("experts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class ExpertService {
    @Context
    private SecurityContext securityContext;

    @EJB
    ExpertBean expertBean;

    @GET
    @Path("/")
    public List<ExpertDTO> getAllExpertsWS() {
        return toDTOs(expertBean.getAllExperts());
    }

    private ExpertDTO toDTONoPassword(Expert expert) {
        return new ExpertDTO(
                expert.getUsername(),
                expert.getEmail(),
                expert.getName(),
                expert.getInsurance()
        );
    }

    private List<ExpertDTO> toDTOs(List<Expert> experts) {
        return experts.stream().map(this::toDTONoPassword).collect(Collectors.toList());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("{username}")
    public Response getExpertDetails(@PathParam("username") String username) {
        java.security.Principal principal = securityContext.getUserPrincipal();
        Expert expert = expertBean.find(username);

        if(!principal.getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("ERROR_FINDING_CLIENT")
                    .build();
        }
        return Response.ok(toDTONoPassword(expert)).build();
    }
}
