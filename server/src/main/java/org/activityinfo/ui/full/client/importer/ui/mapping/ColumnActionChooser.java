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
import org.activityinfo.ui.full.client.importer.model.ColumnAction;
import org.activityinfo.ui.full.client.importer.ui.BootstrapFormBuilder;

import java.util.List;
import java.util.Map;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radio buttons.
 */
public class ColumnActionChooser extends Composite implements HasValue<ColumnAction> {

    private static int nextUniqueGroupNum = 1;

    private String group = "columnBinding" + (nextUniqueGroupNum++);

    private Map<ColumnAction, RadioButton> buttons = Maps.newHashMap();
    private ColumnAction value;

    public ColumnActionChooser(List<ColumnAction> actions) {
        BootstrapFormBuilder form = new BootstrapFormBuilder();


        for (final ColumnAction action : actions) {
            addRadioButton(form, action);
        }
        initWidget(form.buildForm());
    }

    private RadioButton addRadioButton(BootstrapFormBuilder form, final ColumnAction action) {
        RadioButton button = new RadioButton(group, action.getLabel());
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (!Objects.equal(action, value)) {
                    value = action;
                    ValueChangeEvent.fire(ColumnActionChooser.this, value);
                }
            }
        });
        buttons.put(action, button);
        return button;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<ColumnAction> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public ColumnAction getValue() {
        return value;
    }

    @Override
    public void setValue(ColumnAction value) {
        setValue(value, true);
    }

    @Override
    public void setValue(ColumnAction newValue, boolean fireEvents) {
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
