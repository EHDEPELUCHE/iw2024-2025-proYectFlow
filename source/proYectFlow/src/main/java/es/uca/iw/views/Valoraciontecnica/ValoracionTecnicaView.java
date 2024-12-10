package es.uca.iw.views.Valoraciontecnica;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import es.uca.iw.data.Proyecto;
import es.uca.iw.services.ProyectoService;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Valoración Técnica")
@Route("ValoracionTecnica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_OTP")
public class ValoracionTecnicaView extends Composite<VerticalLayout> implements HasUrlParameter<String>{
    Optional<Proyecto> proyecto;
    ProyectoService proyectoService;
    UUID uuid;
    public ValoracionTecnicaView(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @Override
    public void setParameter(BeforeEvent event,  String parameter) {
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

        if (proyecto == null || proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(" );
            getContent().add(title);
        } else {
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();

            getContent().add(new H1("Detalles del Proyecto"));
            Grid<Proyecto> grid = new Grid<>(Proyecto.class);
            grid.setItems(List.of(proyectoAux));
            grid.setColumns("nombre", "descripcion", "fechaSolicitud", "coste", "aportacionInicial");
            grid.addComponentColumn(proyecto -> {
            Button downloadButton = new Button("Memoria");
            downloadButton.addClickListener(e -> {
                // Logic to download the PDF
                byte[] pdfContent = null;
                try {
                pdfContent = proyectoService.getPdf(proyecto.getId());
                } catch (IOException ex) {
                throw new RuntimeException(ex);
                }
                if (pdfContent != null) {
                byte[] finalPdfContent = pdfContent;
                StreamResource resource = new StreamResource(proyecto.getPdfNombre(), () -> new ByteArrayInputStream(finalPdfContent));
                Anchor downloadLink = new Anchor(resource, "Download");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getElement().setAttribute("style", "display: none;");
                getContent().add(downloadLink);
                downloadLink.getElement().callJsFunction("click");
                downloadLink.remove();
                }
            }); return downloadButton;
            }).setHeader("PDF").setAutoWidth(true);
            grid.setHeight("150px"); // Set the grid height to fit the number of rows
            //grid.getElement().getStyle().set("--lumo-size-m", "30px"); // Set the row height
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
            precio.setWidth("300px"); // Set the width of the field
            //precio.setMin(BigDecimal.ZERO);
            //precio.setMax(proyectoAux.getCoste());
            horlayout.add(precio);

            //Dejamos que rellene las horas que estima que tardará y recursos
            BigDecimalField Horas = new BigDecimalField("Horas total");
            Horas.setLabel("Esfuerzo necesario de nuestros empleados");
            Horas.setWidth("300px"); // Set the width of the field
            //Horas.setMin(BigDecimal.ZERO);
           // Horas.setMax(BigDecimal.valueOf(10));
            horlayout.add(Horas);

            //Idoneidad técnica
            BigDecimalField Idoneidadtecnica = new BigDecimalField("Idoneidad total");
            Idoneidadtecnica.setLabel("Idoneidad total");
            Idoneidadtecnica.setWidth("300px"); // Set the width of the field
            //Idoneidadtecnica.setMin(BigDecimal.ZERO);
            //Idoneidadtecnica.setMax(BigDecimal.valueOf(10));
            horlayout.add(Idoneidadtecnica);

            getContent().add(horlayout);

            Button Guardar = new Button("Guardar");
            Guardar.addClickListener(e -> {
                if(precio.getValue().compareTo(BigDecimal.valueOf(10)) == 1 || precio.getValue().compareTo(BigDecimal.ZERO) == -1
                || Horas.getValue().compareTo(BigDecimal.valueOf(10)) == 1 || Horas.getValue().compareTo(BigDecimal.ZERO) == -1
                || Idoneidadtecnica.getValue().compareTo(BigDecimal.valueOf(10)) == 1 || Idoneidadtecnica.getValue().compareTo(BigDecimal.ZERO) == -1)
            {
                Notification.show("Las notas tienen que estar entre 0 y 10");
            } else {
                proyectoService.setValoracionTecnica(precio.getValue(), Horas.getValue(), Idoneidadtecnica.getValue(), proyectoAux);
            }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(Guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
            getContent().add(new H4("* Indique un 0 en idoneidad técnica si alguna ley no se cumple y hace imposible este proyecto"));
        }
    }
}
