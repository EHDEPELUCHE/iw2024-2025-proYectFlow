package es.uca.iw.views.iniciosesión;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Inicio Sesión")
@Route("inicio-sesion")
@Menu(order = 2, icon = "line-awesome/svg/user.svg")
public class InicioSesiónView extends Composite<VerticalLayout> {

    public InicioSesiónView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        LoginForm loginForm = new LoginForm();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);

        getContent().add(layoutColumn2);
        layoutColumn2.add(loginForm);
    }
}
