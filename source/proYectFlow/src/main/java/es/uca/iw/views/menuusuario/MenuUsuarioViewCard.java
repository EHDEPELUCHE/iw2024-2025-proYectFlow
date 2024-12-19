package es.uca.iw.views.menuusuario;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import java.util.List;

@AnonymousAllowed
public class MenuUsuarioViewCard extends ListItem {


    public MenuUsuarioViewCard(String title, String descriptionText, String imageUrl, String label, String buttonText, String navigationTarget, List<String> allowedRoles) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        // IMAGEN
        Div imageContainer = new Div();
        imageContainer.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        imageContainer.setHeight("160px");

        if (imageUrl != null) {
            Image image = new Image();
            image.setWidth("100%");
            image.setSrc(imageUrl);
            image.setAlt(title != null ? title : "Image");
            imageContainer.add(image);
            add(imageContainer);
        }

        //TITULO
        if (title != null) {
            Span titleSpan = new Span();
            titleSpan.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
            titleSpan.setText(title);
            add(titleSpan);
        }

        //DESCRIPCION
        if (title != null) {
            Paragraph description = new Paragraph(descriptionText != null ? descriptionText :
                    "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut.");
            description.addClassName(Margin.Vertical.MEDIUM);
            add(description);
        }

        //ETIQUETA
        if (label != null) {
            Span badge = new Span();
            badge.addClassNames(FontSize.SMALL, TextColor.SECONDARY, Margin.Vertical.SMALL);
            badge.getElement().setAttribute("theme", "badge");
            badge.setText(label);
            add(badge);
        }

        //BOTON DE REDIRECCION
        String finalButtonText = (buttonText != null) ? buttonText : "Administrar"; // Valor por defecto
        Button buttonLink = new Button(finalButtonText);
        buttonLink.addClickListener(e -> {
            UI.getCurrent().navigate(navigationTarget);
        });
        add(buttonLink);

        // Lógica para roles (solo para referencia, no afecta diseño)
        if (allowedRoles != null) {
            getElement().setProperty("allowedRoles", String.join(",", allowedRoles));
        }
    }
}
