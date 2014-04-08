package org.activityinfo.ui.client.widget;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds affix behavior to table.
 *
 * @author yuriyz on 4/7/14.
 */
public class CellTableAffixer {

    //    private static final Logger LOGGER = Logger.getLogger(CellTableAffixer.class.getName());
    public static final String AFFIX_CLASS_NAME = "affix";

    private final CellTable table;
    private final ScrollPanel scrollAncestor;
    private final TableSectionElement tableHeadElement;
    private final CellTableHeaderWidthApplier widthApplier;

    private boolean affixed = false;

    public CellTableAffixer(final CellTable table) {
        this.table = table;
        this.tableHeadElement = table.getTableHeadElement();
        this.scrollAncestor = getScrollAncestor(this.table);
        this.widthApplier = new CellTableHeaderWidthApplier(table);

        // attach scroll handler to scroll ancestor
        if (this.scrollAncestor != null) {
            this.scrollAncestor.addScrollHandler(new ScrollHandler() {
                @Override
                public void onScroll(ScrollEvent event) {
                    CellTableAffixer.this.onScroll();
                }
            });
        } else { // attach scroll handler to window (if scrollAncestor can't be identified)
            Window.addWindowScrollHandler(new Window.ScrollHandler() {
                @Override
                public void onWindowScroll(Window.ScrollEvent event) {
                    CellTableAffixer.this.onScroll();
                }
            });
        }

        widthApplier.saveHeaderWidthInformation();
    }

    private int getVerticalScrollPosition() {
        if (scrollAncestor != null) {
            return scrollAncestor.getVerticalScrollPosition();
        } else {
            return Window.getScrollTop();
        }
    }

    private void onScroll() {
        final int verticalScroll = getVerticalScrollPosition();

        final int tableTop = table.getAbsoluteTop();
        boolean shouldAffix = verticalScroll > tableTop;

        if (affixed == shouldAffix) {
            return;
        }

        affixed = shouldAffix;
        if (shouldAffix) {
            tableHeadElement.addClassName(AFFIX_CLASS_NAME);
            tableHeadElement.getStyle().setTop(scrollAncestor != null ? scrollAncestor.getAbsoluteTop() : 0, Style.Unit.PX);
            widthApplier.restoreHeaderWidthInformation();
        } else {
            tableHeadElement.removeClassName(AFFIX_CLASS_NAME);
            tableHeadElement.getStyle().clearTop();
            widthApplier.clearHeaderWidthInformation();
        }
    }

    private static ScrollPanel getScrollAncestor(Widget widget) {
        if (widget != null && widget.getParent() != null) {
            final Widget parent = widget.getParent();
            if (parent instanceof ScrollPanel) {
                return (ScrollPanel) parent;
            } else {
                return getScrollAncestor(parent);
            }
        }
        return null;
    }

}
