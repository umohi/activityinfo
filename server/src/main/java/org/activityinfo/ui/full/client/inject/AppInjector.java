/**
 * Support classes for the Dependency Injection Framework, grace a Gin
 */
package org.activityinfo.ui.full.client.inject;

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

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import org.activityinfo.ui.full.client.EventBus;
import org.activityinfo.ui.full.client.HistoryManager;
import org.activityinfo.ui.full.client.UsageTracker;
import org.activityinfo.ui.full.client.dispatch.remote.cache.AdminEntityCache;
import org.activityinfo.ui.full.client.dispatch.remote.cache.SchemaCache;
import org.activityinfo.ui.full.client.local.LocalController;
import org.activityinfo.ui.full.client.local.LocalModule;
import org.activityinfo.ui.full.client.page.app.AppLoader;
import org.activityinfo.ui.full.client.page.config.ConfigLoader;
import org.activityinfo.ui.full.client.page.config.ConfigModule;
import org.activityinfo.ui.full.client.page.dashboard.DashboardLoader;
import org.activityinfo.ui.full.client.page.entry.DataEntryLoader;
import org.activityinfo.ui.full.client.page.entry.EntryModule;
import org.activityinfo.ui.full.client.page.entry.FormPanelPageLoader;
import org.activityinfo.ui.full.client.page.home.PageLoader;
import org.activityinfo.ui.full.client.page.report.ReportLoader;
import org.activityinfo.ui.full.client.page.report.ReportModule;
import org.activityinfo.ui.full.client.page.search.SearchLoader;
import org.activityinfo.ui.full.client.page.search.SearchModule;
import org.activityinfo.ui.full.client.report.editor.map.MapModule;

/**
 * GIN injector.
 * <p/>                                     ap
 * TODO: having this number of explicit entries is probably not ideal, try to
 * make better use of injection and injecting Provider<>s
 */
@GinModules({
        AppModule.class,
        ReportModule.class,
        EntryModule.class,
        MapModule.class,
        ConfigModule.class,
        LocalModule.class,
        SearchModule.class
})
public interface AppInjector extends Ginjector {
    EventBus getEventBus();

    HistoryManager getHistoryManager();

    DataEntryLoader createDataEntryLoader();

    ReportLoader createReportLoader();

    ConfigLoader createConfigLoader();

    LocalController createOfflineController();

    UsageTracker getUsageTracker();

    SearchLoader createSearchLoader();

    DashboardLoader createDashboardLoader();

    FormPanelPageLoader createSiteFormLoader();

    PageLoader createFolderPageLoader();

    SchemaCache createSchemaCache();

    AdminEntityCache createAdminCache();

    AppLoader createAppLoader();
}
