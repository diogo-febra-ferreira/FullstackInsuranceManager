package pt.ipleiria.estg.dei.ei.dae.project.dtos;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;

import java.io.Serializable;

public class ExpertDTO implements Serializable {
    String username, password, email, name;
    Insurance insurance;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public ExpertDTO() {
    }

    public ExpertDTO(String username, String password, String email, String name, Insurance insurance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.insurance = insurance;
    }

    public ExpertDTO(String username, String email, String name, Insurance insurance) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.insurance = insurance;
    }
}
