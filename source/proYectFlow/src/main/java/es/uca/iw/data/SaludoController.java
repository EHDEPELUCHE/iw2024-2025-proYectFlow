package es.uca.iw.proYectFlow.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {
    @GetMapping("/saludar")
    public String saludar() {
        return "Hola Mundo";
    }
    @GetMapping("/saludar/persona")
    public String getPersona(){
        return (new Persona("Juan", "Pérez")).toString();
    }
}
