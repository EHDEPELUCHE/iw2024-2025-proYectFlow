package es.uca.iw.Convocatoria;

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

    BigDecimalField presupuestototal = new BigDecimalField("Presupuesto");
    DatePicker fecha_inicio = new DatePicker();
    DatePicker fecha_limite = new DatePicker();
    DatePicker fecha_final = new DatePicker();

    private final BeanValidationBinder<Convocatoria> binder = new BeanValidationBinder<>(Convocatoria.class);

    public EditarConvocatoriaView(ConvocatoriaService convocatoriaService) {
        this.convocatoriaService = convocatoriaService;

        H1 title = new H1("Editar Convocatoria");

        presupuestototal.setLabel("Presupuesto");
        presupuestototal.setRequiredIndicatorVisible(true);

        fecha_inicio.setLabel("Fecha de inicio de la convocatoria");
        fecha_inicio.setRequiredIndicatorVisible(true);

        fecha_limite.setLabel("Fecha límite para presentar proyectos");
        fecha_limite.setRequiredIndicatorVisible(true);

        fecha_final.setLabel("Fecha en la que termina la cartera de proyectos este año");
        fecha_final.setRequiredIndicatorVisible(true);

        Button guardarButton = new Button("Guardar cambios");
        guardarButton.addClickShortcut(Key.ENTER);
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        guardarButton.addClickListener(e -> {
            if (convocatoria != null) {
                // Actualizar el objeto convocatoria con los valores del formulario
                convocatoria.setPresupuestototal(presupuestototal.getValue());
                convocatoria.setFecha_inicio(Date.from(fecha_inicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFecha_limite(Date.from(fecha_limite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFecha_final(Date.from(fecha_final.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                // Guardar los cambios en la base de datos
                convocatoriaService.guardar(convocatoria);

                // Mostrar un mensaje de éxito
                Notification.show("Convocatoria actualizada correctamente");
            } else {
                Notification.show("Error: No se pudo encontrar la convocatoria para actualizar.");
            }
        });

        FormLayout formLayout = new FormLayout(presupuestototal, fecha_inicio, fecha_limite, fecha_final);
        getContent().add(title, formLayout, guardarButton);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        UUID id = UUID.fromString(parameter);
        convocatoria = convocatoriaService.findById(id);

        if (convocatoria != null) {
            presupuestototal.setValue(convocatoria.getPresupuestototal());
            fecha_inicio.setValue(convocatoria.getFecha_inicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            fecha_limite.setValue(convocatoria.getFecha_limite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            fecha_final.setValue(convocatoria.getFecha_final().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            binder.setBean(convocatoria);
        } else {
            Notification.show("Convocatoria no encontrada");
        }
    }
}

