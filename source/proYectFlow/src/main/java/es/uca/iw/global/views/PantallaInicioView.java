package es.uca.iw.global.views;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.usuario.views.MenuUsuarioViewCard;

/**
 * La clase PantallaInicioView representa la vista principal de la aplicación ProYectFlow.
 * Extiende VerticalLayout y está anotada con @AnonymousAllowed, @PageTitle, @Route y @Menu.
 * 
 * Esta vista contiene los siguientes componentes:
 * - Una imagen (imagenfondo) con el fondo de la UCA.
 * - Un encabezado (titulo) que da la bienvenida a los usuarios a ProYectFlow y les invita a iniciar sesión y registrar su proyecto.
 * - Un botón (crearProyecto) que navega a la página de registro de proyectos cuando se hace clic.
 * - Un segundo encabezado (texto2) que invita a los usuarios a ver los proyectos en desarrollo.
 * - Un botón (verProyectos) que navega a la página de proyectos en desarrollo cuando se hace clic.
 * 
 * Los componentes están alineados horizontalmente al centro y se añaden al diseño.
 */
@AnonymousAllowed
@PageTitle("ProYectFlow")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/globe-solid.svg")

public class PantallaInicioView extends Main {
    private OrderedList imageContainer;

    public PantallaInicioView() {
        constructUI();
    }

    static final String FOTOPROYECTOS = "https://images.unsplash.com/photo-1521791055366-0d553872125f?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    static final String FOTO = "https://images.unsplash.com/photo-1507925921958-8a62f3d1a50d?q=80&w=1776&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    static final String FOTOINICIOSESION = "https://images.unsplash.com/photo-1641598547773-6c5e1b9ed0c0?q=80&w=2074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    static final String FOTOREGISTRO = "https://images.unsplash.com/photo-1668338857295-7b10bcea47b1?q=80&w=1935&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    static final String FOTOREGISTRO2 = "https://images.unsplash.com/photo-1585587937454-d8fc4d2aa37c?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    private void constructUI() {
        addClassNames("menu-usuario-view-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 titulo = new H2("Bienvenido a ProYectFlow, inicia sesión y registra tu proyecto");
        titulo.getElement().setAttribute("role", "heading");
        titulo.getElement().setAttribute("aria-level", "2");
        headerContainer.add(titulo);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        imageContainer.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))");
        imageContainer.getStyle().set("gap", "1rem");

        container.add(headerContainer);
        add(container, imageContainer);

        // Filtrar las tarjetas según el rol

        addCardIfRoleMatches("Solicita un proyecto",
                FOTOPROYECTOS, "Solicitar", "registro-proyecto");

        addCardIfRoleMatches("Empieza ahora, regístrate",
                FOTOREGISTRO2, "Regístrate", "registro-usuario");

        addCardIfRoleMatches("Inicia sesión",
                FOTOINICIOSESION, "Iniciar", "login");

        addCardIfRoleMatches("Consulta los proyectos en desarrollo",
                FOTO, "Consultar", "ProyectosDesarrollo");
    }

    // Agrega la tarjeta si tiene el rol de usuario necesario
    private void addCardIfRoleMatches(String title, String imageUrl, String buttonText, String navigationTarget) {

            imageContainer.add(new MenuUsuarioViewCard(title, null, imageUrl, null, buttonText,
                    navigationTarget, null));

    }
}