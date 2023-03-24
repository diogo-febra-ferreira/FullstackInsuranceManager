package pt.ipleiria.estg.dei.ei.dae.project.entities;

import org.hibernate.annotations.Cascade;
import pt.ipleiria.estg.dei.ei.dae.project.Insurance;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllExperts",
                query = "SELECT e FROM Expert e ORDER BY e.name" // JPQL
        )
})
public class Expert extends User{

    @OneToMany
    List<Occurrence> occurrences;

    Insurance insurance;

    public Expert() {
        this.occurrences = new ArrayList<>();
    }

    public Expert(String username, String password, String name, String email, Insurance insurance) {
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

    public Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
    }

    public void addOccurrence(Occurrence occurrence){
        if(!this.occurrences.contains(occurrence)) {
            this.occurrences.add(occurrence);
        }
    }

    public void removeOccurrence(Occurrence occurrence){
        this.occurrences.remove(occurrence);
    }
}
