package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Hasher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class ClientBean {

    @PersistenceContext
    EntityManager em;

    @Inject //
    private Hasher hasher;

    public boolean exists(String username) {
        Query query = em.createQuery(
                "SELECT COUNT(c.username) FROM Client c WHERE c.username = :username",
                Long.class
        );
        query.setParameter("username", username);
        return (Long)query.getSingleResult() > 0L;
    }

    public void create(String username, String password, String name, String email) throws MyEntityExistsException, MyEntityNotFoundException, MyConstraintViolationException {

        if(exists(username)){
            throw new MyEntityExistsException("The client " + username + " already exists in the database");
        }

        try {
            Client client = new Client(username, hasher.hash(password), email, name);
            em.persist(client);
        } catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }


    }

    public List<Client> getAllClients() {
        return (List<Client>) em.createNamedQuery("getAllClients").getResultList();
    }

    //for clients username = nif
    public Client find(String username){
        return em.find(Client.class, username);
    }

    public void update(String username, String password, String name, String email) throws MyEntityNotFoundException{
        Client client = em.find(Client.class, username);
        if (client == null) {

            throw new MyEntityNotFoundException("[UPDATE CLIENT] The client " + username + " was not found in the database.");
        }

        em.lock(client, LockModeType.OPTIMISTIC);

        client.setPassword(hasher.hash(password));
        client.setName(name);
        client.setEmail(email);
    }
    public void delete(String username) throws MyEntityNotFoundException {
        Client client = find(username);
        if (!exists(username)) {
            throw new MyEntityNotFoundException("[DELETE CLIENT] The client " + username + " was not found in the database");
        }
        em.remove(client);
    }
}
