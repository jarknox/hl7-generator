package com.javajman.hl7gen;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class Utilities
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);
	final static public List<String> abbrevUSState = new ArrayList<String>(Arrays.asList("AL", "AK", "AS", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FM", "FL", "GA", "GU", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MH", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
			"NM", "NY", "NC", "ND", "MP", "OH", "OK", "OR", "PW", "PA", "PR", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VI", "VA", "WA", "WV", "WI", "WY"));
	
 private final static Random r = new Random();
	public static String generateDemo(Person person, String messageType, String fullMessageType) {
		
		String name = person.getFullName().replace(" ", "^");
		String ssn = person.getNationalIdentityCardNumber();
		Date bday = person.getDateOfBirth().toDate();
		String gender = person.getSex().name();
		String streetAddress = person.getAddress().getAddressLine1();
		String streetAddress2 = "";
		String city = person.getAddress().getCity();
		
		// fairy did not include a state so we will random get one
		int rnd = new Random().nextInt(abbrevUSState.size());
	
		String state =  abbrevUSState.get(rnd);
		
		String zip = person.getAddress().getPostalCode();
		
		String demo = "MSH|^~\\&|||||" + formatDate(new Date(), "YYYYMMddHHmmss.SSSZ") +
				"||"+fullMessageType+"|" + r.nextInt(100) + "|P|2.3\r" +
				"EVN|"+messageType+"|" + formatDate(new Date(), "YYYYMMddHHmm") + "\r" +
				"PID|1||" + ssn + "||" + name + "||" + formatDate(bday, "YYYYMMdd") +
				"|"+gender+"|||"+streetAddress+"^"+streetAddress2+"^"+city+"^"+state+"^"+zip+"|\r";
		
		return demo;
	}
	
	public static String generateConSegment(String sourceFacility)
	{
		double randomOptInOut = Math.random();
		String seg = "";
		if(randomOptInOut < 0.5)
		{
			seg = "CON||OPT_IN|||" + sourceFacility + "||||||||20170501|20170501|\r";
		}
		else
		{
			seg = "CON||OPT_OUT|||" + sourceFacility + "||||||||20170501|20170501|\r";
		}
		return seg;
	}
	
	public static void saveFile(String m, String destDir, String name) throws Exception
	{
		HapiContext context = new DefaultHapiContext();
		CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.3");
		context.setModelClassFactory(mcf);
		
		/*
		 * A Parser is used to convert between string representations of messages and instances of
		 * HAPI's "Message" object. In this case, we are using a "GenericParser", which is able to
		 * handle both XML and ER7 (pipe & hat) encodings.
		 */
		Parser p = context.getGenericParser();
		
		try {
			p.parse(m);
		} catch (HL7Exception e) {
			e.printStackTrace();
		}
		
		Path fn = Paths.get(destDir, name.replace(" ", "_") + ".txt");
		File f = new File(destDir +  "" + File.separator + name.replace(" ", "_") + ".txt");
		
		System.out.println("F  ::" + f);
		System.out.println("FN ::" + fn);
		try {
			
			if(f.createNewFile())
			{
				System.out.println("Created file ::" + f.getAbsolutePath());
			}
			else
			{
				System.out.println("For some stupid reason the new file was not created!");
			}
			f.setWritable(true);
			BufferedWriter writer = Files.newBufferedWriter(fn);
			writer.write(m);
			writer.flush();
			writer.close();
			System.out.println("wrote message " + fn.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	public static void generateMDMMessages(int count, String destDir) {
		
		Fairy fairy = Fairy.create();
		Person person = null;
		Person doctor = null;
		
		for (int i = 1; i <= count; i++) {
			
			person = fairy.person();
			doctor = fairy.person();
			
			String sourceFacility =  getRandomDepartment();
			
			String m =
					generateDemo(person, "T02", "MDM^T02") + PV1.generatePV1(doctor, sourceFacility) +
							"TXA|1|CN|TX|" + formatDate(getRecentDate(), "YYYYMMddHHmmss") + "||||||||DOC-ID-1000" + i + "|||||AU||AV\r" +
							"OBX|" + i + "|TX|100" + i + "^Reason For Visit: |" + i + "|" + getRandomDoctorsNote() + "||||||F\r";
			
			try {
				saveFile(m, destDir, person.getFullName());
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
			
		}
		
	}
	
	/*
	reads from symptoms.txt and combines with random sentence beginnings and sentence endings
	 */
	public static String getRandomDoctorsNote() {
		String[] symptoms = {"abnormal_gait"};
		String[] pre = {
				"The patient complained of ",
				"Reason for visit was ",
				"Evaluated patient for ",
				"Visited me because of ",
				"Started treatment for "};
		String[] post = {
				".  Performed tests. ",
				". ",
				". Prescribed medication. ",
				". Drew blood. ",
				". Discharged. ",
				". Admitted to hospital. ",
				". Recommended adjustments. ",
				". Discussed changes. "};
		
		try {
			URI filePath = Main.class.getClassLoader().getResource("symptoms.txt").toURI();
			symptoms = Files.lines(Paths.get(filePath)).toArray(String[]::new);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return pre[r.nextInt(pre.length)] + symptoms[r.nextInt(symptoms.length)].toLowerCase() + post[r.nextInt(post.length)];
	}
	
	/* return a date within the past 60 months */
	public static Date getRecentDate() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - r.nextInt(60));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - r.nextInt(32));
		return cal.getTime();
	}
	
	public static String getRandomDepartment() {
		String[] lines = {"ER"};
		try {
			URI filePath = Utilities.class.getClassLoader().getResource("departments.txt").toURI();
			lines = Files.lines(Paths.get(filePath)).toArray(String[]::new);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return lines[r.nextInt(lines.length)];
	}

//    private String generateRandomSSN() {
//        return String.format("%03d%02d%04d", r.nextInt(1000), r.nextInt(100), r.nextInt(10000));
//    }
	
	/*
	generate a random birthday for someone that is 18-99 years old
	 */
	public static Date randomBirthday() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 18 - r.nextInt(80));
		cal.set(Calendar.MONTH, r.nextInt(12));
		cal.set(Calendar.DAY_OF_MONTH, r.nextInt(32));
		return cal.getTime();
	}
	
	public static Properties loadProp(String propName)
	{
		Properties properties = new Properties();
		try {
			 LOGGER.info("Loading from::" +Utilities.class.getClassLoader().toString() );
			properties.load( Utilities.class.getClassLoader().getResourceAsStream(propName)) ;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return properties;
	}
	
	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}
}
