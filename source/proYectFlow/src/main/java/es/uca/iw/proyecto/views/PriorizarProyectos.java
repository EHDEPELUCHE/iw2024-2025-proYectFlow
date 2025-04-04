package es.uca.iw.proyecto.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import java.io.IOException;

/**
 * La clase PriorizarProyectos representa una vista para priorizar proyectos.
 * Está anotada con @PageTitle, @Route, @Menu, @Uses y @RolesAllowed para definir su metadata y roles de seguridad.
 * Esta clase extiende Div e implementa un componente de UI para mostrar y gestionar proyectos.
 * 
 * La clase contiene los siguientes campos:
 * - convocatoria: La instancia actual de Convocatoria.
 * - proyectoService: Servicio para gestionar entidades de Proyecto.
 * - convocatoriaService: Servicio para gestionar entidades de Convocatoria.
 * - filters: Filtros para consultar entidades de Proyecto.
 * 
 * El constructor inicializa los servicios, recupera la Convocatoria actual y configura los componentes de la UI.
 * Muestra diferentes mensajes y diseños basados en el estado de la convocatoria y los proyectos.
 * 
 * El método createGrid crea un componente Grid para mostrar entidades de Proyecto con varias columnas y acciones.
 * Incluye botones para descargar PDFs y desarrollar proyectos, con diálogos para confirmación.
 * 
 * La clase Filters es una clase abstracta estática que extiende Div e implementa Specification<Proyecto>.
 * Se utiliza para definir filtros personalizados para consultar entidades de Proyecto.
 */
@PageTitle("Priorizar Proyectos ")
@Route("priorizarproyectos")
@Menu(order = 5, icon = "line-awesome/svg/archive-solid.svg")
@Uses(Icon.class)
@RolesAllowed("ROLE_CIO")
public class PriorizarProyectos extends Div {
    final Convocatoria convocatoria;
    private final ProyectoService proyectoService;
    private final ConvocatoriaService convocatoriaService;
    private Filters filters;

