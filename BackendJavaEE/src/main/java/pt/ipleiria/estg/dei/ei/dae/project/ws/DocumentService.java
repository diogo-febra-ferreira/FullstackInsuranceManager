package pt.ipleiria.estg.dei.ei.dae.project.ws;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.DocumentDTO;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.OccurrenceDTO;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.ClientBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.DocumentBean;
import pt.ipleiria.estg.dei.ei.dae.project.ejbs.OccurrenceBean;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Document;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Authenticated;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


    @Path("documents")
    @Authenticated
    @RolesAllowed({"Client", "Expert", "Repairman"})
    public class DocumentService {
        @EJB
        private ClientBean clientBean;
        @EJB
        private DocumentBean documentBean;
        @EJB
        private OccurrenceBean occurrenceBean;
        @Context
        private SecurityContext securityContext;

        @POST
        @Path("")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.APPLICATION_JSON)
        public Response upload(MultipartFormDataInput input) throws IOException, MyConstraintViolationException, MyEntityNotFoundException, MyEntityExistsException {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            String username = securityContext.getUserPrincipal().getName();
            List<InputPart> inputParts = uploadForm.get("file");
            List<Document> documents = new LinkedList<Document>();
            for (InputPart inputPart : inputParts) {
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String filename = getFilename(headers);
// convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                String homedir = System.getProperty("user.home");
                String dirpath = homedir + File.separator + "uploads" + File.separator +
                        username;
                mkdirIfNotExists(dirpath);
                String filepath = dirpath + File.separator + filename;
                writeFile(bytes, filepath);
                Document document = documentBean.create(filepath, filename, username);
                documents.add(document);
            }

            return Response.ok(DocumentDTO.from(documents)).build();
        }

        @PUT
        @Path("{code}/{id}/edit")
        @Produces(MediaType.APPLICATION_JSON)
        public Response uploadUpdate(@PathParam("code") long occurrence,@PathParam("id") long document) {
            OccurrenceDTO occurrenceDTO = occurrenceBean.updateOccurrenceDocument(occurrence,document);
            return Response.ok(occurrenceDTO).build();
        }

        private void mkdirIfNotExists(String path) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        @GET
        @Path("download/{id}")
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        public Response download(@PathParam("id") String id) throws MyEntityNotFoundException {
            Document document = documentBean.find(Long.parseLong(id));

            if (document == null) {
                throw new MyEntityNotFoundException("Document " + id + "does not exist");
            }

            Response.ResponseBuilder response = Response.ok(new File(document.getFilepath()));
            response.header("Content-Disposition", "attachment;filename=" +
                    document.getFilename());
            return response.build();
        }

        @GET
        @Path("")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getDocuments() {
            String username = securityContext.getUserPrincipal().getName();
            List<Document> documents = documentBean.getClientDocuments(username);
            return Response.ok(DocumentDTO.from(documents)).build();
        }

        @GET
        @Path("exists")
        public Response hasDocuments() throws MyEntityNotFoundException {
            String username = securityContext.getUserPrincipal().getName();
            Client client = clientBean.find(username);

            if (client == null) {
                throw new MyEntityNotFoundException("Client " + username + " not found");
            }

            return Response.status(Response.Status.OK)
                    .entity(!client.getDocuments().isEmpty())
                    .build();
        }

        private String getFilename(MultivaluedMap<String, String> headers) {
            String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");
            for (String filename : contentDisposition) {
                if ((filename.trim().startsWith("filename"))) {
                    String[] name = filename.split("=");
                    return name[1].trim().replaceAll("\"", "");
                }
            }
            return "unknown";
        }

        private void writeFile(byte[] content, String filename) throws IOException {
            File file = new File(filename);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fop = new FileOutputStream(file);
            fop.write(content);
            fop.flush();
            fop.close();
        }
    }

