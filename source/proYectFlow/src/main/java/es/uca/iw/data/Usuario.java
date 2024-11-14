package es.uca.iw.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
public class Usuario extends AbstractEntity {
    @Id
    @GeneratedValue
    UUID id = UUID.randomUUID();

    String username, apellido, correo, contrasenna;
    Tipo tipo;

    public Usuario(String nombre, String apellido, String correo, String contrasenna) {
        this.username = nombre;
        this.apellido = apellido;
        this.correo = correo;
        setContrasenna(contrasenna);
        tipo = Tipo.Solicitante;
    }

    public Usuario() {

    }


    public Tipo getTipo() {
        return tipo;
    }

    //PROTEGER PARA ADMIN
    private void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    private String getContrasenna() {
        return contrasenna;
    }

    private void setContrasenna(String contrasena) {

        this.contrasenna = contrasena;
    }

    public UUID getId() {
        return id;
    }
    //METODO DE COMPARAR CONTRASEÑAS

    public String getUsername() {
        return username;
    }

    public void setUsername(String nombre) {
        this.username = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getHashedPassword() {
        return contrasenna;
    }

    public enum Tipo {
        Solicitante, Promotor, CIO, OTP, Administrador;


    }
}
