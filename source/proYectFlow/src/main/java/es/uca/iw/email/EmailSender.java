package es.uca.iw.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 * Clase de servicio para enviar correos electrónicos.
 * Implementa la interfaz EmailService.
 * 
 * Esta clase utiliza JavaMailSender para enviar correos electrónicos y proporciona métodos para obtener la URL del servidor
 * según el perfil activo.
 * 
 * @author 
 */
@Service
public class EmailSender implements EmailService {
    private final JavaMailSender correo;
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    public EmailSender(JavaMailSender emailSender) {
        this.correo = emailSender;
    }

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Override
    public String getServerUrl() {
        // Generate the server URL
        String serverUrl = "http://";
        if ("prod".equals(activeProfile)) {
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
            correo.send(message);
        } catch (Exception e) {
            logger.error("Error al enviar el email", e);
        }
    }
}
