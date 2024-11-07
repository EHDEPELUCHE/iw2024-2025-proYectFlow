package es.uca.iw.views.helloworld;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.proYectFlow.data.Proyecto;

@PageTitle("ProYectFlow")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/globe-solid.svg")
public class HelloWorldView extends VerticalLayout {

    private H2 titulo;
    private Button CrearProyecto;

    public HelloWorldView() {
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
        H1 texto2 = new H1("Proyectos en desarrollo:");
        Grid<Proyecto> tabla = new Grid<>(Proyecto.class);
        setHorizontalComponentAlignment(Alignment.CENTER, imagenfondo, titulo, CrearProyecto, texto2, tabla);

        add(imagenfondo, titulo, CrearProyecto, texto2, tabla);
    }

}
