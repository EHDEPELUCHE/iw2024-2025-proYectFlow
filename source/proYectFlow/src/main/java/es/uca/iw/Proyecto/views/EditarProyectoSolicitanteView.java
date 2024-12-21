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
import es.uca.iw.global.Roles;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Editar mis proyectos")
@Route("EditarProyectoSolicitante")
@RolesAllowed({"ROLE_SOLICITANTE", "ROLE_PROMOTOR"})

public class EditarProyectoSolicitanteView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    ProyectoService proyectoService;
    private final BeanValidationBinder<Proyecto> binder = new BeanValidationBinder<>(Proyecto.class);
    UUID uuid;
    UsuarioService usuarioService;
    AuthenticatedUser authenticatedUser;
    EmailField emailField = new EmailField();
    ComboBox<Usuario> promotor = new ComboBox<>();
    TextField nombre = new TextField();
    TextField descripcion = new TextField();
    TextField alcance = new TextField();
    DatePicker fechaLimite = new DatePicker("fecha límite");
    TextField interesados = new TextField("Interesados");
    BigDecimalField aportacionInicial = new BigDecimalField();
    BigDecimalField coste = new BigDecimalField();
    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);

    public EditarProyectoSolicitanteView(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser) {
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
        if (proyecto == null || proyecto.isEmpty()) {
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
            Button buttonGuardar = new Button();
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
            emailField.setReadOnly(true);
            promotor.setLabel("Promotor");
            promotor.setWidth("min-content");

            promotor.setItems(usuarioService.get(Roles.PROMOTOR));
            promotor.setItemLabelGenerator(Usuario::getNombre);

            if (proyectoAux.getPromotor() != null) {
                promotor.setValue(proyectoAux.getPromotor());
                promotor.setReadOnly(true);
            } else {
                promotor.setValue(null);
                promotor.setReadOnly(false);
            }

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

            Button uploadButton = new Button("Upload PDF...");
            uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            upload.setUploadButton(uploadButton);

            upload.getElement()
                    .addEventListener("max-files-reached-changed", eventy -> {
                        boolean maxFilesReached = eventy.getEventData()
                                .getBoolean("event.detail.value");
                        uploadButton.setEnabled(!maxFilesReached);
                    }).addEventData("event.detail.value");

            Button btncancelar = new Button("Volver", eventy -> UI.getCurrent().navigate(EstadoProyectosView.class));
            btncancelar.addClassName("buttonSecondary");

            Button downloadButton = new Button("Memoria");
            downloadButton.addClickListener(e -> {
                byte[] pdfContent = null;
                try {
                    pdfContent = proyectoService.getPdf(proyecto.get().getId());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (pdfContent != null) {
                    byte[] finalPdfContent = pdfContent;
                    StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(finalPdfContent));
                    Anchor downloadLink = new Anchor(resource, "Download");
                    downloadLink.getElement().setAttribute("download", true);
                    downloadLink.getElement().setAttribute("style", "display: none;");
                    getContent().add(downloadLink);
                    downloadLink.getElement().callJsFunction("click");
                    downloadLink.remove();
                }
            });

            layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.getStyle().set("flex-grow", "1");
            layoutRow.setAlignItems(FlexComponent.Alignment.START);
            layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            buttonGuardar.setText("Guardar");
            buttonGuardar.addClickShortcut(Key.ENTER);
            buttonGuardar.addClickListener(e -> ActualizarProyecto(proyectoAux));
            buttonGuardar.setWidth("min-content");
            buttonGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonSecondary.setText("Cancelar");
            layoutRow.setAlignSelf(FlexComponent.Alignment.START, btncancelar);
            buttonSecondary.setWidth("min-content");
            getContent().add(layoutColumn2);
            layoutColumn2.add(h3);
            layoutColumn2.add(formLayout2Col);
            formLayout2Col.add(emailField);
            formLayout2Col.add(promotor);
            formLayout2Col.add(nombre);
            formLayout2Col.add(descripcion);
            formLayout2Col.add(interesados);
            formLayout2Col.add(alcance);
            formLayout2Col.add(aportacionInicial);
            formLayout2Col.add(coste);
            formLayout2Col.add(fechaLimite);
            formLayout2Col.add(upload);

            buttonGuardar.addClickListener(eventG -> {

                Notification notification = Notification.show("Los datos del proyecto han sido actualizados.");

                notification.setDuration(2000);
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate("estadomisproyectos");
                });
            });

            Button Borrar = new Button("Borrar Proyecto", eventy -> {
                if(proyectoAux.getEstado() != Proyecto.Estado.solicitado){
                    proyectoService.delete(proyectoAux.getId());
                    UI.getCurrent().navigate(ProyectosView.class);
                }else{
                    Notification.show("Este proyecto ya no se puede eliminar");
                }
            });
            Borrar.addClassName("button-danger");
            Borrar.addThemeVariants(ButtonVariant.LUMO_ERROR);

            layoutColumn2.add(layoutRow);
            layoutRow.add(Borrar);
            layoutRow.add(downloadButton);
            layoutRow.add(buttonGuardar);
            layoutRow.add(btncancelar);
            binder.bindInstanceFields(this);
            binder.setBean(proyectoAux);
        }
    }
    private void ActualizarProyecto(Proyecto proyectoAux) {
        if(proyectoAux.getEstado() != Proyecto.Estado.denegado) proyectoService.update(proyectoAux);
        else Notification.show("Este proyecto no se puede editar");
    }
}
