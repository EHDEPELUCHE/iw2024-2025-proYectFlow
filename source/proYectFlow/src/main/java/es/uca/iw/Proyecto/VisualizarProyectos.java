package es.uca.iw.Proyecto;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class VisualizarProyectos extends Div {
    protected Grid<Proyecto> grid;
    protected ProyectoService proyectoService;
    private boolean mostrarColumnaAcciones;

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

        grid.addColumn("nombre").setHeader("Nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setHeader("Descripción").setAutoWidth(true);
        grid.addColumn("interesados").setHeader("Interesados").setAutoWidth(true);
        grid.addColumn("alcance").setHeader("Alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> {
            if (proyecto.getPromotor() != null) {
                return proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido();
            } else {
                return "Sin promotor";
            }
        }).setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setHeader("Coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setHeader("Aportación Inicial").setAutoWidth(true);
        grid.addColumn("fechaSolicitud").setHeader("Fecha de Solicitud").setAutoWidth(true);
        grid.addColumn("fechaLimite").setHeader("Fecha Límite").setAutoWidth(true);
        grid.addColumn("estado").setHeader("Estado").setAutoWidth(true);

        grid.addComponentColumn(this::crearBotonDescargaMemoria).setHeader("PDF").setAutoWidth(true);

        if (mostrarColumnaAcciones) {
            grid.addComponentColumn(this::crearBotonesAcciones).setHeader("Acciones").setAutoWidth(true);
        }
        grid.setItems(query -> proyectoService.list(
                PageRequest.of(query.getPage(), query.getPageSize()),
                filters).stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        return grid;
    }

    private Button crearBotonDescargaMemoria(Proyecto proyecto) {
        Button downloadButton = new Button("Memoria");
        downloadButton.addClickListener(e -> {
            byte[] pdfContent = null;
            try {
                pdfContent = proyectoService.getPdf(proyecto.getId());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (pdfContent != null) {
                byte[] finalPdfContent = pdfContent;
                StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(finalPdfContent));
                Anchor downloadLink = new Anchor(resource, "Download");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getElement().setAttribute("style", "display: none;");
                add(downloadLink);
                downloadLink.getElement().callJsFunction("click");
                downloadLink.remove();
            }
        });
        return downloadButton;
    }

    //CAMBIAR A EDITAR PROYECTO
    protected Component crearBotonesAcciones(Proyecto proyecto) {
        Button cambiarButton = new Button("Editar");
        cambiarButton.addClickListener(e -> {
            //getUI().ifPresent(ui -> ui.navigate("menuUsuarioView/"));
            getUI().ifPresent(ui -> ui.navigate("EditarProyectoSolicitante/" + proyecto.getId().toString()));

        });
        return cambiarButton;
    }
}
