package es.uca.iw.Proyecto;

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

public abstract class VisualizarProyectos extends Div {
    protected Grid<Proyecto> grid;
    protected final ProyectoService proyectoService;
    private final boolean mostrarColumnaAcciones;

    public VisualizarProyectos(ProyectoService proyectoService, boolean mostrarColumnaAcciones) {
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
        cambiarButton.addClickListener(e -> {
            //getUI().ifPresent(ui -> ui.navigate("menuUsuarioView/"));
            getUI().ifPresent(ui -> ui.navigate("EditarProyectoSolicitante/" + proyecto.getId().toString()));

        });
        return cambiarButton;
    }
}
