package es.uca.iw.data.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class promotores {
    private Long id;
    private String nombre;
    private String cargo;

    public promotores() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return nombre.substring(nombre.lastIndexOf(" ")+1);
    }

    public String getCorreo() {
        return ((nombre.substring(0, nombre.lastIndexOf(" ")) + "." +nombre.substring(nombre.lastIndexOf(" ")+1) + "@gmail.com").toLowerCase()).replace(" ","" );
    }
}