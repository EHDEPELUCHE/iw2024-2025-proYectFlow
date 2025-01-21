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

/**
 * Vista para la activación de usuarios.
 * 
 * Esta vista permite a los usuarios activar su cuenta proporcionando su email y un código de activación.
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Activación de usuario".
 * - @Route: Define la ruta de acceso a esta vista como "ActivarUsuario".
 * - @AnonymousAllowed: Permite el acceso a esta vista sin necesidad de autenticación.
 * 
 * Componentes:
 * - UsuarioService usuarioService: Servicio para la gestión de usuarios.
 * - HorizontalLayout horizontalLayout: Layout horizontal para organizar los campos de email y código.
 * - H1 titulo: Título de la vista.
 * - EmailField email: Campo para ingresar el email del usuario.
 * - TextField codigo: Campo para ingresar el código de activación enviado al usuario.
 * - Button guardar: Botón para enviar los datos y activar el usuario.
 * 
 * Funcionalidad:
 * - Al hacer clic en el botón "Guardar" o presionar la tecla ENTER, se llama al método activateUser del servicio usuarioService.
 * - Si la activación es exitosa, se muestra una notificación de éxito.
 * - Si la activación falla, se muestra una notificación de error.
 * 
 * @param usuarioService Servicio para la gestión de usuarios.
 */
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
