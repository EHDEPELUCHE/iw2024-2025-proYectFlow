package es.uca.iw.proyecto.views.registrarproyecto;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import es.uca.iw.global.Roles;
import es.uca.iw.global.views.PantallaInicioView;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;

import java.sql.Blob;
import java.util.Optional;

@PageTitle("Registro Proyecto")
@Route("registro-proyecto")
@Menu(order = 2, icon = "line-awesome/svg/egg-solid.svg")
@PermitAll
public class RegistroProyectoView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Proyecto> binder;
    final UsuarioService usuarioService;
    final AuthenticatedUser authenticatedUser;
    final ProyectoService proyectoService;
    static final String MIN_CONTENT = "min-content";
    EmailField emailField = new EmailField();
    ComboBox<Usuario> promotor = new ComboBox<>();
    TextField nombre = new TextField();
    TextField descripcion = new TextField();
    TextField alcance = new TextField();
    final DatePicker fechaLimite = new DatePicker("fecha límite");
    TextField interesados = new TextField("Interesados");
    BigDecimalField aportacionInicial = new BigDecimalField();
    BigDecimalField coste = new BigDecimalField();
    final MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);
    final ConvocatoriaService convocatoriaService;

    public RegistroProyectoView(UsuarioService usuarioService, AuthenticatedUser authenticatedUser, ProyectoService proyectoService
                                , ConvocatoriaService convocatoriaService) {
        this.usuarioService = usuarioService;
        this.authenticatedUser = authenticatedUser;
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
        binder = new BeanValidationBinder<>(Proyecto.class);

        if(convocatoriaService.convocatoriaActual() != null && convocatoriaService.convocatoriaActual().enPlazo()){
            VerticalLayout layoutColumn2 = new VerticalLayout();
            H3 h3 = new H3();
            FormLayout formLayout2Col = new FormLayout();
            Optional<Usuario> solicitante = authenticatedUser.get();
            HorizontalLayout layoutRow = new HorizontalLayout();
            Button buttonPrimary = new Button();
            Button buttonSecondary = new Button();
            getContent().setWidth("100%");
            getContent().getStyle().set("flex-grow", "1");
            getContent().setJustifyContentMode(JustifyContentMode.START);
            getContent().setAlignItems(Alignment.CENTER);
            layoutColumn2.setWidth("100%");
            layoutColumn2.setMaxWidth("800px");
            layoutColumn2.setHeight(MIN_CONTENT);
            h3.setText("Registro de proyecto");
            h3.setWidth("100%");
            formLayout2Col.setWidth("100%");
            emailField.setLabel("Solicitante");
            if (solicitante.isPresent()) emailField.setValue(solicitante.get().getCorreo());
            else emailField.setValue("No hay solicitante");
            emailField.setWidth(MIN_CONTENT);
            promotor.setLabel("Promotor");
            promotor.setWidth(MIN_CONTENT);

            promotor.setItems(usuarioService.get(Roles.PROMOTOR));
            promotor.setItemLabelGenerator(Usuario::getNombre);

            nombre.setLabel("Nombre del proyecto");
            nombre.setWidth(MIN_CONTENT);

            alcance.setLabel("Alcance");
            alcance.setWidth(MIN_CONTENT);

            descripcion.setLabel("Descripción");
            descripcion.setWidth(MIN_CONTENT);

            interesados.setLabel("Interesados");
            interesados.setWidth(MIN_CONTENT);
            interesados.setAriaLabel("Añadir interesados");

            aportacionInicial.setLabel("Financiación aportada en €");
            aportacionInicial.setWidth(MIN_CONTENT);

            coste.setLabel("Coste Total en €");
            coste.setWidth(MIN_CONTENT);
            fechaLimite.setPlaceholder("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entrar en vigor");
            fechaLimite.setAriaLabel("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entrar en vigor");

            upload.setAcceptedFileTypes("application/pdf", ".pdf");

            Button uploadButton = new Button("Añadir memoria");
            uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            upload.setUploadButton(uploadButton);
            upload.addFailedListener(event -> {
                if (event.getReason().getMessage().contains("Maximum upload size exceeded")) {
                    Notification.show("El tamaño máximo del archivo es de 20MB");
                }
            });
             Paragraph hint = new Paragraph("Maximum file size: 10 MB");
            hint.getStyle().set("color", "var(--lumo-secondary-text-color)");
            upload.setDropLabel(hint);
            upload.getElement()
                    .addEventListener("max-files-reached-changed", event -> {
                        boolean maxFilesReached = event.getEventData()
                                .getBoolean("event.detail.value");
                        uploadButton.setEnabled(!maxFilesReached);
                    }).addEventData("event.detail.value");

            Button btncancelar = new Button("Volver", event -> UI.getCurrent().navigate(PantallaInicioView.class));
            btncancelar.addClassName("buttonSecondary");

            layoutRow.addClassName(Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.getStyle().set("flex-grow", "1");
            layoutRow.setAlignItems(Alignment.START);
            layoutRow.setJustifyContentMode(JustifyContentMode.END);
            buttonPrimary.setText("Continuar");
            buttonPrimary.addClickShortcut(Key.ENTER);
            buttonPrimary.addClickListener(e -> onRegistroProyecto());
            buttonPrimary.setWidth(MIN_CONTENT);
            buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonSecondary.setText("Cancelar");
            layoutRow.setAlignSelf(FlexComponent.Alignment.START, btncancelar);
            buttonSecondary.setWidth(MIN_CONTENT);
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

            layoutColumn2.add(layoutRow);
            layoutRow.add(buttonPrimary);
            layoutRow.add(btncancelar);

            binder.bindInstanceFields(this);
            binder.forField(promotor).bind(Proyecto::getPromotor, Proyecto::setPromotor);
        }else{
            getContent().add(new H2("Lo lamentamos, en estos momentos el plazo de solicitudes está cerrado." ));
        }
    }

    public void onRegistroProyecto() {
        Optional<Usuario> usuarioOptional = Optional.ofNullable(usuarioService.getCorreo(emailField.getValue()));

        if (usuarioOptional.isPresent()) {

            Blob pdfBlob = null;
            try {
                pdfBlob = new javax.sql.rowset.serial.SerialBlob(buffer.getInputStream().readAllBytes());
            } catch (Exception ex) {
                Notification.show("Error al procesar el archivo PDF");
            }

            java.sql.Date fechaSql = null;
            if (fechaLimite.getValue() != null) {
                fechaSql = java.sql.Date.valueOf(fechaLimite.getValue());
            }

            binder.setBean(new Proyecto(nombre.getValue(), descripcion.getValue(), interesados.getValue(),
                    alcance.getValue(), coste.getValue(), aportacionInicial.getValue(),
                    promotor.getValue(), usuarioService.getCorreo(emailField.getValue()), fechaSql, pdfBlob));

            if (binder.validate().isOk()) {
                if (proyectoService.registerProyecto(binder.getBean())) {
                    binder.setBean(new Proyecto());
                    Notification.show("Proyecto registrado correctamente.");
                } else {
                    Notification.show("El proyecto tiene datos incorrectos");
                }
            } else {
                Notification.show("Por favor, verifique los datos de entrada");
            }
        } else {
            Notification.show("El usuario no existe");
        }
    }
}
