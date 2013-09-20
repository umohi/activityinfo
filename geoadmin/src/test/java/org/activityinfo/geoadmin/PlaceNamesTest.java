package org.activityinfo.geoadmin;

import org.junit.Test;

public class PlaceNamesTest {

	@Test
	public void tomasina() {

		check("TOAMASINA I", "TOAMASINA II");
		check("TOAMASINA I", "TOAMASINA I");
		check("Kindu", "Kinshasa");
		check("Kindu", "INS");
		
	
	}

	private void check(String s1, String s2) {
		System.out.println(String.format("%s <=> %s => %f", s1, s2, PlaceNames.similarity(s1, s2)));
	}
}
