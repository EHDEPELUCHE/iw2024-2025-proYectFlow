package es.uca.iw.proyecto;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import es.uca.iw.global.DownloadPdfComponent;


/**
 * InfoProyecto es un componente de UI que muestra información detallada sobre un proyecto.
 * Extiende Composite con un VerticalLayout como su contenido.
 * 
 * @param proyectoService El servicio utilizado para recuperar datos relacionados con el proyecto.
 * @param proyectoAux El proyecto cuyos detalles se van a mostrar.
 * 
 * El componente muestra los siguientes detalles del proyecto:
 * - Solicitante
 * - Promotor
 * - Jefe
 * - Director
 * - Fecha de Solicitud
 * - Nombre
 * - Descripción
 * - Alcance
 * - Interesados
 * - Coste
 * - Aportación Inicial
 * - Fecha Límite de puesta en marcha
 * 
 * Además, proporciona un botón para descargar un documento PDF relacionado con el proyecto.
 */
public class InfoProyecto extends Composite<VerticalLayout>{
    public InfoProyecto(ProyectoService proyectoService, Proyecto proyectoAux) {
            getContent().add(new H1("Detalles del Proyecto"));

            FormLayout formLayout = new FormLayout();
            formLayout.setWidth("100%");
            if(proyectoAux.getSolicitante() != null)
                formLayout.addFormItem(new Span(proyectoAux.getSolicitante().getNombre() + " " + proyectoAux.getSolicitante().getApellido()), "Solicitante");
            if(proyectoAux.getPromotor() != null)
                formLayout.addFormItem(new Span(proyectoAux.getPromotor().getNombre() + " "+ proyectoAux.getPromotor().getApellido()), "Promotor");
            if(proyectoAux.getJefe() != null)
                formLayout.addFormItem(new Span(proyectoAux.getJefe().getNombre() + " "+ proyectoAux.getJefe().getApellido()), "Jefe");
            if(proyectoAux.getDirector() != null)
                formLayout.addFormItem(new Span(proyectoAux.getDirector()), "Director");
            LocalDate localDate = proyectoAux.getFechaSolicitud().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            FormLayout.FormItem formItem = formLayout.addFormItem(new Span(localDate.format(formatterDate)), "Fecha de Solicitud");
            formItem.getElement().getStyle().set("white-space", "nowrap");

            formLayout.addFormItem(new Span(proyectoAux.getNombre()), "Nombre");
            formLayout.addFormItem(new Span(proyectoAux.getDescripcion()), "Descripción");
            formLayout.addFormItem(new Span(proyectoAux.getAlcance()), "Alcance");
            formLayout.addFormItem(new Span(proyectoAux.getInteresados()), "Interesados");

            formLayout.addFormItem(new Span(proyectoAux.getCoste() + "€"), "Coste");
            formLayout.addFormItem(new Span(proyectoAux.getAportacionInicial() + "€"), "Aportación Inicial");

            if (proyectoAux.getFechaLimite() != null) {
                LocalDate localDateL = proyectoAux.getFechaLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter formatterDateL = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                formLayout.addFormItem(new Span(localDateL.format(formatterDateL)), "Fecha Límite de puesta en marcha");
            }

            Button downloadButton = DownloadPdfComponent.createDownloadButton("Memoria", () -> {
                try {
                    return proyectoService.getPdf(proyectoAux.getId());
                } catch (IOException ex) {
                    throw new RuntimeException("Error al obtener el PDF", ex);
                }
            });
            getContent().add(formLayout, downloadButton);
    }

}
