package org.activityinfo.ui.desktop.client.widget.editor;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class EditableHeading extends Widget implements LeafValueEditor<String> {

    private String value;
    private SpanElement valueSpan;
    
    @UiConstructor
    public EditableHeading(int size) {       
        setElement(Document.get().createHElement(size));
        addStyleName(EditorStylesheet.INSTANCE.editable());

        valueSpan = Document.get().createSpanElement();
        valueSpan.addClassName(EditorStylesheet.INSTANCE.editableValue());
        getElement().appendChild(valueSpan);
        
        sinkEvents(Event.ONCLICK);
    }

    @Override
    public void onBrowserEvent(Event event) {
        String newName = Window.prompt("Enter a new name for this database", value);
        if(!Strings.isNullOrEmpty(newName)) {
            setValue(newName);
        }
    }

    @Override
    public void setValue(String value) {
        this.value = value;
        valueSpan.setInnerSafeHtml(SafeHtmlUtils.fromString(value));
    }

    @Override
    public String getValue() {
        return value;
    }
}
