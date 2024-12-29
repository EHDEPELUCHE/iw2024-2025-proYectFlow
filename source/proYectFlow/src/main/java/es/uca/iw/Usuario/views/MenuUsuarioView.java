package es.uca.iw.Usuario.views;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Menu de usuario")
@Route("menuUsuarioView")
@Menu(order = 8, icon = "line-awesome/svg/border-all-solid.svg")
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

        addCardIfRoleMatches("Nueva solicitud de proyecto",
                "Aquí puedes realizar una nueva solicitud de proyecto",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop",
                "Solicitar", "registro-proyecto",
                List.of("ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP", "ROLE_ADMIN"));

        addCardIfRoleMatches("Gestión de proyectos",
                "Aquí puedes gestionar tus solicitudes de proyectos",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop",
                "Administrar", "estadomisproyectos",
                List.of("ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP", "ROLE_ADMIN"));

        addCardIfRoleMatches("Avalar un proyecto",
                "Aquí puedes gestionar tus solicitudes de aval de proyectos",
                "https://images.unsplash.com/photo-1542626991-cbc4e32524cc?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Avalar", "proyectosPromotor",
                List.of("ROLE_PROMOTOR"));

        addCardIfRoleMatches("Tus proyectos avalados",
                "Aquí puedes ver los proyectos que has avalado",
                "https://images.unsplash.com/photo-1542626991-cbc4e32524cc?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Consultar", "estadomisproyectos",
                List.of("ROLE_PROMOTOR"));

        addCardIfRoleMatches("Proyectos en desarrollo",
                "Aquí puedes ver todos los proyectos en desarrollo",
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?q=80&w=2070&auto=format&fit=crop",
                "Consultar", "ProyectosDesarrollo",
                null);

        addCardIfRoleMatches("Evaluación de proyectos",
                "Aquí puedes evaluar los proyectos pendientes",
                "https://images.unsplash.com/photo-1452457750107-cd084dce177d?q=80&w=2001&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Evaluar", "proyectosOTP",
                List.of("ROLE_OTP"));

        addCardIfRoleMatches("Historial de proyectos",
                "Aquí puedes consultar todos los proyectos",
                "https://images.unsplash.com/photo-1569235186275-626cb53b83ce?q=80&w=2072&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Consultar", "proyectos",
                List.of("ROLE_ADMIN"));

        addCardIfRoleMatches("Gestionar convocatorias",
                "Aquí puedes consultar todas las convocatoria",
                "https://images.unsplash.com/photo-1631972756622-b2d9164c0e53?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDN8fGNhbGVuZGFyaW98ZW58MHx8MHx8fDI%3D",
                "Consultar", "GestionarConvocatorias",
                List.of("ROLE_ADMIN"));

        addCardIfRoleMatches("Nueva convocatoria",
                "Aquí puedes crear una nueva convocatoria",
                "https://images.unsplash.com/photo-1631972756622-b2d9164c0e53?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NDN8fGNhbGVuZGFyaW98ZW58MHx8MHx8fDI%3D",
                "Crear", "CrearConvocatoria",
                List.of("ROLE_ADMIN"));

        addCardIfRoleMatches("Gestionar usuarios",
                "Aquí puedes gestionar los usuarios",
                "https://images.unsplash.com/photo-1668338857295-7b10bcea47b1?q=80&w=1935&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Gestionar", "AdministrarUsuarios",
                List.of("ROLE_ADMIN"));

        addCardIfRoleMatches("Añadir nuevo usuario",
                "Aquí puedes crear un nuevo usuario",
                "https://images.unsplash.com/photo-1668338857295-7b10bcea47b1?q=80&w=1935&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Crear", "registro-usuario-admin",
                List.of("ROLE_ADMIN"));

        addCardIfRoleMatches("Datos personales",
                "Aquí puedes gestionar tus datos personales",
                "https://images.unsplash.com/photo-1668338857295-7b10bcea47b1?q=80&w=1935&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "Administrar", "Ver-mis-datos",
                List.of("ROLE_SOLICITANTE", "ROLE_PROMOTOR", "ROLE_CIO", "ROLE_OTP", "ROLE_ADMIN"));
    }

    // Agrega la tarjeta si tiene el rol de usuario necesario
    private void addCardIfRoleMatches(String title, String description, String imageUrl,
                                      String buttonText, String navigationTarget, List<String> allowedRoles) {
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            imageContainer.add(new MenuUsuarioViewCard(title, description, imageUrl, null, buttonText,
                    navigationTarget, allowedRoles));
            return;
        } else {
            List<String> userRoles = getCurrentUserRoles();
            boolean hasPermission = allowedRoles.stream()
                    .anyMatch(role -> userRoles.contains(role));
            if (hasPermission) {
                imageContainer.add(new MenuUsuarioViewCard(title, description, imageUrl, null, buttonText,
                        navigationTarget, allowedRoles));
            }
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