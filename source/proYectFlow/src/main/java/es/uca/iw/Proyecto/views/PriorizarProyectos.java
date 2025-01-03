package es.uca.iw.Proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import es.uca.iw.Convocatoria.Convocatoria;
import es.uca.iw.Convocatoria.ConvocatoriaService;
import es.uca.iw.Proyecto.Proyecto;
import es.uca.iw.Proyecto.ProyectoService;
import es.uca.iw.Proyecto.VisualizarProyectos;
import es.uca.iw.Usuario.Usuario;
import es.uca.iw.global.DownloadPdfComponent;
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

@PageTitle("Priorizar Proyectos ")
@Route("priorizarproyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_CIO")

public class PriorizarProyectos extends Div {
    static AuthenticatedUser user;
    private final ProyectoService proyectoService;
    private Filters filters;
    private final ConvocatoriaService convocatoriaService;
    final Convocatoria convocatoria;

    public PriorizarProyectos(ProyectoService proyectoService, AuthenticatedUser user, ConvocatoriaService convocatoriaService) {
        this.user = user;
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
        convocatoria = convocatoriaService.ConvocatoriaActual();
        H1 h1Titulo = new H1("Proyectos evaluados");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);
        setSizeFull();
        H2 h2Titulo = new H2("Selecciona los proyectos que van a desarrollarse");
        h2Titulo.addClassNames(LumoUtility.Margin.Bottom.SMALL, LumoUtility.Margin.Top.SMALL,
                LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Left.LARGE);
        setSizeFull();
        addClassNames("proyectos-view");
        Date hoy = new Date();
        boolean hasInvalidStateProjects = proyectoService.list(PageRequest.of(0, Integer.MAX_VALUE)).stream()
            .anyMatch(proyecto -> proyecto.getEstado() == Proyecto.Estado.avalado || proyecto.getEstado() == Proyecto.Estado.evaluadoTecnicamente);

        if (!convocatoriaService.ConvocatoriaActual().EnPlazo()) {
            if (hasInvalidStateProjects) {
                add(new H1("Existen proyectos en estado avalado o evaluado técnicamente"));
                return;
            }else{
                filters = new Filters() {
                    @Override
                    public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        Predicate estadoPredicate = criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.evaluadoEstrategicamente);
                        query.orderBy(criteriaBuilder.desc(root.get("puntuacionEstrategica")));
                        return criteriaBuilder.and(estadoPredicate);
                    }
                };
                VerticalLayout layout = new VerticalLayout(h1Titulo,h2Titulo, filters, createGrid());
                layout.setSizeFull();
                layout.setPadding(false);
                layout.setSpacing(false);
                add(layout);
            }
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
        Grid<Proyecto> grid = new Grid<>(Proyecto.class, false);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> (proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido())).setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaSolicitud()))
                .setHeader("Fecha Solicitud").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaLimite()))
                .setHeader("Fecha Límite").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn("estado").setAutoWidth(true);

        grid.addComponentColumn(proyecto -> {
            return DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                try {
                    return proyectoService.getPdf(proyecto.getId());
                } catch (IOException ex) {
                    throw new RuntimeException("Error al obtener el PDF", ex);
                }
            });
        }).setHeader("PDF").setAutoWidth(true);

        grid.addComponentColumn(proyecto -> {
            Button evaluarButton = new Button("Desarrollar");
            evaluarButton.addClickListener(e -> {
                Dialog dialog = new Dialog();
                dialog.setModal(true);
                dialog.setResizable(false);
                dialog.open();
                dialog.setHeaderTitle("¿Desarrollar este proyecto?");
                H2 mostrarPresupuesto = new H2("Presupuesto restante: " + convocatoria.getPresupuestorestante());
                Checkbox realizar = new Checkbox("Realizar proyecto");
                Button Confirmar = new Button("Confirmar");
                Confirmar.setClassName("buttonPrimary");
                Confirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Confirmar.addClickShortcut(Key.ENTER);
                Confirmar.addClickListener(event -> {
                    if (realizar.getValue()) {
                        //Si da el dinero
                        if (convocatoria.getPresupuestorestante().compareTo(proyecto.getCoste().subtract(proyecto.getAportacionInicial()))>=0){
                            convocatoria.setPresupuestorestante(convocatoria.getPresupuestorestante().subtract(proyecto.getCoste().subtract(proyecto.getAportacionInicial())));
                            convocatoriaService.guardar(convocatoria);
                            proyectoService.desarrollar(proyecto, realizar.getValue());
                            dialog.close();
                            Notification.show("Este proyecto se realizará");
                        }else{
                            Notification.show("El presupuesto disponible es insuficiente para desarrollar este proyecto");
                        }
                    }
                });
                Button cancelar = new Button("Cancelar");
                cancelar.setClassName("buttonSecondary");

                cancelar.addClickListener(event -> {dialog.close();});
                Button NoDesarrollar = new Button("No desarrollar");
                NoDesarrollar.addClassName("button-danger");
                NoDesarrollar.addThemeVariants(ButtonVariant.LUMO_ERROR);
                NoDesarrollar.addClickListener(event -> {
                    proyectoService.desarrollar(proyecto, false);
                    dialog.close();});
                dialog.add(mostrarPresupuesto, realizar);
                dialog.getFooter().add(NoDesarrollar, Confirmar, cancelar);
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
        private final Proyecto.Estado estado = Proyecto.Estado.evaluadoEstrategicamente;
    }
}

