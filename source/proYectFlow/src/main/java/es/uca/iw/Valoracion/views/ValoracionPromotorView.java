package es.uca.iw.Valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.global.DownloadPdfComponent;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Valoración Promotor")
@Route("ValoracionPromotor")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_PROMOTOR")
public class ValoracionPromotorView extends Composite<VerticalLayout> implements HasUrlParameter<String> {
    Optional<Proyecto> proyecto;
    final ProyectoService proyectoService;
    UUID uuid;

    public ValoracionPromotorView(ProyectoService proyectoService) {
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

        if (proyecto.isEmpty() || proyecto.isEmpty()) {
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

            if (proyectoAux.getFechaLimite() != null) {
                LocalDate localDateL = proyectoAux.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter formatterDateL = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                formLayout.addFormItem(new Span(localDateL.format(formatterDateL)), "Fecha Límite de puesta en marcha");
            }

            Button downloadButton = DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                try {
                    return proyectoService.getPdf(proyecto.get().getId());
                } catch (IOException ex) {
                    throw new RuntimeException("Error al obtener el PDF", ex);
                }
            });

            getContent().add(formLayout, downloadButton);

            //AVALAR?
            getContent().add(new H2("¿Quiere avalar esta propuesta?"));

            RadioButtonGroup<String> eleccionValorarGroup = new RadioButtonGroup<>();
            eleccionValorarGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
            //eleccionValorarGroup.setLabel("Eleccion Valorar");
            eleccionValorarGroup.setItems("Sí, avalo este proyecto", "No, rechazo avalar este proyecto");
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

            eleccionValorarGroup.addValueChangeListener(eventval -> {
                if ("Sí, avalo este proyecto".equals(eventval.getValue())) {
                    valoracionGroup.setEnabled(true);
                } else {
                    valoracionGroup.setEnabled(false);
                    valoracionGroup.setValue(0);
                }
            });

            // Botón guardar
            Button guardar = new Button("Guardar");
            guardar.addClickListener(e -> {
                Integer valorSeleccionado = valoracionGroup.getValue();
                String valorarOk = eleccionValorarGroup.getValue();
                if (valorSeleccionado == null) {
                    Notification.show("Por favor, seleccione una valoración válida.");
                } else {
                    Notification notification;

                    if ("Sí, avalo este proyecto".equals(valorarOk)) {
                        proyectoService.setValoracionPromotor(BigDecimal.valueOf(valorSeleccionado), true, proyectoAux);
                        notification = Notification.show("Valoración guardada con éxito.");
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    } else {
                        proyectoService.setValoracionPromotor(BigDecimal.valueOf(valorSeleccionado), false, proyectoAux);
                        notification = Notification.show("Propuesta de valoración rechazada con éxito.");
                    }
                    //Redireccion
                    notification.setDuration(2000);
                    notification.addDetachListener(detachEvent -> {
                        UI.getCurrent().navigate("proyectosPromotor");
                    });
                }
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
        }
    }
}
