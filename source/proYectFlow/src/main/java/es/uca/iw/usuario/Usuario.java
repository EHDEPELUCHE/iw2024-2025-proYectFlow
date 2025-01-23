package es.uca.iw.usuario;

import es.uca.iw.global.AbstractEntity;
import es.uca.iw.global.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * La clase Usuario representa a un usuario en el sistema.
 * Implementa la interfaz UserDetails para integrarse con Spring Security.
 * <p>
 * Anotaciones:
 * - @Entity: Indica que esta clase es una entidad JPA.
 * - @EntityListeners(AuditingEntityListener.class): Añade soporte para auditoría.
 * - @Table: Define la tabla y sus índices en la base de datos.
 * <p>
 * Atributos:
 * - username: Nombre de usuario único y no vacío.
 * - nombre: Nombre del usuario, no vacío.
 * - apellido: Apellido del usuario, no vacío.
 * - correo: Correo electrónico único y no vacío.
 * - contrasenna: Contraseña del usuario, no vacía.
 * - tipo: Rol del usuario, almacenado como texto.
 * - activo: Indica si el usuario está activo.
 * - codigo: Código adicional del usuario.
 * <p>
 * Constructores:
 * - Usuario(String nombre, String username, String apellido, String correo, String contrasenna): Inicializa un usuario con rol SOLICITANTE y no activo.
 * - Usuario(): Constructor vacío.
 * - Usuario(String nombre, String username, String apellido, String correo, String contrasenna, Roles tipo): Inicializa un usuario con un rol específico y no activo.
 * <p>
 * Métodos:
 * - Getters y setters para todos los atributos.
 * - getAuthorities(): Devuelve la autoridad del usuario basada en su rol.
 * - isEnabled(): Indica si el usuario está activo.
 * - getPassword(): Devuelve la contraseña del usuario.
 * - getHashedPassword(): Devuelve la contraseña del usuario.
 * - equals(Object o): Compara este usuario con otro objeto.
 * - hashCode(): Devuelve el código hash del usuario.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        indexes = @Index(
                name = "idx_correo",
                columnList = "correo",
                unique = true)
)
@NamedEntityGraph(
        name = "Usuario.detail",
        attributeNodes = {
                @NamedAttributeNode("tipo")
        }
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

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @CreatedBy
    private Usuario createdBy;

    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    @LastModifiedBy
    private Usuario lastModifiedBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;

    public Usuario(String nombre, String username, String apellido, String correo, String contrasenna) {
        this.nombre = nombre;
        this.username = username;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenna = contrasenna;
        this.tipo = Roles.SOLICITANTE;
        this.activo = false;
    }

    public Usuario() {
        //Empty constructor
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

    public Usuario getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Usuario lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Usuario getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Usuario createdBy) {
        this.createdBy = createdBy;
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
        return getContrasenna();
    }

    public String getHashedPassword() {
        return getPassword();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        if (!super.equals(o)) return false;

        Usuario usuario = (Usuario) o;

        if (activo != usuario.activo) return false;
        if (username != null ? !username.equals(usuario.username) : usuario.username != null) return false;
        if (nombre != null ? !nombre.equals(usuario.nombre) : usuario.nombre != null) return false;
        if (apellido != null ? !apellido.equals(usuario.apellido) : usuario.apellido != null) return false;
        if (correo != null ? !correo.equals(usuario.correo) : usuario.correo != null) return false;
        if (contrasenna != null ? !contrasenna.equals(usuario.contrasenna) : usuario.contrasenna != null) return false;
        if (tipo != usuario.tipo) return false;
        return codigo != null ? codigo.equals(usuario.codigo) : usuario.codigo == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (apellido != null ? apellido.hashCode() : 0);
        result = 31 * result + (correo != null ? correo.hashCode() : 0);
        result = 31 * result + (contrasenna != null ? contrasenna.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (activo ? 1 : 0);
        result = 31 * result + (codigo != null ? codigo.hashCode() : 0);
        return result;
    }
}