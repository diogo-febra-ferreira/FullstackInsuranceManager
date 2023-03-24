package pt.ipleiria.estg.dei.ei.dae.project.dtos;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Occurrence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientDTO implements Serializable {
    String username, password, email, name;
    List<Occurrence> occurrences;

    public ClientDTO() {
        this.occurrences = new ArrayList<>();
    }

    public ClientDTO(String username, String password, String email, String name, List<Occurrence> occurrences) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        //this.occurrences = occurrences;
    }

    public ClientDTO(String username,  String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

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

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }
}
