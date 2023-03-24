package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Expert;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.Status;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Hasher;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class ExpertBean {

    @EJB
    OccurrenceBean occurrenceBean;
    @EJB
    RepairmanBean repairserviceBean;

    @EJB
    EmailBean emailBean;

    @PersistenceContext
    EntityManager em;

    @Inject //
    private Hasher hasher;

    public boolean exists(String username) {
        Query query = em.createQuery(
                "SELECT COUNT(c.username) FROM Expert c WHERE c.username = :username",
                Long.class
        );
        query.setParameter("username", username);
        return (Long)query.getSingleResult() > 0L;
    }

    public void create(String username, String password, String name, String email, Insurance seguradora) throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {

        if(exists(username)){
            throw new MyEntityExistsException("The expert " + username + " already exists in the database");
        }

        try {
            Expert expert = new Expert(username, hasher.hash(password), name, email, seguradora);
            em.persist(expert);
        } catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }
    }

    public List<Expert> getAllExperts() {
        return (List<Expert>) em.createNamedQuery("getAllExperts").getResultList();
    }

    public Expert find(String username){
        return em.find(Expert.class, username);
    }

    public void update(String username, String password, String name, String email) throws MyEntityNotFoundException{
        Expert expert = find(username);
        if (expert == null) {

            throw new MyEntityNotFoundException("[UPDATE EXPERT] The expert " + username + " was not found in the database.");
        }

        em.lock(expert, LockModeType.OPTIMISTIC);

        expert.setPassword(hasher.hash(password));
        expert.setName(name);
        expert.setEmail(email);
    }
    public void delete(String username) throws MyEntityNotFoundException {
        Expert expert = find(username);
        if (!exists(username)) {
            throw new MyEntityNotFoundException("[DELETE EXPERT] The expert " + username + " was not found in the database");
        }
        em.remove(expert);
    }

    public void validateOccurrence(long code, String expertUsername) throws MessagingException {
        Occurrence occurrence = em.find(Occurrence.class, code);
        Expert expert = find(expertUsername);

        occurrence.setStatus(Status.Valid); //cuidado com o repairing
        occurrence.setExpert(expert);

        emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getCode(), "Your occurrence has been accepted. \n " + "localhost:3000/clients/" + occurrence.getClient().getUsername()+"/"+occurrence.getCode());
    }

    public void rejectOccurrence(long code, String expertUsername) throws MessagingException {
        Occurrence occurrence = em.find(Occurrence.class, code);
        Expert expert = find(expertUsername);

        occurrence.setStatus(Status.Rejected);
        occurrence.setExpert(expert);

        emailBean.send(occurrence.getClient().getEmail(), "Occurrence " + occurrence.getCode(), "Your occurrence has been rejected. \n " + "localhost:3000/clients/" + occurrence.getClient().getUsername()+"/"+occurrence.getCode());

    }
}
