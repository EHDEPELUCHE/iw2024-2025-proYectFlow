package es.uca.iw.views.iniciosesion;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.services.UsuarioService;

@StyleSheet("../../frontend/styles/styles.css")
@PageTitle("Inicio Sesión")
@Route("inicio-sesion")
@Menu(order = 2, icon = "line-awesome/svg/user.svg")
@AnonymousAllowed
public class InicioSesionView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    static LoginOverlay loginOverlay = new LoginOverlay();
    private final AuthenticatedUser authenticatedUser;
    LoginI18n i18n = LoginI18n.createDefault();

    public InicioSesionView(AuthenticatedUser authenticatedUser, UsuarioService usuarioService) {
        this.authenticatedUser = authenticatedUser;
        VerticalLayout layoutColumn2 = new VerticalLayout();
        loginOverlay.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        
        i18nForm.setTitle("Inicio Sesión");
        i18nForm.setUsername("Usuario");
        i18nForm.setPassword("Contraseña");
        i18nForm.setSubmit("Aceptar");

        i18nForm.setForgotPassword("Quiero recuperar mi contraseña");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("El usuario o la contraseña son erróneos");
        i18nErrorMessage.setMessage("INICIO DE SESIÓN INCORRECTO.");
        i18n.setErrorMessage(i18nErrorMessage);

        /*LoginForm loginForm = new LoginForm();
        loginForm.setI18n(i18n);


        loginForm.addLoginListener(event -> {
            String username = event.getUsername();
            String password = event.getPassword();
            if (usuarioService.authenticate(username, password)) {
                // Redirigir al usuario a la página de sus datos
                Notification.show("USUARIO Y CONTRASEÑAS MATCH");
                getUI().ifPresent(ui -> ui.navigate("Ver-mis-datos"));
            } else {
                Notification.show("Fallo en la autenticación.");
                loginForm.setError(true);
            }
        });*/


        loginOverlay.setI18n(i18n);
        loginOverlay.setOpened(true); // Abre el login overlay
        loginOverlay.setForgotPasswordButtonVisible(true); // Opcional

        layoutColumn2.add(loginOverlay);
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
            // Already logged in
            loginOverlay.setOpened(false);
            Notification.show("El usuario no está autenticado, redirigiendo a inicio de sesión.");
            event.forwardTo("Ver-mis-datos");
        }
        loginOverlay.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
