package es.uca.iw.views.Misdatos;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Cambio Contraseña")
@Route("Cambiocontraseña")
@PermitAll
public class CambioContrasenna extends Composite<VerticalLayout> {
    H1 h1 = new H1("CAMNIO");
    public CambioContrasenna() {
        getContent().add(h1);
    }
}
