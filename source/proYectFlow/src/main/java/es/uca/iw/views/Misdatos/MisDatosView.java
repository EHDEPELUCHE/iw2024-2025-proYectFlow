package es.uca.iw.views.Misdatos;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.data.Usuario;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.services.UsuarioService;
import es.uca.iw.views.pantallainicio.PantallaInicioView;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PageTitle("Mis datos")
@Route("Ver-mis-datos")
@Menu(order = 6, icon = "line-awesome/svg/user.svg")
@PermitAll
public class MisDatosView extends Composite<VerticalLayout>  {
    private final BeanValidationBinder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);
    private final AuthenticatedUser authenticatedUser;
    UsuarioService uservice ;

    TextField username = new TextField();
    TextField nombre = new TextField();
    TextField apellido = new TextField();
    EmailField correo = new EmailField();

    public MisDatosView(AuthenticatedUser authenticatedUser, UsuarioService uservice) {
        this.authenticatedUser = authenticatedUser;
        this.uservice = uservice;
        Optional<Usuario> user = authenticatedUser.get();

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();

       // PasswordField passwordField2 = new PasswordField();
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
        nombre.setLabel("Nombre");
        apellido.setLabel("Apellidos");
        correo.setLabel("Email");
        username.setLabel("Usuario");

        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Guardar");
        buttonPrimary.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try{
                    uservice.update(binder.getBean());

                    binder.setBean(new Usuario());
                    Notification.show("datos actualizados correctamente");
                } catch (Exception ex) {
                    Notification.show("Usuario o correo repetido");
                }
            } else {
                Notification.show("Por favor, verifique los datos de entrada");
            }
        });
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary.addClickShortcut(Key.ENTER);
        buttonSecondary.setText("Cancelar");
        buttonSecondary.setWidth("min-content");

        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(nombre);
        formLayout2Col.add(apellido);
        formLayout2Col.add(correo);
        formLayout2Col.add(username);

        RouterLink cambiocontrasenna = new RouterLink("Cambiar contraseña", CambioContrasennaView.class);
        Button btncancelar = new Button("Volver", event -> UI.getCurrent().navigate(PantallaInicioView.class));
        btncancelar.addClassName("buttonSecondary");

        Button Borrar = new Button("Borrar Mis datos", event -> {
            uservice.delete(user.get().getId());
            authenticatedUser.logout();
            UI.getCurrent().navigate(PantallaInicioView.class);
        });
        Borrar.addClassName("button-danger");
        Borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        layoutColumn2.add(cambiocontrasenna, layoutRow);
        layoutRow.add(buttonPrimary, btncancelar);
        getContent().add(Borrar);
        Usuario aux = user.get();
        binder.bindInstanceFields(this);
        binder.setBean(aux);
    }
}