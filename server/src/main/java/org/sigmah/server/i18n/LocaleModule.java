package org.sigmah.server.i18n;

import java.util.Locale;

import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.i18n.UIMessages;

import com.google.inject.AbstractModule;
import com.teklabs.gwt.i18n.server.LocaleProxy;

public class LocaleModule extends AbstractModule {

	public LocaleModule() {
		LocaleProxy.initialize();
	}
	
	@Override
	protected void configure() {
		bind(UIConstants.class).toProvider(new LocalizableResourceProvider<UIConstants>(UIConstants.class));
		bind(UIMessages.class).toProvider(new LocalizableResourceProvider<UIMessages>(UIMessages.class));
		bind(Locale.class).toProvider(LocaleProvider.class);
	}
	
}