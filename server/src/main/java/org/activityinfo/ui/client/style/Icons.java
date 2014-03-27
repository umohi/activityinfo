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

    @Source(value = "icons/icomoon/remove.svg")
    String remove();

    @Source(value = "icons/icomoon/pencil.svg")
    String edit();

    @Source(value = "icons/icomoon/cogs.svg")
    String bulkEdit();

    @Source(value = "icons/icomoon/cog.svg")
    String configure();

    @Source(value = "icons/icomoon/pie.svg")
    String filter();

    @Source(value = "icons/icomoon/wrench.svg")
    String wrench();

    @Source(value = "icons/ocha.svg", glyph = 0xe61a)
    String form();

    @Source(value = "icons/ocha.svg", glyph = 0xe6c4)
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
