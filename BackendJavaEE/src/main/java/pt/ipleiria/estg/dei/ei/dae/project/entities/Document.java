package pt.ipleiria.estg.dei.ei.dae.project.entities;

import javax.persistence.*;
import javax.print.Doc;
import javax.validation.constraints.NotNull;
@Entity
@Table(name = "documents")
@NamedQuery(
        name = "getClientDocuments",
        query = "SELECT doc FROM Document doc WHERE doc.user.username = :username"
)

public class Document {
    @NotNull
    String filepath;
    @NotNull
    String filename;

    @ManyToOne
    @JoinColumn(name = "documents")
    @NotNull
    User user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne
    Occurrence occurrence;

    public Document(){

    }
    public Document(String filepath, String filename, User user, Occurrence occurrence) {
        this.filepath = filepath;
        this.filename = filename;
        this.user = user;
        this.occurrence = occurrence;
    }

    public Document(String filepath, String filename, User user) {
        this.filepath = filepath;
        this.filename = filename;
        this.user = user;
    }

    public Occurrence getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Occurrence occurrence) {
        this.occurrence = occurrence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public User getUser() {
        return user;
    }

    public void setClient(User user) {
        this.user = user;
    }
}
