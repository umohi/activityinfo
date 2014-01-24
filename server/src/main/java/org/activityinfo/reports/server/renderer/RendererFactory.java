package org.activityinfo.reports.server.renderer;

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

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.activityinfo.reports.server.renderer.excel.ExcelMapDataExporter;
import org.activityinfo.reports.server.renderer.excel.ExcelReportRenderer;
import org.activityinfo.reports.server.renderer.image.ImageReportRenderer;
import org.activityinfo.reports.server.renderer.itext.HtmlReportRenderer;
import org.activityinfo.reports.server.renderer.itext.PdfReportRenderer;
import org.activityinfo.reports.server.renderer.itext.RtfReportRenderer;
import org.activityinfo.reports.server.renderer.ppt.PPTRenderer;
import org.activityinfo.api.shared.command.RenderElement;

/**
 * Creates a {@code Renderer} for a given file format
 */
public class RendererFactory {

    private final Injector injector;

    @Inject
    public RendererFactory(Injector injector) {
        this.injector = injector;
    }

    public Renderer get(RenderElement.Format format) {

        switch (format) {
            case PowerPoint:
                return injector.getInstance(PPTRenderer.class);
            case Word:
                return injector.getInstance(RtfReportRenderer.class);
            case Excel:
                return injector.getInstance(ExcelReportRenderer.class);
            case PDF:
                return injector.getInstance(PdfReportRenderer.class);
            case PNG:
                return injector.getInstance(ImageReportRenderer.class);
            case Excel_Data:
                return injector.getInstance(ExcelMapDataExporter.class);
            case HTML:
                return injector.getInstance(HtmlReportRenderer.class);
        }

        throw new IllegalArgumentException();
    }
}
