package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Esta vista representa la página "Proyectos en Desarrollo".
 * Muestra una lista de proyectos que están actualmente en desarrollo.
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página.
 * - @Route: Define la ruta para acceder a esta vista.
 * - @Menu: Añade esta vista al menú de la aplicación con un orden e ícono especificados.
 * - @AnonymousAllowed: Permite el acceso a esta vista sin autenticación.
 * 
 * Dependencias:
 * - ProyectoService: Servicio para gestionar operaciones relacionadas con proyectos.
 * - ConvocatoriaService: Servicio para gestionar operaciones relacionadas con convocatorias.
 * 
 * Componentes de UI:
 * - OrderedList projectContainer: Contenedor para mantener la lista de tarjetas de proyectos.
 * 
 * Constructor:
 * - ProyectosEnDesarrolloView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService):
 *   Inicializa la vista y construye la interfaz de usuario.
 * 
 * Métodos:
 * - private void constructUI(ProyectoService proyectoService, ConvocatoriaService convocatoriaService):
 *   Construye los componentes de la interfaz de usuario y llena la lista de proyectos basada en la convocatoria actual.
 * 
 * - private ProyectoViewCard crearCardProyecto(Proyecto proyecto):
 *   Crea un componente de tarjeta para un proyecto dado con un objetivo de navegación.
 */
@PageTitle("Proyectos en Desarrollo")
@Route("ProyectosDesarrollo")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@AnonymousAllowed
public class ProyectosEnDesarrolloView extends Main {
    private OrderedList projectContainer;
    protected final MultiSelectComboBox<String> convocatoria = new MultiSelectComboBox<>("Convocatoria");
    private final ProyectoService proyectoService;

    public ProyectosEnDesarrolloView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        this.proyectoService = proyectoService;
        constructUI(proyectoService, convocatoriaService);
    }

    private void constructUI(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        addClassNames("proyectos-en-desarrollo-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        Convocatoria convocatoriaActual = convocatoriaService.convocatoriaActual();

        if (convocatoriaActual != null) {
            convocatoria.setItems(convocatoriaService.findAll().stream().map(Convocatoria::getNombre).toList());
            convocatoria.setValue(convocatoriaActual.getNombre());

            Button searchButton = new Button("Buscar", event -> updateProjectList());

            H1 header = new H1("Proyectos en Desarrollo");
            header.addClassNames(FontSize.XXXLARGE, Margin.Bottom.NONE, Margin.Top.XLARGE);

            HorizontalLayout searchLayout = new HorizontalLayout(convocatoria, searchButton);
            searchLayout.setDefaultVerticalComponentAlignment(Alignment.END);

            projectContainer = new OrderedList();
            projectContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
            projectContainer.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))");
            projectContainer.getStyle().set("gap", "1rem");

            add(header,searchLayout, projectContainer);

            updateProjectList();
        } else {
            H1 noProjectsHeader = new H1("No existe ningún proyecto en desarrollo actualmente.");
            noProjectsHeader.addClassNames(FontSize.XXXLARGE, Margin.Top.XLARGE, TextColor.SECONDARY);
            noProjectsHeader.getElement().setAttribute("role", "alert");
            add(noProjectsHeader);
        }
    }

    private void updateProjectList() {
        projectContainer.removeAll();

        Specification<Proyecto> filters = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                root.get("estado").in(Proyecto.Estado.EN_DESARROLLO, Proyecto.Estado.FINALIZADO),
                root.get("convocatoria").get("nombre").in(convocatoria.getValue())
        );

        List<Proyecto> proyectos = proyectoService.list(PageRequest.of(0, 100), filters).getContent();
        proyectos.forEach(proyecto -> projectContainer.add(crearCardProyecto(proyecto)));
    }

    private ProyectoViewCard crearCardProyecto(Proyecto proyecto) {
        String navigationTarget = "InfoProyecto/" + proyecto.getId().toString();
        return new ProyectoViewCard(proyecto, navigationTarget);
    }

    static final String ARIALABEL = "aria-label";
}