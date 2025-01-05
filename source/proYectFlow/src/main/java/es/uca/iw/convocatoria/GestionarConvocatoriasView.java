package es.uca.iw.convocatoria;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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
public class GestionarConvocatoriasView extends Div {
    private final ConvocatoriaService convocatoriaService;

    public GestionarConvocatoriasView(ConvocatoriaService convocatoriaService) {
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

        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaInicio()))
                .setHeader("Fecha inicio").setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaLimite()))
                .setHeader("Fecha límite").setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaFinal()))
                .setHeader("Fecha final").setAutoWidth(true)
                .setSortable(true);

        grid.addColumn("presupuestorestante").setHeader("Presupuesto restante").setAutoWidth(true);
        grid.addColumn("presupuestototal").setHeader("Presupuesto total").setAutoWidth(true);

        grid.addComponentColumn(this::estadoConvocatoria)
                .setHeader("Estado")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addComponentColumn(this::editarConvocatoria).setHeader("Acciones").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.setItems(query -> convocatoriaService.list(
                PageRequest.of(query.getPage(), query.getPageSize())).stream());

        return grid;
    }

    protected Component editarConvocatoria(Convocatoria convocatoria) {
        Button editarButton = new Button("Editar");
        editarButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("EditarConvocatoria/" + convocatoria.getId()));

        });
        return editarButton;
    }

    protected Component estadoConvocatoria(Convocatoria convocatoria) {
        if (convocatoria.getActiva() == false) {
            Button activarButton = new Button("Activar");
            activarButton.addClickListener(e -> {
                try {
                    convocatoriaService.hacerVigente(convocatoria);
                    Notification.show("Convocatoria activada");
                    UI.getCurrent().getPage().reload();
                } catch (IllegalArgumentException ex) {
                    Notification errorNotification = new Notification(ex.getMessage(), 3000, Notification.Position.MIDDLE);
                    errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    errorNotification.open();
                }
            });
            return activarButton;
        } else {
            return new Div(new Text("Activada"));
        }
    }
}