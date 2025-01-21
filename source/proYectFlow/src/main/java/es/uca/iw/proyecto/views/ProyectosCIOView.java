package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.VisualizarProyectos;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.jpa.domain.Specification;


/**
 * La clase ProyectosCIOView representa una vista para evaluar proyectos por el CIO.
 * Extiende la clase VisualizarProyectos y está anotada con varias anotaciones de Vaadin y de seguridad.
 * 
 * <p>Anotaciones:</p>
 * <ul>
 *   <li>@PageTitle("Evaluar Proyectos CIO") - Establece el título de la página como "Evaluar Proyectos CIO".</li>
 *   <li>@Route("proyectosCIO") - Define la ruta para acceder a esta vista como "proyectosCIO".</li>
 *   <li>@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg") - Añade esta vista al menú con un orden y un icono especificados.</li>
 *   <li>@Uses(Icon.class) - Indica que esta vista utiliza la clase Icon.</li>
 *   <li>@RolesAllowed("ROLE_CIO") - Restringe el acceso a usuarios con el rol "ROLE_CIO".</li>
 * </ul>
 * 
 * <p>Constructor:</p>
 * <ul>
 *   <li>ProyectosCIOView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) - Inicializa la vista con los servicios dados.</li>
 * </ul>
 * 
 * <p>Métodos:</p>
 * <ul>
 *   <li>crearBotonesAcciones(Proyecto proyecto) - Crea botones de acción para el proyecto dado, incluyendo un botón "Evaluar" que navega a la vista "ValoracionEstrategica" para el proyecto.</li>
 * </ul>
 * 
 * <p>Comportamiento:</p>
 * <ul>
 *   <li>Si la convocatoria actual no está dentro del periodo de evaluación, inicializa la vista con proyectos por evaluar.</li>
 *   <li>Si la convocatoria actual está dentro del periodo de evaluación, muestra un mensaje indicando que aún no se puede realizar la evaluación.</li>
 * </ul>
 */
@PageTitle("Evaluar Proyectos CIO")
@Route("proyectosCIO")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_CIO")

public class ProyectosCIOView extends VisualizarProyectos {

    public ProyectosCIOView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        super(proyectoService, true);
        Convocatoria convocatoriaActual = convocatoriaService.convocatoriaActual();

        if (!convocatoriaService.convocatoriaActual().enPlazo()) {
            Specification<Proyecto> filtroEvaluados = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.EVALUADO_TECNICAMENTE),
                    criteriaBuilder.equal(root.get("convocatoria"), convocatoriaActual));
            inicializarVistaProyectos("Proyectos por evaluar", filtroEvaluados);

        } else {
            add(new H1("Aún no se puede realizar la evaluación"));
        }
    }

    @Override
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button evaluarButton = new Button("Evaluar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ValoracionEstrategica/" + proyecto.getId().toString())));
        return evaluarButton;
    }
}
