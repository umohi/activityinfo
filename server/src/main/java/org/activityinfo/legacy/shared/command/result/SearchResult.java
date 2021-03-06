package org.activityinfo.legacy.shared.command.result;

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

import org.activityinfo.legacy.shared.impl.search.Searcher;
import org.activityinfo.legacy.shared.model.DTO;
import org.activityinfo.legacy.shared.model.SearchHitDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.legacy.shared.reports.content.PivotContent;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends ListResult<SearchHitDTO> implements
        CommandResult, DTO {
    private PivotContent pivotTabelData; // hierarchy of activities
    private List<SiteDTO> recentAdditions;
    private List<Searcher> failedSearchers = new ArrayList<Searcher>();

    public SearchResult() {
        super();
    }

    public SearchResult(PivotContent pivotTabelData,
                        List<SiteDTO> recentAdditions) {
        super();

        this.pivotTabelData = pivotTabelData;
        this.recentAdditions = recentAdditions;
    }

    public PivotContent getPivotTabelData() {
        return pivotTabelData;
    }

    public void setPivotTabelData(PivotContent pivotTabelData) {
        this.pivotTabelData = pivotTabelData;
    }

    public List<Searcher> getFailedSearchers() {
        return failedSearchers;
    }

    public void setFailedSearchers(List<Searcher> failedSearchers) {
        this.failedSearchers = failedSearchers;
    }

    public List<SiteDTO> getRecentAdditions() {
        return recentAdditions;
    }

    public void setRecentAdditions(List<SiteDTO> recentAdditions) {
        this.recentAdditions = recentAdditions;
    }
}
