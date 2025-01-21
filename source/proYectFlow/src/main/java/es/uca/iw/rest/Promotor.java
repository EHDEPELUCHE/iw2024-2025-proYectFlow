package es.uca.iw.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.Normalizer;

/**
 * La clase Promotor representa un promotor con un identificador, nombre y cargo.
 * 
 * <p>Esta clase está anotada con @JsonIgnoreProperties para ignorar propiedades desconocidas
 * durante la deserialización JSON.</p>
 * 
 * <p>Proporciona métodos para obtener y establecer el identificador, nombre y cargo del promotor.
 * Además, incluye métodos para obtener el apellido y el correo electrónico del promotor.</p>
 * 
 * <p>El correo electrónico se genera a partir del nombre, eliminando tildes y otros caracteres
 * no ASCII, y concatenando el nombre y apellido separados por un punto, seguido de "@example.com".</p>
 * 
 * <p>Ejemplo de uso:</p>
 * <pre>
 * {@code
 * Promotor promotor = new Promotor();
 * promotor.setNombre("Juan Pérez");
 * String correo = promotor.getCorreo(); // "juan.perez@example.com"
 * }
 * </pre>
 * 
 * <p>Nota: El método getNombre() devuelve el nombre sin el apellido, mientras que getApellido()
 * devuelve solo el apellido.</p>
 * 
 * @author Elena
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Promotor {
    private String id;
    private String nombre;
    private String cargo;

    public Promotor() {
        //Empty constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return (nombre.substring(0, nombre.lastIndexOf(" ")));
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getApellido() {
        return nombre.substring(nombre.lastIndexOf(" ") + 1);
    }

    public String getCorreo() {
        String nombreSinTildes = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        nombreSinTildes = nombreSinTildes.replaceAll("[^\\p{ASCII}]", "");

        return ((nombreSinTildes.substring(0, nombreSinTildes.lastIndexOf(" ")) + "." +
                nombreSinTildes.substring(nombreSinTildes.lastIndexOf(" ") + 1) +
                "@example.com").toLowerCase()).replace(" ", "");
    }
}