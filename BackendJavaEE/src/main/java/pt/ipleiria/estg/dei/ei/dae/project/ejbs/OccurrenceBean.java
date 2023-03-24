package pt.ipleiria.estg.dei.ei.dae.project.ejbs;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.Status;
import pt.ipleiria.estg.dei.ei.dae.project.dtos.OccurrenceDTO;
import pt.ipleiria.estg.dei.ei.dae.project.entities.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OccurrenceBean {
    @PersistenceContext
    EntityManager em;

    @EJB
    ClientBean cb;

    @EJB
    EmailBean emailBean;

    @EJB
    RepairmanBean rb;

    @EJB
    PolicyBean pb;

    @EJB
    DocumentBean db;


    public Occurrence create(String description, String insurance, String client_nif, Long document_id, String type, Long codePolicy){
        Client client = em.find(Client.class, client_nif);
        Document document = em.find(Document.class, document_id);
        Expert expert = em.find(Expert.class, "dummyExpert");
        Insurance i = null;
        Status status = Status.Broken;
        //define insurance
        switch(insurance){
            case "Fidelidade":
                i = Insurance.Fidelidade;
                break;
            case "Teleseguros":
                i=Insurance.Teleseguros;
                break;
            case "SegurosSA":
                i=Insurance.SegurosSA;
                break;
            case "SegurosPortugal":
                i=Insurance.SegurosPortugal;
                break;
            case "GlobalInsurance":
                i=Insurance.GlobalInsurance;
                break;
        }

        //check type
        Policy policy = pb.find(codePolicy);

        //If type == policy.getType occorrence is accepted, else is rejected

        if(type.toLowerCase().equals(policy.getType().toLowerCase())){
            status = Status.Broken;
        } else {
            status = Status.Rejected;
        }


        Occurrence occurrence = new Occurrence(description, client, i, document, status, type, expert);
        document.setOccurrence(occurrence);
        em.persist(occurrence);
        em.persist(document);
        return occurrence;
    }

    public List<Occurrence> getAllOccurrences(){
        return (List<Occurrence>) em.createNamedQuery("getAllOccurrences").getResultList();
    }

    public OccurrenceDTO updateOccurrenceDocument(long occurrenceId, long documentId){
        Occurrence occurrence = find(occurrenceId);
        Document document = db.find(documentId);
        occurrence.setDocument(document);
        OccurrenceDTO occurrenceDTO = new OccurrenceDTO(occurrence.getCode(), occurrence.getDescription(),
                occurrence.getInsurance().name(), occurrence.getClient(), occurrence.getStatus(),occurrence.getDocument(),occurrence.getType(), occurrence.getExpert());
        return occurrenceDTO;
    }


    public List<Occurrence> findByInsurance(Insurance insurance){
        return em.createNamedQuery("getInsuranceOccurrences", Occurrence.class)
                .setParameter("insurance", insurance)
                .getResultList();
    }


    public List<Occurrence> findByClient(String client_nif){
        return em.createNamedQuery("getClientOccurrences", Occurrence.class)
                .setParameter("client_nif", client_nif)
                .getResultList();
    }

    public Occurrence find(long code){
        Occurrence occurrence = em.find(Occurrence.class, code);
        return occurrence;
    }

    public void updateStatusRepaired(long code) throws MessagingException {
        Occurrence occurrence = em.find(Occurrence.class,code);
        Client client = occurrence.getClient();
        Expert expert = occurrence.getExpert();
        if (occurrence.getStatus().compareTo(Status.Rejected) == 0){
            System.out.println("The occurrence is invalid");
            throw new IllegalArgumentException("State of occurrence invalid for association with repairman");
        }else{
            occurrence.setStatus(Status.Repaired);
            emailBean.send(client.getEmail(), "Occurrence " + occurrence.getCode() + " status updated to repaired",
                    "Occurrence " + occurrence.getCode() + " status updated to repaired" + "\n" + "localhost:3000/clients/" + client.getUsername()+"/"+occurrence.getCode());
            emailBean.send(expert.getEmail(), "Occurrence " + occurrence.getCode() + " status updated to repaired",
                    "Occurrence " + occurrence.getCode() + " status updated to repaired" + "\n" + "localhost:3000/occurrences/"+occurrence.getCode());
        }
    }
}
