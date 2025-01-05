package es.uca.iw.global.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.Proyecto.views.ProyectosEnDesarrolloView;

@AnonymousAllowed
@PageTitle("ProYectFlow")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/globe-solid.svg")
public class PantallaInicioView extends VerticalLayout {

    private final Button CrearProyecto;

    public PantallaInicioView() {
        Image imagenfondo = new Image("img/fondo_uca.jpg", "fondouca");
        imagenfondo.getElement().setAttribute("alt", "Imagen de fondo de la UCA");

        H2 titulo = new H2("Bienvenido a ProYectFlow. " +
                "Inicia sesión y registra tu proyecto");
        titulo.getElement().setAttribute("role", "heading");
        titulo.getElement().setAttribute("aria-level", "2");

        CrearProyecto = new Button("Propón un proyecto");
        CrearProyecto.addClickListener(e -> {
            CrearProyecto.getUI().ifPresent(ui -> ui.navigate("registro-proyecto"));
        });
        CrearProyecto.addClickShortcut(Key.ENTER);
        CrearProyecto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        CrearProyecto.getElement().setAttribute("aria-label", "Propón un proyecto");

        H2 texto2 = new H2("Para consultar los proyectos que se están desarrollando actualmente pulse el siguiente botón:");
        texto2.getElement().setAttribute("role", "heading");
        texto2.getElement().setAttribute("aria-level", "2");

        Button VerProyectos = new Button("Ver Proyectos en desarrollo");
        VerProyectos.addClickListener(e -> {
            new RouterLink("Ver proyectos en desarrollo", ProyectosEnDesarrolloView.class);
            VerProyectos.getUI().ifPresent(ui -> ui.navigate("ProyectosDesarrollo"));
        });
        VerProyectos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerProyectos.getElement().setAttribute("aria-label", "Ver Proyectos en desarrollo");

        setHorizontalComponentAlignment(Alignment.CENTER, imagenfondo, titulo, CrearProyecto, texto2, VerProyectos);

        add(imagenfondo, titulo, CrearProyecto, texto2, VerProyectos);
    }
}
