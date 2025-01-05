package es.uca.iw.proyecto.views;

import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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
        nombre.setReadOnly(false);
        descripcion.setReadOnly(false);
        alcance.setReadOnly(false);
        interesados.setReadOnly(false);
        aportacionInicial.setReadOnly(false);
        coste.setReadOnly(false);
    }
}