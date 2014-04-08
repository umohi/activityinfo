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

import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

/**
 * @author yuriyz on 4/7/14.
 */
public class CellTable<T> extends com.google.gwt.user.cellview.client.CellTable<T> {

    public CellTable() {
    }

    public CellTable(int pageSize) {
        super(pageSize);
    }

    public CellTable(ProvidesKey<T> keyProvider) {
        super(keyProvider);
    }

    public CellTable(int pageSize, Resources resources) {
        super(pageSize, resources);
    }

    public CellTable(int pageSize, ProvidesKey<T> keyProvider) {
        super(pageSize, keyProvider);
    }

    public CellTable(int pageSize, Resources resources, ProvidesKey<T> keyProvider) {
        super(pageSize, resources, keyProvider);
    }

    public CellTable(int pageSize, Resources resources, ProvidesKey<T> keyProvider, Widget loadingIndicator) {
        super(pageSize, resources, keyProvider, loadingIndicator);
    }

    public CellTable(int pageSize, Resources resources, ProvidesKey<T> keyProvider, Widget loadingIndicator, boolean enableColGroup, boolean attachLoadingPanel) {
        super(pageSize, resources, keyProvider, loadingIndicator, enableColGroup, attachLoadingPanel);
    }

    @Override
    public TableSectionElement getTableHeadElement() {
        return super.getTableHeadElement();
    }
}
