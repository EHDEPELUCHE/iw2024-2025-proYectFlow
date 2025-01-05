package es.uca.iw.convocatoria;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

import java.util.Date;
import java.time.ZoneId;
import java.util.UUID;

@Route("EditarConvocatoria")
@PageTitle("Editar Convocatoria")
@RolesAllowed("ROLE_ADMIN")

public class EditarConvocatoriaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    private final ConvocatoriaService convocatoriaService;
    private Convocatoria convocatoria; // Convocatoria a editar

    final BigDecimalField presupuestototal = new BigDecimalField("Presupuesto");
    final DatePicker fechaInicio = new DatePicker();
    final DatePicker fechaLimite = new DatePicker();
    final DatePicker fechaFinal = new DatePicker();

    private final BeanValidationBinder<Convocatoria> binder = new BeanValidationBinder<>(Convocatoria.class);

    public EditarConvocatoriaView(ConvocatoriaService convocatoriaService) {
        this.convocatoriaService = convocatoriaService;

        H1 title = new H1("Editar Convocatoria");

        presupuestototal.setLabel("Presupuesto");
        presupuestototal.setRequiredIndicatorVisible(true);

        fechaInicio.setLabel("Fecha de inicio de la convocatoria");
        fechaInicio.setRequiredIndicatorVisible(true);

        fechaLimite.setLabel("Fecha límite para presentar proyectos");
        fechaLimite.setRequiredIndicatorVisible(true);

        fechaFinal.setLabel("Fecha en la que termina la cartera de proyectos este año");
        fechaFinal.setRequiredIndicatorVisible(true);

        Button guardarButton = new Button("Guardar cambios");
        guardarButton.addClickShortcut(Key.ENTER);
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        guardarButton.addClickListener(e -> {
            if (convocatoria != null) {
                convocatoria.setPresupuestototal(presupuestototal.getValue());
                convocatoria.setFechaInicio(Date.from(fechaInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFechaLimite(Date.from(fechaLimite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFechaFinal(Date.from(fechaFinal.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                convocatoriaService.guardar(convocatoria);

                Notification.show("Convocatoria actualizada correctamente");
            } else {
                Notification.show("Error: No se pudo encontrar la convocatoria para actualizar.");
            }
        });

        FormLayout formLayout = new FormLayout(presupuestototal, fechaInicio, fechaLimite, fechaFinal);
        getContent().add(title, formLayout, guardarButton);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        UUID id = UUID.fromString(parameter);
        convocatoria = convocatoriaService.findById(id);

        if (convocatoria != null) {
            presupuestototal.setValue(convocatoria.getPresupuestototal());
            fechaInicio.setValue(convocatoria.getFechaInicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            fechaLimite.setValue(convocatoria.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            fechaFinal.setValue(convocatoria.getFechaFinal().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            binder.setBean(convocatoria);
        } else {
            Notification.show("Convocatoria no encontrada");
        }
    }
}

