package es.uca.iw.global;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.util.function.Supplier;

public class DownloadPdfComponent {

    public static Button createDownloadButton(String buttonLabel, Supplier<byte[]> pdfSupplier) {
        Button downloadButton = new Button(buttonLabel);
        downloadButton.addClickListener(e -> {
            byte[] pdfContent;
            try {
                pdfContent = pdfSupplier.get();
            } catch (Exception ex) {
                throw new RuntimeException("Error al obtener el PDF", ex);
            }

            if (pdfContent != null) {
                StreamResource resource = new StreamResource("Memoria.pdf", () -> new ByteArrayInputStream(pdfContent));
                Anchor downloadLink = new Anchor(resource, "Download");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getElement().setAttribute("style", "display: none;");
                downloadButton.getUI().ifPresent(ui -> {
                    ui.getElement().appendChild(downloadLink.getElement());
                    downloadLink.getElement().callJsFunction("click");
                    downloadLink.remove();
                });
            }
        });
        return downloadButton;
    }
}
