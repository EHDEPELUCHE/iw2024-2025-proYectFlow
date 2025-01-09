package es.uca.iw.valoracion.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.*;
import es.uca.iw.global.DownloadPdfComponent;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
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
    static final String PX = "300px";
    final ProyectoService proyectoService;
    Optional<Proyecto> proyecto;
    UUID uuid;

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

            getContent().add(new H1("Valora de 1 a 10 la idoneidad del proyecto según su: "));
            BigDecimal costeFinal = proyectoAux.getCoste().subtract(proyectoAux.getAportacionInicial());
            getContent().add(new H4("Coste total del proyecto: " + costeFinal + " €"));

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

            Button guardar = new Button("Guardar");
            guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            guardar.addClickShortcut(Key.ENTER);
            guardar.addClickListener(e -> {
                proyectoService.setValoracionTecnica(valoracionGroupPrecio.getValue(), valoracionGroupHoras.getValue(), valoracionGroupIT.getValue(), proyectoAux);
                Notification notification = Notification.show("Valoración guardada con éxito.");
                notification.setDuration(2000);
                notification.addDetachListener(detachEvent -> UI.getCurrent().navigate("proyectosOTP"));

            });

            HorizontalLayout buttonLayout = new HorizontalLayout(guardar);
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            getContent().add(buttonLayout);
            getContent().add(new H4("* Indique un 0 en idoneidad técnica si el proyecto viola alguna norma / ley."));
        }
    }
}
