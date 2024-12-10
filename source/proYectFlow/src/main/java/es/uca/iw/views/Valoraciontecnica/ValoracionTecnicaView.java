package es.uca.iw.views.Valoraciontecnica;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import es.uca.iw.data.Proyecto;
import es.uca.iw.services.ProyectoService;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Map;

@PageTitle("Valoración Técnica")
@Route("ValoracionTecnica")
@Menu(order = 1, icon = "line-awesome/svg/user.svg")
@RolesAllowed("ROLE_OTP")
public class ValoracionTecnicaView extends Composite<VerticalLayout> implements HasUrlParameter<Proyecto>{
    Proyecto proyecto;
    ProyectoService proyectoService;

    public ValoracionTecnicaView(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
        if (proyecto == null) {
            H1 title = new H1("Ha ocurrido un error, no se encuentra el proyecto :(");
            getContent().add(title);
        }else{
            //MOSTRAR DATOS DEL PROYECTO

            //Mostramos el precio total menos la valoración inicial
            BigDecimalField precio = new BigDecimalField("Precio total");
            precio.setLabel("Precio total");
            precio.setValue(proyecto.getCoste().subtract(proyecto.getAportacionInicial()));
            getContent().add(precio);

            //Dejamos que rellene las horas que estima que tardará y recursos
            BigDecimalField Horas = new BigDecimalField("Horas total");
            Horas.setLabel("Horas totales de nuestros recursos humanos");
            getContent().add(Horas);



            //Idoneidad técnica
            BigDecimalField Idoneidadtecnica = new BigDecimalField("Idoneidad total");
            Idoneidadtecnica.setLabel("Idoneidad total");
            getContent().add(Idoneidadtecnica);

            Button Guardar = new Button("Guardar");
            Guardar.addClickListener(e -> {proyectoService.setValoracionTecnica(precio.getValue(), Horas.getValue(), Idoneidadtecnica.getValue(), proyecto);});

        }
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter  Proyecto parameter) {
        this.proyecto = parameter;
    }
}
