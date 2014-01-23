package org.activityinfo.api.shared.command;

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

import org.activityinfo.analysis.shared.content.PivotContent;
import org.activityinfo.analysis.shared.model.PivotTableReportElement;

public class GeneratePivotTable implements Command<PivotContent> {
    private PivotTableReportElement model;

    public GeneratePivotTable() {

    }

    public GeneratePivotTable(PivotTableReportElement model) {
        super();
        this.model = model;
    }

    public PivotTableReportElement getModel() {
        return model;
    }

    public void setModel(PivotTableReportElement model) {
        this.model = model;
    }
}