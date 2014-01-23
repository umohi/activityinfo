package org.activityinfo.ui.full.client.importer.page;

import com.google.web.bindery.event.shared.Event;

public class PageChangedEvent extends Event<PageChangedEventHandler> {

    public static Type<PageChangedEventHandler> TYPE = new Type<PageChangedEventHandler>();

    private boolean valid;

    public PageChangedEvent(boolean valid) {
        super();
        this.valid = valid;
    }

    @Override
    public Type<PageChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PageChangedEventHandler handler) {
        handler.onPageChanged(this);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
