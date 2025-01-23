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
import java.util.logging.Level;
import java.util.logging.Logger;

@PageTitle("Valoración CIO")
@Route("ValoracionEstrategica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_CIO")
public class ValoracionEstrategicaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    private static final Logger logger = Logger.getLogger(ValoracionEstrategicaView.class.getName());
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
        logger.log(Level.INFO, "Received parameter: {0}", parameter);
        if (parameter != null && !parameter.isEmpty()) {
            try {
                uuid = UUID.fromString(parameter);
                this.proyecto = proyectoService.get(uuid);
                logger.log(Level.INFO, "Project found with UUID: {0}", uuid);
            } catch (IllegalArgumentException e) {
                this.proyecto = Optional.empty();
                logger.log(Level.WARNING, "Invalid UUID: {0}", parameter);
            }
        } else {
            this.proyecto = Optional.empty();
            logger.log(Level.WARNING, "Parameter is empty or null");
        }

        if (proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
            logger.log(Level.SEVERE, "Project not found");
        } else {
            Proyecto proyectoAux = proyecto.get();
            InfoProyecto infoProyecto = new InfoProyecto(proyectoService, proyectoAux);
            getContent().add(infoProyecto);
            logger.log(Level.INFO, "Displaying project information");

            getContent().add(new H2("Alineamientos estratégicos a contemplar"));
            CheckboxGroup<AlineamientoEstrategico> objetivos = new CheckboxGroup<>();
            objetivos.setLabel("Alineamientos estratégicos");
            objetivos.setItems(alineamientoEstrategicoService.findAllActivos());
            objetivos.setItemLabelGenerator(AlineamientoEstrategico::getObjetivo);
            objetivos.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            getContent().add(objetivos);
            logger.log(Level.INFO, "Displaying strategic alignments");

            getContent().add(new H3("Añade una valoración estratégica al proyecto según su criterio:"));
            HorizontalLayout valoracionLayout = new HorizontalLayout();
            valoracionLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroup = new RadioButtonGroup<>();
            valoracionGroup.setLabel("Selecciona tu valoración:");
            valoracionGroup.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroup.setValue(0); // Valor por defecto
            valoracionLayout.add(valoracionGroup);
            getContent().add(valoracionLayout);
            logger.log(Level.INFO, "Displaying valuation options");

            Button guardar = new Button("Guardar");
            guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guardar.addClickShortcut(Key.ENTER);
            guardar.addClickListener(e -> {
                Integer valorSeleccionado = valoracionGroup.getValue();
                if (valorSeleccionado == null || valorSeleccionado < 0 || valorSeleccionado > 10) {
                    Notification.show("Por favor, selecciona una valoración válida entre 0 y 10.");
                    logger.log(Level.WARNING, "Invalid valuation selected: {0}", valorSeleccionado);
                } else {
                    List<AlineamientoEstrategico> alineamientos = new ArrayList<>(objetivos.getValue());
                    boolean allValid = alineamientos.stream().allMatch(ae -> ae.getId() != null && alineamientoEstrategicoService.findById(ae.getId()) != null);
                    if (allValid) {
                        proyectoService.setValoracionEstrategica(BigDecimal.valueOf(valorSeleccionado), proyectoAux);
                        Notification notification = Notification.show("Valoración guardada con éxito.");
                        notification.setDuration(500);
                        notification.addDetachListener(detachEvent -> UI.getCurrent().navigate("proyectosCIO"));
                        logger.log(Level.INFO, "Valuation saved successfully: {0}", valorSeleccionado);
                    } else {
                        Notification.show("Error: Algunos alineamientos estratégicos no son válidos.");
                        logger.log(Level.SEVERE, "Invalid strategic alignments selected");
                    }
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
            logger.log(Level.INFO, "Save button added");
        }
    }
}
