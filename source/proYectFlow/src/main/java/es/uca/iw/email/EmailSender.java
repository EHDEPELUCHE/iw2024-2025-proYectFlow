package es.uca.iw.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    private final int serverPort = 8080;

    @Override
    public String getServerUrl() {
        // Generate the server URL
        String serverUrl = "http://";
        serverUrl += "proyectflow.westeurope.cloudapp.azure.com";
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
