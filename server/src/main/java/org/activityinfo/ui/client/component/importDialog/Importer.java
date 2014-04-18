package org.activityinfo.ui.client.component.importDialog;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.strategy.*;
import org.activityinfo.core.shared.importing.validation.ValidatedRow;
import org.activityinfo.core.shared.importing.validation.ValidatedTable;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.fp.client.Promise;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


public class Importer {

    private ResourceLocator resourceLocator;

    private class TargetField {
        private FormTree.Node node;
        private FieldImportStrategy strategy;

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

    public Promise<ValidatedTable> validate(final ImportModel model) {

        final List<FieldImporter> importers = createImporters(model);
        final List<FieldImporterColumn> columns = collectImporterColumns(importers);
        final List<ValidatedRow> rows = Lists.newArrayList();

        return Promise.forEach(importers, new Function<FieldImporter, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable FieldImporter input) {
                return input.prepare(resourceLocator, model.getSource().getRows());
            }
        }).then(new Function<Void, ValidatedTable>() {
            @Nullable
            @Override
            public ValidatedTable apply(@Nullable Void input) {
                doValidation(model, importers, rows);
                return new ValidatedTable(columns, rows);
            }
        });
    }

    private List<FieldImporter> createImporters(final ImportModel model) {
        final List<FieldImporter> importers = Lists.newArrayList();
        for(TargetField field : fields) {
            Map<TargetSiteId,ColumnAccessor> mappedColumns = model.getMappedColumns(field.node.getFieldId());
            if(!mappedColumns.isEmpty()) {
                System.out.println(field + " => " + mappedColumns);

                FieldImporter importer = field.strategy.createImporter(field.node, mappedColumns);

                importers.add(importer);
            }
        }
        return importers;
    }

    private List<FieldImporterColumn> collectImporterColumns(List<FieldImporter> importers) {
        final List<FieldImporterColumn> collectTo = Lists.newArrayList();
        for (FieldImporter importer : importers) {
            collectTo.addAll(importer.getColumns());
        }
        return collectTo;
    }

    private void doValidation(ImportModel model, List<FieldImporter> importers, List<ValidatedRow> rows) {

        for(SourceRow row : model.getSource().getRows()) {
            List<ValidationResult> results = Lists.newArrayList();
            for(FieldImporter importer : importers) {
                importer.validateInstance(row, results);
            }
            rows.add(new ValidatedRow(row, results));
        }
    }

    public Promise<Void> persist(final ImportModel model) {

        final List<FieldImporter> importers = createImporters(model);
        final List<FieldImporterColumn> columns = collectImporterColumns(importers);

        throw new UnsupportedOperationException("TODO!");
    }
}
