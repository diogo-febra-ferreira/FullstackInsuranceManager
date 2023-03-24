package pt.ipleiria.estg.dei.ei.dae.project.dtos;

import pt.ipleiria.estg.dei.ei.dae.project.entities.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DocumentDTO implements Serializable {
    Long id;
    String filename;

    public DocumentDTO() {
    }

    public DocumentDTO(Long id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public static Object from(List<Document> documents) {
        List<DocumentDTO> dtos = new ArrayList<>();
        for (Document document :
                documents) {
            dtos.add(new DocumentDTO(document.getId(), document.getFilename()));
        }
        return dtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
