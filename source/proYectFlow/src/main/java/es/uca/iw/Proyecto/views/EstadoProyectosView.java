package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.global.GLOBALES;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@PageTitle("Estado de mis Proyectos")
@Route("estadomisproyectos")
@Menu(order = 2, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@PermitAll
public class EstadoProyectosView extends Div {
    private final ProyectoService proyectoService;
    private Grid<Proyecto> grid;

    public EstadoProyectosView(ProyectoService proyectoService, AuthenticatedUser userAuthenticated) {
        this.proyectoService = proyectoService;
        Optional<Usuario> usuarioOptional = userAuthenticated.get();
        setSizeFull();
        addClassNames("proyectos-view");

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get(); // Usuario autenticado
            H1 h1Titulo = new H1("Mis proyectos");
            h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                    LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);
            // Crear filtro para proyectos
            Specification<Proyecto> filters = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("solicitante"), usuario);
            VerticalLayout layout = new VerticalLayout(h1Titulo, createGrid(filters));
            layout.setSizeFull();
            layout.setPadding(false);
            layout.setSpacing(false);
            add(layout);
        } else {
            add(new H1("No tienes proyectos disponibles."));
        }
    }

    private Component createGrid(Specification<Proyecto> filters) {
        grid = new Grid<>(Proyecto.class, false);

        // Configurar columnas del grid
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

        // Columna para descargar PDF
        grid.addComponentColumn(proyecto -> {
            Button downloadButton = new Button("Memoria");
            downloadButton.addClickListener(e -> {
                try {
                    byte[] pdfContent = proyectoService.getPdf(proyecto.getId());
                    if (pdfContent != null) {
                        StreamResource resource = new StreamResource("Memoria.pdf",
                                () -> new ByteArrayInputStream(pdfContent));
                        Anchor downloadLink = new Anchor(resource, "Descargar");
                        downloadLink.getElement().setAttribute("download", true);
                        downloadLink.getElement().setAttribute("style", "display: none;");
                        add(downloadLink);
                        downloadLink.getElement().callJsFunction("click");
                        downloadLink.remove();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Agregar log adecuado en producción
                }
            });
            return downloadButton;
        }).setHeader("PDF").setAutoWidth(true);
        grid.addComponentColumn(proyecto -> {
            if (proyecto.getPromotor() == null) {
                Button assignPromotorButton = new Button("Asignar Promotor");
                Date hoy = new Date();
                if (GLOBALES.FECHA_LIMITE.compareTo(hoy) < 0) {
                    assignPromotorButton.addClickListener(e -> {
                        // Lógica para asignar promotor CAMBIAR
                    });
                    return assignPromotorButton;
                } else {
                    return new Div(new H5("Ya ha pasado el plazo, lo sentimos"));
                }
            } else {
                return new Div();
            }
        }).setHeader("Acciones").setAutoWidth(true);

        // Configurar los datos del grid
        grid.setItems(query -> proyectoService.list(
                PageRequest.of(query.getPage(), query.getPageSize()),
                filters).stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        return grid;
    }
}
