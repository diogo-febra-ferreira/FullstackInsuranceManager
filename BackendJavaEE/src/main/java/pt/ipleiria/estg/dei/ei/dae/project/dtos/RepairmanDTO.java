package pt.ipleiria.estg.dei.ei.dae.project.dtos;

import pt.ipleiria.estg.dei.ei.dae.project.Insurance;
import pt.ipleiria.estg.dei.ei.dae.project.entities.Repairman;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepairmanDTO {
    String username, password, email, name;
    List<OccurrenceDTO> occurrences;
    Insurance insurance;

    public RepairmanDTO() {
        occurrences = new ArrayList<>();
    }

    public RepairmanDTO(String username, String password, String email, String name, Insurance insurance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.insurance = insurance;
        occurrences = new ArrayList<>();
    }

    public static RepairmanDTO from(Repairman repairman) {
        return new RepairmanDTO(
                repairman.getUsername(),
                repairman.getPassword(),
                repairman.getEmail(),
                repairman.getName(),
                repairman.getInsurance()
        );
    }

    public static List<RepairmanDTO> from(List<Repairman> repairmen) {
        return repairmen.stream().map(RepairmanDTO::from).collect(Collectors.toList());
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

    public List<OccurrenceDTO> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<OccurrenceDTO> occurrences) {
        this.occurrences = occurrences;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }
}
