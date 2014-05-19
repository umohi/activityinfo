package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.activityinfo.core.shared.importing.model.ColumnAction;
import org.activityinfo.core.shared.importing.model.IgnoreAction;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.widget.RadioButton;

import java.util.List;
import java.util.Map;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radioButton buttons.
 */
public class ColumnActionSelector extends Composite implements HasValue<ColumnAction> {

    private static int nextUniqueGroupNum = 1;

    private String group = "columnBinding" + (nextUniqueGroupNum++);

    private Map<ColumnAction, RadioButton> buttons = Maps.newHashMap();
    private ColumnAction value = IgnoreAction.INSTANCE;
    private final RadioButton ignoreButton;


    public ColumnActionSelector(List<MapExistingAction> actions) {

        FlowPanel panel = new FlowPanel();

        ignoreButton = createRadioButton(I18N.CONSTANTS.ignoreColumnAction(), IgnoreAction.INSTANCE);
        ignoreButton.addStyleName(ColumnMappingStyles.INSTANCE.stateIgnored());
        ignoreButton.setValue(true);
        panel.add(ignoreButton);

        for(final MapExistingAction action : actions) {
            final ImportTarget target = action.getTarget();
            RadioButton button = createRadioButton(target.getLabel(), action);
            panel.add(button);
        }

        initWidget(new ScrollPanel(panel));
    }

    private RadioButton createRadioButton(String label, final ColumnAction action) {
        RadioButton button = new RadioButton(group, label);
        button.setTabIndex(buttons.size()+1);

        if(action == IgnoreAction.INSTANCE) {
            button.addStyleName(ColumnMappingStyles.INSTANCE.stateIgnored());
        }

        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (!Objects.equal(action, value)) {
                    value = action;
                    ValueChangeEvent.fire(ColumnActionSelector.this, value);
                }
            }
        });
        buttons.put(action, button);
        return button;
    }


    public void setFocus() {
        if(ignoreButton.getValue()) {
            ignoreButton.setFocus(true);
        }
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

            if(newValue == null) {
                // clear the old selection
                buttons.get(value).setValue(false);
                this.value = newValue;

            } else {
                this.value = newValue;

                // update the UI
                RadioButton button = buttons.get(newValue);
                assert button != null : "No button for " + newValue;
                button.setValue(true);

                // update our internal value
                buttons.get(newValue).setValue(true);
            }

            // notify listeners
            if (fireEvents) {
                ValueChangeEvent.fire(this, value);
            }
        }
    }

}
