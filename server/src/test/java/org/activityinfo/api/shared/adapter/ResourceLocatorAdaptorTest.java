package org.activityinfo.api.shared.adapter;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Projection;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.criteria.ClassCriteria;
import org.activityinfo.api2.shared.form.FormInstance;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.activityinfo.api.shared.adapter.CuidAdapter.*;
import static org.activityinfo.api2.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class ResourceLocatorAdaptorTest extends CommandTestCase2 {

    private static final int CAUSE_ATTRIBUTE_GROUP_ID = 1;

    private static final int PROVINCE_ADMIN_LEVEL_ID = 1;

    private static final Cuid PROVINCE_CLASS = CuidAdapter.adminLevelFormClass(PROVINCE_ADMIN_LEVEL_ID);

    private static final int PEAR_DATABASE_ID = 1;

    private static final int HEALTH_CENTER_LOCATION_TYPE = 1;

    private static final Cuid HEALTH_CENTER_CLASS = CuidAdapter.locationFormClass(HEALTH_CENTER_LOCATION_TYPE);

    private ResourceLocatorAdaptor resourceLocator;

    @Before
    public final void setup() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
    }

    @Test
    public void simpleAttributeQuery() {
        assertThat(queryByClass(attributeGroupFormClass(CAUSE_ATTRIBUTE_GROUP_ID)), Matchers.hasSize(2));
    }

    @Test
    public void simpleAdminEntityQuery() {
        assertThat(queryByClass(adminLevelFormClass(PROVINCE_ADMIN_LEVEL_ID)), Matchers.hasSize(4));
    }

    @Test
    public void simplePartnerQuery() {
        assertThat(queryByClass(partnerFormClass(PEAR_DATABASE_ID)), Matchers.hasSize(3));
    }

    @Test
    public void simpleLocationQuery() {
        assertThat(queryByClass(locationFormClass(HEALTH_CENTER_LOCATION_TYPE)), Matchers.hasSize(4));
    }

    @Test
    public void projection() {

        // fields to request
        FieldPath locationName = new FieldPath(LocationClassAdapter.getNameFieldId(HEALTH_CENTER_CLASS));
        FieldPath locationAdminUnit = new FieldPath(LocationClassAdapter.getAdminFieldId(HEALTH_CENTER_CLASS));
        FieldPath locationAdminUnitName = new FieldPath(locationAdminUnit,
                AdminLevelClassAdapter.getNameFieldId(PROVINCE_CLASS));


        List<Projection> projections = assertResolves(resourceLocator.query(
                Lists.newArrayList(locationName, locationAdminUnitName),
                new ClassCriteria(HEALTH_CENTER_CLASS)));

        System.out.println(Joiner.on("\n").join(projections));
    }

    private List<FormInstance> queryByClass(Cuid classId) {
        Promise<List<FormInstance>> promise = resourceLocator.queryInstances(new ClassCriteria(classId));

        List<FormInstance> list = assertResolves(promise);

        System.out.println(Joiner.on("\n").join(list));
        return list;
    }
}
