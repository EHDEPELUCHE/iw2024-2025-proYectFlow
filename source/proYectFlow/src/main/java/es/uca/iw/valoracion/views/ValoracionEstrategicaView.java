package es.uca.iw.valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.*;
import es.uca.iw.proyecto.AlineamientoEstrategico;
import es.uca.iw.proyecto.AlineamientoEstrategicoService;
import es.uca.iw.proyecto.InfoProyecto;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Vista para la valoración estratégica de proyectos por parte del CIO.
 * 
 * <p>Esta vista permite al CIO evaluar un proyecto específico y asignarle una valoración estratégica.
 * Además, permite seleccionar alineamientos estratégicos relevantes para el proyecto.</p>
 * 
 * <p>La vista se compone de los siguientes elementos:</p>
 * <ul>
 *   <li>Un título que indica si ha ocurrido un error al cargar el proyecto.</li>
 *   <li>Información detallada del proyecto.</li>
 *   <li>Un grupo de casillas de verificación para seleccionar alineamientos estratégicos.</li>
 *   <li>Un grupo de botones de radio para asignar una valoración estratégica al proyecto.</li>
 *   <li>Un botón para guardar la valoración y los alineamientos seleccionados.</li>
 * </ul>
 * 
 * <p>La vista está protegida por roles y solo es accesible para usuarios con el rol "ROLE_CIO".</p>
 * 
 * @param proyectoService Servicio para gestionar proyectos.
 * @param alineamientoEstrategicoService Servicio para gestionar alineamientos estratégicos.
 */
@PageTitle("Valoración CIO")
@Route("ValoracionEstrategica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_CIO")
public class ValoracionEstrategicaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    final AlineamientoEstrategicoService alineamientoEstrategicoService;
    UUID uuid;

    public ValoracionEstrategicaView(ProyectoService proyectoService, AlineamientoEstrategicoService alineamientoEstrategicoService) {
        this.proyectoService = proyectoService;
        this.alineamientoEstrategicoService = alineamientoEstrategicoService;
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
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();
            InfoProyecto infoProyecto = new InfoProyecto(proyectoService, proyectoAux);
            getContent().add(infoProyecto);

            //CheckBox
            getContent().add(new H2("Alineamientos estratégicos a contemplar"));
            CheckboxGroup<AlineamientoEstrategico> objetivos = new CheckboxGroup<>();
            objetivos.setLabel("Alineamientos estratégicos");
            objetivos.setItems(alineamientoEstrategicoService.findAllActivos());
            objetivos.setItemLabelGenerator(alineamientoEstrategico -> alineamientoEstrategico.getObjetivo());
            objetivos.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

            getContent().add(objetivos);

            //Valoracion
            getContent().add(new H3("Añade una valoración estratégica al proyecto según su criterio:"));
            HorizontalLayout valoracionLayout = new HorizontalLayout();
            valoracionLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroup = new RadioButtonGroup<>();
            valoracionGroup.setLabel("Selecciona tu valoración:");
            valoracionGroup.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroup.setValue(0); // Valor por defecto

            valoracionLayout.add(valoracionGroup);
            getContent().add(valoracionLayout);
            
            // Boton guardar
            Button guardar = new Button("Guardar");
            guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guardar.addClickShortcut(Key.ENTER);
            guardar.addClickListener(e -> {
                Integer valorSeleccionado = valoracionGroup.getValue();
                if (valorSeleccionado == null || valorSeleccionado < 0 || valorSeleccionado > 10)
                    Notification.show("Por favor, selecciona una valoración válida entre 0 y 10.");
                else {
                    List<AlineamientoEstrategico> alineamientos = new ArrayList<>(objetivos.getValue());
                    
                    // Ensure all AlineamientoEstrategico objects have valid IDs
                    boolean allValid = alineamientos.stream().allMatch(ae -> ae.getId() != null && alineamientoEstrategicoService.findById(ae.getId())!=null);
                    if (allValid) {
                        proyectoService.setValoracionEstrategica(BigDecimal.valueOf(valorSeleccionado), proyectoAux);
                        Notification notification = Notification.show("Valoración guardada con éxito.");
                        notification.setDuration(2000);
                    notification.addDetachListener(detachEvent -> UI.getCurrent().navigate("proyectosCIO"));
                    } else
                        Notification.show("Error: Algunos alineamientos estratégicos no son válidos.");
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
        }
    }
}