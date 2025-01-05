package es.uca.iw.Usuario.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioService;
import es.uca.iw.global.Roles;
import es.uca.iw.rest.Promotor;
import es.uca.iw.rest.Respuesta;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.logging.Logger;

@PageTitle("Administración de usuarios")
@Route("AdministrarUsuarios")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_ADMIN")
public class GestionarUsuariosView extends Composite<VerticalLayout> {
    private static final Logger logger = Logger.getLogger(GestionarUsuariosView.class.getName());
    private final RestTemplate restTemplate;
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final Grid<Usuario> grid;

    @Autowired
    public GestionarUsuariosView(UsuarioService usuarioService, ProyectoService proyectoService) {
        this.restTemplate = new RestTemplate();
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;

        H1 titulo = new H1("Actualizar promotores en el sistema");
        Button meterPromotoresButton = new Button("Meter Promotores");
        meterPromotoresButton.addClickListener(e -> guardarPromotores());
        getContent().add(titulo, meterPromotoresButton);

        H1 titulousuario = new H1("Gestionar Usuarios");
        EmailField correoField = new EmailField("Correo del usuario a buscar");
        Button buscarButton = new Button("Buscar");
        buscarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buscarButton.addClickListener(e -> gestionarUsuario(correoField.getValue()));
        buscarButton.addClickShortcut(Key.ENTER);
        getContent().add(titulousuario, correoField, buscarButton);

        grid = new Grid<>(Usuario.class, false);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre");
        grid.addColumn(Usuario::getApellido).setHeader("Apellido");
        grid.addColumn(Usuario::getCorreo).setHeader("Correo");
        grid.addColumn(Usuario::getTipo).setHeader("Rol");
        grid.addColumn(Usuario::getActivo).setHeader("Activo");

        grid.addComponentColumn(usuario -> {
            Button modificarButton = new Button("Modificar");
            modificarButton.addClickListener(e -> gestionarUsuario(usuario.getCorreo()));
            return modificarButton;
        }).setHeader("Modificar");

        Pageable pageable = Pageable.unpaged();
        Page<Usuario> usuariosPage = usuarioService.list(pageable);
        grid.setItems(usuariosPage.getContent());

        getContent().add(grid);
    }

    private void gestionarUsuario(String correo) {
        Usuario user = usuarioService.getCorreo(correo);
        if (user != null) {
            Dialog dialog = new Dialog();
            dialog.open();
            dialog.setHeaderTitle("Usuario: " + user.getNombre());
            dialog.add(new H4("Escriba los datos que desea modificar"));

            FormLayout formLayout = new FormLayout();
            TextField usernameField = new TextField("Username");
            usernameField.setValue(user.getUsername());
            TextField nombreField = new TextField("Nombre");
            nombreField.setValue(user.getNombre());
            TextField apellidoField = new TextField("Apellido");
            apellidoField.setValue(user.getApellido());
            EmailField correoNuevoField = new EmailField("Correo");
            correoNuevoField.setValue(user.getCorreo());
            correoNuevoField.setRequired(true);

            PasswordField passwordField = new PasswordField("Contraseña");
            PasswordField confirmarPasswordField = new PasswordField("Repetir contraseña");

            ComboBox<Roles> tipoComboBox = new ComboBox<>();
            tipoComboBox.setLabel("Rol");
            tipoComboBox.setItems(Roles.values());
            tipoComboBox.setValue(user.getTipo());

            Checkbox activoCheckbox = new Checkbox("Validado");
            activoCheckbox.setValue(user.getActivo());

            Button borrarButton = new Button("Borrar datos del usuario", event -> {
                proyectoService.desligarUsuario(user);
                grid.getDataProvider().refreshItem(user);
                usuarioService.delete(user.getId());
                dialog.close();
            });
            borrarButton.addClassName("button-danger");
            borrarButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            formLayout.add(nombreField, apellidoField, correoNuevoField, usernameField, passwordField, confirmarPasswordField, tipoComboBox, activoCheckbox);
            dialog.add(formLayout, borrarButton);

            Button saveButton = new Button("Guardar", e -> {
                try {
                    if (Objects.equals(passwordField.getValue(), confirmarPasswordField.getValue())) {
                        if (!Objects.equals(usernameField.getValue(), usernameField.getEmptyValue()))
                            user.setUsername(usernameField.getValue());
                        if (!Objects.equals(nombreField.getValue(), nombreField.getEmptyValue()))
                            user.setNombre(nombreField.getValue());
                        if (!Objects.equals(apellidoField.getValue(), apellidoField.getEmptyValue()))
                            user.setApellido(apellidoField.getValue());
                        user.setCorreo(correoNuevoField.getValue());
                        if (!Objects.equals(passwordField.getValue(), passwordField.getEmptyValue()))
                            user.setContrasenna(usuarioService.encriptar(passwordField.getValue()));
                        if (tipoComboBox.getValue() != tipoComboBox.getEmptyValue()) user.setTipo(tipoComboBox.getValue());
                        user.setActivo(activoCheckbox.getValue());
                        usuarioService.update(user);
                        Notification.show("Usuario guardado correctamente");
                        grid.getDataProvider().refreshItem(user);
                        dialog.close();
                    } else {
                        Notification.show("Revise los datos de entrada");
                    }
                } catch (Exception ex) {
                    Notification.show("Revise los datos de entrada: " + ex.getMessage());
                }
            });
            saveButton.addClassName("buttonPrimary");
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            saveButton.addClickShortcut(Key.ENTER);

            Button cancelButton = new Button("Cancelar", e -> dialog.close());
            cancelButton.addClassName("buttonSecondary");

            dialog.getFooter().add(cancelButton, saveButton);

            getContent().add(dialog);
        } else {
            Notification.show("No existe el usuario con el correo: " + correo);
        }
    }

    @Transactional
    public void guardarPromotores() {
        String API_URL = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
        try {
            ResponseEntity<Respuesta> response = restTemplate.getForEntity(API_URL, Respuesta.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    usuarioService.destituyePromotores();
                } catch (Exception e) {
                    logger.severe("Error al destituir promotores: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                Respuesta apiResponse = response.getBody();
                Notification.show("Respuesta de la API: " + response.getBody());
                if (apiResponse != null && apiResponse.getData() != null) {
                    for (Promotor promotor : apiResponse.getData()) {
                        logger.info("Promotor nombre: " + promotor.getNombre());
                        logger.info("Promotor apellido: " + promotor.getApellido());
                        try {
                            usuarioService.createPromotor(promotor);
                            Notification.show("Promotor guardado exitosamente");
                        } catch (Exception ex) {
                            logger.severe("Error al guardar el promotor: " + ex.getMessage());
                            Notification.show("Error al guardar el promotor");
                        }
                    }
                }
            } else {
                logger.warning("Error en la solicitud: " + response.getStatusCode());
                Notification.show("Error en la solicitud: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.severe("Error al obtener los promotores: " + e.getMessage());
            Notification.show("Error al obtener los promotores");
        }
    }
}
