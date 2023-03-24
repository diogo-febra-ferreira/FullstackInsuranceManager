package pt.ipleiria.estg.dei.ei.dae.project.entities;

import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
        name = "getAllClients",
        query = "SELECT c FROM Client c ORDER BY c.name" // JPQL
         )
})
public class Client extends User implements Serializable  {

    //Clients username = nif

    @OneToMany
    List<Occurrence> occurrences;

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Client() {
        this.occurrences = new ArrayList<>();
        this.documents = new ArrayList<>();
    }

    public Client(String username, String password, String email, String name) {
        super(username, password, name, email);
        this.occurrences = new ArrayList<>();
        this.documents = new ArrayList<>();
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
}
