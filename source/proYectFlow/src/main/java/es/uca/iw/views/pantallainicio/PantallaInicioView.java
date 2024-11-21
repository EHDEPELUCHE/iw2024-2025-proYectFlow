package es.uca.iw.views.pantallainicio;

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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.data.Proyecto;
import es.uca.iw.views.proyectos.ProyectosView;

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
        H1 texto2 = new H1("Proyectos en desarrollo:");
        Grid<Proyecto> tabla = new Grid<>(Proyecto.class);
        setHorizontalComponentAlignment(Alignment.CENTER, imagenfondo, titulo, CrearProyecto, texto2, tabla);


        RouterLink acceder=new RouterLink("acceder", ProyectosView.class);
        add(imagenfondo, titulo, CrearProyecto, texto2, tabla,acceder);
    }

}
