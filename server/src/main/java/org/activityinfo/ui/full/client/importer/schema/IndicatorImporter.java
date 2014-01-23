package org.activityinfo.ui.full.client.importer.schema;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import org.activityinfo.api.shared.command.Command;
import org.activityinfo.api.shared.command.CreateEntity;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.ui.full.client.dispatch.Dispatcher;
import org.activityinfo.ui.full.client.importer.binding.DraftModel;
import org.activityinfo.ui.full.client.importer.binding.InstanceImporter;
import org.activityinfo.ui.full.client.importer.binding.InstanceMatch;
import org.activityinfo.ui.full.client.importer.ont.OntClass;
import org.activityinfo.ui.full.client.importer.ont.PropertyPath;

import java.util.List;
import java.util.Map;

public class IndicatorImporter implements InstanceImporter {

    private Map<PropertyPath, Object> providedValues = Maps.newHashMap();
    private UserDatabaseDTO database;


    public IndicatorImporter(Dispatcher dispatcher, UserDatabaseDTO database) {
        this.database = database;
        providedValues.put(new PropertyPath(IndicatorClass.ACTIVITY, ActivityClass.DATABASE), database);
    }

    @Override
    public OntClass getOntClass() {
        return new IndicatorClass();
    }

    @Override
    public Map<PropertyPath, Object> getProvidedValues() {
        return providedValues;
    }

    @Override
    public OntClass resolveOntClass(String range) {
        if (range.equals("Activity")) {
            return new ActivityClass();
        } else if (range.equals("Database")) {
            return new DatabaseClass();
        }
        throw new IllegalArgumentException(range);
    }

    @Override
    public InstanceMatch matchInstance(String ontClass,
                                       Map<String, Object> properties) {

        String name = (String) properties.get("name");
        String category = (String) properties.get("category");

        ActivityDTO existing = findActivity(database.getActivities(), name, category);
        if (existing != null) {
            InstanceMatch match = new InstanceMatch();
            match.setInstanceId("" + existing.getId());
            match.setNewInstance(false);
            return match;
        }

        return null;
    }

    private ActivityDTO findActivity(List<ActivityDTO> activities, String name, String category) {
        for (ActivityDTO activity : activities) {
            if (Objects.equal(activity.getName(), name) && Objects.equal(activity.getCategory(), category)) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public Command<?> createCommand(DraftModel draftModel) {
        return new CreateEntity("Indicator", draftModel.asLegacyPropertyMap());
    }
}
