package es.uca.iw.data;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Usuario extends AbstractEntity implements UserDetails {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasenna;

    @Enumerated(EnumType.STRING) // Para almacenar el enum como texto
    @Column(nullable = false)

    private Roles tipo;

    /*private String username, nombre, apellido, correo, contrasenna;
    Roles tipo;*/

    public Usuario(String nombre, String username, String apellido, String correo, String contrasenna) {
        this.nombre = nombre;
        this.username = username;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenna = contrasenna;
        tipo = Roles.SOLICITANTE;
    }

    public Usuario() {}

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    //METODO DE COMPARAR CONTRASEÑAS

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) { this.username = user; }

    @Override
    public String getPassword() {
        return contrasenna;
    }

    public void setPassword(String password) {
        this.contrasenna = password;
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