package org.activityinfo.server.login;

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

import com.google.common.collect.Maps;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.util.Map;

/**
 * The Bootstrap module is responsible for the minimal static html necessary to
 * login, retrieve lost passwords, etc.
 */
public class LoginModule extends ServletModule {

    @Override
    protected void configureServlets() {

        serve("/ActivityInfo/ActivityInfo.nocache.js")
                .with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.appcache")
                .with(SelectionServlet.class);
        serve("/ActivityInfo/ActivityInfo.gears.manifest")
                .with(SelectionServlet.class);

        Map<String, String> initParams = Maps.newHashMap();
        filter("/login*").through(GuiceContainer.class);
        filter("/unsupportedBrowser").through(GuiceContainer.class);

        filterContainer(initParams,
                HostController.class,
                LoginController.class,
                LogoutController.class,
                ConfirmInviteController.class,
                ResetPasswordController.class,
                ChangePasswordController.class,
                SignUpController.class,
                SignUpConfirmationController.class,
                SignUpAddressExistsController.class);

    }

    private void filterContainer(Map<String, String> params,
                                 Class<?>... endpointClasses) {
        for (Class<?> c : endpointClasses) {
            bind(c);

            String path = null;

            try {
                path = (String) c.getField("ENDPOINT").get(null);
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }

            filter(path).through(GuiceContainer.class, params);
        }
    }
}
