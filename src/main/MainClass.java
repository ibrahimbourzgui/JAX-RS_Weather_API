package main;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainClass {
public static void main(String[] args) {
	URL urlService;
	try {
		urlService = new 
				URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/rabat?unitGroup=metric&key=SJ84SW3W624ZZMXW2VGKAK2U7&contentType=json");
		HttpURLConnection urlCnx= (HttpURLConnection) urlService.openConnection();
		urlCnx.setRequestMethod("GET");
		InputStream input= new BufferedInputStream(urlCnx.getInputStream());
		Scanner sc= new Scanner(input);
		JSONObject fluxJSON = new JSONObject(sc.nextLine());
		ZoneId zoneId=ZoneId.of(fluxJSON.getString("timezone"));
		System.out.println("Time zone est : "+zoneId);
		System.out.printf("Météo : %s%n", fluxJSON.getString("resolvedAddress"));

		JSONArray values=fluxJSON.getJSONArray("days");
		System.out.printf("Date\t\t\tMaxTemp\t\tMinTemp\t\tPrecip\t\tSource%n");
		for (int i = 0; i < values.length(); i++) {
			JSONObject dayValue = values.getJSONObject(i);
			ZonedDateTime datetime=ZonedDateTime.ofInstant(Instant.ofEpochSecond(dayValue.getLong("datetimeEpoch")), zoneId);
			double tempMaxi=dayValue.getDouble("tempmax");
			double tempMin=dayValue.getDouble("tempmin");
			double precipitation=dayValue.getDouble("precip");
			String source=dayValue.getString("source");
			System.out.printf("%s\t\t%.1f\t\t%.1f\t\t%.1f\t\t%s%n", datetime.format(DateTimeFormatter.ISO_LOCAL_DATE), 
			tempMaxi, tempMin, precipitation,source ); 
		}
	} catch (Exception e) {
		// TODO: handle exception
	}
}
}
