package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;


@PageTitle("Proyectos OTP")
@Route("proyectosOTP")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_OTP")
public class ProyectosOTPView extends VisualizarProyectos {

    public ProyectosOTPView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        super(proyectoService, true);

        if (!convocatoriaService.convocatoriaActual().enPlazo()) {
            Specification<Proyecto> filters = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.AVALADO);

            inicializarVistaProyectos("Proyectos por evaluar", filters);
        } else {
            add(new H1("Aún no se puede realizar la evaluación"));
        }
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Evaluar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ValoracionTecnica/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}
