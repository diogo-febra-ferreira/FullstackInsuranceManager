package pt.ipleiria.estg.dei.ei.dae.project.ws;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.OccurrenceDTO;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.PaginatedDTOs;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.RepairmanDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.*;
import pt.ipleiria.estg.dei.ei.dae.project.entities.*;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.requests.PageRequest;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Path("repairmen")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Authenticated
public class RepairmanService {
    @Context
    private SecurityContext securityContext;

    @EJB
    RepairmanBean repairserviceBean;

    @EJB
    OccurrenceBean occurrenceBean;

    @EJB
    ClientBean clientBean;

    @EJB
    EmailBean emailBean;

    @EJB
    ExpertBean expertBean;

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/") // means: the relative url path is “/api/repairmen/”
    public Response getAllRepairmenWS(@BeanParam @Valid PageRequest pageRequest) {
        Long count = repairserviceBean.count();

        if (pageRequest.getOffset() > count) {
            return Response.ok(new PaginatedDTOs<>(count)).build();
        }

        List<Repairman> repairmen = repairserviceBean.getAllRepairman(pageRequest.getOffset(), pageRequest.getLimit());

        PaginatedDTOs paginatedDTO = new PaginatedDTOs<>(RepairmanDTO.from(repairmen), count, pageRequest.getOffset());

        return Response.ok(paginatedDTO).build();
    }

    @GET // means: to call this endpoint, we need to use the HTTP GET method
    @Path("/insurance/{insurance}") // means: the relative url path is “/api/repairmen/”
    public Response getAllRepairmenWithInsurance(@BeanParam @Valid PageRequest pageRequest,@PathParam("insurance") String insuranceString) {
        Long count = repairserviceBean.count();

        if (pageRequest.getOffset() > count) {
            return Response.ok(new PaginatedDTOs<>(count)).build();
        }

        List<Repairman> repairmen = repairserviceBean.getRepairmanWithInsuranceOrNull(pageRequest.getOffset(), pageRequest.getLimit(),Insurance.valueOf(insuranceString));

        PaginatedDTOs paginatedDTO = new PaginatedDTOs<>(RepairmanDTO.from(repairmen), count, pageRequest.getOffset());

        return Response.ok(paginatedDTO).build();
    }

    private RepairmanDTO toDTO(Repairman repairman) {
        return new RepairmanDTO(
                repairman.getUsername(),
                repairman.getPassword(),
                repairman.getEmail(),
                repairman.getName(),
                repairman.getInsurance()
        );
    }
    private List<RepairmanDTO> toDTOs(List<Repairman> repairmen) {
        return repairmen.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GET
    @Authenticated
    @RolesAllowed({"Repairman"})
    @Path("{username}")
    public Response getRepairmanDetails(@PathParam("username") String username) {
        Principal principal = securityContext.getUserPrincipal();
        if(!principal.getName().equals(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return
                Response.ok(RepairmanDTO.from(repairserviceBean.findOrFail(username))).build();
    }

    private OccurrenceDTO occurrenceToDTO(Occurrence occurrence) {
        OccurrenceDTO occurrenceDTO = new OccurrenceDTO(
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

        return occurrenceDTO;
    }

    private List<OccurrenceDTO> occurrencesToDTOs(List<Occurrence> occurrences) {
        return occurrences.stream().map(this::occurrenceToDTO).collect(Collectors.toList());
    }

    @GET
    @Path("{username}/occurrences")
    public Response getRepairmenOccurrences(@PathParam("username") String username) {
        return Response.ok(OccurrenceDTO.from(repairserviceBean.associated(username))).build();
    }

    @GET
    @Path("occurrence/{code}/enrolled")
    public Response getRepairmenOccurrencesEnrolled(@PathParam("code") long code) {
        return Response.ok(RepairmanDTO.from(repairserviceBean.getOccurrencesEnrolledRepairman(code))).build();
    }

    @GET
    @Path("occurrence/{code}/unrolled")
    public Response getRepairmenOccurrencesUnrolled(@PathParam("code") long code) {
        return Response.ok(RepairmanDTO.from(repairserviceBean.getOccurrencesUnrolledRepairman(code))).build();
    }

    @POST
    @Path("/")
    public Response create(RepairmanDTO repairmanDTO)
            throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {

        repairserviceBean.create(
                repairmanDTO.getUsername(),
                repairmanDTO.getPassword(),
                repairmanDTO.getName(),
                repairmanDTO.getEmail(),
                repairmanDTO.getInsurance()
        );

        Repairman repairman = repairserviceBean.find(repairmanDTO.getUsername());

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(repairman))
                .build();
    }

    @POST
    @Path("/{username}/{code}/associate")
    public Response EnrollRepairmanInOccurrance(@PathParam("username") String username,@PathParam("code") long code) throws MyEntityExistsException, MyEntityNotFoundException, MessagingException {
        Repairman repairman = repairserviceBean.find(username);
        Occurrence occurrence = occurrenceBean.find(code);

        if(repairman != null && occurrence != null){
            repairserviceBean.associateRepairmanWithOccurrence(username,code);

            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/{username}/{code}/desassociate")
    public Response UnEnrollRepairmanInOccurrance(@PathParam("username") String username,@PathParam("code") long code) throws MyEntityExistsException, MyEntityNotFoundException{
        Repairman repairman = repairserviceBean.find(username);
        Occurrence occurrence = occurrenceBean.find(code);

        if(repairman != null && occurrence != null){
            repairserviceBean.desassociateRepairmanWithOccurrence(username,code);

            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/update/occurrence/{code}")
    public Response updateStatusRepaired(@PathParam("code") long code)
            throws MessagingException {

        occurrenceBean.updateStatusRepaired(code);

        return Response.status(Response.Status.OK).entity("Occurrence status updated").build();
    }

}
