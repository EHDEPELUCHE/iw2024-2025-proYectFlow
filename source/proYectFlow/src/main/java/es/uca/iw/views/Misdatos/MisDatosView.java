package es.uca.iw.views.Misdatos;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;

@PageTitle("Mis datos")
@Route("Ver-mis-datos")
@Menu(order = 6, icon = "line-awesome/svg/user.svg")
@PermitAll
public class MisDatosView extends Composite<VerticalLayout> /*implements BeforeEnterObserver*/ {
    private final AuthenticatedUser authenticatedUser;
    LoginOverlay loginOverlay = new LoginOverlay();

    public MisDatosView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        loginOverlay.setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        TextField textField = new TextField();
        TextField textField2 = new TextField();
        EmailField emailField = new EmailField();
        TextField textField3 = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField passwordField2 = new PasswordField();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonSecondary = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Datos personales");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        textField.setLabel("Nombre");
        textField2.setLabel("Apellidos");
        emailField.setLabel("Email");
        textField3.setLabel("Teléfono");
        passwordField.setLabel("Contraseña");
        passwordField.setWidth("min-content");
        passwordField2.setLabel("Repetir contraseña");
        passwordField2.setWidth("min-content");
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Save");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancel");
        buttonSecondary.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(textField);
        formLayout2Col.add(textField2);
        formLayout2Col.add(emailField);
        formLayout2Col.add(textField3);
        formLayout2Col.add(passwordField);
        formLayout2Col.add(passwordField2);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimary);
        layoutRow.add(buttonSecondary);
        //loginOverlay.setOpened(true);
    }
/*
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            //loginOverlay.setOpened(false);
            //event.forwardTo("");
        } else {
            getUI().ifPresent(ui -> ui.navigate("inicio-sesion"));
        }
        //loginOverlay.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }*/
}