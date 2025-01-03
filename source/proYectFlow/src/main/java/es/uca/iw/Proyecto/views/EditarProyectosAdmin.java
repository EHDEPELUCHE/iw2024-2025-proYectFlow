package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.UsuarioService;
import es.uca.iw.global.DownloadPdfComponent;
import es.uca.iw.global.Roles;
import es.uca.iw.global.views.PantallaInicioView;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Editar Proyecto")
@Route("EditarProyecto")
@RolesAllowed("ROLE_ADMIN")

public class EditarProyectosAdmin extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    private final BeanValidationBinder<Proyecto> binder = new BeanValidationBinder<>(Proyecto.class);
    UUID uuid;
    final UsuarioService usuarioService;
    final AuthenticatedUser authenticatedUser;
    final EmailField emailField = new EmailField();
    final ComboBox<Usuario> promotor = new ComboBox<>();
    final TextField nombre = new TextField();
    final TextField descripcion = new TextField();
    final TextField alcance = new TextField();
    final DatePicker fechaLimite = new DatePicker("fecha límite");
    final TextField interesados = new TextField("Interesados");
    final BigDecimalField aportacionInicial = new BigDecimalField();
    final BigDecimalField coste = new BigDecimalField();
    final MemoryBuffer buffer = new MemoryBuffer();
    final Upload upload = new Upload(buffer);

    public EditarProyectosAdmin(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser) {
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            try {
                uuid = UUID.fromString(parameter);
                this.proyecto = proyectoService.get(uuid);
            } catch (IllegalArgumentException e) {
                this.proyecto = Optional.empty();
            }
        } else {
            this.proyecto = Optional.empty();
        }
        if (proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        } else {
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();

            getContent().add(new H1("Detalles del Proyecto"));
            VerticalLayout layoutColumn2 = new VerticalLayout();
            H3 h3 = new H3();
            FormLayout formLayout2Col = new FormLayout();
            Optional<Usuario> solicitante = authenticatedUser.get();

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
            h3.setText("Registro de proyecto");
            h3.setWidth("100%");
            formLayout2Col.setWidth("100%");
            emailField.setLabel("Solicitante");
            emailField.setValue(solicitante.get().getCorreo());
            emailField.setWidth("min-content");
            promotor.setLabel("Promotor");
            promotor.setWidth("min-content");

            promotor.setItems(usuarioService.get(Roles.PROMOTOR));
            promotor.setItemLabelGenerator(Usuario::getNombre);

            nombre.setLabel("Nombre del proyecto");
            nombre.setWidth("min-content");
            nombre.setValue(proyectoAux.getNombre());

            alcance.setLabel("Alcance");
            alcance.setWidth("min-content");
            alcance.setValue(proyectoAux.getAlcance());

            descripcion.setLabel("Descripción");
            descripcion.setWidth("min-content");
            descripcion.setValue(proyectoAux.getDescripcion());

            interesados.setLabel("Interesados");
            interesados.setWidth("min-content");
            interesados.setValue(proyectoAux.getInteresados());

            aportacionInicial.setLabel("Financiación aportada en €");
            aportacionInicial.setWidth("min-content");
            aportacionInicial.setValue(proyectoAux.getAportacionInicial());

            coste.setLabel("Coste Total en €");
            coste.setWidth("min-content");
            coste.setValue(proyectoAux.getCoste());

            fechaLimite.setPlaceholder("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entrar en vigor");
            fechaLimite.setAriaLabel("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entrar en vigor");
            if (proyectoAux.getFechaLimite() != null) {
                fechaLimite.setValue(proyectoAux.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            upload.setAcceptedFileTypes("application/pdf", ".pdf");

            Button uploadButton = new Button("Añadir memoria");
            uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            upload.setUploadButton(uploadButton);

            upload.getElement()
                    .addEventListener("max-files-reached-changed", eventy -> {
                        boolean maxFilesReached = eventy.getEventData()
                                .getBoolean("event.detail.value");
                        uploadButton.setEnabled(!maxFilesReached);
                    }).addEventData("event.detail.value");

            Button btncancelar = new Button("Volver", eventy -> UI.getCurrent().navigate(ProyectosView.class));
            btncancelar.addClassName("buttonSecondary");

            // Usar el DownloadPdfComponent
            Button downloadButton = DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                try {
                    return proyectoService.getPdf(proyecto.get().getId());
                } catch (IOException ex) {
                    throw new RuntimeException("Error al obtener el PDF", ex);
                }
            });

            layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.getStyle().set("flex-grow", "1");
            layoutRow.setAlignItems(FlexComponent.Alignment.START);
            layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            buttonPrimary.setText("Guardar");
            buttonPrimary.addClickShortcut(Key.ENTER);
            buttonPrimary.addClickListener(e -> ActualizarProyecto(proyectoAux));
            buttonPrimary.setWidth("min-content");
            buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonSecondary.setText("Cancelar");
            layoutRow.setAlignSelf(FlexComponent.Alignment.START, btncancelar);
            buttonSecondary.setWidth("min-content");
            getContent().add(layoutColumn2);
            layoutColumn2.add(h3);
            layoutColumn2.add(formLayout2Col);
            formLayout2Col.add(emailField, promotor, nombre, descripcion, interesados, alcance, aportacionInicial, coste, fechaLimite, upload);

            Button Borrar = new Button("Borrar Proyecto", eventy -> {
                if(proyectoAux.getEstado() == Proyecto.Estado.denegado){
                    proyectoService.delete(proyectoAux.getId());
                    UI.getCurrent().navigate(ProyectosView.class);
                }else{
                    Notification.show("Este proyecto aún no se puede eliminar");
                }
            });
            Borrar.addClassName("button-danger");
            Borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);

            layoutColumn2.add(layoutRow);
            layoutRow.add(Borrar);
            layoutRow.add(downloadButton);
            layoutRow.add(buttonPrimary);
            layoutRow.add(btncancelar);
            binder.bindInstanceFields(this);
            binder.setBean(proyectoAux);
        }
    }

    private void ActualizarProyecto(Proyecto proyectoAux) {
        if(proyectoAux.getEstado() == Proyecto.Estado.solicitado) proyectoService.update(proyectoAux);
        else Notification.show("Este proyecto no se puede editar");
    }
}
