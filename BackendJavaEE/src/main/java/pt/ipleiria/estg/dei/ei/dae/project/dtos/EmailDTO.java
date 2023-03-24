package pt.ipleiria.estg.dei.ei.dae.project.dtos;

public class EmailDTO {
    private String occurrence;

    private String message;

    public EmailDTO() {}

    public String getOccurrence() {
        return occurrence;
    }

    public String getMessage() {
        return message;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
