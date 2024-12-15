package es.uca.iw.views.menuusuario;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;

@PageTitle("Menu de usuario")
@Route("menuUsuarioView")
@Menu(order = 7, icon = "line-awesome/svg/border-all-solid.svg")
@PermitAll
public class MenuUsuarioView extends Main implements HasComponents, HasStyle {

    private OrderedList imageContainer;

    public MenuUsuarioView() {
        constructUI();

        imageContainer.add(new MenuUsuarioViewCard("Proyectos",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"));
        imageContainer.add(new MenuUsuarioViewCard("Snow covered mountain",
                "https://images.unsplash.com/photo-1512273222628-4daea6e55abb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80"));
        imageContainer.add(new MenuUsuarioViewCard("River between mountains",
                "https://images.unsplash.com/photo-1536048810607-3dc7f86981cb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=375&q=80"));
        imageContainer.add(new MenuUsuarioViewCard(
                "Montañas nevadas bajo las estrellas", // Texto alternativo para la imagen
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
        {{
            // Rellenar campos adicionales
            Span header = (Span) getChildren()
                    .filter(component -> component instanceof Span && ((Span) component).getText().equals("Title"))
                    .findFirst()
                    .orElse(null);
            if (header != null) header.setText("Montañas Nevadas"); // Título

            Span subtitle = (Span) getChildren()
                    .filter(component -> component instanceof Span && ((Span) component).getText().equals("Card subtitle"))
                    .findFirst()
                    .orElse(null);
            if (subtitle != null) subtitle.setText("Una vista impresionante"); // Subtítulo

            Paragraph description = (Paragraph) getChildren()
                    .filter(component -> component instanceof Paragraph)
                    .findFirst()
                    .orElse(null);
            if (description != null) description.setText("Una espectacular vista nocturna de montañas nevadas bajo un cielo estrellado."); // Descripción

            Span badge = (Span) getChildren()
                    .filter(component -> component instanceof Span && ((Span) component).getElement().hasAttribute("theme"))
                    .findFirst()
                    .orElse(null);
            if (badge != null) badge.setText("Naturaleza"); // Etiqueta

            Button actionButton = (Button) getChildren()
                    .filter(component -> component instanceof Button)
                    .findFirst()
                    .orElse(null);
            if (actionButton != null) actionButton.setText("Gestionar Proyectos");
        }});

    }

    private void constructUI() {
        addClassNames("menu-usuario-view-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Mi Menú");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Desde aquí puedes gestionar todos tus proyectos y datos personales");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        imageContainer.getStyle().set("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))");
        imageContainer.getStyle().set("gap", "1rem");

        container.add(headerContainer);
        add(container, imageContainer);
    }
}
