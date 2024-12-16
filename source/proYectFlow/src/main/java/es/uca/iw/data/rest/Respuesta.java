package es.uca.iw.data.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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