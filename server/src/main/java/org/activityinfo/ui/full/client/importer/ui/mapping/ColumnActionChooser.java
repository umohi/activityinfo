package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.bedatadriven.rebar.sql.annotations.Column;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RadioButton;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.api2.shared.model.CoordinateAxis;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.BootstrapFormBuilder;

import java.util.List;
import java.util.Map;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radio buttons.
 */
public class ColumnActionChooser extends Composite implements HasValue<ColumnTarget> {

    private static int nextUniqueGroupNum = 1;

    private String group = "columnBinding" + (nextUniqueGroupNum++);

    private Map<ColumnTarget, RadioButton> buttons = Maps.newHashMap();
    private ColumnTarget value;

    public ColumnActionChooser(List<ColumnOption> options) {
        BootstrapFormBuilder form = new BootstrapFormBuilder();

        for(ColumnOption option : options) {
            addRadioButton(form, option.getTarget(), option.getLabel());
        }

        initWidget(form.buildForm());
    }

    private RadioButton addRadioButton(BootstrapFormBuilder form, final ColumnTarget action, String label) {
        RadioButton button = new RadioButton(group, label);
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
            ValueChangeHandler<ColumnTarget> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public ColumnTarget getValue() {
        return value;
    }

    @Override
    public void setValue(ColumnTarget value) {
        setValue(value, true);
    }

    @Override
    public void setValue(ColumnTarget newValue, boolean fireEvents) {
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
