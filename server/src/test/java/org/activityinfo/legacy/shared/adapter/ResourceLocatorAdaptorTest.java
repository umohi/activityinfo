package org.activityinfo.legacy.shared.adapter;


import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.PromiseMatchers;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.command.GetLocations;
import org.activityinfo.legacy.shared.command.result.LocationResult;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.core.shared.criteria.ParentCriteria.isChildOf;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;
import static org.activityinfo.legacy.shared.adapter.LocationClassAdapter.getAdminFieldId;
import static org.activityinfo.legacy.shared.adapter.LocationClassAdapter.getNameFieldId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
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

    public static final int IRUMU = 21;


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
    @OnDataSet("/dbunit/jordan-locations.db.xml")
    public void getLocation() {
        Cuid classId = locationFormClass(50512);
        FormInstance instance = assertResolves(resourceLocator.getFormInstance(locationInstanceId(1590565828)));
        Set<Cuid> adminUnits = instance.getReferences(field(classId, ADMIN_FIELD));
        System.out.println(adminUnits);

    }

    @Test
    public void persistSiteException() {

        FormInstance instance = new FormInstance(CuidAdapter.cuid(SITE_DOMAIN, new KeyGenerator().generateInt()),
                NFI_DIST_FORM_CLASS);

        Promise<Void> result;

        result = resourceLocator.persist(instance);
        assertThat(result.getState(), equalTo(Promise.State.REJECTED));

        result = resourceLocator.persist(Arrays.asList(instance, instance));
        assertThat(result.getState(), equalTo(Promise.State.REJECTED));
    }

    @Test
    public void persistLocation() {

        FormInstance instance = new FormInstance(CuidAdapter.generateLocationCuid(), HEALTH_CENTER_CLASS);
        instance.set(field(HEALTH_CENTER_CLASS, NAME_FIELD), "CS Ubuntu");
        instance.set(field(HEALTH_CENTER_CLASS, GEOMETRY_FIELD), new AiLatLng(-1, 13));
        instance.set(field(HEALTH_CENTER_CLASS, ADMIN_FIELD), entity(IRUMU));

        assertResolves(resourceLocator.persist(instance));

        // ensure that everything worked out
        GetLocations query = new GetLocations(getLegacyIdFromCuid(instance.getId()));
        LocationResult result = execute(query);
        LocationDTO location = result.getData().get(0);

        assertThat(location.getName(), equalTo("CS Ubuntu"));
        assertThat(location.getAdminEntity(1).getName(), equalTo("Ituri"));
        assertThat(location.getAdminEntity(2).getName(), equalTo("Irumu"));
        assertThat(location.getLatitude(), equalTo(-1d));
        assertThat(location.getLongitude(), equalTo(13d));
    }

    @Test
    public void updateLocation() {

//        <location locationId="1" name="Penekusu Kivu" locationTypeId="1"
//        X="1.532" Y="27.323" timeEdited="1"/>
//        <locationAdminLink locationId="1" adminEntityId="2"/>
//        <locationAdminLink locationId="1" adminEntityId="12"/>

        FormInstance instance = assertResolves(resourceLocator.getFormInstance(locationInstanceId(1)));
        instance.set(field(HEALTH_CENTER_CLASS, NAME_FIELD), "New Penekusu");

        assertResolves(resourceLocator.persist(instance));

        GetLocations query = new GetLocations(1);
        LocationResult result = execute(query);
        LocationDTO location = result.getData().get(0);

        assertThat(location.getName(), equalTo("New Penekusu"));
        assertThat(location.getLocationTypeId(), equalTo(1));
        assertThat(location.getLatitude(), equalTo(27.323));
        assertThat(location.getLongitude(), equalTo(1.532));
        assertThat(location.getAdminEntity(1).getId(), equalTo(2));
        assertThat(location.getAdminEntity(2).getId(), equalTo(12));
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


    @Test
    public void deleteLocation() {

        ResourceLocatorAdaptor adapter = new ResourceLocatorAdaptor(getDispatcher());
        Cuid instanceToDelete = CuidAdapter.locationInstanceId(1);
        adapter.remove(Arrays.asList(instanceToDelete));

        List<FormInstance> formInstances = assertResolves(adapter.queryInstances(new ClassCriteria(CuidAdapter.locationFormClass(1))));

        for(FormInstance instance : formInstances) {
            if(instance.getId().equals(instanceToDelete)) {
                throw new AssertionError();
            }
        }
    }

    @Test
    public void siteProjections() {

        Cuid partnerClassId = CuidAdapter.partnerFormClass(PEAR_DATABASE_ID);

        ResourceLocatorAdaptor adapter = new ResourceLocatorAdaptor(getDispatcher());
        FieldPath villageName = new FieldPath(getNameFieldId(VILLAGE_CLASS));
        FieldPath provinceName = new FieldPath(getAdminFieldId(VILLAGE_CLASS), field(PROVINCE_CLASS, CuidAdapter.NAME_FIELD));
        FieldPath partnerName = new FieldPath(partnerField(NFI_DIST_ID), field(partnerClassId, NAME_FIELD));
        FieldPath indicator1 = new FieldPath(indicatorField(1));
        FieldPath startDate = new FieldPath(field(NFI_DIST_FORM_CLASS, CuidAdapter.START_DATE_FIELD));
        FieldPath endDate = new FieldPath(field(NFI_DIST_FORM_CLASS, CuidAdapter.END_DATE_FIELD));


        List<Projection> projections = assertResolves(adapter.query(
                new InstanceQuery(
                        asList(partnerName, villageName, provinceName, indicator1, endDate),
                        new ClassCriteria(NFI_DIST_FORM_CLASS))));

        System.out.println(Joiner.on("\n").join(projections));

        final Projection firstProjection = projections.get(0);
        assertThat(projections.size(), equalTo(3));
        assertThat(firstProjection.getStringValue(provinceName), equalTo("Sud Kivu"));
        assertEquals(firstProjection.getValue(startDate), null);
        assertEquals(firstProjection.getValue(endDate), new LocalDate(2009, 1, 2));
    }

    @Test
    public void geodb() {

        ResourceLocatorAdaptor adapter = new ResourceLocatorAdaptor(getDispatcher());

        FormInstance geodbFolder = assertResolves(adapter.getFormInstance(FolderListAdapter.GEODB_ID));

        List<FormInstance> countries = assertResolves(adapter.queryInstances(isChildOf(geodbFolder.getId())));
        assertThat(countries, Matchers.hasSize(1));

        FormInstance rdc = countries.get(0);
        assertThat(rdc.getString(ApplicationProperties.COUNTRY_NAME_FIELD), equalTo("Rdc"));

        List<FormInstance> levels = assertResolves(adapter.queryInstances(isChildOf(rdc.getId())));
        System.out.println(levels);

    }

}
