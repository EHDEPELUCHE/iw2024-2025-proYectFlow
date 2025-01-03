package es.uca.iw.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body);

    String getServerUrl();
}
