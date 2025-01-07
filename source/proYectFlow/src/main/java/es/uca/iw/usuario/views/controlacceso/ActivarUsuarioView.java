package es.uca.iw.usuario.views.controlacceso;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.usuario.UsuarioService;

@PageTitle("Activación de usuario")
@Route("ActivarUsuario")
@AnonymousAllowed
public class ActivarUsuarioView extends Composite<VerticalLayout> {
    final UsuarioService usuarioService;

    public ActivarUsuarioView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        H1 titulo = new H1("Activar Usuario");
        EmailField email = new EmailField("Tu email");
        TextField codigo = new TextField("Codigo enviado");
        Button guardar = new Button("Guardar");
        guardar.addClickShortcut(Key.ENTER);
        guardar.addClickListener(e -> {
            if (usuarioService.activateUser(email.getValue(), codigo.getValue())) {
                Notification.show("Usuario activado con exito");
            } else {
                Notification.show("Error al activar usuario");
            }
        });
        getContent().add(titulo);
        horizontalLayout.add(email, codigo);
        getContent().add(horizontalLayout, guardar);
    }
}
