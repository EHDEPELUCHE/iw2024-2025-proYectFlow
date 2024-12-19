package es.uca.iw.views.pantallainicio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.flow.router.RouterLink;
import es.uca.iw.services.ProyectoService;
import es.uca.iw.views.Misdatos.MisDatosView;
import es.uca.iw.views.proyectos.ProyectosEnDesarrolloView;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import es.uca.iw.data.Proyecto;

@AnonymousAllowed
@PageTitle("ProYectFlow")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/globe-solid.svg")
public class PantallaInicioView extends VerticalLayout {

    private H2 titulo;
    private Button CrearProyecto;


    public PantallaInicioView() {

        Image imagenfondo = new Image("img/fondo_uca.jpg", "fondouca");

        titulo = new H2("Bienvenido a ProYectFlow. " +
                "Inicia sesión y registra tu proyecto");
        CrearProyecto = new Button("Propón un proyecto");
        CrearProyecto.addClickListener(e -> {
            CrearProyecto.getUI().ifPresent(ui -> ui.navigate("registro-proyecto"));
        });
        CrearProyecto.addClickShortcut(Key.ENTER);
        CrearProyecto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //setMargin(true);
        H1 texto2 = new H1("Para consultar los proyectos que se están desarrollando actualmente pulse el siguiente botón:");
        Button VerProyectos = new Button("Ver Proyectos en desarrollo");
        VerProyectos.addClickListener(e -> {
            new RouterLink("Ver proyectos en desarrollo", ProyectosEnDesarrolloView.class);
            VerProyectos.getUI().ifPresent(ui -> ui.navigate("ProyectosDesarrollo"));
        });
        VerProyectos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        setHorizontalComponentAlignment(Alignment.CENTER, imagenfondo, titulo, CrearProyecto, texto2, VerProyectos);

        add(imagenfondo, titulo, CrearProyecto, texto2, VerProyectos);
    }

}
