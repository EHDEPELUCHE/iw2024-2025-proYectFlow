package es.uca.iw.convocatoria;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;

import jakarta.annotation.security.RolesAllowed;

import java.util.Date;
import java.time.ZoneId;
import java.util.UUID;

/**
 * La clase EditarConvocatoriaView representa una vista para editar una Convocatoria existente.
 * Extiende Composite<VerticalLayout> e implementa HasUrlParameter<String> para manejar parámetros de URL.
 * 
 * Anotaciones:
 * - @Route("EditarConvocatoria"): Define la ruta para esta vista.
 * - @PageTitle("Editar Convocatoria"): Establece el título de la página.
 * - @RolesAllowed("ROLE_ADMIN"): Restringe el acceso a usuarios con el rol ROLE_ADMIN.
 * 
 * Campos:
 * - convocatoriaService: Servicio para manejar operaciones de Convocatoria.
 * - convocatoria: La entidad Convocatoria que se está editando.
 * - presupuestototal: BigDecimalField para ingresar el presupuesto total.
 * - fechaInicio: DatePicker para seleccionar la fecha de inicio de la Convocatoria.
 * - fechaLimite: DatePicker para seleccionar la fecha límite para la presentación de proyectos.
 * - fechaFinal: DatePicker para seleccionar la fecha de finalización de la cartera de proyectos para el año.
 * - binder: BeanValidationBinder para vincular los campos de Convocatoria al formulario.
 * 
 * Constructor:
 * - EditarConvocatoriaView(ConvocatoriaService convocatoriaService): Inicializa la vista con el ConvocatoriaService dado.
 * 
 * Métodos:
 * - setParameter(BeforeEvent event, String parameter): Establece la Convocatoria a editar basada en el parámetro de URL.
 * 
 * La vista incluye un diseño de formulario con campos para editar la Convocatoria y un botón para guardar los cambios.
 * Cuando se hace clic en el botón de guardar, la Convocatoria se actualiza y guarda utilizando el ConvocatoriaService.
 */
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
    final IntegerField recHumanosDisponibles = new IntegerField("Recursos humanos disponibles");


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

        recHumanosDisponibles.setLabel("Recursos humanos disponibles (Trabajadores)");
        recHumanosDisponibles.setRequiredIndicatorVisible(true);

        Button guardarButton = new Button("Guardar cambios");
        guardarButton.addClickShortcut(Key.ENTER);
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        guardarButton.addClickListener(e -> {
            if (convocatoria != null) {
                convocatoria.setPresupuestototal(presupuestototal.getValue());
                convocatoria.setFechaInicio(Date.from(fechaInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFechaLimite(Date.from(fechaLimite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setFechaFinal(Date.from(fechaFinal.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                convocatoria.setRecHumanosDisponibles(recHumanosDisponibles.getValue());
                convocatoriaService.guardar(convocatoria);

                Notification.show("Convocatoria actualizada correctamente");
            } else {
                Notification.show("Error: No se pudo encontrar la convocatoria para actualizar.");
            }
        });

        FormLayout formLayout = new FormLayout(presupuestototal, fechaInicio, fechaLimite, fechaFinal, recHumanosDisponibles);
        Button Volver = new Button("Volver", e -> getUI().ifPresent(ui -> ui.navigate("GestionarConvocatorias")));
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(guardarButton, Volver);
        getContent().add(title, formLayout, layout);
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
            recHumanosDisponibles.setValue(convocatoria.getRecHumanosDisponibles());
            binder.setBean(convocatoria);
        } else {
            Notification.show("Convocatoria no encontrada");
        }
    }
}

