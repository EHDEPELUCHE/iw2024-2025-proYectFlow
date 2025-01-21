package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import es.uca.iw.convocatoria.Convocatoria;
import es.uca.iw.convocatoria.ConvocatoriaService;
import es.uca.iw.global.DownloadPdfComponent;
import es.uca.iw.proyecto.Proyecto;
import es.uca.iw.proyecto.ProyectoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * La clase ProyectosView representa una vista para gestionar proyectos en la aplicación.
 * Está anotada con varias anotaciones de Vaadin y Spring Security para definir su
 * ruta, título de la página, orden en el menú y control de acceso.
 * 
 * Se crea una instancia de esta clase con un ProyectoService, que se utiliza para
 * interactuar con el backend para operaciones relacionadas con proyectos.
 * 
 * La vista consta de un título, filtros y una cuadrícula para mostrar los proyectos.
 * 
 * Los filtros permiten a los usuarios buscar proyectos en función de varios criterios como
 * nombre, promotor, rango de fechas y estado del proyecto.
 * 
 * La cuadrícula muestra los detalles del proyecto e incluye columnas para varios atributos del
 * proyecto como nombre, descripción, interesados, alcance, promotor, coste,
 * aportación inicial, fecha de solicitud y estado. También incluye botones para
 * descargar un PDF del proyecto y editar el proyecto.
 * 
 * La clase Filters es una clase interna estática abstracta que define los componentes de filtro
 * y su comportamiento. Implementa la interfaz Specification para proporcionar la lógica de filtrado.
 * 
 * El método createDownloadButton crea un botón para descargar un PDF del
 * proyecto, y el método createEditButton crea un botón para editar el proyecto.
 * 
 * El método refreshGrid actualiza los datos de la cuadrícula.
 */
@PageTitle("Proyectos")
@Route("proyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_ADMIN")
public class ProyectosView extends Div {
    static final String ARIALABEL = "aria-label";
    static final String NOMBRE = "nombre";
    static final String FECHA_SOLICITUD = "fechaSolicitud";
    private final ProyectoService proyectoService;
    protected final ConvocatoriaService convocatoriaService;
    private Grid<Proyecto> grid;
    private Filters filters;

    public ProyectosView(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
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
        h1Titulo.getElement().setAttribute(ARIALABEL, "Título de la página");
        return h1Titulo;
    }

    private Filters createFilters() {
        return new Filters(this::refreshGrid, convocatoriaService) {
            @Override
            public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                if (!nombre.getValue().trim().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                            criteriaBuilder.lower(root.get(NOMBRE)), "%" + nombre.getValue().toLowerCase() + "%"));
                }
                if (!promotor.getValue().trim().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("promotor").get(NOMBRE)), "%" + promotor.getValue().toLowerCase() + "%"));
                }
                if (startDate.getValue() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(
                            root.get(FECHA_SOLICITUD), Timestamp.valueOf(startDate.getValue().atStartOfDay())));
                }
                if (endDate.getValue() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(
                            root.get(FECHA_SOLICITUD), Timestamp.valueOf(endDate.getValue().atStartOfDay())));
                }
                if (!estado.isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("estado").in(
                            estado.getValue().stream().map(Proyecto.Estado::valueOf).toList()));
                }
                if (!convocatoria.isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("convocatoria").get("nombre").in(convocatoria.getValue()));
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
        grid.getElement().setAttribute(ARIALABEL, "Tabla de proyectos");
        return grid;
    }

    private void configureGridColumns() {
        grid.addColumn(proyecto -> proyecto.getSolicitante() != null ?
                proyecto.getSolicitante().getNombre() + " " + proyecto.getSolicitante().getApellido() :
                "Usuario no disponible").setHeader("Solicitante").setAutoWidth(true);
        grid.addColumn(NOMBRE).setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> proyecto.getPromotor() != null ?
                proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido() :
                "Sin promotor").setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn(FECHA_SOLICITUD).setAutoWidth(true);
        grid.addColumn("estado").setAutoWidth(true);
        grid.addColumn(proyecto -> proyecto.getJefe() != null ?
                proyecto.getJefe().getNombre() + " " + proyecto.getJefe().getApellido() :
                "Sin jefe").setHeader("Jefe de proyecto").setAutoWidth(true);
        grid.addColumn(proyecto-> proyecto.getDirector() != null ?
                proyecto.getDirector() :  "Sin director").setHeader("Director").setAutoWidth(true);
        grid.addColumn(proyecto -> proyecto.getConvocatoria() != null ?
            proyecto.getConvocatoria().getNombre() :
            "Sin convocatoria").setHeader("Convocatoria").setAutoWidth(true);
        grid.addComponentColumn(this::createDownloadButton).setHeader("PDF").setAutoWidth(true);
        grid.addComponentColumn(this::createEditButton).setHeader("Acciones").setAutoWidth(true);
    }

    private Button createDownloadButton(Proyecto proyecto) {
        return DownloadPdfComponent.createDownloadButton("Memoria", () -> {
            try {
                return proyectoService.getPdf(proyecto.getId());
            } catch (IOException ex) {
                throw new RuntimeException("Error al obtener el PDF", ex);
            }
        });
    }

    private Button createEditButton(Proyecto proyecto) {
        Button evaluarButton = new Button("Editar");
        evaluarButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("EditarProyecto/" + (proyecto.getId()).toString())));
        evaluarButton.getElement().setAttribute(ARIALABEL, "Editar proyecto");
        return evaluarButton;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    public abstract static class Filters extends Div implements Specification<Proyecto> {
        protected final TextField nombre = new TextField("Nombre");
        protected final TextField promotor = new TextField("Promotor");
        protected final DatePicker startDate = new DatePicker("Fecha solicitud");
        protected final DatePicker endDate = new DatePicker();
        protected final MultiSelectComboBox<String> estado = new MultiSelectComboBox<>("Estado del proyecto");
        protected final MultiSelectComboBox<String> convocatoria = new MultiSelectComboBox<>("Convocatoria");

        protected Filters(Runnable onSearch, ConvocatoriaService convocatoriaService) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            configureFilters(onSearch, convocatoriaService);
        }

        private void configureFilters(Runnable onSearch, ConvocatoriaService convocatoriaService) {
            nombre.setPlaceholder("Nombre o apellido");
            estado.setItems(Arrays.stream(Proyecto.Estado.values()).map(Enum::name).toList());
            // Aquí puedes añadir las convocatorias disponibles
            convocatoria.setItems(convocatoriaService.findAll().stream().map(Convocatoria::getNombre).toList());

            Button resetBtn = createResetButton(onSearch);
            Button searchBtn = createSearchButton(onSearch);

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            horizontalLayout.add(nombre, promotor, createDateRangeFilter(), estado, convocatoria, actions);
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
                convocatoria.clear();
                onSearch.run();
            });
            resetBtn.getElement().setAttribute(ARIALABEL, "Borrar filtros");
            return resetBtn;
        }

        private Button createSearchButton(Runnable onSearch) {
            Button searchBtn = new Button("Buscar");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());
            searchBtn.getElement().setAttribute(ARIALABEL, "Buscar proyectos");
            return searchBtn;
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("Desde");
            endDate.setPlaceholder("Hasta");
            startDate.setAriaLabel("Fecha desde");
            endDate.setAriaLabel("Fecha hasta");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }
    }
}