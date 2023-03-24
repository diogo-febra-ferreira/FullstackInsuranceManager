package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import org.hibernate.Hibernate;
import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Repairman;
import pt.ipleiria.estg.dei.ei.dae.project.Status;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.project.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.project.security.Hasher;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class RepairmanBean {
    @PersistenceContext
    EntityManager em;
    @Inject //
    private Hasher hasher;

    @EJB
    OccurrenceBean ob;

    @EJB
    EmailBean eb;

    @Resource(name = "java:/jboss/mail/fakeSMTP")
    private Session mailSession;

    public void create(String username, String password, String name, String email, Insurance seguradora) throws MyEntityExistsException, MyConstraintViolationException{


        try {
            Repairman repairman = new Repairman(username, hasher.hash(password), name, email, seguradora);
            em.persist(repairman);
        } catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }
    }

    public Repairman find(String username) {
        Repairman r = em.find(Repairman.class, username);
        Hibernate.initialize(r.getOccurrences());
        return r;
    }

    public List<Occurrence> getRepairmanNotAssociated(String username){
        Repairman repairman = em.find(Repairman.class, username);
        List<Occurrence> differences = new ArrayList<>(ob.getAllOccurrences());

        differences.removeAll(repairman.getOccurrences());
        return differences;
    }

    public Repairman findOrFail(String username) {
        Repairman repairman = em.getReference(Repairman.class, username);
        Hibernate.initialize(repairman);
        return repairman;
    }

    public List<Repairman> getAllRepairman(int offset, int limit) {
        return (List<Repairman>) em.createNamedQuery("getAllRepairmen")
                .setFirstResult(offset)
                .setMaxResults(limit).getResultList();
    }

    public List<Repairman> getAllRepairman() {
        return (List<Repairman>) em.createNamedQuery("getAllRepairmen").getResultList();
    }



    public List<Repairman> getRepairmanWithInsuranceOrNull(int offset, int limit, Insurance insurance) {

        List<Repairman> allRepairmen = (List<Repairman>) em.createNamedQuery("getAllRepairmen")
                .setFirstResult(offset)
                .setMaxResults(limit).getResultList();

        List<Repairman> aux = new ArrayList<>();
        for (Repairman r: allRepairmen) {
            if(r.getInsurance().equals(insurance) || r.getInsurance()==null){
                aux.add(r);
            }
        }
        return aux;
    }

    public List<Occurrence> associated(String username) {
        List<Occurrence> occurrences = findOrFail(username).getOccurrences();
        Hibernate.initialize(occurrences);

        return occurrences;
    }

    public List<Repairman> getOccurrencesEnrolledRepairman(long code) {
        Occurrence occurence = ob.find(code);
        List<Repairman> aux = new ArrayList<>();
        for (Repairman r: getAllRepairman()) {

            if(r.getOccurrences().contains(occurence)){
                aux.add(r);
            }
        }
        return aux;
    }

    public List<Repairman> getOccurrencesUnrolledRepairman(long code) {
        Occurrence occurence = ob.find(code);
        List<Repairman> aux = new ArrayList<>();
        for (Repairman r: getAllRepairman()) {

            if((!r.getOccurrences().contains(occurence)) && (occurence.getInsurance().equals(r.getInsurance()) || r.getInsurance()==null)){
                aux.add(r);
            }
        }
        return aux;
    }

    public Long count() {
        return em.createQuery("SELECT COUNT(*) FROM " + Repairman.class.getSimpleName(), Long.class).getSingleResult();
    }

    public void associateRepairmanWithOccurrence(String username, long code) throws MessagingException {
        Repairman repairman = find(username);
        if (repairman == null){
            System.out.println("The repairman is invalid");
        }else{
            Occurrence occurrence = ob.find(code);
            if (occurrence == null || occurrence.getStatus().compareTo(Status.Rejected) == 0 || occurrence.getStatus().compareTo(Status.Repaired) == 0){
                System.out.println("The occurrence is invalid");
                throw new IllegalArgumentException("State of occurrence invalid for association with repairman");
            }else{
                occurrence.setStatus(Status.Repairing);
                occurrence.addRepairman(repairman);
                repairman.addOccurrence(occurrence);

                    eb.send(repairman.getEmail(), "Associated to Occurrence N : " + occurrence.getCode(),
                            "You were associated to the occurrence N : " + occurrence.getCode() + "\n" + "Here is the link where you can view the occurrence you were associated with: localhost:3000/repairmans/code/" + occurrence.getCode());

            }

        }
    }

    public void desassociateRepairmanWithOccurrence(String username,long id){
        Repairman repairman = em.find(Repairman.class,username);
        if (repairman == null){
            System.out.println("The repairman is invalid");
        }else{
            Occurrence occurrence = em.find(Occurrence.class,id);
            if (occurrence == null){
                System.out.println("The occurrence is invalid");
            }else{
                repairman.removeOccurrence(occurrence);
                occurrence.removeRepairman(repairman);
            }
        }
    }

    public void deleteRow(List<Repairman> repairmen, long id){
        Occurrence occurrence = em.find(Occurrence.class,id);
        if (occurrence == null){
            System.out.println("The occurrence is invalid");
        }else {

            for (Repairman repairman : repairmen) {

                repairman.removeOccurrence(occurrence);
            }
        }
    }
}
