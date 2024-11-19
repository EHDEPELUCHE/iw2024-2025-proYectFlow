package es.uca.iw.data;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class Usuario extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

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

    private String registerCode = "";
    private boolean enabled = false;

    /*
    private String username, nombre, apellido, correo, contrasenna;
    Roles tipo;

    public Usuario(String nombre, String username, String apellido, String correo, String contrasenna) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.username = username;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenna = contrasenna;
        tipo = Roles.SOLICITANTE;
    }
    
    public Usuario() {}
    */
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String user) { this.username = user; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasenna() { return contrasenna; }
    public void setContrasenna(String contrasena) { this.contrasenna = contrasena; }

    public Roles getTipo() { return tipo; }
    public void setTipo(Roles tipo) { this.tipo = tipo; }

    @Override
    public int hashCode() { return (id != null) ? id.hashCode() : super.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Usuario other)) return false;
        if (id != null) return id.equals(other.id);
        return super.equals(other);
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }
        
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(tipo.name()));
    }
    
    @Override
    public String getPassword() { return contrasenna; }
}