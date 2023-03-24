package pt.ipleiria.estg.dei.ei.dae.project.exceptions.mappers;

import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

public class MyEntityNotFoundExceptionMapper implements ExceptionMapper<MyEntityNotFoundException> {

    private final static Logger logger = Logger.getLogger(MyEntityNotFoundException.class.getCanonicalName());


    @Override
    public Response toResponse(MyEntityNotFoundException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.NOT_FOUND).entity(errorMsg).build();
    }
}
