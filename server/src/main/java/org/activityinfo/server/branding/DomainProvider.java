package org.activityinfo.server.branding;

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
import com.google.inject.Provider;
import org.activityinfo.server.database.hibernate.entity.Domain;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides information on the domain branding to use based
 * on this thread's current request.
 */
public class DomainProvider implements Provider<Domain> {

    private final Provider<HttpServletRequest> request;
    private final Provider<EntityManager> entityManager;

    protected DomainProvider() {
        request = null;
        entityManager = null;
    }

    @Inject
    public DomainProvider(Provider<HttpServletRequest> request, Provider<EntityManager> entityManager) {
        super();
        this.request = request;
        this.entityManager = entityManager;
    }

    @Override
    public Domain get() {
        Domain result = entityManager.get().find(Domain.class, request.get().getServerName());
        if (result == null) {
            result = new Domain();
            result.setTitle("ActivityInfo");
            result.setHost(request.get().getServerName());
        }
        return result;
    }
}
