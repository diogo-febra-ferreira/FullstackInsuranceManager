package pt.ipleiria.estg.dei.ei.dae.project.ws;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.OccurrenceDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ClientBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ExpertBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.OccurrenceBean;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("occurrences")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
@RolesAllowed({"Client", "Expert", "Repairman"})

public class OccurrenceService {

    @EJB
    OccurrenceBean occurrenceBean;

    @EJB
    ClientBean clientBean;

    @EJB
    ExpertBean expertBean;


    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/occurrences/”
    public List<OccurrenceDTO> getAllOccurrencesWS() {
        return toDTOsNoRepairmen(occurrenceBean.getAllOccurrences());
    }

    private OccurrenceDTO toDTONoRepairmen(Occurrence occurrence) {
        return new OccurrenceDTO(
                occurrence.getCode(),
                occurrence.getDescription(),
                occurrence.getInsurance().toString(),
                occurrence.getClient(),
                occurrence.getStatus(),
                occurrence.getDocument(),
                occurrence.getType(),
                occurrence.getExpert()
        );
    }

    private OccurrenceDTO toDTORepairmen(Occurrence occurrence) {
        return new OccurrenceDTO(
                occurrence.getCode(),
                occurrence.getDescription(),
                occurrence.getInsurance().toString(),
                occurrence.getClient(),
                occurrence.getRepairmenDTOS(occurrence.getRepairmen()),
                occurrence.getDocument(),
                occurrence.getStatus(),
                occurrence.getType(),
                occurrence.getExpert()
        );
    }

    private List<OccurrenceDTO> toDTOsNoRepairmen(List<Occurrence> occurrences) {
        return occurrences.stream().map(this::toDTONoRepairmen).collect(Collectors.toList());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Client"})
    @Path("{client_nif}/all")
    public Response getClientOccurrences (@PathParam("client_nif") String username) {
        List<Occurrence> occurrences = occurrenceBean.findByClient(username); //find by client

        if (occurrences == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERROR_FINDING_OCCURRENCE_FROM_CLIENT " + username)
                    .build();
        }
        return Response.ok(toDTOsNoRepairmen(occurrences)).build();
    }


    @GET
    @Authenticated
    @RolesAllowed({"Expert", "Client", "Repairman"})
    @Path("{code}")
    public Response getOccurrenceDetails (@PathParam("code") long code) {
        Occurrence occurrence = occurrenceBean.find(code);

        if (occurrence == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERROR_FINDING_OCCURRENCE " + code)
                    .build();
        }
        return Response.ok(toDTONoRepairmen(occurrence)).build();
    }



    @POST
    @Path("create/{code}")
    public Response create(OccurrenceDTO occurrenceDTO, @PathParam("code") long codePolicy)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {
        Occurrence occurrence = occurrenceBean.create(
                occurrenceDTO.getDescription(),
                occurrenceDTO.getInsurance(),
                occurrenceDTO.getClient().getUsername(),
                occurrenceDTO.getDocument().getId(),
                occurrenceDTO.getType(),
                codePolicy
        );
        return Response.status(Response.Status.CREATED)
                .entity(toDTONoRepairmen(occurrence))
                .build();
    }

    @POST
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("{expert}/{code}/{status}")
    public Response validateOccurrence(@PathParam("expert") String expertUsername, @PathParam("code") long code, @PathParam("status") String status ) throws MessagingException {

        if(status.equals("accept") ) {
            expertBean.validateOccurrence(code, expertUsername);
        }else if (status.equals("reject")){
            expertBean.rejectOccurrence(code, expertUsername);
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Occurrence in status broken can either be accepted or rejected")
                    .build();
        }

       return Response.ok().build();
    }

    @GET
    @Authenticated
    @RolesAllowed({"Expert"})
    @Path("{insurance}/show")
    public Response getExpertOccurrences(@PathParam("insurance") Insurance insurance) {
        List<Occurrence> occurrences = occurrenceBean.findByInsurance(insurance); //find by client

        if (occurrences == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("ERROR_FINDING_OCCURRENCE_FROM_INSURANCE " + insurance)
                    .build();
        }
        return Response.ok(toDTOsNoRepairmen(occurrences)).build();
    }
}
