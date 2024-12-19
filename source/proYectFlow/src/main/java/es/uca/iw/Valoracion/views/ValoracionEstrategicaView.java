package es.uca.iw.Valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Valoración CIO")
@Route("ValoracionEstrategica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("CIO")
public class ValoracionEstrategicaView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    ProyectoService proyectoService;
    UUID uuid;

    public ValoracionEstrategicaView(ProyectoService proyectoService) {
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

        if (proyecto == null || proyecto.isEmpty()) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        } else {
            //MOSTRAR DATOS DEL PROYECTO
            Proyecto proyectoAux = proyecto.get();

            getContent().add(new H1("Detalles del Proyecto"));

            FormLayout formLayout = new FormLayout();
            formLayout.setWidth("100%");

            formLayout.addFormItem(new Span(proyectoAux.getSolicitante().getNombre()), "Solicitante");

            LocalDate localDate = proyectoAux.getFechaSolicitud().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            FormLayout.FormItem formItem = formLayout.addFormItem(new Span(localDate.format(formatterDate)), "Fecha de Solicitud");
            formItem.getElement().getStyle().set("white-space", "nowrap");

            formLayout.addFormItem(new Span(proyectoAux.getNombre()), "Nombre");
            formLayout.addFormItem(new Span(proyectoAux.getDescripcion()), "Descripción");
            formLayout.addFormItem(new Span(proyectoAux.getAlcance()), "Alcance");
            formLayout.addFormItem(new Span(proyectoAux.getInteresados()), "Interesados");

            formLayout.addFormItem(new Span(proyectoAux.getCoste() + "€"), "Coste");
            formLayout.addFormItem(new Span(proyectoAux.getAportacionInicial() + "€"), "Aportación Inicial");

            LocalDate localDateL = proyectoAux.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatterDateL = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            formLayout.addFormItem(new Span(localDateL.format(formatterDateL)), "Fecha Límite de puesta en marcha");

            Button downloadButton = new Button("Memoria");
            downloadButton.addClickListener(e -> {
                // Logic to download the PDF
                byte[] pdfContent = null;
                try {
                    pdfContent = proyectoService.getPdf(proyectoAux.getId());
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

            getContent().add(formLayout, downloadButton);

            //AVALAR?
            getContent().add(new H2("¿Quiere avalar esta propuesta?"));

            Checkbox avalarS = new Checkbox("Sí, avalo este proyecto");
            Checkbox avalarN = new Checkbox("No, rechazo avalar este proyecto");

            getContent().add(avalarS, avalarN);

            //Valoracion
            getContent().add(new H3("Añade una valoración al proyecto según su importancia:"));
            HorizontalLayout valoracionLayout = new HorizontalLayout();
            valoracionLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            RadioButtonGroup<Integer> valoracionGroup = new RadioButtonGroup<>();
            valoracionGroup.setLabel("Selecciona tu valoración:");
            valoracionGroup.setItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            valoracionGroup.setValue(0); // Valor por defecto

            valoracionLayout.add(valoracionGroup);
            getContent().add(valoracionLayout);

            // Botón guardar
            Button guardar = new Button("Guardar");
            guardar.addClickListener(e -> {
                Integer valorSeleccionado = valoracionGroup.getValue();
                if (valorSeleccionado == null || valorSeleccionado < 0 || valorSeleccionado > 10) {
                    Notification.show("Por favor, selecciona una valoración válida entre 0 y 10.");
                } else {
                    proyectoService.setValoracionPromotor(BigDecimal.valueOf(valorSeleccionado), avalarS.getValue(), proyectoAux);
                    Notification.show("Valoración guardada con éxito.");
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
        }
    }
}
