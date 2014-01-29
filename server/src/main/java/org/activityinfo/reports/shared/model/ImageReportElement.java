package org.activityinfo.reports.shared.model;

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

import org.activityinfo.reports.shared.content.NullContent;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

/**
 * Defines an external, static image to be included in the report
 */
public class ImageReportElement extends ReportElement<NullContent> {

    private String url;

    /**
     * @return the URL of the image to include in the report
     */
    @XmlElement
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Set<Integer> getIndicators() {
        return Collections.emptySet();
    }

}