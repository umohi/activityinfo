package org.activityinfo.jsonrpc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ActivityInfoService {

	private String endpoint = "http://polygons.gactivityinfo.appspot.com/api/";

	private Gson gson = new Gson();
	
	public List<AdminEntity> getAdminEntities(int adminLevelId) throws JsonSyntaxException, IOException {
		URL url = new URL(endpoint + "AdminEntities?levelId=" + adminLevelId);
		return gson.fromJson(Resources.toString(url, Charsets.UTF_8), AdminEntityResult.class).getData();
	}
	
}
