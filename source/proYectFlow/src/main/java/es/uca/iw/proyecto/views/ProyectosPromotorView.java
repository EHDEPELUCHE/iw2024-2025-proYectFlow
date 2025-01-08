package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;


@PageTitle("Avala un proyecto")
@Route("proyectosPromotor")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_PROMOTOR")
public class ProyectosPromotorView extends VisualizarProyectos {
    public ProyectosPromotorView(ProyectoService proyectoService, AuthenticatedUser user) {
        super(proyectoService, true);
        Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("promotor"), user.get().orElse(null)),
                criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.SOLICITADO)
        );
        inicializarVistaProyectos("Proyectos pendientes de ser avalados", filters);
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Avalar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ValoracionPromotor/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}