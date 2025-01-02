package es.uca.iw.Convocatoria;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.ZoneId;
import java.util.Date;

@Route("CrearConvocatoria")
@PageTitle("Nueva Convocatoria")
@Menu(order = 7, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class CrearConvocatoriaView extends Composite<VerticalLayout> {
    private final BeanValidationBinder<Convocatoria> binder = new BeanValidationBinder<>(Convocatoria.class);
    ConvocatoriaService convocatoriaservice;
    BigDecimalField presupuestototal = new BigDecimalField("Presupuesto");
    DatePicker fecha_inicio = new DatePicker();
    DatePicker fecha_limite = new DatePicker();
    DatePicker fecha_final = new DatePicker();

    public CrearConvocatoriaView(ConvocatoriaService convocatoriaservice) {
        this.convocatoriaservice = convocatoriaservice;

        H1 title = new H1("Crear una nueva convocatoria");

        presupuestototal.setLabel("Presupuesto");
        presupuestototal.setRequiredIndicatorVisible(true);

        fecha_inicio.setLabel("Fecha de inicio de la convocatoria");
        fecha_inicio.setRequiredIndicatorVisible(true);

        fecha_limite.setLabel("Fecha limite para presentar proyectos");
        fecha_limite.setRequiredIndicatorVisible(true);

        fecha_final.setLabel("Fecha en la que termina la cartera de proyectos este año ");
        fecha_final.setRequiredIndicatorVisible(true);

        Button guardarButton = new Button("Crear convocatoria");
        guardarButton.addClickShortcut(Key.ENTER);
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.addClickListener(e -> {
            Convocatoria convocatoria = new Convocatoria(
                    presupuestototal.getValue(),
                    Date.from(fecha_limite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(fecha_inicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(fecha_final.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
            );

            convocatoria.setActiva(false);
            convocatoriaservice.guardar(convocatoria);
            Notification.show("Convocatoria creada correctamente");

            Button vigenteButton = new Button("Hacerla vigente");
            vigenteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            vigenteButton.addClickListener(ev -> {
                convocatoriaservice.hacerVigente(convocatoria);
            });
            getContent().add(vigenteButton);
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(presupuestototal, fecha_inicio, fecha_limite, fecha_final);
        getContent().add(title, formLayout, guardarButton);
    }
}
