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
import es.uca.iw.proyecto.views.ProyectosEnDesarrolloView;

@AnonymousAllowed
@PageTitle("ProYectFlow")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/globe-solid.svg")
public class PantallaInicioView extends VerticalLayout {

    private final Button crearProyecto;

    public PantallaInicioView() {
        Image imagenfondo = new Image("img/fondo_uca.jpg", "fondouca");
        imagenfondo.getElement().setAttribute("alt", "Imagen de fondo de la UCA");

        H2 titulo = new H2("Bienvenido a ProYectFlow. " +
                "Inicia sesión y registra tu proyecto");
        titulo.getElement().setAttribute("role", "heading");
        titulo.getElement().setAttribute("aria-level", "2");

        crearProyecto = new Button("Propón un proyecto");
        crearProyecto.addClickListener(e -> crearProyecto.getUI().ifPresent(ui -> ui.navigate("registro-proyecto")));
        crearProyecto.addClickShortcut(Key.ENTER);
        crearProyecto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearProyecto.getElement().setAttribute("aria-label", "Propón un proyecto");

        H2 texto2 = new H2("Para consultar los proyectos que se están desarrollando actualmente pulse el siguiente botón:");
        texto2.getElement().setAttribute("role", "heading");
        texto2.getElement().setAttribute("aria-level", "2");

        Button verProyectos = new Button("Ver Proyectos en desarrollo");
        verProyectos.addClickListener(e -> {
            new RouterLink("Ver proyectos en desarrollo", ProyectosEnDesarrolloView.class);
            verProyectos.getUI().ifPresent(ui -> ui.navigate("ProyectosDesarrollo"));
        });
        verProyectos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        verProyectos.getElement().setAttribute("aria-label", "Ver Proyectos en desarrollo");

        setHorizontalComponentAlignment(Alignment.CENTER, imagenfondo, titulo, crearProyecto, texto2, verProyectos);

        add(imagenfondo, titulo, crearProyecto, texto2, verProyectos);
    }
}
