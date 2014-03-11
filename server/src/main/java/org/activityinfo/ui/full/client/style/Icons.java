package org.activityinfo.ui.full.client.style;

import com.bedatadriven.rebar.style.client.Icon;
import com.bedatadriven.rebar.style.client.IconSet;
import com.google.gwt.core.shared.GWT;

/**
 * Application-wide icons
 */
public interface Icons extends IconSet {

    Icons INSTANCE = GWT.create(Icons.class);

    @Icon("icons/add.svg")
    String add();

    @Icon("icons/ocha/reporting.svg")
    String editForm();

    @Icon("icons/icomoon/remove.svg")
    String delete();

    @Icon("icons/icomoon/mobile.svg")
    String mobileDevice();

    @Icon("icons/icomoon/file-excel.svg")
    String excelFile();

    @Icon("icons/icomoon/cloud-upload.svg")
    String importIcon();
}
