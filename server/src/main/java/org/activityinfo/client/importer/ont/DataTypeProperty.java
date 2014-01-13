package org.activityinfo.client.importer.ont;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.activityinfo.client.importer.binding.PropertyBinder;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;


/**
 * 
 *  Defines a property to which a column could be matched
 * 
 *  @param <T> the type of the model
 */
public class DataTypeProperty<T, C> implements Property  {
		
	public enum Type {
		FREE_TEXT,
		CHOICE,
		DATE
	}
	
	private String id;
	private String label;
	private PropertyBinder<T, C> binder;
	private Function<String, C> converter;
	private Function<String, List<String>> suggestionOracle; 
	private Type type;

	
	private DataTypeProperty(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public static <T> DataTypeProperty<T, String> create(String id, String label, PropertyBinder<T, String> binder) {
		DataTypeProperty<T, String> property = new DataTypeProperty<T, String>(id, label);
		property.binder = binder;
		property.converter = Functions.identity();
		property.type = Type.FREE_TEXT;
		return property;
	}
	
	public static <T, C> DataTypeProperty<T, C> create(String id, String label, 
			Type type,
			PropertyBinder<T, C> binder, 
			Function<String, C> converter) {
		DataTypeProperty<T, C> property = new DataTypeProperty<T, C>(id, label);
		property.binder = binder;
		property.converter = converter;
		property.type = type;
		return property;
	}

	public static <T, C> DataTypeProperty<T, C> create(String id, String label, 
			Map<String, C> map,
			PropertyBinder<T, C> binder) {
		DataTypeProperty<T, C> property = new DataTypeProperty<T, C>(id, label);
		property.binder = binder;
		property.type = Type.CHOICE;
		property.converter = Functions.forMap(map, null);
		property.suggestionOracle = new SimpleSuggestionOracle(map.keySet());
		return property;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	public Type getType() {
		return type;
	}
	
	public Function<String, List<String>> getSuggestionOracle() {
		return suggestionOracle;
	}
	
	public boolean isRequired() {
		return true;
	}

	public void clearValue(T model) {
		binder.update(model, null);
	}
	
	public boolean tryConvertAndUpdate(T model, String stringValue) {
		C convertedValue = converter.apply(stringValue);
		if(convertedValue == null) {
			clearValue(model);
			return false;
		} else {
			binder.update(model, convertedValue);
			return true;
		}
	}
	
	public C tryConvert(String stringValue) {
		return converter.apply(stringValue);
	}
	
	private static class SimpleSuggestionOracle implements Function<String, List<String>> {

		private final List<String> suggestions;
		
		public SimpleSuggestionOracle(Collection<String> suggestions) {
			this.suggestions = Lists.newArrayList(suggestions);
			Collections.sort(this.suggestions);
		}
		
		@Override
		public List<String> apply(String input) {
			return suggestions;
		}
	}
}
