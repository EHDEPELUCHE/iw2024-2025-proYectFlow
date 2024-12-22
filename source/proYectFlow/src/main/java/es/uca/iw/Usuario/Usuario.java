package es.uca.iw.Usuario;

import es.uca.iw.global.AbstractEntity;
import es.uca.iw.global.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(
        indexes = @Index(
                name = "idx_correo",
                columnList = "correo",
                unique = true)
)
public class Usuario extends AbstractEntity implements UserDetails {
    @NotEmpty
    @Column(unique = true, nullable = false)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    private String nombre;

    @NotEmpty
    @Column(nullable = false)
    private String apellido;

    @NotEmpty
    @Email
    @Column(unique = true, nullable = false)

    private String correo;

    @NotEmpty
    @Column(nullable = false)
    private String contrasenna;

    @Enumerated(EnumType.STRING) // Para almacenar el enum como texto
    @Column(nullable = false)
    private Roles tipo;

    private boolean activo = false;
    private String codigo;

    public Usuario(String nombre, String username, String apellido, String correo, String contrasenna) {
        this.nombre = nombre;
        this.username = username;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenna = contrasenna;
        this.tipo = Roles.SOLICITANTE;
        this.activo = false;
    }

    // Constructor vacio CAMBIAR
    public Usuario() {
    }

    public Usuario(String nombre, String username, String apellido, String correo, String contrasenna, Roles tipo) {
        this.nombre = nombre;
        this.username = username;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenna = contrasenna;
        this.tipo = tipo;
        this.activo = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasena) {
        this.contrasenna = contrasena;
    }

    public Roles getTipo() {
        return tipo;
    }

    public void setTipo(Roles tipo) {
        this.tipo = tipo;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // A partir de aqui funciones propias del modulo de seguridad
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipo.name()));
    }

    @Override
    public boolean isEnabled() {
        return this.activo;
    }

    @Override
    public String getPassword() {
        return contrasenna;
    }

    public String getHashedPassword() {
        return contrasenna;
    }
}