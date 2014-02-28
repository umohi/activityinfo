package org.activityinfo.ui.full.client.importer.match;


import com.google.appengine.repackaged.com.google.common.collect.Lists;
import org.activityinfo.reports.shared.model.TextReportElement;
import org.junit.Test;

import java.util.List;

public class TextScorererTest {

    @Test
    public void nonMatches() {
        // These should not match at all
        // They are a separate set of entities
        // and it's obvious to the naked eye that they
        // are nowhere near close

        // it's important that our alogorithm
        // is able to see this

        List<String> regions = Lists.<String>newArrayList(
                "Region de Bruxelles-Capitale / Brussels Hoofdstede",
                "Vlaams Gewest",
                "Region wallonne");

        test("West-Vla", regions);
        test("Oost-Vla", regions);
        test("Antwerpen", regions);
        test("Brussel", regions);
        test("Limburg", regions);
        test("Vla-Bra", regions);
    }

    private void test(String province, List<String> regions) {
        TextScorerer scorer = new TextScorerer();
        for(String region : regions) {
            double score = scorer.score(province, region);
            System.out.println(String.format("%f", score) + " " + province + " <-> " + region);
        }
    }

}
