package es.uca.iw.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
@StyleSheet("../frontend/styles/styles.css")
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        HorizontalLayout horAux = new HorizontalLayout();
        HorizontalLayout horAuxFondo = new HorizontalLayout();
        horAux.setSpacing(true);
        horAux.setMargin(true);
        horAux.setPadding(true);
        horAux.setAlignItems(FlexComponent.Alignment.CENTER);

        Image image = new Image("favicon.ico", "My Streamed Image");
        image.setHeight("50px");
        image.setWidth("50px");

        image.addClickListener(imageClickEvent -> {
            image.getUI().ifPresent(ui -> ui.navigate(""));
        });

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE);

        Avatar avatarBasic = new Avatar();
        avatarBasic.setName("Usuario");
        avatarBasic.setWidth("40px");
        avatarBasic.setHeight("40px");
        avatarBasic.addClassNames(LumoUtility.AlignSelf.END);
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuItem = menuBar.addItem(avatarBasic);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem("Profile");
        subMenu.addItem("Settings");
        subMenu.addItem("Help");
        subMenu.addItem("Sign out");


        horAuxFondo.add(menuBar);
        horAux.add(toggle, image, viewTitle, horAuxFondo);

        horAuxFondo.setAlignItems(FlexComponent.Alignment.END);
        horAux.setSizeFull();
        horAux.setSpacing(true);
        horAuxFondo.setSpacing(true);
        horAuxFondo.setSizeFull();
        horAuxFondo.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.END);

        //horAux.addClassName("fondo");
        //horAux.expand(viewTitle, avatarBasic);
        addToNavbar(true, toggle, horAux);
    }

    private void addDrawerContent() {
        Span appName = new Span("proYectFlow");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

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
