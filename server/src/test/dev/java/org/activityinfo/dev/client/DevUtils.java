package org.activityinfo.dev.client;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.user.client.Random;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.form.*;

import java.util.Date;
import java.util.List;

/**
 * @author yuriyz on 1/24/14.
 */
public class DevUtils {

    public static final int SINGLE_SMALL_ID = 1;
    public static final int SINGLE_MEDIUM_ID = 2;
    public static final int SINGLE_UNBOUND_ID = 3;
    public static final int MULTIPLE_SMALL_ID = 4;
    public static final int MULTIPLE_MEDIUM_ID = 5;
    public static final int MULTIPLE_UNBOUND_ID = 6;

    private DevUtils() {
    }

    public static Cuid randomIri() {
        return new Cuid(Random.nextInt() + "_" + new Date().getTime());
    }

    static FormInstance createTestUserFormInstance(FormClass formClass) {
        final FormInstance instance = new FormInstance(randomIri(), formClass.getId());
        for (FormField field : formClass.getFields()) {
            switch (field.getType()) {
                case FREE_TEXT:
                    instance.set(field.getId(), field.getId().asString());
                    break;
                case GEOGRAPHIC_POINT:
                    break;
                case LOCAL_DATE:
                    instance.set(field.getId(), new Date(0));
                    break;
                case QUANTITY:
                    instance.set(field.getId(), 5.5);
                    break;
                case REFERENCE:
                    instance.set(field.getId(), Sets.newHashSet(CuidAdapter.attributeId(2)));
                    break;
            }
        }
        return instance;
    }

    static FormClass createTestUserForm() {
        final FormField item1 = new FormField(randomIri());
        item1.setType(FormFieldType.FREE_TEXT);
        item1.setLabel(new LocalizedString("Advocacy meetings with GRSS to promote responsibility for PoC, tackle impunity and strenghten accountability"));
        item1.setDescription(new LocalizedString("The State Coordinator and different sections are involved in many meetings with the GRSS. When the specific objective of the meeting is to discuss PoC issues, these should be counted for the indicator. CAD, PoC Advisors and SOC should be responsible for providing information for this."));
        item1.setUnit(new LocalizedString("Meetings"));

        final FormField item2 = new FormField(randomIri());
        item2.setType(FormFieldType.FREE_TEXT);
        item2.setLabel(new LocalizedString("Advocacy meetings can be informal or formal and can seek to address internal conduct and discipline, but also focus on a proactive implementation of those obligations.  As above the objective of the meeting should be explicitly related to PoC. MLOs should provide this information."));
        item2.setDescription(new LocalizedString("Advocacy meetings with SPLA to improve compliance with human rights obigations in PoC high risk locations "));
        item2.setUnit(new LocalizedString("Meetings"));

        final FormField quantityField = new FormField(randomIri());
        quantityField.setType(FormFieldType.QUANTITY);
        quantityField.setDescription(new LocalizedString("Quantity description"));
        quantityField.setLabel(new LocalizedString("Quantity label"));
        quantityField.setRequired(true);

        final FormField freeTextField = new FormField(randomIri());
        freeTextField.setType(FormFieldType.FREE_TEXT);
        freeTextField.setDescription(new LocalizedString("Free text description"));
        freeTextField.setLabel(new LocalizedString("Free text"));
        freeTextField.setRequired(true);

        final FormField localDateField = new FormField(randomIri());
        localDateField.setType(FormFieldType.LOCAL_DATE);
        localDateField.setDescription(new LocalizedString("Local date description"));
        localDateField.setLabel(new LocalizedString("Local date"));

        final FormField geographicPointField = new FormField(randomIri());
        geographicPointField.setType(FormFieldType.GEOGRAPHIC_POINT);
        geographicPointField.setDescription(new LocalizedString("Geographic point description"));
        geographicPointField.setLabel(new LocalizedString("Geographic point"));

        final FormSection section1 = new FormSection(randomIri());
        section1.setLabel(new LocalizedString("Tier 1 - Reduced physical threats to civilians"));
        section1.addElement(quantityField);
        section1.addElement(freeTextField);
        section1.addElement(localDateField);
        section1.addElement(geographicPointField);
        section1.getElements().addAll(createReferenceFields());

        final FormSection section2 = new FormSection(randomIri());
        section2.setLabel(new LocalizedString("Tier 2 - GRSS fulfill PoC responsibility"));
        section2.addElement(item1);
        section2.addElement(item2);

        final FormClass form = new FormClass(randomIri());
        form.addElement(section1);
        form.addElement(section2);
        return form;
    }

