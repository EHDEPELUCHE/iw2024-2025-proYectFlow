package es.uca.iw.global.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.security.AuthenticatedUser;
import es.uca.iw.usuario.Usuario;
import es.uca.iw.usuario.views.MenuUsuarioView;
import es.uca.iw.usuario.views.MisDatosView;

import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {
    static final String arialabel = "aria-label";
    static final String fondo = "fondo";
    static final String tabindex = "tabindex";
    final AuthenticatedUser user;
    private H1 viewTitle;

    public MainLayout(AuthenticatedUser user) {
        this.user = user;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.setClassName(fondo);
        toggle.getElement().setAttribute(tabindex, "0"); // Make focusable
        toggle.getElement().setAttribute("role", "button"); // ARIA role

        HorizontalLayout horAux = new HorizontalLayout();
        HorizontalLayout horAuxFondo = new HorizontalLayout();
        horAux.setSpacing(true);
        horAux.setMargin(true);
        horAux.setPadding(true);
        horAux.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image("favicon.ico", "Logo");
        image.setHeight("50px");
        image.setWidth("50px");
        image.getElement().setAttribute("alt", "Logo de la aplicación");
        image.getElement().setAttribute(tabindex, "0"); // Make focusable
        image.getElement().setAttribute("role", "link"); // ARIA role

        image.addClickListener(imageClickEvent -> {
            image.getUI().ifPresent(ui -> ui.navigate(""));
        });

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, fondo);
        viewTitle.getElement().setAttribute(arialabel, "Título de la vista");

        Image ayuda = new Image("img/ayuda.png", "Ayuda");
        ayuda.setHeight("50px");
        ayuda.setWidth("50px");
        ayuda.getElement().setAttribute("alt", "Icono de ayuda");
        ayuda.getElement().setAttribute(tabindex, "0"); // Make focusable
        ayuda.getElement().setAttribute("role", "button"); // ARIA role
        horAuxFondo.add(ayuda);
        ayuda.addClickListener(ayudaClickEvent -> {
            ayuda.getUI().ifPresent(ui -> ui.navigate("Ayuda"));
        });

        Avatar avatarBasic = new Avatar();
        if (user.get().isPresent()) {
            Optional<Usuario> usuario = user.get();
            if (usuario.isPresent()) avatarBasic.setName(usuario.get().getNombre());
            else avatarBasic.setName("Usuario");
            avatarBasic.setWidth("40px");
            avatarBasic.setHeight("40px");
            avatarBasic.addClassNames(LumoUtility.AlignSelf.END, "fondoAvatar");
            avatarBasic.getElement().setAttribute(arialabel, "Avatar de usuario");

            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem menuItem = menuBar.addItem(avatarBasic);
            menuItem.getElement().setAttribute(arialabel, "Menú de usuario");
            SubMenu subMenu = menuItem.getSubMenu();
            subMenu.addItem(new RouterLink("Mis datos", MisDatosView.class));

            MenuItem logoutItem = subMenu.addItem("Cerrar sesión");
            logoutItem.getElement().setAttribute(tabindex, "0"); // Make focusable
            logoutItem.getElement().setAttribute(arialabel, "Cerrar sesión");
            logoutItem.addClickListener(event -> {
                user.logout();
            });

            menuItem.getElement().setAttribute(tabindex, "0"); // Make focusable
            menuItem.addClassName(fondo);
            horAuxFondo.add(menuBar);
        } else {
            avatarBasic.setName("Usuario");
            avatarBasic.setWidth("40px");
            avatarBasic.setHeight("40px");
            avatarBasic.addClassNames(LumoUtility.AlignSelf.END, "fondoAvatar");
            avatarBasic.getElement().setAttribute(arialabel, "Avatar de usuario");

            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem menuItem = menuBar.addItem(avatarBasic);
            menuItem.getElement().setAttribute(arialabel, "Menú de usuario");
            SubMenu subMenu = menuItem.getSubMenu();
            subMenu.addItem(new RouterLink("Iniciar sesión", MenuUsuarioView.class));

            menuItem.getElement().setAttribute(tabindex, "0"); // Make focusable
            menuItem.addClassName(fondo);
            horAuxFondo.add(menuBar);
        }

        horAux.add(toggle, image, viewTitle, horAuxFondo);

        horAuxFondo.setAlignItems(FlexComponent.Alignment.CENTER);
        horAux.setSizeFull();
        horAux.setSpacing(true);
        horAuxFondo.setSpacing(true);
        horAuxFondo.setSizeFull();
        horAuxFondo.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.END);

        horAux.addClassName(fondo);
        addToNavbar(true, horAux);
    }

    private void addDrawerContent() {
        Span appName = new Span("proYectFlow");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        appName.getElement().setAttribute(arialabel, "Nombre de la aplicación");
        appName.getElement().setAttribute("aria-level", "1"); // ARIA heading level
        appName.getElement().setAttribute("role", "heading"); // ARIA role
        appName.getElement().setAttribute(tabindex, "0"); // Make focusable
        Header header = new Header(appName);
        header.addClassName(fondo);
        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.getElement().setAttribute(arialabel, "Navegación principal");
        nav.getElement().setAttribute("role", "navigation"); // ARIA role
        nav.getElement().setAttribute(tabindex, "0"); // Make focusable
        nav.getElement().setAttribute("style", "overflow-y: auto;");

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            SideNavItem item;
            if (entry.icon() != null) {
                item = new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon()));
            } else {
                item = new SideNavItem(entry.title(), entry.path());
            }
            item.getElement().setAttribute(tabindex, "0"); // Make focusable
            item.getElement().setAttribute("role", "link"); // ARIA role
            item.getElement().setAttribute(arialabel, entry.title()); // ARIA label
            item.getElement().setAttribute("aria-current", "page"); // ARIA current page


            nav.addItem(item);
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.getElement().setAttribute(arialabel, "Pie de página");
        layout.getElement().setAttribute("role", "contentinfo"); // ARIA role
        layout.getElement().setAttribute(tabindex, "0"); // Make focusable
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}
