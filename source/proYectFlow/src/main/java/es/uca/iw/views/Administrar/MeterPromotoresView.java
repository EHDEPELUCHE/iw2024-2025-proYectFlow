package es.uca.iw.views.Administrar;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.data.Roles;
import es.uca.iw.data.rest.RestTemplateConfig;
import es.uca.iw.data.rest.promotores;
import es.uca.iw.data.rest.servicio;
import es.uca.iw.services.ProyectoService;
import es.uca.iw.services.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Administración de usuarios")
@Route("AdministrarUsuarios")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ADMIN")
public class MeterPromotoresView extends Composite<VerticalLayout> {
    RestTemplate restTemplate;
    UsuarioService usuarioService;
    private static final Logger logger = Logger.getLogger(MeterPromotoresView.class.getName());

    @Autowired
    public MeterPromotoresView(RestTemplate restTemplate, UsuarioService usuarioService) throws Exception {
        this.restTemplate = restTemplate;
        this.usuarioService = usuarioService;
        Button METER_PROMOTORES = new Button("Meter Promotores");
        METER_PROMOTORES.addClickListener(e -> GuardarPromotores());
        getContent().add(METER_PROMOTORES);

    }

    @Transactional
    public void GuardarPromotores() {
        String API_URL = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
    
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RestTemplateConfig.class)) {
            RestTemplate restTemplate = context.getBean(RestTemplate.class);
    
            try {
                // Realizar solicitud GET
                ResponseEntity<servicio> response = restTemplate.getForEntity(API_URL, servicio.class);
    
                // Verificar si la respuesta es exitosa
                if (response.getStatusCode().is2xxSuccessful()) {
                  //  proyectoService.elimarpromotores(usuarioService.get(Roles.PROMOTOR));
                   // usuarioService.delete(Roles.PROMOTOR);
                    try {
                        usuarioService.destituyePromotores();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    servicio apiResponse = response.getBody();
                    Notification.show("Respuesta de la API: " + response.getBody());
                    // Trabajar con los datos
                    if (apiResponse != null && apiResponse.getPromotor() != null) {
                        for (promotores promotor : apiResponse.getPromotor()) {
                            logger.info("Promotor nombre: " + promotor.getNombre());
                            logger.info("Promotor apellido: " + promotor.getApellido());
                            try {
                                usuarioService.createPromotor(promotor);
                                Notification.show("Promotor guardado exitosamente");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Notification.show("Error al guardar el promotor");
                            }
                        }
                    }
                } else {
                    System.out.println("Error en la solicitud: " + response.getStatusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
