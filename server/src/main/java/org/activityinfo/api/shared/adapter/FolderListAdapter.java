package org.activityinfo.api.shared.adapter;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.base.Function;
import org.activityinfo.api.shared.model.ActivityDTO;
import org.activityinfo.api.shared.model.SchemaDTO;
import org.activityinfo.api.shared.model.UserDatabaseDTO;
import org.activityinfo.api2.shared.Properties;
import org.activityinfo.api2.shared.criteria.Criteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.system.FolderClass;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.activityinfo.api.shared.adapter.CuidAdapter.*;

/**
 * Extracts a list of databases as a list of folders
 */
public class FolderListAdapter implements Function<SchemaDTO, List<FormInstance>> {
    private final Criteria criteria;

    public FolderListAdapter(Criteria criteria) {
        this.criteria = criteria;
    }

    @Nullable
    @Override
    public List<FormInstance> apply(SchemaDTO schemaDTO) {
        List<FormInstance> instances = Lists.newArrayList();
        for(UserDatabaseDTO db : schemaDTO.getDatabases()) {
            FormInstance dbFolder = newFolder(db);
            if(criteria.apply(dbFolder)) {
                instances.add(dbFolder);
            }

            Set<String> categories = new HashSet<>();

            for(ActivityDTO activity : db.getActivities()) {

                FormInstance activityClass = new FormInstance(activityFormClass(activity.getId()), Properties.FORM_CLASS);

                if(!Strings.isNullOrEmpty(activity.getCategory())) {
                    categories.add(activity.getCategory());
                    activityClass.setParentId(activityCategoryFolderId(db, activity.getCategory()));
                } else {
                    activityClass.setParentId(databaseId(db));
                }

                activityClass.set(Properties.LABEL_PROPERTY, activity.getName());

                if(criteria.apply(activityClass)) {
                    instances.add(activityClass);
                }
            }

            for(String category : categories) {
                FormInstance categoryFolder = new FormInstance(
                        activityCategoryFolderId(db, category), FolderClass.FORM_CLASS);
                categoryFolder.setParentId(dbFolder.getId());
                categoryFolder.set(Properties.LABEL_PROPERTY, category);

                if(criteria.apply(categoryFolder)) {
                    instances.add(categoryFolder);
                }
            }
        }
        return instances;
    }

    private FormInstance newFolder(UserDatabaseDTO db) {
        FormInstance folder = new FormInstance(cuid(DATABASE_DOMAIN, db.getId()),
                FolderClass.FORM_CLASS);
        folder.set(FolderClass.LABEL_FIELD_ID, db.getName());
        folder.set(FolderClass.DESCRIPTION_FIELD_ID, db.getFullName());
        return folder;
    }

}
