package es.uca.iw.global.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * La clase AyudaView representa una página de ayuda y preguntas frecuentes (FAQ).
 * Extiende la clase Composite con un VerticalLayout como su contenido.
 * 
 * <p>Esta vista es accesible a través de la ruta "Ayuda" y se permite el acceso anónimo.
 * Contiene un título y un acordeón con secciones para ayudar a los usuarios a resolver problemas comunes.</p>
 * 
 * <p>Atributos:</p>
 * <ul>
 *   <li>{@code ARIA_LABEL}: Una cadena constante utilizada para establecer etiquetas ARIA para accesibilidad.</li>
 * </ul>
 * 
 * <p>Constructor:</p>
 * <ul>
 *   <li>{@code AyudaView()}: Inicializa la vista con un título y un acordeón que contiene secciones de ayuda.</li>
 * </ul>
 * 
 * <p>Secciones del Acordeón:</p>
 * <ul>
 *   <li>"Problemas al iniciar sesión": Proporciona pasos para resolver problemas de inicio de sesión, incluyendo verificar el registro de correo, activar la cuenta de usuario y contactar con soporte si es necesario.</li>
 * </ul>
 */
@PageTitle("Ayuda")
@Route("Ayuda")
@AnonymousAllowed
public class AyudaView extends Composite<VerticalLayout> {
    static final String ARIA_LABEL = "aria-label";

    public AyudaView() {
        H1 title = new H1("Ayuda y preguntas frecuentes");
        title.getElement().setAttribute(ARIA_LABEL, "Título de la página de ayuda");

        Accordion acordeon = new Accordion();
        acordeon.getElement().setAttribute(ARIA_LABEL, "Secciones de ayuda");

        Span pasoIniciosesion1 = new Span("Mire si al registrarse escribió bien su correo y recibió el código");
        Span pasoIniciosesion2 = new Span("Acceda a /ActivarUsuario y escriba su correo y el código que se le envió");
        Span pasoIniciosesion3 = new Span("Si ni con la cuenta activa y usando su USUARIO y CONTRASEÑA puede iniciar sesión, pruebe a crear otra cuenta con otro nombre de usuario y mismo correo. Si no lo consigue póngase en contacto con: iwproyectflow@gmail.com");

        VerticalLayout problema1Layout = new VerticalLayout(pasoIniciosesion1, pasoIniciosesion2, pasoIniciosesion3);
        problema1Layout.getElement().setAttribute(ARIA_LABEL, "Pasos para resolver problemas al iniciar sesión");

        acordeon.add("Problemas al iniciar sesión", problema1Layout);

        getContent().add(title, acordeon);
    }
}
