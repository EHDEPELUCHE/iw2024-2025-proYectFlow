package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.*;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.proyecto.InfoProyecto;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.proyecto.Proyecto.Estado;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.checkbox.Checkbox;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


/**
 * La clase GradoAvanceView representa una vista para mostrar y actualizar el progreso de un proyecto.
 * Extiende Composite<VerticalLayout> e implementa HasUrlParameter<String> para manejar parámetros de URL.
 * 
 * Anotaciones:
 * - @PageTitle: Establece el título de la página como "Grado de avance".
 * - @Route: Define la ruta para acceder a esta vista como "GradoAvance".
 * - @Menu: Añade esta vista al menú con orden 1 y un icono.
 * - @RolesAllowed: Restringe el acceso a usuarios con el rol "ROLE_OTP".
 * 
 * Campos:
 * - proyecto: Un Optional<Proyecto> que representa el proyecto.
 * - proyectoService: Un servicio para gestionar proyectos.
 * - uuid: Un UUID que representa el identificador único del proyecto.
 * 
 * Constructor:
 * - GradoAvanceView(ProyectoService proyectoService): Inicializa la vista con el ProyectoService dado.
 * 
 * Métodos:
 * - setParameter(BeforeEvent event, String parameter): Maneja el parámetro de URL para obtener y mostrar los detalles del proyecto.
 *   - Si el parámetro es válido, obtiene el proyecto usando el ProyectoService.
 *   - Si el proyecto no se encuentra, muestra un mensaje de error.
 *   - Si el proyecto se encuentra, muestra los detalles del proyecto, la barra de progreso y permite actualizar el progreso.
 * 
 * Componentes:
 * - H1: Muestra un mensaje de error si el proyecto no se encuentra.
 * - InfoProyecto: Muestra la información del proyecto.
 * - H2: Muestra el título "Grado de avance del proyecto".
 * - ProgressBar: Muestra el progreso actual del proyecto.
 * - NativeLabel: Muestra el porcentaje de progreso actual.
 * - H3: Muestra el título "Actualizar el grado de avance del proyecto".
 * - HorizontalLayout: Contiene el BigDecimalField para actualizar el progreso.
 * - BigDecimalField: Permite al usuario ingresar el nuevo valor de progreso.
 * - Button: Permite al usuario actualizar el progreso o navegar de vuelta a la lista de proyectos.
 * - Notification: Muestra mensajes de éxito o error.
 * 
 * Uso:
 * - Esta vista se utiliza para mostrar y actualizar el progreso de un proyecto.
 * - Es accesible a través de la ruta "GradoAvance" y requiere el rol "ROLE_OTP".
 */
@PageTitle("Grado de avance")
@Route("GradoAvance")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_OTP")
public class GradoAvanceView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    final ConvocatoriaService convocatoriaService;
    UUID uuid;

    public GradoAvanceView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
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

        if (proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        } else {
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();

            InfoProyecto infoProyecto = new InfoProyecto(proyectoService, proyectoAux);
            getContent().add(infoProyecto);

            getContent().add(new H2("Grado de avance del proyecto"));

            ProgressBar progressBar = new ProgressBar();
            progressBar.setValue(proyectoAux.getGradoAvance()/100);
            NativeLabel progressBarLabelText = new NativeLabel("Porcentage Actual: " + proyectoAux.getGradoAvance() + "%");
            progressBarLabelText.setId("gAvance");
            // Associates the label with the progressbar for screen readers:
            progressBar.getElement().setAttribute("aria-labelledby", "gAvance");
            getContent().add(progressBar);
            //Valoracion
            getContent().add(new H3("Actualizar el grado de avance del proyecto"));
            HorizontalLayout valoracionLayout = new HorizontalLayout();
            valoracionLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            BigDecimalField avance = new BigDecimalField();
            avance.setLabel("Grado de avance");
            avance.setValue(BigDecimal.valueOf(proyectoAux.getGradoAvance()));
            valoracionLayout.add(avance);
            getContent().add(valoracionLayout);

            // Botón actualizar
            Button actualizar = new Button("Actualizar");
            actualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            actualizar.addClickShortcut(Key.ENTER);
            actualizar.addClickListener(e -> {
                if(avance.getValue() == avance.getEmptyValue()) {
                    Notification.show("No se ha introducido un grado de avance válido.");
                    return;
                }
                Double valorSeleccionado = avance.getValue().doubleValue();
                if(valorSeleccionado < 0 || valorSeleccionado > 100) {
                    Notification.show("El grado de avance debe estar entre 0 y 100.");
                    return;
                }else{
                    proyectoAux.setGradoAvance(valorSeleccionado);
                    proyectoService.update(proyectoAux);
                    Notification.show("Grado de avance actualizado correctamente", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    UI.getCurrent().refreshCurrentRoute(isAttached());
                }
               
            });
            if(proyectoAux.getGradoAvance() == 100) {
                Convocatoria convocatoria = proyectoAux.getConvocatoria();
                actualizar.setEnabled(false);
                Checkbox finalizado = new Checkbox("Proyecto finalizado");
                getContent().add(finalizado);
                Button confirmar = new Button("Confirmar finalización");
                confirmar.addClickListener(e -> {
                    if(finalizado.getValue().booleanValue()){
                        proyectoAux.setEstado(Estado.FINALIZADO);
                        //Liberamos rec humanos
                        convocatoria.setRecHumanosDisponibles(convocatoria.getRecHumanosDisponibles() + proyectoAux.getRecHumanos());
                        convocatoriaService.guardar(convocatoria);
                        proyectoService.update(proyectoAux);
                        Notification.show("Proyecto finalizado correctamente", 1000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }else{
                        proyectoAux.setGradoAvance(99.0);
                        proyectoService.update(proyectoAux);           
                    }
                   UI.getCurrent().refreshCurrentRoute(isAttached());
                });
                getContent().add(confirmar);
            }
            Button volver = new Button("Volver");
            volver.addClickListener(e -> UI.getCurrent().navigate("proyectosjefe"));
            HorizontalLayout buttonLayout = new HorizontalLayout(actualizar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            buttonLayout.add(volver);
            getContent().add(buttonLayout);
        }
    }
}