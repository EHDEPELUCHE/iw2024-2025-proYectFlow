package es.uca.iw.usuario.views.controlacceso;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.security.AuthenticatedUser;

/**
 * Vista de inicio de sesión para usuarios anónimos.
 * 
 * <p>Esta clase representa la vista de inicio de sesión utilizando un LoginOverlay
 * y permite a los usuarios autenticarse o registrarse si no tienen una cuenta.</p>
 * 
 * <p>La vista está configurada con un título, campos de usuario y contraseña, y un botón
 * para enviar el formulario. También incluye un enlace para redirigir a los usuarios
 * a la página de registro si no tienen una cuenta.</p>
 * 
 * <p>Además, maneja la autenticación y redirección de usuarios autenticados a la vista
 * "Ver-mis-datos".</p>
 * 
 * <p>La clase implementa BeforeEnterObserver para verificar si el usuario ya está autenticado
 * antes de entrar a la vista.</p>
 * 
 * @PageTitle("Inicio Sesión")
 * @Route("login")
 * @AnonymousAllowed
 */
@PageTitle("Inicio Sesión")
@Route("login")
@AnonymousAllowed
public class InicioSesionView extends Composite<VerticalLayout> implements BeforeEnterObserver {
    private final LoginOverlay loginOverlay;
    private final AuthenticatedUser authenticatedUser;

    public InicioSesionView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.loginOverlay = new LoginOverlay();

        VerticalLayout layoutColumn2 = new VerticalLayout();

        // Configuración del LoginOverlay
        loginOverlay.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form i18nForm = i18n.getForm();

        i18nForm.setTitle("Inicio Sesión");
        i18nForm.setUsername("Usuario");
        i18nForm.setPassword("Contraseña");
        i18nForm.setSubmit("Aceptar");
        i18nForm.setForgotPassword("¿No tienes cuenta? Regístrate");
        loginOverlay.addForgotPasswordListener(event -> {
            loginOverlay.setOpened(false);
            getUI().ifPresent(ui -> ui.navigate("registro-usuario"));
        });
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("El usuario o la contraseña son erróneos");
        i18nErrorMessage.setMessage("INICIO DE SESIÓN INCORRECTO.");
        i18n.setErrorMessage(i18nErrorMessage);

        loginOverlay.setI18n(i18n);
        loginOverlay.setOpened(true);
        loginOverlay.setForgotPasswordButtonVisible(true); // Opcional
        loginOverlay.setTitle("Inicio Sesión");
        loginOverlay.setAction("login");

        // Autenticación y redirección
        loginOverlay.addLoginListener(event -> {
            event.getUsername();
            event.getPassword();
        });

        HorizontalLayout layoutRow = new HorizontalLayout();
        RouterLink registrarUsuario = new RouterLink("¿No tienes cuenta? Regístrate", RegistrodeusuarioView.class);
        registrarUsuario.getStyle().set("margin-top", "10px");
        layoutRow.add(registrarUsuario);

        layoutColumn2.add(loginOverlay, layoutRow);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, loginOverlay);

        getContent().add(layoutColumn2);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            loginOverlay.setOpened(false);
            event.forwardTo("Ver-mis-datos");
        }
        loginOverlay.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
