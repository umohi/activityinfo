package org.activityinfo.ui.client.component.importDialog;

import com.google.common.collect.Lists;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.strategy.FieldImportStrategies;
import org.activityinfo.core.shared.importing.strategy.FieldImportStrategy;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;
import org.activityinfo.core.shared.importing.validation.ValidatedResult;
import org.activityinfo.fp.client.Promise;

import java.util.List;


public class Importer {

    private ResourceLocator resourceLocator;

    static class TargetField {
        FormTree.Node node;
        FieldImportStrategy strategy;

        private TargetField(FormTree.Node node, FieldImportStrategy strategy) {
            this.node = node;
            this.strategy = strategy;
        }

        @Override
        public String toString() {
            return node.toString();
        }
    }

    private List<TargetField> fields = Lists.newArrayList();

    public Importer(ResourceLocator resourceLocator, FormTree formTree) {
        this.resourceLocator = resourceLocator;
        for(FormTree.Node rootField : formTree.getRootFields()) {
            fields.add(new TargetField(rootField, FieldImportStrategies.get().forField(rootField)));
        }
    }

    public List<ImportTarget> getImportTargets() {
        List<ImportTarget> targets = Lists.newArrayList();
        for(TargetField binding : fields) {
            targets.addAll(binding.strategy.getImportSites(binding.node));
        }
        return targets;
    }

    public Promise<ValidatedResult> validate(final ImportModel model) {
        final ImportCommandExecutor modeller = new ImportCommandExecutor(model, fields, resourceLocator);
        return modeller.execute(new ValidateImportCommand());
    }

    public Promise<Void> persist(final ImportModel model) {
        final ImportCommandExecutor modeller = new ImportCommandExecutor(model, fields, resourceLocator);
        return modeller.execute(new PersistImportCommand());
    }
}
