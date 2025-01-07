package es.uca.iw.global.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Ayuda")
@Route("Ayuda")
@AnonymousAllowed
public class AyudaView extends Composite<VerticalLayout> {
    static final String ariaLabel = "aria-label";

    public AyudaView() {
        H1 title = new H1("Ayuda y preguntas frecuentes");
        title.getElement().setAttribute(ariaLabel, "Título de la página de ayuda");

        Accordion acordeon = new Accordion();
        acordeon.getElement().setAttribute(ariaLabel, "Secciones de ayuda");

        Span pasoIniciosesion1 = new Span("Mire si al registrarse escribió bien su correo y recibió el código");
        Span pasoIniciosesion2 = new Span("Acceda a /ActivarUsuario y escriba su correo y el código que se le envió");
        Span pasoIniciosesion3 = new Span("Si ni con la cuenta activa y usando su USUARIO y CONTRASEÑA puede iniciar sesión, pruebe a crear otra cuenta con otro nombre de usuario y mismo correo. Si no lo consigue póngase en contacto con: iwproyectflow@gmail.com");

        VerticalLayout problema1Layout = new VerticalLayout(pasoIniciosesion1, pasoIniciosesion2, pasoIniciosesion3);
        problema1Layout.getElement().setAttribute(ariaLabel, "Pasos para resolver problemas al iniciar sesión");

        acordeon.add("Problemas al iniciar sesión", problema1Layout);

        getContent().add(title, acordeon);
    }
}
