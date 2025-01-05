package es.uca.iw.usuario.views.controlacceso;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.global.Roles;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Registro de usuario admin")
@Route("registro-usuario-admin")
@Menu(order = 2, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_ADMIN")
//@AnonymousAllowed
public class RegistroUsuarioAdminView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Usuario> binder;
    final TextField username = new TextField();
    final TextField nombre = new TextField();
    final TextField apellido = new TextField();
    final EmailField correo = new EmailField();
    final PasswordField contrasenna = new PasswordField();
    final ComboBox<Roles> Tipo = new ComboBox<>();
    final UsuarioService servicio;
    final String mincontent = "min-content";

    public RegistroUsuarioAdminView(UsuarioService usuarioService) {
        PasswordField passwordField2 = new PasswordField();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonSecondary = new Button();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();

        this.servicio = usuarioService;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight(mincontent);
        h3.setText("Registro de usuario");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        username.setLabel("usuario");
        nombre.setLabel("nombre");
        apellido.setLabel("apellidos");
        correo.setLabel("email");
        contrasenna.setLabel("contraseña");
        contrasenna.setWidth(mincontent);
        Tipo.setLabel("Rol");
        Tipo.setWidth(mincontent);
        Tipo.setItems(Roles.values());

        passwordField2.setLabel("Repetir contraseña");
        passwordField2.setWidth(mincontent);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Guardar");
        buttonPrimary.addClickListener(e -> onRegisterButtonClick(passwordField2));
        buttonPrimary.addClickShortcut(Key.ENTER);

        buttonPrimary.setWidth(mincontent);
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancelar");
        buttonSecondary.setWidth(mincontent);
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(username);
        formLayout2Col.add(nombre);
        formLayout2Col.add(apellido);
        formLayout2Col.add(correo);
        //formLayout2Col.add(textField3);
        formLayout2Col.add(contrasenna);
        formLayout2Col.add(passwordField2);
        formLayout2Col.add(Tipo);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimary);
        layoutRow.add(buttonSecondary);
        binder = new BeanValidationBinder<>(Usuario.class);
        binder.bindInstanceFields(this);
    }

    public void onRegisterButtonClick(PasswordField passwordField2) {
        H4 status = new H4();
        Usuario usuarioRegistro = new Usuario(nombre.getValue(),
                username.getValue(), apellido.getValue(),
                correo.getValue(), contrasenna.getValue(), Tipo.getValue());
        binder.setBean(usuarioRegistro);

        if (!contrasenna.getValue().equals(passwordField2.getValue())) {
            Notification.show("Las contraseñas no coinciden");
            return;
        }

        if (binder.validate().isOk()) {

            if (servicio.registerUserAdmin(binder.getBean())) {
                status.setText("Excelente. ¡Por favor mira tu bandeja de entrada de correo!");
                status.setVisible(true);
                binder.setBean(new Usuario());
                Notification.show("Usuario registrado con éxito");
                passwordField2.setValue("");
            } else {
                Notification.show("El nombre de usuario ya está en uso");
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
        }
    }
}
