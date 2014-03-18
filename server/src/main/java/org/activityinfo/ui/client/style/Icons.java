package org.activityinfo.ui.client.style;

import com.bedatadriven.rebar.style.client.IconSet;
import com.google.gwt.core.shared.GWT;

/**
 * Application-wide icons
 */
public interface Icons extends IconSet {

    Icons INSTANCE = GWT.create(Icons.class);

    @Source(value = "icons/fontawesome.svg", glyph = 0xf055)
    String add();

    @Source(value = "icons/ocha/reporting.svg", glyph = 0xe61a)
    String form();

    @Source(value = "icons/ocha/reporting.svg", glyph = 0xe6c4)
    String location();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf115)
    String folder();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf114)
    String folderOpen();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf057)
    String delete();

    @Source("icons/icomoon/mobile.svg")
    String mobileDevice();

    @Source("icons/icomoon/file-excel.svg")
    String excelFile();

    @Source("icons/icomoon/cloud-upload.svg")
    String importIcon();

    @Source("icons/icomoon/table.svg")
    String table();

    @Source("icons/icomoon/earth.svg")
    String map();

    @Source("icons/icomoon/info.svg")
    String overview();

    /**
     * Symbolizes a problem connecting to the server
     */
    @Source("icons/disconnected.svg")
    String connectionProblem();

    /**
     * Symbolizes an unexpected exception (that is, a bug!)
     */
    @Source(value = "icons/fontawesome.svg", glyph = 0xf188)
    String exception();

}
