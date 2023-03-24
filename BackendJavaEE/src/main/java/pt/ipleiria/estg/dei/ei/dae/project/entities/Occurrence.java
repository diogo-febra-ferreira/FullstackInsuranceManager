package pt.ipleiria.estg.dei.ei.dae.project.entities;


import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.Status;

import pt.ipleiria.estg.dei.ei.dae.project.dtos.RepairmanDTO;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllOccurrences",
                query = "SELECT o FROM Occurrence o"
        ),
        @NamedQuery(
                name = "getClientOccurrences",
                query = "SELECT o FROM Occurrence o WHERE o.client.username = :client_nif"
        ),
        @NamedQuery(
                name = "getInsuranceOccurrences",
                query = "SELECT o FROM Occurrence o WHERE o.insurance = :insurance"
        )
})
@Table(
        name = "occurrence"
)
public class Occurrence extends Versionable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long code;

    String description;

    Insurance insurance;

    Status status;

    String type;

    @ManyToOne
    @JoinColumn(name = "client_nif")
    @NotNull
    Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "occurrence_users",
            joinColumns = @JoinColumn(name = "occurrence_code", referencedColumnName = "code"),
            inverseJoinColumns = @JoinColumn(name = "repairman_username", referencedColumnName = "username"))
    List<Repairman> repairmen;

    @OneToOne
    Document document;

    @ManyToOne
    @JoinColumn(name = "expert_username")
    Expert expert;

    public Occurrence() {
        repairmen = new ArrayList<>();
    }

    public String getName() {
        return description;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Occurrence(String description, Client client, Insurance insurance, Document document, Status status, String type, Expert expert) {
        this.description = description;
        this.client = client;
        this.insurance = insurance;
        repairmen = new ArrayList<>();
        this.document = document;
        this.status = status;
        this.type = type;
        this.expert = expert;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Repairman> getRepairmen() {
        return repairmen;
    }

    public List<RepairmanDTO> getRepairmenDTOS(List<Repairman> repairmen){
        List<RepairmanDTO> repairmanDTOS = new ArrayList<>();
        for (Repairman repairman :repairmen) {
            RepairmanDTO repairmanDTO = new RepairmanDTO(repairman.getUsername(), null, repairman.getEmail(), repairman.getName(), repairman.getInsurance());
            if (!repairmanDTOS.contains(repairmanDTO)){
                repairmanDTOS.add(repairmanDTO);
            }
        }
        System.out.println("repairmanDTOS" + repairmanDTOS);
        return repairmanDTOS;
    }

    public void setRepairmen(List<Repairman> repairmen) {
        this.repairmen = repairmen;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public void addRepairman(Repairman repairman){
        if (!repairmen.contains(repairman)){
            System.out.println(repairman);
            System.out.println("here");
            repairmen.add(repairman);
        }
    }

    public void removeRepairman(Repairman repairman){
        repairmen.remove(repairman);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
