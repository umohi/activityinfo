package org.activityinfo.ui.full.client.analysis.sampling;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.beans.FieldId;
import org.activityinfo.api2.shared.beans.InstanceBean;

import java.util.List;

/**
 *
 */
public interface ComplexSamplePlan extends InstanceBean {

    public static Cuid CLASS_ID = new Cuid("c234234243");

    public static Cuid STAGES_ID  = new Cuid("c234234");


    List<Stage> getStages();



}
