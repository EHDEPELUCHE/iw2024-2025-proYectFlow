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
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.Usuario.views.MenuUsuarioView;
import es.uca.iw.Usuario.views.MisDatosView;
import es.uca.iw.security.AuthenticatedUser;

import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {
    AuthenticatedUser user;

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
        toggle.setClassName("fondo");
        HorizontalLayout horAux = new HorizontalLayout();
        HorizontalLayout horAuxFondo = new HorizontalLayout();
        horAux.setSpacing(true);
        horAux.setMargin(true);
        horAux.setPadding(true);
        horAux.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image("favicon.ico", "Logo");
        image.setHeight("50px");
        image.setWidth("50px");

        image.addClickListener(imageClickEvent -> {
            image.getUI().ifPresent(ui -> ui.navigate(""));
        });

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, "fondo");

        Image ayuda = new Image("img/ayuda.png", "Ayuda");
        ayuda.setHeight("50px");
        ayuda.setWidth("50px");
        horAuxFondo.add(ayuda);
        ayuda.addClickListener(ayudaClickEvent -> {
            RouterLink pagayuda = new RouterLink("Ayuda", AyudaView.class);
            ayuda.getUI().ifPresent(ui -> ui.navigate("Ayuda"));
        });

        Avatar avatarBasic = new Avatar();
        if (user.get().isPresent()) {
            Optional<Usuario> usuario = user.get();
            avatarBasic.setName(usuario.get().getNombre());
            avatarBasic.setWidth("40px");
            avatarBasic.setHeight("40px");
            avatarBasic.addClassNames(LumoUtility.AlignSelf.END, "fondoAvatar");
            MenuBar menuBar = new MenuBar();

            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem menuItem = menuBar.addItem(avatarBasic);
            SubMenu subMenu = menuItem.getSubMenu();
            subMenu.addItem(new RouterLink("Mis datos", MisDatosView.class));

            MenuItem logoutItem = subMenu.addItem("Cerrar sesión");
            logoutItem.addClickListener(event -> {
                user.logout();
            });

            menuItem.addClassName("fondo");
            horAuxFondo.add(menuBar);
        } else {
            avatarBasic.setName("Usuario");
            avatarBasic.setWidth("40px");
            avatarBasic.setHeight("40px");
            avatarBasic.addClassNames(LumoUtility.AlignSelf.END, "fondoAvatar");
            MenuBar menuBar = new MenuBar();

            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            MenuItem menuItem = menuBar.addItem(avatarBasic);
            SubMenu subMenu = menuItem.getSubMenu();
            subMenu.addItem(new RouterLink("Iniciar sesión", MenuUsuarioView.class));

            menuItem.addClassName("fondo");
            horAuxFondo.add(menuBar);
        }



        horAux.add(toggle, image, viewTitle, horAuxFondo);

        horAuxFondo.setAlignItems(FlexComponent.Alignment.CENTER);
        horAux.setSizeFull();
        horAux.setSpacing(true);
        horAuxFondo.setSpacing(true);
        horAuxFondo.setSizeFull();
        horAuxFondo.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.END);

        horAux.addClassName("fondo");
        //horAux.expand(viewTitle, avatarBasic);
        addToNavbar(true, horAux);
    }

    private void addDrawerContent() {
        Span appName = new Span("proYectFlow");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
        header.addClassName("fondo");
        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        // layout.addClassName("fondo");
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
