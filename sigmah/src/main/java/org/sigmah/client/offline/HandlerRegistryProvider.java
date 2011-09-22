package org.sigmah.client.offline;

import org.sigmah.client.offline.command.HandlerRegistry;
import org.sigmah.shared.command.AddLocation;
import org.sigmah.shared.command.CreateSite;
import org.sigmah.shared.command.GetAdminEntities;
import org.sigmah.shared.command.GetPartnersWithSites;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.UpdateSite;
import org.sigmah.shared.command.handler.AddLocationHandler;
import org.sigmah.shared.command.handler.CreateSiteHandler;
import org.sigmah.shared.command.handler.GetAdminEntitiesHandler;
import org.sigmah.shared.command.handler.GetPartnersWithSitesHandler;
import org.sigmah.shared.command.handler.GetSchemaHandler;
import org.sigmah.shared.command.handler.GetSitesHandler;
import org.sigmah.shared.command.handler.UpdateSiteHandler;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class HandlerRegistryProvider implements Provider<HandlerRegistry> {

	private final HandlerRegistry registry; 
	
	@Inject
	public HandlerRegistryProvider(
			GetSchemaHandler schemaHandler,
            GetSitesHandler sitesHandler,
            GetAdminEntitiesHandler adminHandler,
            GetPartnersWithSitesHandler partnersWithSitesHandler,
            CreateSiteHandler createSiteHandler,
            UpdateSiteHandler updateSiteHandler,
            AddLocationHandler addLocationHandler) { 
            //SearchHandler searchHandler) {
		
		registry = new HandlerRegistry();
    	registry.registerHandler(GetSchema.class, schemaHandler);
    	registry.registerHandler(GetSites.class, sitesHandler);
    	registry.registerHandler(GetAdminEntities.class, adminHandler);
    	registry.registerHandler(GetPartnersWithSites.class, partnersWithSitesHandler);
    	registry.registerHandler(CreateSite.class, createSiteHandler);
    	registry.registerHandler(UpdateSite.class, updateSiteHandler);
    	registry.registerHandler(AddLocation.class, addLocationHandler);
    	//registry.registerHandler(Search.class, searchHandler);
	}

	@Override
	public HandlerRegistry get() {
		return registry;
	}	
}
