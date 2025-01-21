package es.uca.iw.proyecto;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.global.DownloadPdfComponent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;

/**
 * La clase abstracta VisualizarProyectos extiende de Div y proporciona una vista para visualizar proyectos.
 * 
 * @param proyectoService El servicio de proyectos utilizado para obtener datos de los proyectos.
 * @param mostrarColumnaAcciones Indica si se debe mostrar la columna de acciones en la vista.
 * @param grid La cuadrícula utilizada para mostrar los datos de los proyectos.
 * 
 * Métodos:
 * 
 * - VisualizarProyectos(ProyectoService proyectoService, boolean mostrarColumnaAcciones): Constructor que inicializa el servicio de proyectos y la visibilidad de la columna de acciones.
 * 
 * - inicializarVistaProyectos(String titulo, Specification<Proyecto> filters): Método protegido que inicializa la vista de proyectos con un título y filtros específicos.
 * 
 * - crearGridDatosProyecto(Specification<Proyecto> filters): Método privado que crea y configura la cuadrícula de datos del proyecto.
 * 
 * - crearBotonDescargaMemoria(Proyecto proyecto): Método privado que crea un botón para descargar el PDF de la memoria del proyecto.
 * 
 * - crearBotonesAcciones(Proyecto proyecto): Método protegido que crea los botones de acciones para editar el proyecto.
 */
public abstract class VisualizarProyectos extends Div {
    protected final ProyectoService proyectoService;
    private final boolean mostrarColumnaAcciones;
    protected Grid<Proyecto> grid;

    protected VisualizarProyectos(ProyectoService proyectoService, boolean mostrarColumnaAcciones) {
        this.proyectoService = proyectoService;
        this.mostrarColumnaAcciones = mostrarColumnaAcciones;
        setSizeFull();
        addClassNames("proyectos-view");
    }

    protected void inicializarVistaProyectos(String titulo, Specification<Proyecto> filters) {
        H1 h1Titulo = new H1(titulo);
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);

        VerticalLayout layout = new VerticalLayout(h1Titulo, crearGridDatosProyecto(filters));
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Component crearGridDatosProyecto(Specification<Proyecto> filters) {
        grid = new Grid<>(Proyecto.class, false);

        grid.addColumn("nombre").setHeader("Nombre").setAutoWidth(true).setSortable(true);
        grid.addColumn("descripcion").setHeader("Descripción").setAutoWidth(true);
        grid.addColumn("interesados").setHeader("Interesados").setAutoWidth(true);
        grid.addColumn("alcance").setHeader("Alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> {
                    if (proyecto.getPromotor() != null) {
                        return proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido();
                    } else {
                        return "Sin promotor";
                    }
                }).setHeader("Promotor").setAutoWidth(true)
                .setSortable(true);

                grid.addColumn(proyecto -> {
                    if (proyecto.getJefe() != null) {
                        return proyecto.getJefe().getNombre() + " " + proyecto.getJefe().getApellido();
                    } else {
                        return "Sin jefe";
                    }
                }).setHeader("jefe").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn("coste").setHeader("Coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setHeader("Aportación Inicial").setAutoWidth(true);

        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaSolicitud()))
                .setHeader("Fecha de Solicitud").setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaLimite()))
                .setHeader("Fecha Límite").setAutoWidth(true)
                .setSortable(true);

        grid.addColumn("estado").setHeader("Estado").setAutoWidth(true);

        grid.addComponentColumn(this::crearBotonDescargaMemoria).setHeader("PDF").setAutoWidth(true);

        if (mostrarColumnaAcciones) {
            grid.addComponentColumn(this::crearBotonesAcciones).setHeader("Acciones").setAutoWidth(true);
        }

        grid.setItems(query -> proyectoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        return grid;
    }

    private Button crearBotonDescargaMemoria(Proyecto proyecto) {
        return DownloadPdfComponent.createDownloadButton("Memoria", () -> {
            try {
                return proyectoService.getPdf(proyecto.getId());
            } catch (IOException ex) {
                throw new RuntimeException("Error al obtener el PDF", ex);
            }
        });
    }

    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button cambiarButton = new Button("Editar");
        cambiarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("EditarProyectoSolicitante/" + proyecto.getId().toString())));
        return cambiarButton;
    }
}
