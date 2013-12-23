package org.activityinfo.ui.desktop.client.widget.list;

import org.activityinfo.ui.core.client.model.ModelList;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simplified version of the CellList widget which simply uses cells to render
 * a block of HTML from the provided list. This is useful when the HTML just contains
 * links or static information.
 * @param <T>
 */
public class SimpleCellList<T> implements HasResource<ModelList<T>> {

    private final HTML html;
    private final Cell<T> cell;
    
    public SimpleCellList(Cell<T> cell) {
        super();
        this.html = new HTML();
        this.cell = cell;
    }

    @Override
    public Widget asWidget() {
        return html;
    }

    @Override
    public void showResource(ModelList<T> resource) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        for(T item : resource.getItems()) {
            cell.render(null, item, sb);
        }
        html.setHTML(sb.toSafeHtml());
    }
}
