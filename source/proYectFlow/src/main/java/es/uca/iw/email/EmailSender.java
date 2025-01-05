package es.uca.iw.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender implements EmailService {
    private final JavaMailSender emailSender;
 
    public EmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public String getServerUrl() {
        // Generate the server URL
        String serverUrl = "http://";
        if (System.getenv("ENVIRONMENT") != null && System.getenv("ENVIRONMENT").equals("prod")) {
            serverUrl += "proyectflow.westeurope.cloudapp.azure.com";
            
        } else {
            int serverPort = 8080;
            serverUrl += "localhost:" + serverPort;
        }
        serverUrl += "/";
        return serverUrl;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("iwproyectflow@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
            System.out.println("Correo enviado con éxito!");
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
