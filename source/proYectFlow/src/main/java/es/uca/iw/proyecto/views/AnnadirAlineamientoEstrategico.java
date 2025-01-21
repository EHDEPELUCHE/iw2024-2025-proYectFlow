package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.proyecto.AlineamientoEstrategico;
import es.uca.iw.proyecto.AlineamientoEstrategicoService;
import jakarta.annotation.security.RolesAllowed;
import java.util.Objects;

/**
 * La clase AnnadirAlineamientoEstrategico representa una vista para añadir alineamientos estratégicos.
 * Esta vista es accesible solo para usuarios con el rol "ROLE_CIO".
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Añadir Alineamiento Estrategico".
 * - @Route: Define la ruta para acceder a esta vista como "añadiralineamiento".
 * - @Menu: Añade esta vista al menú con orden 2 y un icono.
 * - @Uses: Indica que esta vista utiliza la clase Icon.
 * - @RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_CIO".
 * 
 * Componentes:
 * - TextArea objetivo: Un área de texto para introducir un nuevo objetivo de alineamiento estratégico.
 * - Button btnAceptar: Un botón para aceptar y guardar el nuevo objetivo.
 * - H1 h1: Un encabezado para la vista.
 * - H2 tabla: Un encabezado para la tabla de alineamientos estratégicos existentes.
 * - Grid<AlineamientoEstrategico> grid: Una tabla que muestra los alineamientos estratégicos existentes con acciones para activarlos o desactivarlos.
 * 
 * Constructor:
 * - AnnadirAlineamientoEstrategico(AlineamientoEstrategicoService alineamientoEstrategicoService): Inicializa la vista con el servicio proporcionado.
 * 
 * Funcionalidad:
 * - Añade un nuevo objetivo de alineamiento estratégico cuando se hace clic en el botón "Aceptar", si el objetivo no está vacío.
 * - Muestra una notificación indicando si el objetivo se añadió correctamente o si hubo un error.
 * - Muestra los alineamientos estratégicos existentes en una tabla con botones para activar o desactivar cada alineamiento.
 * - Actualiza la tabla y muestra notificaciones cuando un alineamiento es activado o desactivado.
 */
@PageTitle("Añadir Alineamiento Estrategico")
@Route("añadiralineamiento")
@Menu(order = 2, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_CIO")
public class AnnadirAlineamientoEstrategico extends Composite<VerticalLayout> {
    final AlineamientoEstrategicoService alineamientoEstrategicoService;

    public AnnadirAlineamientoEstrategico(AlineamientoEstrategicoService alineamientoEstrategicoService) {
        this.alineamientoEstrategicoService = alineamientoEstrategicoService;

        TextArea objetivo = new TextArea("Objetivo nuevo");
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAceptar.addClickShortcut(Key.ENTER);
        H1 h1 = new H1("Añadir Alineamiento Estratégico");
        btnAceptar.addClickListener(e -> {
            AlineamientoEstrategico alineamientoEstrategico = new AlineamientoEstrategico();
            if (!Objects.equals(objetivo.getValue(), objetivo.getEmptyValue())) {
                alineamientoEstrategico.setObjetivo(objetivo.getValue());
                alineamientoEstrategico.setActivo(true);
                try {
                    alineamientoEstrategicoService.guardar(alineamientoEstrategico);
                    Notification.show("Objetivo añadido correctamente");
                    objetivo.clear();
                } catch (Exception ex) {
                    Notification.show("Los datos introducidos son erróneos: " + ex.getMessage());
                }
            } else {
                Notification.show("El objetivo no puede estar vacio");
            }
        });
        H2 tabla = new H2("Alineamientos Estratégicos");
        Grid<AlineamientoEstrategico> grid = new Grid<>(AlineamientoEstrategico.class, false);
        grid.addColumn(AlineamientoEstrategico::getObjetivo);
        grid.setItems(alineamientoEstrategicoService.findAll());
        grid.addComponentColumn(alineamientoEstrategico -> {
            Button btnActivar = new Button("Activar", new Icon("lumo", "checkmark"));
            btnActivar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            btnActivar.addClickListener(e -> {
                alineamientoEstrategico.setActivo(true);
                alineamientoEstrategicoService.guardar(alineamientoEstrategico);
                grid.getDataProvider().refreshItem(alineamientoEstrategico);
                Notification.show("Alineamiento activado");
            });

            Button btnDesactivar = new Button("Desactivar", new Icon("lumo", "cross"));
            btnDesactivar.addThemeVariants(ButtonVariant.LUMO_ERROR);
            btnDesactivar.addClickListener(e -> {
                alineamientoEstrategico.setActivo(false);
                alineamientoEstrategicoService.guardar(alineamientoEstrategico);
                grid.getDataProvider().refreshItem(alineamientoEstrategico);
                Notification.show("Alineamiento desactivado");
            });

            VerticalLayout layout = new VerticalLayout();
            layout.add(Boolean.TRUE.equals(alineamientoEstrategico.getActivo()) ? btnDesactivar : btnActivar);
            return layout;
        }).setHeader("Acciones");
        getContent().add(h1, objetivo, btnAceptar, tabla, grid);
    }
}