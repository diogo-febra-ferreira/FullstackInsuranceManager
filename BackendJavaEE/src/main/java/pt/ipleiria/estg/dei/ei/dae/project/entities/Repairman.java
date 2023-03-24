package pt.ipleiria.estg.dei.ei.dae.project.entities;


import pt.ipleiria.estg.dei.ei.dae.project.Insurance;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllRepairmen",
                query = "SELECT u FROM Repairman u ORDER BY u.username" // JPQL
        )
})
public class Repairman extends User {

    Insurance insurance;
    @ManyToMany(mappedBy = "repairmen")
    List<Occurrence> occurrences;
    public Repairman() {
        occurrences = new ArrayList<>();
    }

    public Repairman(String username, String password, String name, String email, Insurance insurance) {
        super(username, password, name, email);
        this.insurance = insurance;
        occurrences = new ArrayList<>();
    }

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }

    public void addOccurrence(Occurrence occurrence){
        if(!occurrences.contains(occurrence)){
            occurrences.add(occurrence);
        }
    }

    public void removeOccurrence(Occurrence occurrence){
        occurrences.remove(occurrence);
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }
}
