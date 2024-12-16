package es.uca.iw.views.proyectos;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.data.Proyecto;
import es.uca.iw.services.ProyectoService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@PageTitle("Todos los Proyectos")
@Route("proyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@AnonymousAllowed
public class ProyectosView extends Div {

    private final ProyectoService proyectoService;
    private Grid<Proyecto> grid;
    private Filters filters;

    public ProyectosView(ProyectoService proyectoService) {
        H1 h1Titulo = new H1("Proyectos");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);

        setSizeFull();
        addClassNames("proyectos-view");
        this.proyectoService = proyectoService;

        filters = new Filters(() -> refreshGrid()) {
            @Override
            public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.conjunction();
            }
        };

        VerticalLayout layout = new VerticalLayout(h1Titulo, filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
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
        grid.addColumn(proyecto -> proyecto.getSolicitante().getNombre() + " " + proyecto.getSolicitante().getApellido())
                .setHeader("Solicitante").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> {
            if (proyecto.getPromotor() != null) {
            return proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido();
            } else {
            return "Sin promotor";
            }
        }).setHeader("Promotor").setAutoWidth(true);

        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn("fechaSolicitud").setAutoWidth(true);
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

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    public static abstract class Filters extends Div implements Specification<Proyecto> {

        private final TextField nombre = new TextField("Nombre");
        private final TextField promotor = new TextField("Promotor");
        private final DatePicker startDate = new DatePicker("Fecha solicitud");
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> estado = new MultiSelectComboBox<>("Estado del proyecto");
        //private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            nombre.setPlaceholder("Nombre o apellido");

            estado.setItems("Solicitado", "Avalado", "Aceptado", "En desarollo", "Denegado");

            //roles.setItems("Worker", "Supervisor", "Manager", "External");
            //roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Borrar");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                nombre.clear();
                promotor.clear();
                startDate.clear();
                endDate.clear();
                estado.clear();
                //roles.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Buscar");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(nombre, promotor, createDateRangeFilter(), estado, /*roles,*/ actions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("Desde");

            endDate.setPlaceholder("Hasta");

            // For screen readers
            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }
    }
}

