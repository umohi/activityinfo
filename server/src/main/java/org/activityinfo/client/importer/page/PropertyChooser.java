package org.activityinfo.client.importer.page;

import java.util.List;
import java.util.Map;

import org.activityinfo.client.importer.ont.DataTypeProperty;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * A simple panel that provides the user with a list of possible properties
 * to which to map the column as a list of radio buttons.
 */
public class PropertyChooser<T> extends Composite implements HasValue<DataTypeProperty<T, ?>> {
	
	private static int nextUniqueGroupNum = 1;

	private String group = "columnBinding" + (nextUniqueGroupNum++);
	
	private Map<DataTypeProperty<T, ?>, RadioButton> buttons = Maps.newHashMap();
	private DataTypeProperty<T, ?> value;

	public PropertyChooser(List<DataTypeProperty<T, ?>> properties) {
		FlowPanel panel = new FlowPanel();
		
		panel.add(addRadioButton(null, "Ignore"));
		
		for(final DataTypeProperty<T, ?> property : properties) {
			panel.add(addRadioButton(property, property.getLabel()));
		}
		initWidget(panel);
	}

	private RadioButton addRadioButton(final DataTypeProperty<T, ?> property, String label) {
		RadioButton button = new RadioButton(group, label);
		button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(!Objects.equal(property, value)) {
					value = property;
					ValueChangeEvent.fire(PropertyChooser.this, value);
				}
			}
		});
		buttons.put(property, button);
		return button;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<DataTypeProperty<T, ?>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public DataTypeProperty<T, ?> getValue() {
		return value;
	}

	@Override
	public void setValue(DataTypeProperty<T, ?> value) {
		setValue(value, true);
	}

	@Override
	public void setValue(DataTypeProperty<T, ?> newValue, boolean fireEvents) {
		if(!Objects.equal(this.value, newValue)) {
			this.value = newValue;
			
			// update the UI
			RadioButton button = buttons.get(newValue);
			assert button != null : "No button for " + newValue;
			button.setValue(true);
			
			// update our internal value
			buttons.get(newValue).setValue(true);
			
			// notify listeners
			if(fireEvents) {
				ValueChangeEvent.fire(this, value);
			}
		}
	}
}
