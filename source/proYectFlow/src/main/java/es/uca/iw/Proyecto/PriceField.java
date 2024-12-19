package es.uca.iw.Proyecto;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;

import java.math.BigDecimal;

public class PriceField extends CustomField<PriceField.Price> {
    private String[] currencies = new String[]{"EUR", "USD", "PLN"};
    private Select<String> currency;
    private BigDecimalField amount;

    public PriceField() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        amount = new BigDecimalField();
        amount.setPlaceholder("Amount");

        currency = new Select<>();
        currency.setItems(currencies);
        currency.setWidth("100px");

        horizontalLayout.add(amount, currency);

        add(horizontalLayout);
    }

    @Override
    protected Price generateModelValue() {
        return new Price(amount.getValue(), currency.getValue());
    }

    @Override
    protected void setPresentationValue(Price value) {
        amount.setValue(value.amount);
        currency.setValue(value.currency);
    }

    public record Price(BigDecimal amount, String currency) {
    }
}