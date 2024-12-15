package es.uca.iw.data.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class servicio {
    private String status;
    @JsonProperty("data")
    private List<promotores> data;


    public List<promotores> getPromotor() {
        return data;
    }

    public void setPromotor(List<promotores> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static servicio fetchDataFromApi() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RestTemplateConfig.class);
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        String url = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
        try {
            return restTemplate.getForEntity(url, servicio.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            context.close();
        }
    }
}