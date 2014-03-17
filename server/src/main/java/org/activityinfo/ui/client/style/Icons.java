package org.activityinfo.ui.client.style;

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
    String form();

    @Icon("icons/icomoon/location.svg")
    String location();

    @Icon("icons/icomoon/folder-open.svg")
    String folder();

    @Icon("icons/icomoon/remove.svg")
    String delete();

    @Icon("icons/icomoon/mobile.svg")
    String mobileDevice();

    @Icon("icons/icomoon/file-excel.svg")
    String excelFile();

    @Icon("icons/icomoon/cloud-upload.svg")
    String importIcon();

    @Icon("icons/icomoon/table.svg")
    String table();

    @Icon("icons/icomoon/earth.svg")
    String map();

    @Icon("icons/icomoon/info.svg")
    String overview();

    /**
     * Symbolizes a problem connecting to the server
     */
    @Icon("icons/disconnected.svg")
    String connectionProblem();

    /**
     * Symbolizes an unexpected exception (that is, a bug!)
     */
    @Icon("icons/ocha/bug.svg")
    String exception();

}