    private static List<FormField> createReferenceFields() {
        final FormField singleSmallField = new FormField(randomIri());
        singleSmallField.setType(FormFieldType.REFERENCE);
        singleSmallField.setRange(CuidAdapter.attributeGroupFormClass(SINGLE_SMALL_ID));
        singleSmallField.setDescription(new LocalizedString("Single small description"));
        singleSmallField.setLabel(new LocalizedString("Single small"));
        singleSmallField.setCardinality(FormFieldCardinality.SINGLE);

        final FormField singleMediumField = new FormField(randomIri());
        singleMediumField.setType(FormFieldType.REFERENCE);
        singleMediumField.setRange(CuidAdapter.attributeGroupFormClass(SINGLE_MEDIUM_ID));
        singleMediumField.setDescription(new LocalizedString("Single medium description"));
        singleMediumField.setLabel(new LocalizedString("Single medium"));
        singleMediumField.setCardinality(FormFieldCardinality.SINGLE);

        final FormField singleUnboundField = new FormField(randomIri());
        singleUnboundField.setType(FormFieldType.REFERENCE);
        singleUnboundField.setRange(CuidAdapter.attributeGroupFormClass(SINGLE_UNBOUND_ID));
        singleUnboundField.setDescription(new LocalizedString("Single unbound description"));
        singleUnboundField.setLabel(new LocalizedString("Single unbound"));
        singleUnboundField.setCardinality(FormFieldCardinality.SINGLE);

        final FormField multipleSmallField = new FormField(randomIri());
        multipleSmallField.setType(FormFieldType.REFERENCE);
        multipleSmallField.setRange(CuidAdapter.attributeGroupFormClass(MULTIPLE_SMALL_ID));
        multipleSmallField.setDescription(new LocalizedString("Multiple small description"));
        multipleSmallField.setLabel(new LocalizedString("Multiple small"));
        multipleSmallField.setCardinality(FormFieldCardinality.MULTIPLE);

        final FormField multipleMediumField = new FormField(randomIri());
        multipleMediumField.setType(FormFieldType.REFERENCE);
        multipleMediumField.setRange(CuidAdapter.attributeGroupFormClass(MULTIPLE_MEDIUM_ID));
        multipleMediumField.setDescription(new LocalizedString("Multiple medium description"));
        multipleMediumField.setLabel(new LocalizedString("Multiple medium"));
        multipleMediumField.setCardinality(FormFieldCardinality.MULTIPLE);

        final FormField multipleUnboundField = new FormField(randomIri());
        multipleUnboundField.setType(FormFieldType.REFERENCE);
        multipleUnboundField.setRange(CuidAdapter.attributeGroupFormClass(MULTIPLE_UNBOUND_ID));
        multipleUnboundField.setDescription(new LocalizedString("Multiple unbound description"));
        multipleUnboundField.setLabel(new LocalizedString("Multiple unbound"));
        multipleUnboundField.setCardinality(FormFieldCardinality.MULTIPLE);

        final List<FormField> list = Lists.newArrayList();
        list.add(singleSmallField);
        list.add(singleMediumField);
        list.add(singleUnboundField);
        list.add(multipleSmallField);
        list.add(multipleMediumField);
        list.add(multipleUnboundField);
        return list;
    }

    public static List<FormInstance> getFormInstanceList(int legacyId) {
        final List<FormInstance> instances = Lists.newArrayList();
        switch (legacyId) {
            case SINGLE_SMALL_ID:
            case MULTIPLE_SMALL_ID:
                fillList(instances, 3, legacyId);
                break;
            case SINGLE_MEDIUM_ID:
            case MULTIPLE_MEDIUM_ID:
                fillList(instances, 13, legacyId);
                break;
            case SINGLE_UNBOUND_ID:
            case MULTIPLE_UNBOUND_ID:
                fillList(instances, 23, legacyId);
                break;
        }
        return instances;
    }

    private static void fillList(List<FormInstance> instances, int itemCount, int legacyId) {
        for (int i = 0; i < itemCount; i++) {
            instances.add(createAttributeFormInstance(i, legacyId));
        }
    }

    public static FormInstance createAttributeFormInstance(int index, int classId) {
        FormInstance instance = new FormInstance(CuidAdapter.attributeId(index), CuidAdapter.attributeGroupFormClass(classId));
        instance.set(CuidAdapter.getFormInstanceLabelCuid(instance), "a" + index);
        return instance;
    }
}
