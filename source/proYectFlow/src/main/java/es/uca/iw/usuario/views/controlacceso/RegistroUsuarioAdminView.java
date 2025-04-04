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

/**
 * Vista para el registro de usuarios administradores.
 * 
 * Esta vista permite a los administradores registrar nuevos usuarios con roles específicos.
 * 
 * Anotaciones:
 * - @PageTitle: Título de la página.
 * - @Route: Ruta de la vista.
 * - @Menu: Configuración del menú (orden e icono).
 * - @RolesAllowed: Roles permitidos para acceder a esta vista.
 * 
 * Componentes:
 * - BeanValidationBinder<Usuario>: Binder para la validación de campos del formulario.
 * - TextField username: Campo de texto para el nombre de usuario.
 * - TextField nombre: Campo de texto para el nombre.
 * - TextField apellido: Campo de texto para el apellido.
 * - EmailField correo: Campo de texto para el correo electrónico.
 * - PasswordField contrasenna: Campo de texto para la contraseña.
 * - ComboBox<Roles> tipo: ComboBox para seleccionar el rol del usuario.
 * - UsuarioService servicio: Servicio para la gestión de usuarios.
 * 
 * Constructor:
 * - RegistroUsuarioAdminView(UsuarioService usuarioService): Inicializa la vista con el servicio de usuario.
 * 
 * Métodos:
 * - onRegisterButtonClick(PasswordField passwordField2): Maneja el evento de clic del botón de registro.
 * 
 * Uso:
 * Esta vista se utiliza para registrar nuevos usuarios administradores, validando los datos de entrada y mostrando notificaciones según el resultado del registro.
 */
@PageTitle("Registro de usuario admin")
@Route("registro-usuario-admin")
@Menu(order = 2, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_ADMIN")
public class RegistroUsuarioAdminView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Usuario> binder;
    final TextField username = new TextField();
    final TextField nombre = new TextField();
    final TextField apellido = new TextField();
    final EmailField correo = new EmailField();
    final PasswordField contrasenna = new PasswordField();
    final ComboBox<Roles> tipo = new ComboBox<>();
    final UsuarioService servicio;
    static final String MIN_CONTENT = "min-content";

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
        layoutColumn2.setHeight(MIN_CONTENT);
        h3.setText("Registro de usuario");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        username.setLabel("usuario");
        nombre.setLabel("nombre");
        apellido.setLabel("apellidos");
        correo.setLabel("email");
        contrasenna.setLabel("contraseña");
        contrasenna.setWidth(MIN_CONTENT);
        tipo.setLabel("Rol");
        tipo.setWidth(MIN_CONTENT);
        tipo.setItems(Roles.values());

        passwordField2.setLabel("Repetir contraseña");
        passwordField2.setWidth(MIN_CONTENT);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimary.setText("Guardar");
        buttonPrimary.addClickListener(e -> onRegisterButtonClick(passwordField2));
        buttonPrimary.addClickShortcut(Key.ENTER);

        buttonPrimary.setWidth(MIN_CONTENT);
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancelar");
        buttonSecondary.setWidth(MIN_CONTENT);
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(username);
        formLayout2Col.add(nombre);
        formLayout2Col.add(apellido);
        formLayout2Col.add(correo);
        formLayout2Col.add(contrasenna);
        formLayout2Col.add(passwordField2);
        formLayout2Col.add(tipo);
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
                correo.getValue(), contrasenna.getValue(), tipo.getValue());
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
                Notification.show("Usuario registrado con éxito, a la espera de activación");
                passwordField2.setValue("");
            } else {
                Notification.show("El nombre de usuario ya está en uso");
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
        }
    }
}
