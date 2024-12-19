package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.global.GLOBALES;
import es.uca.iw.security.AuthenticatedUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@PageTitle("Proyectos OTP")
@Route("proyectosOTP")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_OTP")
public class ProyectosOTPView extends Div {
    static AuthenticatedUser user;
    private final ProyectoService proyectoService;
    private Grid<Proyecto> grid;
    private Filters filters;

    public ProyectosOTPView(ProyectoService proyectoService, AuthenticatedUser user) {
        this.user = user;
        H1 h1Titulo = new H1("Proyectos por evaluar");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);
        setSizeFull();
        addClassNames("proyectos-view");
        this.proyectoService = proyectoService;
        Date hoy = new Date();
        if (GLOBALES.FECHA_LIMITE.compareTo(hoy) > 0) {
            filters = new Filters() {
                @Override
                public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Predicate estadoPredicate = criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.avalado);
                    return criteriaBuilder.and(estadoPredicate);
                }
            };
            VerticalLayout layout = new VerticalLayout(h1Titulo, filters, createGrid());
            layout.setSizeFull();
            layout.setPadding(false);
            layout.setSpacing(false);
            add(layout);
        } else {
            add(new H1("Aún no se puede realizar la evaluación"));
        }
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");
        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    private Component createGrid() {
        grid = new Grid<>(Proyecto.class, false);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> (proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido())).setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn("fechaSolicitud").setAutoWidth(true);
        grid.addColumn("fechaLimite").setAutoWidth(true);
        grid.addColumn("estado").setAutoWidth(true);
        grid.addComponentColumn(proyecto -> {
            Button downloadButton = new Button("Memoria");
            downloadButton.addClickListener(e -> {
                // Logic to download the PDF
                byte[] pdfContent = null;
                try {
                    pdfContent = proyectoService.getPdf(proyecto.getId());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                if (pdfContent != null) {
                    byte[] finalPdfContent = pdfContent;
                    StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(finalPdfContent));
                    Anchor downloadLink = new Anchor(resource, "Download");
                    downloadLink.getElement().setAttribute("download", true);
                    downloadLink.getElement().setAttribute("style", "display: none;");
                    add(downloadLink);
                    downloadLink.getElement().callJsFunction("click");
                    downloadLink.remove();
                }
            });
            return downloadButton;
        }).setHeader("PDF").setAutoWidth(true);

        grid.addComponentColumn(proyecto -> {
            Button evaluarButton = new Button("Evaluar");
            evaluarButton.addClickListener(e -> {
                getUI().ifPresent(ui -> ui.navigate("ValoracionTecnica/" + (proyecto.getId()).toString()));
            });
            return evaluarButton;
        }).setHeader("Acciones").setAutoWidth(true);

        grid.setItems(query -> proyectoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    public static abstract class Filters extends Div implements Specification<Proyecto> {
        private final Optional<Usuario> promotor = user.get();

        private final Proyecto.Estado estado = Proyecto.Estado.solicitado;
        //private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");
    }
}
