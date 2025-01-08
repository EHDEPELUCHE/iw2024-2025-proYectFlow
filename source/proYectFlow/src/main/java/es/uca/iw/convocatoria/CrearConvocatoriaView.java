package es.uca.iw.convocatoria;

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
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;

import java.time.ZoneId;
import java.util.Date;

@Route("CrearConvocatoria")
@PageTitle("Nueva Convocatoria")
@Menu(order = 7, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class CrearConvocatoriaView extends Composite<VerticalLayout> {
    final ConvocatoriaService convocatoriaservice;
    final BigDecimalField presupuestototal = new BigDecimalField("Presupuesto");
    final DatePicker fechaInicio = new DatePicker();
    final DatePicker fechaLimite = new DatePicker();
    final DatePicker fechaFinal = new DatePicker();

    public CrearConvocatoriaView(ConvocatoriaService convocatoriaservice, UsuarioService usuarioService, ProyectoService proyectoService) {
        this.convocatoriaservice = convocatoriaservice;

        H1 title = new H1("Crear una nueva convocatoria");

        presupuestototal.setLabel("Presupuesto");
        presupuestototal.setRequiredIndicatorVisible(true);

        fechaInicio.setLabel("Fecha de inicio de la convocatoria");
        fechaInicio.setRequiredIndicatorVisible(true);

        fechaLimite.setLabel("Fecha limite para presentar proyectos");
        fechaLimite.setRequiredIndicatorVisible(true);

        fechaFinal.setLabel("Fecha en la que termina la cartera de proyectos este año ");
        fechaFinal.setRequiredIndicatorVisible(true);

        Button guardarButton = new Button("Crear convocatoria");
        guardarButton.addClickShortcut(Key.ENTER);
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.addClickListener(e -> {
            try{
                Convocatoria convocatoria = new Convocatoria(
                    presupuestototal.getValue(),
                    Date.from(fechaLimite.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(fechaInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(fechaFinal.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
                
                convocatoria.setActiva(false);
                convocatoriaservice.guardar(convocatoria);
                Notification.show("Convocatoria creada correctamente");
                presupuestototal.clear();
                fechaInicio.clear();
                fechaLimite.clear();
                fechaFinal.clear();
                getUI().ifPresent(ui -> ui.navigate("GestionarConvocatorias"));
            } catch (Exception ex) {
                Notification.show("Error al crear la convocatoria, revise las fechas");

            }
           
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(presupuestototal, fechaInicio, fechaLimite, fechaFinal);
        getContent().add(title, formLayout, guardarButton);
    }
}
