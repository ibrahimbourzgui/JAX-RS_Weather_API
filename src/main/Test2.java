package main;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test2 {
	public static void timelineRequestHttpClient() throws Exception {
		   //set up the end point
		   String apiEndPoint= "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
		   String location="Rabat";
		   String startDate=null;
		   String endDate=null;
		   String unitGroup="metric";
		   String apiKey="SJ84SW3W624ZZMXW2VGKAK2U7";

		   StringBuilder requestBuilder=new StringBuilder(apiEndPoint);
		   requestBuilder.append(URLEncoder.encode(location, StandardCharsets.UTF_8.toString()));	
		   if (startDate!=null && !startDate.isEmpty()) {
				requestBuilder.append("/").append(startDate);
				if (endDate!=null && !endDate.isEmpty()) {
					requestBuilder.append("/").append(endDate);
				}
		}			
		   URIBuilder builder = new URIBuilder(requestBuilder.toString());			
		   builder.setParameter("unitGroup", unitGroup).setParameter("key", apiKey);
				
		   HttpClient httpclient = new DefaultHttpClient();
		   HttpGet get = new HttpGet(builder.build());		
		   HttpResponse response = httpclient.execute(get);    
		   String rawResult=null;
		   try {
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.printf("Bad response status code:%d%n", response.getStatusLine().getStatusCode());
				return;
			}
						
		HttpEntity entity = response.getEntity();
			   if (entity != null) {
			    	rawResult=EntityUtils.toString(entity, "utf-8");
			    }			  			    
					} finally {	}			
					
					parseTimelineJson(rawResult);
	}
	private static void parseTimelineJson(String rawResult) throws Exception{
		
		if (rawResult==null || rawResult.isEmpty()) {
			System.out.printf("Pas de données retournées .....%n");
			return;
		}
		
		JSONObject timelineResponse = new JSONObject(rawResult);			
		ZoneId zoneId=ZoneId.of(timelineResponse.getString("timezone"));		
		System.out.printf("Météo de : %s%n", timelineResponse.getString("resolvedAddress"));
		
		JSONArray values=timelineResponse.getJSONArray("days");
		
		System.out.printf("Date\t\tMaxTemp\tMinTemp\tPrecip\tSource%n");
		for (int i = 0; i < values.length(); i++) {
			JSONObject dayValue = values.getJSONObject(i);
            
            ZonedDateTime datetime=ZonedDateTime.ofInstant(Instant.ofEpochSecond(dayValue.getLong("datetimeEpoch")), zoneId);
            
            double maxtemp=dayValue.getDouble("tempmax");
            double mintemp=dayValue.getDouble("tempmin");
            double pop=dayValue.getDouble("precip");
            String source=dayValue.getString("source");
            System.out.printf("%s\t%.1f\t%.1f\t%.1f\t%s%n", datetime.format(DateTimeFormatter.ISO_LOCAL_DATE), maxtemp, mintemp, pop,source );
        }
	}
	public static void main(String[] args)  throws Exception {
		Test2.timelineRequestHttpClient();
	}
}
