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
        getContent().add(h1, objetivo, btnAceptar, tabla, grid);
    }
}
