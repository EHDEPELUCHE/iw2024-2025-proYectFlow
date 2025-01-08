package es.uca.iw.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.Normalizer;

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