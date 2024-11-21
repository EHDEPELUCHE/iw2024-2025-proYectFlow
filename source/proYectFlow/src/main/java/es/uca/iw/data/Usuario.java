package es.uca.iw.data;

<<<<<<< Updated upstream
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;
=======
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    public void setUsername(String nombre) {
        this.username = nombre;
=======
    public void setUsername(String user) {
        this.username = user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    //NO se pa q lo tiene el profe
   /* public void setRegisterCode(String substring) {
        id = substring;
    }*/
=======
>>>>>>> Stashed changes
}