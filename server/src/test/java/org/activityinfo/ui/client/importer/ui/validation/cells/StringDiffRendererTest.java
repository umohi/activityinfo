package org.activityinfo.ui.client.importer.ui.validation.cells;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by alex on 2/19/14.
 */
public class StringDiffRendererTest {

    private StringDiffRenderer renderer;

    @Before
    public void setUp() throws Exception {
        renderer = new StringDiffRenderer(
                new ValidationCellTemplatesStub());
    }

    @Test
    public void test() {
        diff("North Kivu", "Nord Kivu");
        diff("Ho Chi Min", "Hochimin City");

    }

    private void diff(String s, String t) {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        renderer.render(html, s, t);
        System.out.println(html.toSafeHtml().asString());
    }


}
