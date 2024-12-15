package es.uca.iw.views.menuusuario;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@PageTitle("Menu de usuario")
@Route("menuUsuarioView")
@Menu(order = 7, icon = "line-awesome/svg/border-all-solid.svg")
@PermitAll

public class MenuUsuarioView extends Main implements HasComponents, HasStyle {

    private OrderedList imageContainer;

    public MenuUsuarioView() {
        constructUI();
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

        // Filtrar las tarjetas según el rol
        addCardIfRoleMatches("Gestión de Proyectos",
                "Aquí puedes gestionar tus solicitudes de proyectos",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop",
                "Administrar", "proyectos",
                List.of("ROLE_SOLICITANTE"));

        addCardIfRoleMatches("Nueva solicitud de Proyecto",
                "Aquí puedes realizar una nueva solicitud de proyecto",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop",
                "Solicitar", "registro-proyecto",
                List.of("ROLE_SOLICITANTE"));

        addCardIfRoleMatches("Avalar un Proyecto",
                "Aquí puedes gestionar tus solicitudes de aval de proyectos",
                "https://images.unsplash.com/photo-1542626991-cbc4e32524cc?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Avalar", "proyectosPromotor",
                List.of("ROLE_PROMOTOR"));

        addCardIfRoleMatches("Tus proyectos avalados",
                "Aquí puedes ver los proyectos que has avalado",
                "https://images.unsplash.com/photo-1542626991-cbc4e32524cc?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Consultar", "estadomisproyectos",
                List.of("ROLE_PROMOTOR"));

        addCardIfRoleMatches("Evaluación de Proyectos",
                "Aquí puedes evaluar los proyectos pendientes",
                "https://images.unsplash.com/photo-1452457750107-cd084dce177d?q=80&w=2001&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Evaluar", "proyectosOTP",
                List.of("ROLE_OTP"));

        addCardIfRoleMatches("Datos Personales",
                "Aquí puedes gestionar tus datos personales",
                "https://images.unsplash.com/photo-1668338857295-7b10bcea47b1?q=80&w=1935&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Administrar", "Ver-mis-datos",
                List.of("ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP", "ROLE_ADMIN"));
    }

    // Agrega la tarjeta si tiene el rol de usuario necesario
    private void addCardIfRoleMatches(String title, String description, String imageUrl,
                                      String buttonText, String navigationTarget, List<String> allowedRoles) {

        List<String> userRoles = getCurrentUserRoles();

        boolean hasPermission = allowedRoles.stream()
                .anyMatch(role -> userRoles.contains(role));

        if (hasPermission) {
            imageContainer.add(new MenuUsuarioViewCard(title, description, imageUrl, null, buttonText,
                    navigationTarget, allowedRoles));
        }
    }

    private List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return roles;
        }
        return List.of(); // Si no hay roles, devolvemos una lista vacía
    }
}