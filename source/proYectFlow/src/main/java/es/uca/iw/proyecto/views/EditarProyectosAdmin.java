package es.uca.iw.proyecto.views;

import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * La clase EditarProyectosAdmin es una vista para editar proyectos, específicamente para usuarios con el rol "ROLE_ADMIN".
 * Extiende la clase EditarProyectosBaseView y proporciona funcionalidad adicional para los administradores.
 * 
 * <p>Esta vista está mapeada a la ruta "EditarProyecto" y está anotada con @PageTitle("Editar Proyecto") 
 * y @RolesAllowed("ROLE_ADMIN") para asegurar que solo los usuarios con el rol de administrador puedan acceder a ella.</p>
 * 
 * <p>El constructor inicializa la vista con los servicios necesarios: ProyectoService, UsuarioService, 
 * y AuthenticatedUser.</p>
 * 
 * <p>El método setupFields sobrescribe el método de la clase base para hacer que todos los campos sean editables para el usuario administrador.</p>
 * 
 * @param proyectoService el servicio para gestionar proyectos
 * @param usuarioService el servicio para gestionar usuarios
 * @param authenticatedUser el usuario actualmente autenticado
 */
@PageTitle("Editar Proyecto")
@Route("EditarProyecto")
@RolesAllowed("ROLE_ADMIN")
public class EditarProyectosAdmin extends EditarProyectosBaseView {
    public EditarProyectosAdmin(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser) {
        super(proyectoService, usuarioService, authenticatedUser);
    }

    @Override
    protected void setupFields(Proyecto proyectoAux) {
        super.setupFields(proyectoAux);

        emailField.setReadOnly(false);
        promotor.setReadOnly(false);
        jefe.setReadOnly(false);
        nombre.setReadOnly(false);
        descripcion.setReadOnly(false);
        alcance.setReadOnly(false);
        interesados.setReadOnly(false);
        aportacionInicial.setReadOnly(false);
        coste.setReadOnly(false);
    }
}