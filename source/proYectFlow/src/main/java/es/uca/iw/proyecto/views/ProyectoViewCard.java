package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.proyecto.Proyecto;

/**
 * ProyectoViewCard es un componente personalizado que extiende ListItem y representa una vista en tarjeta para un proyecto.
 * Muestra la imagen del proyecto, título, descripción, progreso y un botón para navegar a más detalles.
 *
 * <p>Anotaciones:
 * <ul>
 *   <li>@AnonymousAllowed - Permite el acceso anónimo a esta vista.</li>
 * </ul>
 *
 * <p>Constructor:
 * <ul>
 *   <li>ProyectoViewCard(Proyecto proyecto, String navigationTarget) - Inicializa la tarjeta con los detalles del proyecto y el objetivo de navegación dados.</li>
 * </ul>
 *
 * <p>Componentes:
 * <ul>
 *   <li>Div imageContainer - Contenedor para la imagen del proyecto.</li>
 *   <li>Image image - Muestra la imagen del proyecto.</li>
 *   <li>Span titleSpan - Muestra el título del proyecto si está disponible.</li>
 *   <li>Paragraph description - Muestra la descripción del proyecto si está disponible.</li>
 *   <li>ProgressBar progressBar - Muestra el progreso del proyecto como una barra de progreso.</li>
 *   <li>Button buttonLink - Botón para navegar a la vista detallada del proyecto.</li>
 * </ul>
 *
 * @param proyecto El objeto del proyecto que contiene los detalles a mostrar.
 * @param navigationTarget La URL de destino de navegación para el botón "Ver más detalles".
 */
@AnonymousAllowed
public class ProyectoViewCard extends ListItem{

    public ProyectoViewCard(Proyecto proyecto, String navigationTarget) {
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.START, LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.LARGE);

        // IMAGEN
        Div imageContainer = new Div();
        imageContainer.addClassNames(LumoUtility.Background.CONTRAST, LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Overflow.HIDDEN, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Width.FULL);
        imageContainer.setHeight("160px");

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc("https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop");
        image.setAlt(proyecto.getNombre() != null ? proyecto.getNombre() : "Imagen del Proyecto");
        imageContainer.add(image);
        add(imageContainer);

        //TITULO
        if (proyecto.getNombre() != null) {
            Span titleSpan = new Span();
            titleSpan.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.SEMIBOLD);
            titleSpan.setText(proyecto.getNombre());
            add(titleSpan);
        }

        //DESCRIPCION
        if (proyecto.getDescripcion() != null) {
            Paragraph description = new Paragraph(proyecto.getDescripcion());
            description.addClassName(LumoUtility.Margin.Vertical.MEDIUM);
            add(description);
        }

        //PROGRESO
        String progressText = "Avance del proyecto: " + proyecto.getGradoAvance() + "%";
        Double progress = proyecto.getGradoAvance();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(progress / 100.0);
        progressBar.setWidthFull();
        progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
        add(progressText);
        add(progressBar);

        //BOTON DE REDIRECCION
        Button buttonLink = new Button("Ver más detalles");
        buttonLink.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(navigationTarget))
        );
        add(buttonLink);
    }
}