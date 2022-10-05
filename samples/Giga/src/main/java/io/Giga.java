package io;

import com.easypost.model.Rate;
import io.model.Business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Giga {

	public static final Integer HEAD   = 0;
	public static final Integer BOTTOM = 1;

	public static final BigDecimal COMMISSION = new BigDecimal(0.0421);

	public static final String SUPER_PASSWORD = "password";
	public static final String SUPER = "croteau.mike+super@gmail.com";

	public static final String SUPER_ROLE     = "SUPER_ROLE";
	public static final String BUSINESS_ROLE  = "BUSINESS_ROLE";
	public static final String CUSTOMER_ROLE  = "CUSTOMER_ROLE";

	public static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
	public static final String DATE_PRETTY = "HH:mmaa dd MMM";

	public static final String REQUEST_MAINTENANCE  	= "requests::";
	public static final String DATA_IMPORT_MAINTENANCE  = "data::";
	public static final String MEDIA_IMPORT_MAINTENANCE = "media::";
	public static final String USER_MAINTENANCE         = "users::";
	public static final String DESIGN_MAINTENANCE       = "designs::";
	public static final String PAGE_MAINTENANCE     	= "pages::";
	public static final String ITEM_MAINTENANCE     	= "items::";
	public static final String ASSET_MAINTENANCE    	= "assets::";
	public static final String CATEGORY_MAINTENANCE 	= "categories::";
	public static final String BUSINESS_MAINTENANCE 	= "businesses::";

	public static final String ITEM_IMAGE     = "nike_dc2725704-1.jpeg";
	public static final String OCEAN_ENDPOINT = "https://barter.sfo3.digitaloceanspaces.com/";

	public static String getUri(String value){
		if(value != null)
			return value
					.replaceAll("[^a-zA-Z0-9]", "_")
					.replaceAll(" ", "_")
					.replaceAll(" ", "")
					.replaceAll(" ", "")
					.toLowerCase();
		return "";
	}

	public static String getPhone(String phone){
		if(phone != null)
			return phone
					.replaceAll("[^a-zA-Z0-9]", "")
					.replaceAll(" ", "")
					.replaceAll(" ", "")
					.replaceAll(" ", "");
		return "";
	}

	public static String getSpaces(String email) {
		if(email != null)
			return email.replaceAll(" ", "")
					.replaceAll(" ", "")
					.replaceAll(" ", "");
		return "";
	}

	public static List<Business> sort(List<Business> businesses){
		Comparator<Business> comparator = (mn, mx) -> mx.getName().compareTo(mn.getName());
		Collections.sort(businesses, comparator);
		return businesses;
	}

	public static List<Rate> sortRates(List<Rate> rates){
		Comparator<Rate> comparator = (mn, mx) -> mx.getRate().compareTo(mn.getRate());
		Collections.sort(rates, comparator);
		return rates;
	}


	public static int getNumber(int max){
		Random r = new Random();
		return r.nextInt(max);
	}

	public static String getString(int n) {
		String CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890";
		StringBuilder uuid = new StringBuilder();
		Random rnd = new Random();
		while (uuid.length() < n) {
			int index = (int) (rnd.nextFloat() * CHARS.length());
			uuid.append(CHARS.charAt(index));
		}
		return uuid.toString();
	}

	public static Long getDate(int days){
		LocalDateTime ldt = LocalDateTime.now().minusDays(7);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Giga.DATE_FORMAT);
		String date = dtf.format(ldt);
		System.out.println("date : " + date);
		return Long.valueOf(date);
	}

	public static Long getDate(){
		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Giga.DATE_FORMAT);
		String date = dtf.format(ldt);
		return Long.valueOf(date);
	}

	public static Long getDateTimezone(String timezone){
		LocalDateTime ldt = LocalDateTime.now();
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffset = zone.getRules().getOffset(ldt);
		ZonedDateTime zdt = ldt.atOffset(zoneOffset)
				.atZoneSameInstant(ZoneId.of(timezone));
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Giga.DATE_FORMAT);
		String date = dtf.format(zdt);
		return Long.valueOf(date);
	}

	public static Long getDateTimezoneMins(int mins, String timezone){
		LocalDateTime ldt = LocalDateTime.now().plusMinutes(mins);
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffset = zone.getRules().getOffset(ldt);
		ZonedDateTime zdt = ldt.atOffset(zoneOffset)
				.atZoneSameInstant(ZoneId.of(timezone));
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Giga.DATE_FORMAT);
		String date = dtf.format(zdt);
		return Long.parseLong(date);
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String getPretty(Long date){
		String dateString = "";
		try {
			SimpleDateFormat parser = new SimpleDateFormat(Giga.DATE_FORMAT);
			Date d = parser.parse(Long.toString(date));

			SimpleDateFormat sdf2 = new SimpleDateFormat(Giga.DATE_PRETTY);
			dateString = sdf2.format(d);
		}catch(Exception ex){}
		return dateString;
	}

	public static boolean containsCharacters(String str) {
		Pattern p = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static String getExt(final String path) {
		String result = null;
		if (path != null) {
			result = "";
			if (path.lastIndexOf('.') != -1) {
				result = path.substring(path.lastIndexOf('.'));
				if (result.startsWith(".")) {
					result = result.substring(1);
				}
			}
		}
		return result;
	}

	public static String pad(String value, int places, String character){
		if(value.length() < places){
			value = character.concat(value);
			pad(value, places, character);
		}
		return value;
	}
}