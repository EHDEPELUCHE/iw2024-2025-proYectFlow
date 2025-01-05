package es.uca.iw.proyecto.views;

import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

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

        if (proyectoAux.getPromotor() != null) {
            promotor.setReadOnly(true);
        } else {
            promotor.setValue(null);
            promotor.setReadOnly(false);
        }
    }
}