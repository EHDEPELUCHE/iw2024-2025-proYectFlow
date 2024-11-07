package es.uca.iw.views.iniciosesión;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@StyleSheet("../../frontend/styles/styles.css")
@PageTitle("Inicio Sesión")
@Route("inicio-sesion")
@Menu(order = 2, icon = "line-awesome/svg/user.svg")
public class InicioSesiónView extends Composite<VerticalLayout> {


    public InicioSesiónView() {
        
        VerticalLayout layoutColumn2 = new VerticalLayout();

        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle("Inicio Sesión");
        i18nForm.setUsername("Usuario");
        i18nForm.setPassword("Contrasña");
        i18nForm.setSubmit("Aceptar");
        i18nForm.setForgotPassword("Quiero recuperar mi contraseña");
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("Error con el usuario o contraseña");
        i18nErrorMessage.setMessage(
                "Ha ocurrido un error.");
        i18n.setErrorMessage(i18nErrorMessage);

        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(i18n);

        layoutColumn2.add(loginForm);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);

        getContent().add(layoutColumn2);
    }
}
