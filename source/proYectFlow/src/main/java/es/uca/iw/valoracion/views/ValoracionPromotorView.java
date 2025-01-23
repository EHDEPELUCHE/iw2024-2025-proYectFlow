package es.uca.iw.valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import es.uca.iw.proyecto.InfoProyecto;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@PageTitle("Valoración Promotor")
@Route("ValoracionPromotor")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_PROMOTOR")
public class ValoracionPromotorView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    private static final Logger logger = Logger.getLogger(ValoracionPromotorView.class.getName());
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    final UsuarioService usuarioService;
    UUID uuid;
    static final String SI_AVALO = "Sí, avalo este proyecto";

    public ValoracionPromotorView(ProyectoService proyectoService, UsuarioService usuarioService) {
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            try {
                uuid = UUID.fromString(parameter);
                this.proyecto = proyectoService.get(uuid);
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, "UUID inválido: {0}", parameter);
                this.proyecto = Optional.empty();
            }
        } else {
            this.proyecto = Optional.empty();
        }

        if (proyecto.isEmpty()) {
            logger.log(Level.WARNING, "Proyecto no encontrado para UUID: {0}", parameter);
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        } else {
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();

            InfoProyecto infoProyecto = new InfoProyecto(proyectoService, proyectoAux);
            getContent().add(infoProyecto);

            getContent().add(new H2("¿Quiere avalar esta propuesta?"));

            RadioButtonGroup<String> eleccionValorarGroup = new RadioButtonGroup<>();
            eleccionValorarGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            eleccionValorarGroup.setItems(SI_AVALO, "No, rechazo avalar este proyecto");
            getContent().add(eleccionValorarGroup);

            //Valoracion
            getContent().add(new H3("Añade una valoración al proyecto según su importancia:"));
            HorizontalLayout valoracionLayout = new HorizontalLayout();
            valoracionLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroup = new RadioButtonGroup<>();
            valoracionGroup.setLabel("Selecciona tu valoración:");
            valoracionGroup.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroup.setValue(0); // Valor por defecto
            valoracionGroup.setEnabled(false);

            valoracionLayout.add(valoracionGroup);
            getContent().add(valoracionLayout);

            ComboBox<Usuario> directorCombo = new ComboBox<>();
            directorCombo.setLabel("Selecciona el director del proyecto:");
            directorCombo.setItems(usuarioService.list());
            directorCombo.setItemLabelGenerator(usuario -> usuario.getNombre() + " " + usuario.getApellido());
            directorCombo.setEnabled(false);
            getContent().add(directorCombo);

            Checkbox directorEscrito = new Checkbox();
            directorEscrito.setLabel("Si su director no está en la lista de usuarios, seleccione esta opción:");
            directorEscrito.setEnabled(false);
            getContent().add(directorEscrito);
            
            TextField director = new TextField();
            director.setLabel("Persona que designa como director de este proyecto: ");
            director.setPlaceholder("Nombre y apellidos del director");
            director.setEnabled(false);
            getContent().add(director);

            directorEscrito.addValueChangeListener(eventval -> {
                if(Boolean.TRUE.equals(directorEscrito.getValue())){
                    director.setEnabled(true);
                    directorCombo.setEnabled(false);
                    directorCombo.setValue(directorCombo.getEmptyValue());
                }else{
                    director.setEnabled(false);
                    directorCombo.setEnabled(true);
                    director.setValue(director.getEmptyValue());
                }
            });

            eleccionValorarGroup.addValueChangeListener(eventval -> {
                if (SI_AVALO.equals(eventval.getValue())) {
                    valoracionGroup.setEnabled(true);
                    directorCombo.setEnabled(true);
                    directorEscrito.setEnabled(true);
                } else {
                    valoracionGroup.setEnabled(false);
                    directorCombo.setEnabled(false);
                    valoracionGroup.setValue(0);
                    directorEscrito.setEnabled(false);
                }
            });

            // Botón guardar
            Button guardar = new Button("Guardar");
            guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guardar.addClickShortcut(Key.ENTER);
            guardar.addClickListener(e -> {
                if(eleccionValorarGroup.getValue() == null) {
                    Notification.show("Por favor, seleccione una opción.");
                    return;
                }
                Integer valorSeleccionado = valoracionGroup.getValue();
                String valorarOk = eleccionValorarGroup.getValue();
                if (valorSeleccionado == null) {
                    Notification.show("Por favor, seleccione una valoración válida.");
                } else {
                    Notification notification;

                    if (SI_AVALO.equals(valorarOk)) {
                        if(director.getValue().isEmpty() && directorCombo.getValue() == null){
                            Notification.show("Por favor, introduzca el nombre del director o un usuario disponible.");
                            return;
                        }else{
                            if(director.getValue().isEmpty())
                                proyectoAux.setDirector(directorCombo.getValue().getNombre() + " " + directorCombo.getValue().getApellido());
                            else
                                proyectoAux.setDirector(director.getValue());
                            proyectoService.setValoracionPromotor(BigDecimal.valueOf(valorSeleccionado), true, proyectoAux);
                            notification = Notification.show("Valoración guardada con éxito.");
                            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        }
                    } else {
                        proyectoService.setValoracionPromotor(BigDecimal.valueOf(valorSeleccionado), false, proyectoAux);
                        notification = Notification.show("Propuesta de valoración rechazada con éxito.");
                    }
                    //Redireccion
                    notification.setDuration(500);
                    notification.addDetachListener(detachEvent -> UI.getCurrent().navigate("proyectosPromotor"));
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
        }
    }
}
