package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.global.DownloadPdfComponent;
import es.uca.iw.global.Roles;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import java.io.IOException;
import java.sql.Blob;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

public class EditarProyectosBaseView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    static final String MIN_CONTENT = "min-content";
    final ProyectoService proyectoService;
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
    private final BeanValidationBinder<Proyecto> binder = new BeanValidationBinder<>(Proyecto.class);
    Optional<Proyecto> proyecto;
    UUID uuid;

    public EditarProyectosBaseView(ProyectoService proyectoService, UsuarioService usuarioService, AuthenticatedUser authenticatedUser) {
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
        } else 
            this.proyecto = Optional.empty();

        if (proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        } else {
            Proyecto proyectoAux = proyecto.get();

            getContent().add(new H1("Detalles del Proyecto"));
            VerticalLayout layoutColumn2 = new VerticalLayout();
            H3 h3 = new H3();
            FormLayout formLayout2Col = new FormLayout();

            HorizontalLayout layoutRow = new HorizontalLayout();
            Button guardarButton = new Button();
            getContent().setWidth("100%");
            getContent().getStyle().set("flex-grow", "1");
            getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
            getContent().setAlignItems(FlexComponent.Alignment.CENTER);
            layoutColumn2.setWidth("100%");
            layoutColumn2.setMaxWidth("800px");
            layoutColumn2.setHeight(MIN_CONTENT);
            h3.setText("Editar datos del proyecto");
            h3.setWidth("100%");
            formLayout2Col.setWidth("100%");

            setupFields(proyectoAux);

            layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.getStyle().set("flex-grow", "1");
            layoutRow.setAlignItems(FlexComponent.Alignment.START);
            layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            guardarButton.setText("Guardar");
            guardarButton.addClickShortcut(Key.ENTER);
            guardarButton.addClickListener(e -> actualizarProyecto(proyectoAux));
            guardarButton.setWidth(MIN_CONTENT);
            guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            layoutColumn2.add(h3);
            layoutColumn2.add(formLayout2Col);
            formLayout2Col.add(emailField, promotor, nombre, descripcion, interesados, alcance, aportacionInicial, coste, fechaLimite, upload);

            Button cancelarButton = new Button("Volver");
            authenticatedUser.get().ifPresent(user -> {
                if (user.getTipo() == Roles.ADMIN)
                    cancelarButton.addClickListener(e -> UI.getCurrent().navigate(ProyectosView.class));
                else
                    cancelarButton.addClickListener(e -> UI.getCurrent().navigate(EstadoProyectosView.class));
            });

            layoutRow.setAlignSelf(FlexComponent.Alignment.START, cancelarButton);

            Button borrarProyectoButton = new Button("Borrar Proyecto", eventy -> {
                if (proyectoAux.getEstado() == Proyecto.Estado.DENEGADO || 
                    (proyectoAux.getEstado() == Proyecto.Estado.SOLICITADO && proyectoAux.getPromotor() == null)) 
                {
                    proyectoService.delete(proyectoAux.getId());
                    authenticatedUser.get().ifPresent(user -> {
                        if (user.getTipo() == Roles.ADMIN)
                            UI.getCurrent().navigate(ProyectosView.class);
                        else
                            UI.getCurrent().navigate(EstadoProyectosView.class);
                    });
                } else
                    Notification.show("Este proyecto aún no se puede eliminar");
            });

            borrarProyectoButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button downloadButton = DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                try {
                    return proyectoService.getPdf(proyecto.get().getId());
                } catch (IOException ex) {
                    throw new RuntimeException("Error al obtener el PDF", ex);
                }
            });

            layoutRow.add(borrarProyectoButton, downloadButton, guardarButton, cancelarButton);
            layoutColumn2.add(layoutRow);
            getContent().add(layoutColumn2);
            binder.bindInstanceFields(this);
            binder.setBean(proyectoAux);
        }
    }

    void setupFields(Proyecto proyectoAux) {
        emailField.setLabel("Solicitante");
        emailField.setValue(proyectoAux.getSolicitante().getCorreo());
        emailField.setWidth(MIN_CONTENT);
        emailField.setReadOnly(true);

        promotor.setLabel("Promotor");
        promotor.setWidth(MIN_CONTENT);
        promotor.setItems(usuarioService.get(Roles.PROMOTOR));
        promotor.setItemLabelGenerator(usuario -> usuario.getNombre() + " " + usuario.getApellido());
        promotor.setValue(proyectoAux.getPromotor());

        nombre.setLabel("Nombre del proyecto");
        nombre.setWidth(MIN_CONTENT);
        nombre.setValue(proyectoAux.getNombre());

        alcance.setLabel("Alcance");
        alcance.setWidth(MIN_CONTENT);
        alcance.setValue(proyectoAux.getAlcance());

        descripcion.setLabel("Descripción");
        descripcion.setWidth(MIN_CONTENT);
        descripcion.setValue(proyectoAux.getDescripcion());

        interesados.setLabel("Interesados");
        interesados.setWidth(MIN_CONTENT);
        interesados.setValue(proyectoAux.getInteresados());

        aportacionInicial.setLabel("Financiación aportada en €");
        aportacionInicial.setWidth(MIN_CONTENT);
        aportacionInicial.setValue(proyectoAux.getAportacionInicial());

        coste.setLabel("Coste Total en €");
        coste.setWidth(MIN_CONTENT);
        coste.setValue(proyectoAux.getCoste());

        fechaLimite.setPlaceholder("Añadir solo si el proyecto se realiza para cumplir con alguna ley próxima a entrar en vigor");
        fechaLimite.setAriaLabel("Añadir solo si el proyecto se realiza para cumplir con alguna ley próxima a entrar en vigor");
        if (proyectoAux.getFechaLimite() != null) 
            fechaLimite.setValue(proyectoAux.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFileSize(10 * 1024 * 1024);

        Button uploadButton = new Button("Añadir memoria");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(event -> {
            try {
                byte[] bytes = buffer.getInputStream().readAllBytes();
                
                Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                proyectoAux.setMemoria(blob);
                if (proyectoAux.getMemoria() != null)
                    Notification.show("Archivo subido correctamente: " + event.getFileName());
                else
                    Notification.show("Error: No se pudo guardar el archivo.");
            } catch (Exception ex) {
                Notification.show("Error al subir el archivo: " + ex.getMessage());
            }
        });
        Paragraph hint = new Paragraph();
        hint.getElement().setText("Tamaño máximo permitido: 10 MB\nTipo de archivo permitido: .pdf");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)");
        upload.setDropLabel(hint);  
    }

    private void actualizarProyecto(Proyecto proyectoAux) {
        if(proyectoAux.getAportacionInicial().compareTo(proyectoAux.getCoste()) > 0)
            Notification.show("La aportación inicial no puede ser mayor que el coste total");
        else if(proyectoAux.getFechaLimite().compareTo(proyectoAux.getFechaSolicitud()) <= 0)
            Notification.show("La fecha límite debe ser posterior a la fecha de solicitud");
        else{
            proyectoService.update(proyectoAux);
            Notification.show("Proyecto actualizado");
        }
    }
}