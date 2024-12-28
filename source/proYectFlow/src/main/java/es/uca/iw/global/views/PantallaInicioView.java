package es.uca.iw.global.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
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

        H2 texto2 = new H2("Para consultar los proyectos que se están desarrollando actualmente pulse el siguiente botón:");

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
