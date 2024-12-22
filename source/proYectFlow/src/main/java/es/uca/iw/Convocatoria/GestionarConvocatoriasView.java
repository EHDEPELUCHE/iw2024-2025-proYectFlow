package es.uca.iw.Convocatoria;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
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

import java.util.Date;
import java.time.ZoneId;

@Route("GestionarConvocatorias")
@PageTitle("Gestionar Convocatorias")
@Menu(order = 1, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class GestionarConvocatoriasView extends Composite<VerticalLayout> {
    ConvocatoriaService convocatoriaservice;
    BigDecimalField presupuestototal = new BigDecimalField("Presupuesto");
    DatePicker fecha_inicio = new DatePicker();
    DatePicker fecha_limite = new DatePicker();
    DatePicker fecha_final = new DatePicker();

    private final BeanValidationBinder<Convocatoria> binder = new BeanValidationBinder<>(Convocatoria.class);
    public GestionarConvocatoriasView(ConvocatoriaService convocatoriaservice) {
        this.convocatoriaservice = convocatoriaservice;
        Convocatoria convocatoria = convocatoriaservice.ConvocatoriaActual();

        H1 title = new H1("Gestionar siguiente convocatoria");


        presupuestototal.setLabel("Presupuesto");
        presupuestototal.setRequiredIndicatorVisible(true);
        if (convocatoria != null) {
            presupuestototal.setValue(convocatoria.getPresupuestototal());
        }



        if (convocatoria != null) {
            fecha_inicio.setValue(convocatoria.getFecha_inicio().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        fecha_inicio.setRequiredIndicatorVisible(true);
        fecha_inicio.setLabel("Fecha de inicio de la convocatoria");


        fecha_limite.setLabel("Fecha limite para presentar proyectos");
        fecha_limite.setRequiredIndicatorVisible(true);
        if (convocatoria != null) {
            fecha_limite.setValue(convocatoria.getFecha_limite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }



        fecha_final.setLabel("Fecha en la que termina la cartera de proyectos este año ");
        fecha_final.setRequiredIndicatorVisible(true);
        if (convocatoria != null) {
            fecha_final.setValue(convocatoria.getFecha_final().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }


        Button Guardar = new Button("Hacerla vigente");
        Guardar.addClickListener(e -> {
            Convocatoria convocatoriaActual;
            if (convocatoria != null) {
                convocatoriaActual = binder.getBean();

            }else{
                convocatoriaActual = new Convocatoria(
                        presupuestototal.getValue(),
                        Date.from(fecha_limite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(fecha_inicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(fecha_final.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );


            }
            convocatoriaservice.hacerVigente(convocatoriaActual, convocatoria);
            Notification.show("Datos actualizados correctamente");



        });
        FormLayout formLayout = new FormLayout();
        formLayout.add(presupuestototal, fecha_inicio, fecha_limite, fecha_final);
        if (convocatoria != null) {
            binder.bindInstanceFields(this);
            binder.setBean(convocatoria);

        }

        getContent().add(title, formLayout, Guardar);

    }
}
