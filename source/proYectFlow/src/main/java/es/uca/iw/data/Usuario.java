package es.uca.iw.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class Usuario extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue
    UUID id = UUID.randomUUID();

    String username, apellido, correo, contrasenna;
    Roles tipo;

    public Usuario(String nombre, String apellido, String correo, String contrasenna) {
        this.username = nombre;
        this.apellido = apellido;
        this.correo = correo;
        setContrasenna(contrasenna);
        tipo = Roles.SOLICITANTE;
    }

    public Usuario() {

    }

    public Roles getTipo() {
        return tipo;
    }

    //PROTEGER PARA ADMIN
    public void setTipo(Roles tipo) {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    //METODO DE COMPARAR CONTRASEÑAS

    @Override
    public String getPassword() {
        return contrasenna;
    }

    public void setPassword(String password) {
        this.contrasenna = password;
    }

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

    //NO se pa q lo tiene el profe
   /* public void setRegisterCode(String substring) {
        id = substring;
    }*/
}