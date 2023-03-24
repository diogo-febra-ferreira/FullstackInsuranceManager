package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import pt.ipleiria.estg.dei.ei.dae.project.entities.*;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.print.Doc;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class DocumentBean {

    @PersistenceContext
    EntityManager em;

    @Inject //
    private Hasher hasher;

    public Document create(String filepath, String filename, String username) throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {
        User user = em.find(User.class, username);
        Document document = new Document(filepath, filename, user);
        user.addDocument(document);
        em.persist(document);
        return document;
    }


    public Document find(Long id){
        return em.find(Document.class, id);
    }

    public List<Document> getClientDocuments(String username) {
        User user = em.find(User.class, username);

        return user.getDocuments();
    }
}
