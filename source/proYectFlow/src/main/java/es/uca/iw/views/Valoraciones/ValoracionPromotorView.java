package es.uca.iw.views.Valoraciones;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import es.uca.iw.data.Proyecto;
import es.uca.iw.services.ProyectoService;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Valoración Promotor")
@Route("ValoracionPromotor")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_PROMOTOR")
public class ValoracionPromotorView extends Composite<VerticalLayout> implements HasUrlParameter<String>{
    Optional<Proyecto> proyecto;
    ProyectoService proyectoService;
    UUID uuid;
    public ValoracionPromotorView(ProyectoService proyectoService) {
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
                        StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(finalPdfContent));
                        Anchor downloadLink = new Anchor(resource, "Download");
                        downloadLink.getElement().setAttribute("download", true);
                        downloadLink.getElement().setAttribute("style", "display: none;");
                        getContent().add(downloadLink);
                        downloadLink.getElement().callJsFunction("click");
                        downloadLink.remove();
                    }
                });
                return downloadButton;
            }).setHeader("PDF").setAutoWidth(true);
            grid.setAllRowsVisible(true);
            getContent().add(grid);

            getContent().add(new H1("Añade una valoración de 1 a 10 en los siguientes campos: "));
            HorizontalLayout horlayout = new HorizontalLayout();
            FormLayout formLayout = new FormLayout();
            
            formLayout.setWidth("100%");

            horlayout.addClassName(Gap.MEDIUM);
            horlayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            horlayout.setWidthFull();
            horlayout.add(formLayout);
            //Mostramos el precio total menos la valoración inicial
            Checkbox avalar = new Checkbox();
            avalar.setLabel("Sí, avalo este proyecto");
            formLayout.add(avalar);
            BigDecimalField prioridad = new BigDecimalField();
            prioridad.setLabel("Prioridad");
            formLayout.add(prioridad);
            getContent().add(horlayout);
            
            Button Guardar = new Button("Guardar");
            Guardar.addClickListener(e -> {
                if(prioridad.getValue().compareTo(BigDecimal.valueOf(10)) == 1 || prioridad.getValue().compareTo(BigDecimal.ZERO) == -1
                        )
                {
                    Notification.show("Las notas tienen que estar entre 0 y 10");
                } else {
                    proyectoService.setValoracionPromotor(prioridad.getValue(), avalar.getValue(), proyectoAux);
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(Guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
        }
    }
}