    public PriorizarProyectos(ProyectoService proyectoService, ConvocatoriaService convocatoriaService) {
        this.proyectoService = proyectoService;
        this.convocatoriaService = convocatoriaService;
        convocatoria = convocatoriaService.convocatoriaActual();
        H1 h1Titulo = new H1("Proyectos evaluados");
        h1Titulo.addClassNames(LumoUtility.Margin.Bottom.NONE, LumoUtility.Margin.Top.XLARGE,
                LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Left.LARGE);
        setSizeFull();
        H2 h2Titulo = new H2("Selecciona los proyectos que van a desarrollarse");
        h2Titulo.addClassNames(LumoUtility.Margin.Bottom.SMALL, LumoUtility.Margin.Top.SMALL,
                LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Left.LARGE);
        setSizeFull();
        addClassNames("proyectos-view");
        boolean hasInvalidStateProjects = proyectoService.list(PageRequest.of(0, Integer.MAX_VALUE)).stream()
            .anyMatch(proyecto -> (proyecto.getEstado() == Proyecto.Estado.AVALADO || proyecto.getEstado() == Proyecto.Estado.EVALUADO_TECNICAMENTE)
                && proyecto.getConvocatoria().equals(convocatoria));

        if (!convocatoriaService.convocatoriaActual().enPlazo()) {
            if (hasInvalidStateProjects)
                add(new H1("Existen proyectos en estado avalado o evaluado técnicamente"));
            else {
                filters = new Filters() {
                    @Override
                    public Predicate toPredicate(Root<Proyecto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        Predicate estadoPredicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("estado"), Proyecto.Estado.EVALUADO_ESTRATEGICAMENTE),
                                criteriaBuilder.equal(root.get("convocatoria"), convocatoria));
                        query.orderBy(criteriaBuilder.desc(root.get("puntuacionEstrategica")));
                        return criteriaBuilder.and(estadoPredicate);
                    }
                };
                VerticalLayout layout = new VerticalLayout(h1Titulo, h2Titulo, filters, createGrid());
                layout.setSizeFull();
                layout.setPadding(false);
                layout.setSpacing(false);
                add(layout);
            }
        } else
            add(new H1("Aún no se puede realizar la evaluación"));
    }

    private Component createGrid() {
        Grid<Proyecto> grid = new Grid<>(Proyecto.class, false);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("interesados").setAutoWidth(true);
        grid.addColumn("alcance").setAutoWidth(true);
        grid.addColumn(proyecto -> (proyecto.getPromotor().getNombre() + " " + proyecto.getPromotor().getApellido())).setHeader("Promotor").setAutoWidth(true);
        grid.addColumn("coste").setAutoWidth(true);
        grid.addColumn("recHumanos").setAutoWidth(true).setHeader("Recursos Humanos Necesarios");
        grid.addColumn("aportacionInicial").setAutoWidth(true);
        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaSolicitud()))
                .setHeader("Fecha Solicitud").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn(solicitud -> solicitud.formatoFecha(solicitud.getFechaLimite()))
                .setHeader("Fecha Límite").setAutoWidth(true)
                .setSortable(true);
        grid.addColumn("estado").setAutoWidth(true);

        grid.addComponentColumn(proyecto -> DownloadPdfComponent.createDownloadButton("Memoria", () -> {
            try {
                return proyectoService.getPdf(proyecto.getId());
            } catch (IOException ex) {
                throw new RuntimeException("Error al obtener el PDF", ex);
            }
        })).setHeader("PDF").setAutoWidth(true);

        grid.addComponentColumn(proyecto -> {
            Button evaluarButton = new Button("Desarrollar");
            evaluarButton.addClickListener(e -> {
                Dialog dialog = new Dialog();
                dialog.setModal(true);
                dialog.setResizable(false);
                dialog.open();
                dialog.setHeaderTitle("¿Desarrollar este proyecto?");
                H2 mostrarPresupuesto = new H2("Presupuesto restante: " + convocatoria.getPresupuestorestante());
                H3 mostrarRecHumanos = new H3("Recursos humanos disponibles: " + convocatoria.getRecHumanosRestantes());
                Button confirmar = new Button("Confirmar");
                confirmar.setClassName("buttonPrimary");
                confirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                confirmar.addClickShortcut(Key.ENTER);
                confirmar.addClickListener(event -> {
                    //Si da el dinero
                    try{
                        if (convocatoria.getPresupuestorestante().compareTo(proyecto.getCoste().subtract(proyecto.getAportacionInicial())) >= 0) {
                            if(convocatoria.getRecHumanosRestantes() < proyecto.getRecHumanos()){
                                Notification.show("No hay suficientes recursos humanos disponibles para desarrollar este proyecto");
                                return;
                            }
                            convocatoria.setPresupuestorestante(convocatoria.getPresupuestorestante().subtract(proyecto.getCoste().subtract(proyecto.getAportacionInicial())));
                            convocatoria.setRecHumanosRestantes(convocatoria.getRecHumanosRestantes() - proyecto.getRecHumanos());
                            convocatoriaService.guardar(convocatoria);
                            proyectoService.desarrollar(proyecto, true);
                            dialog.close();
                            Notification.show("Este proyecto se realizará");
                        } else
                            Notification.show("El presupuesto disponible es insuficiente para desarrollar este proyecto");
                        Notification notification = new Notification();
                        notification.setDuration(5);
                        UI.getCurrent().getPage().reload();
                    }catch(OptimisticLockingFailureException ex){
                        Notification.show("Error al desarrollar el proyecto, intentelo mása tarde");
                    }
                    
                });
                Button cancelar = new Button("Cancelar");
                cancelar.setClassName("buttonSecondary");

                cancelar.addClickListener(event -> dialog.close());
                Button noDesarrollar = new Button("No desarrollar");
                noDesarrollar.addClassName("button-danger");
                noDesarrollar.addThemeVariants(ButtonVariant.LUMO_ERROR);
                noDesarrollar.addClickListener(event -> {
                    proyectoService.desarrollar(proyecto, false);
                    getUI().ifPresent(ui -> ui.getPage().reload());
                    dialog.close();
                });
                dialog.add(mostrarPresupuesto, mostrarRecHumanos);
                dialog.getFooter().add(noDesarrollar, confirmar, cancelar);
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

    public abstract static class Filters extends Div implements Specification<Proyecto> {
        //Empty class
    }
}