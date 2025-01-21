package es.uca.iw.email;
/**
 * Interfaz que representa un servicio de correo electrónico.
 * Proporciona métodos para enviar correos electrónicos y recuperar la URL del servidor.
 */
public interface EmailService {
    void sendEmail(String to, String subject, String body);

    String getServerUrl();
}
