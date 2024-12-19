package es.uca.iw.services;

public interface EmailService {
    public void sendEmail(String to, String subject, String body);
    public String getServerUrl();
}
