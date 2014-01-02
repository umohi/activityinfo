package org.activityinfo.ui.desktop.client.widget.editor;

import com.bedatadriven.rebar.style.client.Source;
import com.bedatadriven.rebar.style.client.Stylesheet;
import com.google.gwt.core.shared.GWT;

@Source("editor.less")
public interface EditorStylesheet extends Stylesheet {
    
    public static final EditorStylesheet INSTANCE = GWT.create(EditorStylesheet.class);
    
    String editable();
    
    String editableValue();
}
