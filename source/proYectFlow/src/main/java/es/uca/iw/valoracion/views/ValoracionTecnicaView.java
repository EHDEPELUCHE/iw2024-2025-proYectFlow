package es.uca.iw.valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.*;

import es.uca.iw.global.Roles;
import es.uca.iw.proyecto.InfoProyecto;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.UsuarioService;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Vista para la valoración técnica de un proyecto.
 * 
 * Esta vista permite a los usuarios con el rol "ROLE_OTP" evaluar un proyecto
 * en función de varios criterios, como el coste, las horas/recursos necesarios
 * y la idoneidad técnica. Además, permite asignar un jefe de proyecto.
 * 
 * Anotaciones:
 * - @PageTitle: Título de la página.
 * - @Route: Ruta de la vista.
 * - @Menu: Configuración del menú (orden e icono).
 * - @RolesAllowed: Roles permitidos para acceder a esta vista.
 * 
 * Atributos:
 * - PX: Constante para definir el tamaño de ciertos componentes.
 * - proyectoService: Servicio para gestionar proyectos.
 * - usuarioService: Servicio para gestionar usuarios.
 * - proyecto: Proyecto a evaluar.
 * - uuid: Identificador único del proyecto.
 * 
 * Métodos:
 * - Constructor: Inicializa los servicios de proyecto y usuario.
 * - setParameter: Establece el parámetro de la URL y carga el proyecto correspondiente.
 * 
 * En caso de que el proyecto no se encuentre, muestra un mensaje de error.
 * Si el proyecto se encuentra, muestra los datos del proyecto y permite realizar
 * la valoración técnica en función de varios criterios.
 * 
 * Componentes de la vista:
 * - InfoProyecto: Información del proyecto.
 * - RadioButtonGroup: Grupos de botones de radio para valorar el precio, horas/recursos necesarios e idoneidad técnica.
 * - ComboBox: Selector para elegir el jefe de proyecto.
 * - Button: Botón para guardar la valoración.
 * 
 * Al guardar la valoración, se valida que se haya seleccionado un jefe de proyecto
 * y se actualiza la valoración técnica del proyecto en el servicio correspondiente.
 * 
 * Navegación:
 * - Al guardar la valoración con éxito, se muestra una notificación y se redirige a la vista "proyectosOTP".
 */
@PageTitle("Valoración Técnica")
@Route("ValoracionTecnica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_OTP")
public class ValoracionTecnicaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    static final String PX = "300px";
    final ProyectoService proyectoService;
    final UsuarioService usuarioService;
    Optional<Proyecto> proyecto;
    UUID uuid;

    public ValoracionTecnicaView(ProyectoService proyectoService, UsuarioService usuarioService) {
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

            getContent().add(new H1("Valora de 1 a 10 la idoneidad del proyecto según su: "));
            BigDecimal costeFinal = proyectoAux.getCoste().subtract(proyectoAux.getAportacionInicial());
            getContent().add(new H4("Coste total del proyecto: " + costeFinal + " €"));
            getContent().add(new H4("Presupuesto de la convocatoria actual: " + proyectoAux.getConvocatoria().getPresupuestorestante() + " €"));

            //Mostramos el precio total menos la valoración inicial
            HorizontalLayout valoracionLayoutPrecio = new HorizontalLayout();
            valoracionLayoutPrecio.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroupPrecio = new RadioButtonGroup<>();
            valoracionGroupPrecio.setLabel("Precio:");
            valoracionGroupPrecio.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroupPrecio.setValue(0); // Valor por defecto
            valoracionGroupPrecio.setEnabled(true);

            valoracionLayoutPrecio.add(valoracionGroupPrecio);
            getContent().add(valoracionLayoutPrecio);

            //Dejamos que rellene las horas que estima que tardará y recursos
            HorizontalLayout valoracionLayoutHoras = new HorizontalLayout();
            valoracionLayoutHoras.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroupHoras = new RadioButtonGroup<>();
            valoracionGroupHoras.setLabel("Horas / Recursos necesarios:");
            valoracionGroupHoras.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroupHoras.setValue(0); // Valor por defecto
            valoracionGroupHoras.setEnabled(true);

            valoracionLayoutHoras.add(valoracionGroupHoras);
            getContent().add(valoracionLayoutHoras);

            //Idoneidad técnica
            HorizontalLayout valoracionLayoutIT = new HorizontalLayout();
            valoracionLayoutIT.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroupIT = new RadioButtonGroup<>();
            valoracionGroupIT.setLabel("Idoneidad técnica:");
            valoracionGroupIT.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroupIT.setValue(0); // Valor por defecto
            valoracionGroupIT.setEnabled(true);

            valoracionLayoutIT.add(valoracionGroupIT);
            getContent().add(valoracionLayoutIT);

            ComboBox<Usuario> jefes = new ComboBox<>();
            jefes.setLabel("Jefe de proyecto");
            jefes.setItems(usuarioService.get(Roles.OTP));
            jefes.setItemLabelGenerator(usuario -> usuario.getNombre() + " " + usuario.getApellido());
            jefes.setRequired(true);
            getContent().add(jefes);

            Button guardar = new Button("Guardar");
            guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guardar.addClickShortcut(Key.ENTER);
            guardar.addClickListener(e -> {
                if(jefes.getValue() == jefes.getEmptyValue()) {
                    Notification.show("Por favor, seleccione un jefe de proyecto independientemente de la valoración.");
                    return;
                }else{
                    proyectoAux.setJefe(jefes.getValue());
                    proyectoService.setValoracionTecnica(valoracionGroupPrecio.getValue(), valoracionGroupHoras.getValue(), valoracionGroupIT.getValue(), proyectoAux);
                    Notification notification = Notification.show("Valoración guardada con éxito.");
                    notification.setDuration(500);
                    notification.addDetachListener(detachEvent -> UI.getCurrent().navigate("proyectosOTP"));
                }
                

            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
            getContent().add(new H4("* Indique un 0 en idoneidad técnica si el proyecto viola alguna norma / ley."));
        }
    }
}
