package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RadioButton;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.ui.BootstrapFormBuilder;

import java.util.List;
import java.util.Map;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radio buttons.
 */
public class FieldChooser extends Composite implements HasValue<FieldPath> {

    private static int nextUniqueGroupNum = 1;

    private String group = "columnBinding" + (nextUniqueGroupNum++);

    private Map<FieldPath, RadioButton> buttons = Maps.newHashMap();
    private FieldPath value;

    public FieldChooser(List<FieldPath> properties) {
        BootstrapFormBuilder form = new BootstrapFormBuilder();

        addRadioButton(form, null, "Ignore");

        for (final FieldPath property : properties) {
            addRadioButton(form, property, property.getLabel());
        }
        initWidget(form.buildForm());
    }

    private RadioButton addRadioButton(BootstrapFormBuilder form, final FieldPath property, String label) {
        RadioButton button = new RadioButton(group, label);
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (!Objects.equal(property, value)) {
                    value = property;
                    ValueChangeEvent.fire(FieldChooser.this, value);
                }
            }
        });
        buttons.put(property, button);
        return button;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<FieldPath> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public FieldPath getValue() {
        return value;
    }

    @Override
    public void setValue(FieldPath value) {
        setValue(value, true);
    }

    @Override
    public void setValue(FieldPath newValue, boolean fireEvents) {
        if (!Objects.equal(this.value, newValue)) {
            this.value = newValue;

            // update the UI
            RadioButton button = buttons.get(newValue);
            assert button != null : "No button for " + newValue;
            button.setValue(true);

            // update our internal value
            buttons.get(newValue).setValue(true);

            // notify listeners
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }
    }
}
