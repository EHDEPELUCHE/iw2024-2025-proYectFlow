package es.uca.iw.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * La clase Respuesta representa una respuesta JSON que contiene un estado y una lista de objetos Promotor.
 * 
 * La anotación @JsonIgnoreProperties(ignoreUnknown = true) indica que se deben ignorar las propiedades desconocidas
 * durante la deserialización de JSON.
 * 
 * La anotación @JsonProperty("data") se utiliza para mapear la propiedad "data" del JSON a la lista de objetos Promotor.
 * 
 * Métodos:
 * - getData(): Devuelve la lista de objetos Promotor.
 * - setData(List<Promotor> data): Establece la lista de objetos Promotor.
 * - getStatus(): Devuelve el estado de la respuesta.
 * - setStatus(String status): Establece el estado de la respuesta.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Respuesta {
    private String status;

    @JsonProperty("data")
    private List<Promotor> data;

    public List<Promotor> getData() {
        return data;
    }

    public void setData(List<Promotor> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}