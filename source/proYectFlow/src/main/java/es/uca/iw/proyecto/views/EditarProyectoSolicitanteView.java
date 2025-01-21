package es.uca.iw.proyecto.views;

import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Esta clase de vista representa la página "Editar mis proyectos", que permite a los usuarios con roles específicos editar sus proyectos.
 * Extiende EditarProyectosBaseView y proporciona una configuración adicional para los campos específicos del rol "Solicitante".
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Editar mis proyectos".
 * - @Route: Mapea la vista a la ruta URL "EditarProyectoSolicitante".
 * - @RolesAllowed: Restringe el acceso a usuarios con los roles "ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP" y "ROLE_ADMIN".
 * 
 * Constructor:
 * - EditarProyectoSolicitanteView(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser):
 *   Inicializa la vista con los servicios proporcionados y el usuario autenticado.
 * 
 * Métodos:
 * - setupFields(Proyecto proyectoAux): Sobrescribe el método base para configurar los campos del proyecto.
 *   - Establece el campo de correo electrónico como de solo lectura.
 *   - Establece los campos nombre, descripcion, alcance, interesados, aportacionInicial y coste como editables.
 *   - Establece el campo promotor como de solo lectura si hay un promotor asignado al proyecto, de lo contrario permite la edición.
 * 
 * @param proyectoService El servicio para gestionar proyectos.
 * @param usuarioService El servicio para gestionar usuarios.
 * @param authenticatedUser El usuario actualmente autenticado.
 */
@PageTitle("Editar mis proyectos")
@Route("EditarProyectoSolicitante")
@RolesAllowed({"ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP", "ROLE_ADMIN"})
public class EditarProyectoSolicitanteView extends EditarProyectosBaseView {
    public EditarProyectoSolicitanteView(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser) {
        super(proyectoService, usuarioService, authenticatedUser);
    }

    @Override
    protected void setupFields(Proyecto proyectoAux) {
        super.setupFields(proyectoAux);

        emailField.setReadOnly(true);
        nombre.setReadOnly(false);
        descripcion.setReadOnly(false);
        alcance.setReadOnly(false);
        interesados.setReadOnly(false);
        aportacionInicial.setReadOnly(false);
        coste.setReadOnly(false);
        jefe.setReadOnly(true);

        if (proyectoAux.getPromotor() != null) {
            promotor.setReadOnly(true);

        } else {
            promotor.setValue(null);
            promotor.setReadOnly(false);
        }
    }
}