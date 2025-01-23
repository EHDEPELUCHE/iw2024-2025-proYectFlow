package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import es.uca.iw.proyecto.InfoProyecto;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;

import java.util.Optional;
import java.util.UUID;

/**
 * La clase InfoProyectoView representa una vista para mostrar información del proyecto.
 * Está anotada con @PageTitle, @Route, @Menu y @AnonymousAllowed para definir
 * su título, ruta, orden en el menú y permisos de acceso respectivamente.
 * 
 * Esta clase extiende Composite<VerticalLayout> e implementa HasUrlParameter<String>
 * para manejar parámetros de URL para la identificación del proyecto.
 * 
 * La vista recupera la información del proyecto basada en un parámetro UUID de la URL.
 * Si se encuentra el proyecto, muestra los detalles del proyecto utilizando el componente InfoProyecto.
 * Si no se encuentra el proyecto, muestra un mensaje de error.
 * 
 * @param proyectoService El servicio utilizado para recuperar la información del proyecto.
 */
@PageTitle("Información del proyecto")
@Route("InfoProyecto")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@AnonymousAllowed
public class InfoProyectoView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    UUID uuid;

    public InfoProyectoView(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
        
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

            Button volver = new Button("Volver", e -> UI.getCurrent().navigate("ProyectosDesarrollo"));
            getContent().add(volver);

        }
    }
}
