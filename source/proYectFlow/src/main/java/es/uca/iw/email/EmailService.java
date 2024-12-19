package es.uca.iw.email;

public interface EmailService {
    public void sendEmail(String to, String subject, String body);

    public String getServerUrl();
}
