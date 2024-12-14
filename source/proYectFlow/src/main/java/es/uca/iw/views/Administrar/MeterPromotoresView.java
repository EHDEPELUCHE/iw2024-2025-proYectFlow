package es.uca.iw.views.Administrar;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.data.rest.promotores;
import es.uca.iw.data.rest.servicio;
import es.uca.iw.services.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    public void GuardarPromotores() {
        servicio serv = servicio.fetchDataFromApi();
        if (serv != null) {
            logger.info("Servicio status: " + serv.getStatus());
            Notification.show("Status: " + serv.getStatus());
            if (serv.getPromotor() != null) {
                List<promotores> promotores = serv.getPromotor();
                for (promotores promotor : promotores) {
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
            } else {
                logger.warning("Promotor es nulo");
                Notification.show("Promotor es nulo");
            }
        } else {
            logger.warning("Servicio es nulo");
            Notification.show("Servicio es nulo");
        }
    }
}
