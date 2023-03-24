package pt.ipleiria.estg.dei.ei.dae.project.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Policy {
    String client;
    String insurance;
    String type;
    String covers;
    @Id
    String code;

    public Policy() {
    }

    public Policy(String client, String insurance, String type, String covers, String code) {
        this.client = client;
        this.insurance = insurance;
        this.type = type;
        this.covers = covers;
        this.code = code;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCovers() {
        return covers;
    }

    public void setCovers(String covers) {
        this.covers = covers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
