package org.activityinfo.ui.client.pageView;

import com.google.common.base.Function;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.ui.client.pageView.folder.FolderPageView;
import org.activityinfo.ui.client.pageView.formClass.FormClassPageView;
import org.activityinfo.ui.client.widget.DisplayWidget;

/**
 * Creates a InstancePageView given a class id
 */
public class InstancePageViewFactory implements Function<FormInstance, DisplayWidget<FormInstance>> {

    private final ResourceLocator resourceLocator;

    public InstancePageViewFactory(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    @Override
    public InstancePageView apply(FormInstance instance) {
        Cuid classId = instance.getClassId();
        if(classId.equals(FolderClass.CLASS_ID)) {
            return new FolderPageView(resourceLocator);
        } else if(classId.equals(FormClass.CLASS_ID)) {
            return new FormClassPageView(resourceLocator);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
