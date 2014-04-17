package org.activityinfo.core.shared.importing.strategy;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import org.activityinfo.core.client.type.converter.JsConverterFactory;
import org.activityinfo.core.server.type.converter.JvmConverterFactory;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.type.converter.ConverterFactory;

import java.util.List;


public class FieldImportStrategies {

    private static FieldImportStrategies INSTANCE;

    private List<FieldImportStrategy> strategies = Lists.newArrayList();

    private FieldImportStrategies() {

        ConverterFactory converterFactory;
        if(GWT.isClient()) {
            converterFactory = JsConverterFactory.get();
        } else {
            converterFactory = JvmConverterFactory.get();
        }

        strategies.add(new SingleClassReferenceStrategy());
        strategies.add(new DataFieldImportStrategy(converterFactory));
        strategies.add(new GeographicPointImportStrategy());
    }

    public FieldImportStrategy forField(FormTree.Node fieldNode) {
        for(FieldImportStrategy strategy : strategies) {
            if(strategy.accept(fieldNode)) {
                return strategy;
            }
        }
        throw new UnsupportedOperationException();
    }

    public static FieldImportStrategies get() {
        if(INSTANCE == null) {
            INSTANCE = new FieldImportStrategies();
        }
        return INSTANCE;
    }
}
