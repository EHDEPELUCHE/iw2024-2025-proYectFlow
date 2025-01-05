package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Proyecto.VisualizarProyectos;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;


@PageTitle("Evaluar Proyectos CIO")
@Route("proyectosCIO")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_CIO")

public class ProyectosCIOView extends VisualizarProyectos {

    public ProyectosCIOView(ProyectoService proyectoService, AuthenticatedUser user, ConvocatoriaService convocatoriaService) {
        super(proyectoService, true);

        if (!convocatoriaService.ConvocatoriaActual().EnPlazo()) {
            Specification<Proyecto> filtroEvaluados = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.evaluadoTecnicamente);
            inicializarVistaProyectos("Proyectos por evaluar", filtroEvaluados);

        } else {
            add(new H1("Aún no se puede realizar la evaluación"));
        }
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Evaluar");
        evaluarButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("ValoracionEstrategica/" + proyecto.getId().toString()));
        });
        return evaluarButton;
    }
}
