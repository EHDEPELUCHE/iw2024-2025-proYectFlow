package es.uca.iw.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.annotation.Id;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.util.UUID;

@Entity
public class Usuario extends AbstractEntity {
    @Id
    @GeneratedValue
    UUID id = UUID.randomUUID();
    String nombre, apellido, correo, contrasenna;
    Tipo tipo;

    public Usuario(String nombre, String apellido, String correo, String contrasenna) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        setContrasenna(contrasenna);
        tipo = Tipo.Solicitante;
    }

    public Usuario() {

    }

    private static String Encrypt(String plain) {
        byte[] encryptedBytes = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            PublicKey PublicKey = kp.getPublic();
            Cipher cipher;
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, PublicKey);
            encryptedBytes = cipher.doFinal(plain.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (encryptedBytes != null)
            return bytesToString(encryptedBytes);
        else
            return "Error";
    }

    public static String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
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
        contrasena = Encrypt(contrasena);
        this.contrasenna = contrasena;
    }

    public UUID getId() {
        return id;
    }
    //METODO DE COMPARAR CONTRASEÑAS

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

    public enum Tipo {Solicitante, Promotor, CIO, OTP, Administrador}
}
