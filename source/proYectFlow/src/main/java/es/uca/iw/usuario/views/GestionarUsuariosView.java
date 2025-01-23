package es.uca.iw.usuario.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
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
import es.uca.iw.global.Roles;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.rest.Promotor;
import es.uca.iw.rest.Respuesta;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Vista para la gestión de usuarios en el sistema.
 * 
 * <p>Esta clase proporciona una interfaz para administrar usuarios, incluyendo la
 * capacidad de buscar, modificar y eliminar usuarios, así como actualizar promotores
 * en el sistema.</p>
 * 
 * <p>La vista incluye un formulario para buscar usuarios por correo electrónico,
 * una tabla para mostrar los usuarios y un diálogo para modificar los datos de un
 * usuario seleccionado.</p>
 * 
 * <p>Además, se proporciona un botón para actualizar los promotores mediante una
 * llamada a una API externa.</p>
 * 
 * <p>Esta clase está anotada con:</p>
 * <ul>
 *   <li>{@code @PageTitle}: Establece el título de la página.</li>
 *   <li>{@code @Route}: Define la ruta de la vista.</li>
 *   <li>{@code @Menu}: Configura el ítem del menú.</li>
 *   <li>{@code @RolesAllowed}: Restringe el acceso a usuarios con el rol "ROLE_ADMIN".</li>
 *   <li>{@code @EnableAsync}: Habilita la ejecución asíncrona de métodos.</li>
 * </ul>
 * 
 * <p>Campos:</p>
 * <ul>
 *   <li>{@code logger}: Logger para registrar mensajes de la aplicación.</li>
 *   <li>{@code restTemplate}: Cliente para realizar solicitudes HTTP.</li>
 *   <li>{@code usuarioService}: Servicio para la gestión de usuarios.</li>
 *   <li>{@code proyectoService}: Servicio para la gestión de proyectos.</li>
 *   <li>{@code grid}: Tabla para mostrar los usuarios.</li>
 * </ul>
 * 
 * <p>Métodos:</p>
 * <ul>
 *   <li>{@code GestionarUsuariosView}: Constructor que inicializa la vista.</li>
 *   <li>{@code gestionarUsuario}: Método para gestionar un usuario específico.</li>
 *   <li>{@code guardarPromotores}: Método asíncrono para actualizar los promotores desde una API externa.</li>
 * </ul>
 */
@PageTitle("Administración de usuarios")
@Route("AdministrarUsuarios")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_ADMIN")
@EnableAsync
public class GestionarUsuariosView extends Composite<VerticalLayout> {
    private static final Logger logger = Logger.getLogger(GestionarUsuariosView.class.getName());
    private final RestTemplate restTemplate;
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final Grid<Usuario> grid;

    public GestionarUsuariosView(UsuarioService usuarioService, ProyectoService proyectoService) {
        this.restTemplate = new RestTemplate();
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;

        H1 titulo = new H1("Actualizar promotores en el sistema");
        Button meterPromotoresButton = new Button("Meter Promotores");
        meterPromotoresButton.addClickListener(e -> {
            Thread thread = new Thread(() -> guardarPromotores());
            thread.start();
            Notification.show("Actualizando promotores...", 5000, Notification.Position.MIDDLE);              
        });
        getContent().add(titulo, meterPromotoresButton);

        H1 titulousuario = new H1("Gestionar Usuarios");
        EmailField correoField = new EmailField("Correo del usuario a buscar");
        Button buscarButton = new Button("Buscar");
        buscarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buscarButton.addClickListener(e -> gestionarUsuario(correoField.getValue()));
        buscarButton.addClickShortcut(Key.ENTER);
        getContent().add(titulousuario, correoField, buscarButton);

        grid = new Grid<>(Usuario.class, false);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Usuario::getApellido).setHeader("Apellido").setSortable(true);
        grid.addColumn(Usuario::getCorreo).setHeader("Correo").setSortable(true);
        grid.addColumn(Usuario::getTipo).setHeader("Rol").setSortable(true);
        grid.addColumn(Usuario::getActivo).setHeader("Activo").setSortable(true);

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
                        if (tipoComboBox.getValue() != tipoComboBox.getEmptyValue())
                            user.setTipo(tipoComboBox.getValue());
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

    @Async
    public void guardarPromotores() {
    
        String apiUrl = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
        try {
            ResponseEntity<Respuesta> response = restTemplate.getForEntity(apiUrl, Respuesta.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    usuarioService.destituyePromotores();
                } catch (Exception e) {
                    logger.severe("Error al destituir promotores: " + e.getMessage());
                    throw e;
                }
                
                if (response.getBody() != null && response.getBody().getData() != null) {
                    for (Promotor promotor : response.getBody().getData()) {
                        try {
                            try {
                                usuarioService.createPromotor(promotor);
                            } catch (Exception ex) {
                                UI.getCurrent().access(() -> {
                                   // Notification.show("Error al guardar el promotor");
                                    logger.severe("Error al guardar el promotor: " + ex.getMessage());
                                });
                               // Notification.show("Error al guardar el promotor");
                            }
                        } catch (Exception ex) {
                            UI.getCurrent().access(() -> {
                                //Notification.show("Error al guardar el promotor");
                                logger.severe("Error al guardar el promotor: " + ex.getMessage());
                            });
                        }
                    }
                }
            } else {
               // UI.getCurrent().access(() -> Notification.show("Error en la solicitud: " + response.getStatusCode()));
                logger.warning("Error en la solicitud: " + response.getStatusCode());
                //Notification.show("Error en la solicitud: " + response.getStatusCode());
            }
        } catch (Exception e) {
            //UI.getCurrent().access(() -> Notification.show("Error al obtener los promotores"));
            logger.severe("Error al obtener los promotores: " + e.getMessage());
           // Notification.show("Error al obtener los promotores");
        }
    }
}