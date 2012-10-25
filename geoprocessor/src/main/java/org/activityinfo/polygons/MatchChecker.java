package org.activityinfo.polygons;

import java.util.Map;

import com.google.common.collect.Maps;

public class MatchChecker {

	public Map<Integer, AdminEntity> unmatched = Maps.newHashMap();
	
	public MatchChecker(Iterable<AdminEntity> entities) {
		for(AdminEntity entity : entities) {
			unmatched.put(entity.getId(), entity);
		}
	}
	
	public void onMatched(int id) {
		unmatched.remove(id);
	}
	
	public void check() {
		if(!unmatched.isEmpty()) {
			System.err.println("WARNING: Some entities were not matched to polygons: ");
			for(AdminEntity entity : unmatched.values()) {
				System.err.println("\t" + entity.getName());
			}
		}
	}
	
}
