package es.uca.iw.views.proyectos;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.data.Proyecto;
import es.uca.iw.data.Usuario;
import es.uca.iw.services.ProyectoService;
import es.uca.iw.services.UsuarioService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@PageTitle("Proyectos")
@Route("proyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@AnonymousAllowed
public class ProyectosView extends Div {

    private final ProyectoService proyectoService;
    private Grid<Proyecto> grid;
    private Filters filters;

    public ProyectosView(ProyectoService proyectoService) {
        
        setSizeFull();
        addClassNames("proyectos-view");
        this.proyectoService = proyectoService;

        
        filters = new Filters(() -> refreshGrid()) {
            @Override
            public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.conjunction();
            }


        };
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
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
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn("fechaSolicitud").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn("promotor").setAutoWidth(true);
        grid.addColumn("estado").setAutoWidth(true);
        


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

        private final TextField nombre = new TextField("Name");
        private final TextField phone = new TextField("Phone");
        private final DatePicker startDate = new DatePicker("Date of Birth");
        private final DatePicker endDate = new DatePicker();
        private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
        private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            nombre.setPlaceholder("First or last name");

            occupations.setItems("Insurance Clerk", "Mortarman", "Beer Coil Cleaner", "Scale Attendant");

            roles.setItems("Worker", "Supervisor", "Manager", "External");
            roles.addClassName("double-width");

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                nombre.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                occupations.clear();
                roles.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(nombre, phone, createDateRangeFilter(), occupations, roles, actions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");

            endDate.setPlaceholder("To");

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
