package es.uca.iw.Convocatoria;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;

@Route("GestionarConvocatorias")
@PageTitle("Gestionar Convocatorias")
@Menu(order = 8, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class GestionarConvocatorias extends Div {
    private final ConvocatoriaService convocatoriaService;

    public GestionarConvocatorias(ConvocatoriaService convocatoriaService) {
        this.convocatoriaService = convocatoriaService;

        setSizeFull();
        addClassNames("convocatorias-view");

        H1 h1Titulo = new H1("Historial de convocatorias");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);

        Grid<Convocatoria> grid = crearGridDatosConvocatoria();
        VerticalLayout layout = new VerticalLayout(h1Titulo, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private Grid<Convocatoria> crearGridDatosConvocatoria() {
        Grid<Convocatoria> grid = new Grid<>(Convocatoria.class, false);

        grid.addColumn("nombre").setHeader("Nombre").setAutoWidth(true);
        grid.addColumn("fecha_inicio").setHeader("Fecha inicio").setAutoWidth(true);
        grid.addColumn("fecha_limite").setHeader("Fecha límite").setAutoWidth(true);
        grid.addColumn("fecha_final").setHeader("Fecha final").setAutoWidth(true);
        grid.addColumn("presupuestorestante").setHeader("Presupuesto restante").setAutoWidth(true);
        grid.addColumn("presupuestototal").setHeader("Presupuesto total").setAutoWidth(true);

        grid.addColumn("activa").setHeader("Estado").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setItems(query -> convocatoriaService.list(
                PageRequest.of(query.getPage(), query.getPageSize())).stream());

        return grid;
    }
}