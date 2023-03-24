package pt.ipleiria.estg.dei.ei.dae.project.dtos;


import pt.ipleiria.estg.dei.ei.dae.project.entities.Client;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Document;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Expert;
import pt.ipleiria.estg.dei.ei.dae.project.Status;
import pt.ipleiria.estg.dei.ei.dae.project.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OccurrenceDTO {
    long code;

    String description;

    String insurance;

    ClientDTO client;

    List<RepairmanDTO> repairmen;


    ExpertDTO expert;

    DocumentDTO document;

    Status status;

    String type;

    public OccurrenceDTO() {

    }

    public OccurrenceDTO(long code, String description, String insurance, Client client, List<RepairmanDTO> repairmens, Document document, Status status, String type, Expert expert) {
        this.code = code;
        this.description = description;
        this.insurance = insurance;
        this.client = new ClientDTO(client.getUsername(),client.getEmail(), client.getName());
        this.repairmen = repairmens;
        this.document = new DocumentDTO(document.getId(), document.getFilename());
        this.status = status;
        this.type = type;
        this.expert = new ExpertDTO(expert.getUsername(), expert.getEmail(), expert.getName(), expert.getInsurance());
    }


    public OccurrenceDTO(long code, String description, String insurance, Client client, Status status, Document document, String type, Expert expert) {

        this.code = code;
        this.description = description;
        this.insurance = insurance;
        this.client = new ClientDTO(client.getUsername(), client.getEmail(), client.getName());
        this.status = status;
        this.document = new DocumentDTO(document.getId(), document.getFilename());
        this.repairmen = new ArrayList<>();
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OccurrenceDTO(long code, String description, String insurance, Client client, Status status) {
        this.code = code;
        this.description = description;
        this.insurance = insurance;
        this.client = new ClientDTO(client.getUsername(),  client.getEmail(), client.getName());
        this.status = status;
        this.repairmen = new ArrayList<>();
    }

    public static OccurrenceDTO from(Occurrence occurrence) {
        return new OccurrenceDTO(
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
    }

    public static List<OccurrenceDTO> from(List<Occurrence> occurrences) {
        return occurrences.stream().map(OccurrenceDTO::from).collect(Collectors.toList());
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public ClientDTO getClient() {
        return client;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public ExpertDTO getExpert() {
        return expert;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }


    public void setExpert(ExpertDTO expert) {
        this.expert = expert;
    }

    public List<RepairmanDTO> getRepairmen() {
        return repairmen;
    }

    public void setRepairmen(List<RepairmanDTO> repairmen) {
        this.repairmen = repairmen;
    }

    public void addRepairman(RepairmanDTO repairman){
        if (repairman != null){
            repairmen.add(new RepairmanDTO(repairman.getUsername(),repairman.getPassword(),repairman.getEmail(),repairman.getName(),repairman.getInsurance()));
        }

    }

    public void removeRepairman(RepairmanDTO repairman){
        if (repairman != null){
            repairmen.remove(new RepairmanDTO(repairman.getUsername(),repairman.getPassword(),repairman.getEmail(),repairman.getName(),repairman.getInsurance()));
        }
    }
}
