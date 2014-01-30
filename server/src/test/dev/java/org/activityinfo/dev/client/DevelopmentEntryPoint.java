package org.activityinfo.dev.client;
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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.activityinfo.api2.shared.form.UserForm;
import org.activityinfo.api2.shared.form.UserFormInstance;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.widget.form.UserFormPanel;

/**
 * Dev only entry point.
 *
 * @author yuriyz on 1/24/14.
 */
public class DevelopmentEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        TransitionUtil.ensureBootstrapInjected();
//        gxt();
        pureGwt();
    }

//    public void gxt() {
//        final UserForm userForm = DevUtils.createTestUserForm();
//
//        final UserFormPanel panel = new UserFormPanel(userForm, null);
//        panel.setDesignEnabled(true);
//
//        final ContentPanel container = new ContentPanel();
//        container.setLayout(new RowLayout());
//        container.setLayoutOnChange(true);
//        container.add(panel, new RowData(1, 1, new Margins(0)));
//
//        final Viewport viewport = new Viewport();
//        viewport.setLayout(new FitLayout());
//        viewport.add(container, new MarginData(0));
//        RootLayoutPanel.get().add(viewport);
//    }

//    public interface MyTemplates extends SafeHtmlTemplates {
//        @Template("<input type=\"email\" class=\"form-control\" id=\"exampleInputEmail1\" placeholder=\"Enter email\">")
//        SafeHtml messageWithLink();
//    }
//
//    private static final MyTemplates TEMPLATES = GWT.create(MyTemplates.class);

    public void pureGwt() {
        final UserForm userForm = DevUtils.createTestUserForm();
        final UserFormInstance userFormInstance = DevUtils.createTestUserFormInstance(userForm);
        final UserFormPanel panel = new UserFormPanel(userForm, null);
        RootPanel.get().add(new SimplePanel(panel));

        panel.setValue(userFormInstance);
//        final SiteDialog d = new SiteDialog(userForm);
//        d.show();
    }


//    public Widget createTestWidget() {
//        // Create a Vertical Panel
//        VerticalPanel vPanel = new VerticalPanel();
//        vPanel.setSpacing(5);
//
//        // Add some content to the panel
//        for (int i = 1; i < 10; i++) {
//            vPanel.add(new Button(" " + i));
//        }
//
//        // Return the content
//        vPanel.ensureDebugId("cwVerticalPanel");
//        return vPanel;
//    }
}
