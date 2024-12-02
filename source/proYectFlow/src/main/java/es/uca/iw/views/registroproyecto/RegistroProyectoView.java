package es.uca.iw.views.registroproyecto;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import es.uca.iw.components.pricefield.PriceField;
import es.uca.iw.data.Proyecto;
import es.uca.iw.data.Roles;
import es.uca.iw.data.Usuario;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.services.ProyectoService;
import es.uca.iw.services.UsuarioService;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.util.*;

@PageTitle("Registro Proyecto")
@Route("registro-proyecto")
@Menu(order = 2, icon = "line-awesome/svg/egg-solid.svg")
@PermitAll
public class RegistroProyectoView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Proyecto> binder;
    UsuarioService usuarioService;
    AuthenticatedUser authenticatedUser;
    ProyectoService proyectoService;
    EmailField emailField = new EmailField();
    ComboBox<Usuario> promotor = new ComboBox<>();
    TextField nombre = new TextField();
    TextField descripcion = new TextField();
    TextField interesados = new TextField();
    TextField alcance = new TextField();
    DatePicker fecha = new DatePicker("fecha límite");

    //NumberField numberField = new NumberField();
    BigDecimalField aportacionInicial = new BigDecimalField();
    BigDecimalField coste = new BigDecimalField();
    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);
    public RegistroProyectoView(UsuarioService usuarioService, AuthenticatedUser authenticatedUser, ProyectoService proyectoService) {
        this.usuarioService = usuarioService;
        this.authenticatedUser = authenticatedUser;
        this.proyectoService = proyectoService;
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
        nombre.setLabel("Nombre del proyecto");
        nombre.setWidth("min-content");
        descripcion.setLabel("Descripción");
        descripcion.setWidth("min-content");
        interesados.setLabel("Interesados");
        interesados.setWidth("min-content");
        alcance.setLabel("Alcance");
        alcance.setWidth("min-content");
        //numberField.setLabel("Memoria del proyecto");
        //numberField.setWidth("min-content");
        aportacionInicial.setLabel("Financiación aportada en €");
        aportacionInicial.setWidth("min-content");
        coste.setLabel("Coste Total en €");
        coste.setWidth("min-content");
        fecha.setPlaceholder("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entar en vigor");
        fecha.setAriaLabel("Añadir solo si el proyecto se realiza para cumplimentar alguna ley próxima a entar en vigor");

        upload.setAcceptedFileTypes("application/pdf", ".pdf");

        Button uploadButton = new Button("Upload PDF...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        upload.setUploadButton(uploadButton);

        // Disable the upload button after the file is selected
        // Re-enable the upload button after the file is cleared
        upload.getElement()
                .addEventListener("max-files-reached-changed", event -> {
                    boolean maxFilesReached = event.getEventData()
                            .getBoolean("event.detail.value");
                    uploadButton.setEnabled(!maxFilesReached);
                }).addEventData("event.detail.value");


        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.setAlignItems(Alignment.START);
        layoutRow.setJustifyContentMode(JustifyContentMode.END);
        buttonPrimary.setText("Continuar");
        buttonPrimary.addClickShortcut(Key.ENTER);
        buttonPrimary.addClickListener(e -> OnRegistroProyecto());
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancelar");
        layoutRow.setAlignSelf(FlexComponent.Alignment.START, buttonSecondary);
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
        //formLayout2Col.add(numberField);
        formLayout2Col.add(aportacionInicial);
        formLayout2Col.add(coste);
        formLayout2Col.add(fecha);
        formLayout2Col.add(upload);

        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimary);
        layoutRow.add(buttonSecondary);
        binder = new BeanValidationBinder<>(Proyecto.class);
        binder.bindInstanceFields(this);
    }

    public void OnRegistroProyecto() {
        binder.setBean(new Proyecto(nombre.getValue(), descripcion.getValue(),interesados.getValue(),
                alcance.getValue(),coste.getValue(), aportacionInicial.getValue(),usuarioService.get(emailField.getValue()), promotor.getValue()));

        Notification.show(binder.getBean().toString());

        if (binder.validate().isOk()) {

            if (proyectoService.registerProyecto(binder.getBean())) {
                //status.setText("Excelente. ¡Por favor mira tu bandeja de entrada de correo!");
                //status.setVisible(true);
                binder.setBean(new Proyecto());
                Notification.show("Proyecto registrado correctamente.");

            } else {
                Notification.show("El proyecto tiene datos incorrectos");
            }
        } else {
            Notification.show("Por favor, verifique los datos de entrada");
        }
    }


}
