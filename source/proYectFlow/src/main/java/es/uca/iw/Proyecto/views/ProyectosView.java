package es.uca.iw.Proyecto.views;

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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

@PageTitle("Proyectos")
@Route("proyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class ProyectosView extends Div {
    private final ProyectoService proyectoService;
    private Grid<Proyecto> grid;
    private Filters filters;

    public ProyectosView(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
        initializeView();
    }

    private void initializeView() {
        H1 h1Titulo = createTitle();
        filters = createFilters();
        VerticalLayout layout = new VerticalLayout(h1Titulo, filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private H1 createTitle() {
        H1 h1Titulo = new H1("Proyectos");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);
        return h1Titulo;
    }

    private Filters createFilters() {
        return new Filters(this::refreshGrid) {
            @Override
            public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (!nombre.getValue().trim().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("nombre")), "%" + nombre.getValue().toLowerCase() + "%"));
                }
                if (!promotor.getValue().trim().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("promotor").get("nombre")), "%" + promotor.getValue().toLowerCase() + "%"));
                }
                if (startDate.getValue() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                            root.get("fechaSolicitud"), Timestamp.valueOf(startDate.getValue().atStartOfDay())));
                }
                if (endDate.getValue() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                            root.get("fechaSolicitud"), Timestamp.valueOf(endDate.getValue().atStartOfDay())));
                }
                if (!estado.isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("estado").in(
                            estado.getValue().stream().map(Proyecto.Estado::valueOf).toList()));
                }
                return predicate;
            }
        };
    }

    private Component createGrid() {
        grid = new Grid<>(Proyecto.class, false);
        configureGridColumns();
        grid.setItems(query -> proyectoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
       grid.setAllRowsVisible(true);
        return grid;
    }

    private void configureGridColumns() {
        grid.addColumn(proyecto -> proyecto.getSolicitante() != null ?
                proyecto.getSolicitante().getNombre() + " " + proyecto.getSolicitante().getApellido() :
                "Usuario no disponible").setHeader("Solicitante").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> proyecto.getPromotor() != null ?
                proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido() :
                "Sin promotor").setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn("fechaSolicitud").setAutoWidth(true);
        grid.addColumn("estado").setAutoWidth(true);
        grid.addComponentColumn(this::createDownloadButton).setHeader("PDF").setAutoWidth(true);
        grid.addComponentColumn(this::createEditButton).setHeader("Acciones").setAutoWidth(true);
    }

    private Button createDownloadButton(Proyecto proyecto) {
        Button downloadButton = new Button("Memoria");
        downloadButton.addClickListener(e -> downloadPdf(proyecto));
        return downloadButton;
    }

    private void downloadPdf(Proyecto proyecto) {
        byte[] pdfContent;
        try {
            pdfContent = proyectoService.getPdf(proyecto.getId());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (pdfContent != null) {
            StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(pdfContent));
            Anchor downloadLink = new Anchor(resource, "Download");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.getElement().setAttribute("style", "display: none;");
            add(downloadLink);
            downloadLink.getElement().callJsFunction("click");
            downloadLink.remove();
        }
    }

    private Button createEditButton(Proyecto proyecto) {
        Button evaluarButton = new Button("Editar");
        evaluarButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("EditarProyecto/" + (proyecto.getId()).toString()));
        });
        return evaluarButton;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    public static abstract class Filters extends Div implements Specification<Proyecto> {
        protected final TextField nombre = new TextField("Nombre");
        protected final TextField promotor = new TextField("Promotor");
        protected final DatePicker startDate = new DatePicker("Fecha solicitud");
        protected final DatePicker endDate = new DatePicker();
        protected final MultiSelectComboBox<String> estado = new MultiSelectComboBox<>("Estado del proyecto");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            configureFilters(onSearch);
        }

        private void configureFilters(Runnable onSearch) {
            nombre.setPlaceholder("Nombre o apellido");
            estado.setItems(Arrays.stream(Proyecto.Estado.values()).map(Enum::name).toList());

            Button resetBtn = createResetButton(onSearch);
            Button searchBtn = createSearchButton(onSearch);

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            horizontalLayout.add(nombre, promotor, createDateRangeFilter(), estado, actions);
            add(horizontalLayout);
        }

        private Button createResetButton(Runnable onSearch) {
            Button resetBtn = new Button("Borrar");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                nombre.clear();
                promotor.clear();
                endDate.clear();
                estado.clear();
                onSearch.run();
            });
            return resetBtn;
        }

        private Button createSearchButton(Runnable onSearch) {
            Button searchBtn = new Button("Buscar");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());
            return searchBtn;
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("Desde");
            endDate.setPlaceholder("Hasta");
            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }
    }
}
