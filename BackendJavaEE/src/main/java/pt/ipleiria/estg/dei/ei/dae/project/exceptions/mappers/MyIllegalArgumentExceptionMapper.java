package pt.ipleiria.estg.dei.ei.dae.project.exceptions.mappers;

import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyIllegalArgumentException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

public class MyIllegalArgumentExceptionMapper implements ExceptionMapper<MyIllegalArgumentException> {
    private final static Logger logger = Logger.getLogger(MyIllegalArgumentException.class.getCanonicalName());


    @Override
    public Response toResponse(MyIllegalArgumentException e) {
        String errorMsg = e.getMessage();
        logger.warning("ERROR: " + errorMsg);
        return Response.status(Response.Status.BAD_REQUEST).entity(errorMsg).build();
    }
}
