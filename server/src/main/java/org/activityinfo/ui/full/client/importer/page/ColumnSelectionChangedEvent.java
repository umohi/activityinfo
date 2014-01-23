package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ColumnSelectionChangedEvent extends GwtEvent<ColumnSelectionChangedEvent.Handler> {

    public static Type<Handler> TYPE = new Type<Handler>();

    public interface Handler extends EventHandler {
        void onColumnSelectionChanged(ColumnSelectionChangedEvent e);
    }

    private int selectedColumnIndex;

    public ColumnSelectionChangedEvent(int selectedColumnIndex) {
        super();
        this.selectedColumnIndex = selectedColumnIndex;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onColumnSelectionChanged(this);
    }

    public int getSelectedColumnIndex() {
        return selectedColumnIndex;
    }
}
