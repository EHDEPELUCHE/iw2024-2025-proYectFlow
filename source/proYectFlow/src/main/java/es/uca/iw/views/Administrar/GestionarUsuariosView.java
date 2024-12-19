package es.uca.iw.views.Administrar;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.data.rest.Promotor;
import es.uca.iw.data.rest.Respuesta;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.services.ProyectoService;
import es.uca.iw.services.UsuarioService;
import es.uca.iw.views.pantallainicio.PantallaInicioView;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Administración de usuarios")
@Route("AdministrarUsuarios")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_ADMIN")
public class GestionarUsuariosView extends Composite<VerticalLayout> {

    RestTemplate restTemplate;
    UsuarioService usuarioService;
    ProyectoService proyectoService;
    AuthenticatedUser authenticatedUser;
    private final BeanValidationBinder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);
    private static final Logger logger = Logger.getLogger(GestionarUsuariosView.class.getName());

    @Autowired
    public GestionarUsuariosView(UsuarioService usuarioService, ProyectoService proyectoService, AuthenticatedUser authenticatedUser) throws Exception {
        this.restTemplate = new RestTemplate();
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;
        this.authenticatedUser = authenticatedUser;
        Button METER_PROMOTORES = new Button("Meter Promotores");
        METER_PROMOTORES.addClickListener(e -> GuardarPromotores());
        getContent().add(METER_PROMOTORES);

        //Opción de buscar usuario
        H1 titulousuario = new H1("Gestionar Usuarios");
        EmailField correo = new EmailField("Correo del usuario a buscar");
        Button Buscar = new Button("Buscar");
        Buscar.addClickListener(e -> GestionarUsu(correo.getValue()));
        getContent().add(titulousuario, correo, Buscar);

    }

    private void GestionarUsu(String correo) {
        Usuario user = usuarioService.getCorreo(correo);
        if (user != null) {
            Dialog dialog = new Dialog();
            dialog.open();
            FormLayout formLayout2Col = new FormLayout();
            HorizontalLayout layoutRow = new HorizontalLayout();
            dialog.setHeaderTitle("Usuario: " + user.getNombre());
            dialog.add(new H4("Escriba los datos que desea modificar"));
            TextField username = new TextField("Username");
            username.setValue(user.getUsername());
            TextField nombre = new TextField("Nombre");
            nombre.setValue(user.getNombre());
            TextField apellido = new TextField("Apellido");
            apellido.setValue(user.getApellido());
            EmailField correoNuevo = new EmailField("Correo");
            correoNuevo.setValue(user.getCorreo());
            correoNuevo.setRequired(true);

            PasswordField password = new PasswordField("Contraseña");

            PasswordField confirmarPassword = new PasswordField("Repetir contraseña");
            VerticalLayout dialogLayout = new VerticalLayout();
            ComboBox<Roles> Tipo = new ComboBox<>();
            Tipo.setLabel("Rol");
            Tipo.setWidth("min-content");
            Tipo.setItems(Roles.values());
            Tipo.setValue(user.getTipo());
            Checkbox activo = new Checkbox("Validado");
            activo.setValue(true);

            Button Borrar = new Button("Borrar datos del usuario", event -> {
                proyectoService.desligarUsuario(user);
                usuarioService.delete(user.getId());
                dialog.close();
            });
            Borrar.addClassName("button-danger");

            dialogLayout.add(formLayout2Col);
            formLayout2Col.add(nombre);
            formLayout2Col.add(apellido);
            formLayout2Col.add(correoNuevo);
            formLayout2Col.add(username);
            formLayout2Col.add(password);
            formLayout2Col.add(confirmarPassword);
            formLayout2Col.add(Tipo);
            formLayout2Col.add(activo);
            //formLayout2Col.add(Borrar);


            dialog.add(dialogLayout, Borrar);

            Button saveButton = new Button("Guardar", e -> {
                try{
                    if(Objects.equals(password.getValue(), confirmarPassword.getValue())){
                        if (!Objects.equals(username.getValue(), username.getEmptyValue())) user.setUsername(username.getValue());
                        if (!Objects.equals(nombre.getValue(), nombre.getEmptyValue())) user.setNombre(nombre.getValue());
                        if (!Objects.equals(apellido.getValue(), apellido.getEmptyValue())) user.setApellido(apellido.getValue());
                        user.setCorreo(correoNuevo.getValue());
                        if (!Objects.equals(password.getValue(), password.getEmptyValue())) user.setContrasenna(usuarioService.encriptar(password.getValue()));
                        if (Tipo.getValue() != Tipo.getEmptyValue()) user.setTipo(Tipo.getValue());
                        user.setActivo(activo.getValue());
                        usuarioService.update(user);
                        Notification.show("Usuario guardado correctamente");
                        dialog.close();
                    }else{
                        Notification.show("Revise los datos de entrada");
                    }

                }catch(Exception ex){
                    Notification.show("Revise los datos de entrada" + ex.getMessage());
                }

            });
            saveButton.addClassName("buttonPrimary");
            Button cancelButton = new Button("Cancel", e -> dialog.close());
            cancelButton.addClassName("buttonSecondary");
            dialog.getFooter().add(cancelButton);
            dialog.getFooter().add(saveButton);

            //Button button = new Button("Show dialog", e -> dialog.open());

            getContent().add(dialog);
        }else{
            Notification.show("No existe el usuario con el correo: " + correo);
        }
    }

    @Transactional
    public void GuardarPromotores() {
        String API_URL = "https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors";
    
            RestTemplate restTemplate = new RestTemplate();//context.getBean(RestTemplate.class);
    
            try {
                // Realizar solicitud GET
                ResponseEntity<Respuesta> response = restTemplate.getForEntity(API_URL, Respuesta.class);
    
                // Verificar si la respuesta es exitosa
                if (response.getStatusCode().is2xxSuccessful()) {
                    try {
                        usuarioService.destituyePromotores();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Respuesta apiResponse = response.getBody();
                    Notification.show("Respuesta de la API: " + response.getBody());
                    // Trabajar con los datos
                    if (apiResponse != null && apiResponse.getData() != null) {
                        for (Promotor promotor : apiResponse.getData()) {
                            logger.info("Promotor nombre: " + promotor.getNombre());
                            logger.info("Promotor apellido: " + promotor.getApellido());
                            try {
                                usuarioService.createPromotor(promotor);
                                Notification.show("Promotor guardado exitosamente");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Notification.show("Error al guardar el promotor");
                            }
                        }
                    }
                } else {
                    System.out.println("Error en la solicitud: " + response.getStatusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
}
