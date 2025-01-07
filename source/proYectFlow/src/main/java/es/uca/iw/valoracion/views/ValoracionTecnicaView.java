package es.uca.iw.valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.*;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import es.uca.iw.global.DownloadPdfComponent;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Valoración Técnica")
@Route("ValoracionTecnica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_OTP")
public class ValoracionTecnicaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    UUID uuid;
    static final String PX = "300px";

    public ValoracionTecnicaView(ProyectoService proyectoService) {
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

            getContent().add(new H1("Detalles del Proyecto"));
            Grid<Proyecto> grid = new Grid<>(Proyecto.class);
            grid.setItems(List.of(proyectoAux));
            grid.setColumns("nombre", "descripcion", "fechaSolicitud", "coste", "aportacionInicial");
            grid.addComponentColumn(proyecto1 ->
                DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                    try {
                        return proyectoService.getPdf(proyecto1.getId());
                    } catch (IOException ex) {
                        throw new RuntimeException("Error al obtener el PDF", ex);
                    }
                })
            ).setHeader("PDF").setAutoWidth(true);
            grid.setAllRowsVisible(true);
            getContent().add(grid);

            getContent().add(new H1("Añade una valoración de 1 a 10 en los siguientes campos: "));
            HorizontalLayout horlayout = new HorizontalLayout();
            horlayout.setWidthFull();
            horlayout.setAlignItems(FlexComponent.Alignment.CENTER);
            horlayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            //Mostramos el precio total menos la valoración inicial
            BigDecimalField precio = new BigDecimalField("Precio total");
            precio.setLabel("Precio restante");
            precio.setValue(proyectoAux.getCoste().subtract(proyectoAux.getAportacionInicial()));
            precio.setWidth(PX); // Set the width of the field
            horlayout.add(precio);

            //Dejamos que rellene las horas que estima que tardará y recursos
            BigDecimalField horas = new BigDecimalField("Horas total");
            horas.setLabel("Esfuerzo necesario de nuestros empleados");
            horas.setWidth(PX); // Set the width of the field
            horlayout.add(horas);

            //Idoneidad técnica
            BigDecimalField idoneidadtecnica = new BigDecimalField("Idoneidad total");
            idoneidadtecnica.setLabel("Idoneidad total");
            idoneidadtecnica.setWidth(PX); // Set the width of the field
            horlayout.add(idoneidadtecnica);

            getContent().add(horlayout);

            Button guardar = new Button("Guardar");
            guardar.addClickListener(e -> {
                if (precio.getValue().compareTo(BigDecimal.TEN) > 0 || precio.getValue().compareTo(BigDecimal.ZERO) < 0
                        || horas.getValue().compareTo(BigDecimal.TEN) > 0 || horas.getValue().compareTo(BigDecimal.ZERO) < 0
                        || idoneidadtecnica.getValue().compareTo(BigDecimal.TEN) > 0 || idoneidadtecnica.getValue().compareTo(BigDecimal.ZERO) < 0) {
                    Notification.show("Las notas tienen que estar entre 0 y 10");
                } else {
                    proyectoService.setValoracionTecnica(precio.getValue(), horas.getValue(), idoneidadtecnica.getValue(), proyectoAux);
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
            getContent().add(new H4("* Indique un 0 en idoneidad técnica si alguna ley no se cumple y hace imposible este proyecto"));
        }
    }
}
