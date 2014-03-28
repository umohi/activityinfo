package org.activityinfo.legacy.shared.adapter;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static java.util.Arrays.asList;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;
import static org.activityinfo.legacy.shared.adapter.LocationClassAdapter.getAdminFieldId;
import static org.activityinfo.legacy.shared.adapter.LocationClassAdapter.getNameFieldId;
import static org.hamcrest.CoreMatchers.equalTo;
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

    private static final int NFI_DIST_ID = 1;

    private static final Cuid NFI_DIST_FORM_CLASS = CuidAdapter.activityFormClass(NFI_DIST_ID);

    public static final int VILLAGE_TYPE_ID = 1;

    public static final Cuid VILLAGE_CLASS = CuidAdapter.locationFormClass(VILLAGE_TYPE_ID);


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
                new InstanceQuery(
                    Lists.newArrayList(locationName, locationAdminUnitName),
                    new ClassCriteria(HEALTH_CENTER_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));
    }

    private List<FormInstance> queryByClass(Cuid classId) {
        Promise<List<FormInstance>> promise = resourceLocator.queryInstances(new ClassCriteria(classId));

        List<FormInstance> list = assertResolves(promise);

        System.out.println(Joiner.on("\n").join(list));
        return list;
    }


    @Test
    public void locationProjection() {


        ResourceLocatorAdaptor adapter = new ResourceLocatorAdaptor(getDispatcher());
        FieldPath villageName = new FieldPath(getNameFieldId(VILLAGE_CLASS));
        FieldPath provinceName = new FieldPath(
                getAdminFieldId(VILLAGE_CLASS),
                field(PROVINCE_CLASS, CuidAdapter.NAME_FIELD));

        List<Projection> projections = assertResolves(adapter.query(
                new InstanceQuery(
                        asList(villageName, provinceName),
                        new ClassCriteria(VILLAGE_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));

        assertThat(projections.size(), equalTo(4));
        assertThat(projections.get(0).getStringValue(provinceName), equalTo("Sud Kivu"));
    }


    @Ignore("WIP")
    @Test
    public void siteProjections() {

        Cuid partnerClassId = CuidAdapter.partnerFormClass(PEAR_DATABASE_ID);


        ResourceLocatorAdaptor adapter = new ResourceLocatorAdaptor(getDispatcher());
        FieldPath villageName = new FieldPath(getNameFieldId(VILLAGE_CLASS));
        FieldPath provinceName = new FieldPath(getAdminFieldId(VILLAGE_CLASS), field(PROVINCE_CLASS, CuidAdapter.NAME_FIELD));
        FieldPath partnerName = new FieldPath(partnerField(NFI_DIST_ID), field(partnerClassId, NAME_FIELD));
        FieldPath indicator1 = new FieldPath(indicatorField(1));


        List<Projection> projections = assertResolves(adapter.query(
                new InstanceQuery(
                        asList(partnerName, villageName, provinceName, indicator1),
                        new ClassCriteria(NFI_DIST_FORM_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));

        assertThat(projections.size(), equalTo(4));
        assertThat(projections.get(0).getStringValue(provinceName), equalTo("Sud Kivu"));
    }


}
