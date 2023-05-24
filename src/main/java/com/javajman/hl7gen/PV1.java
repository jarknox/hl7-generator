package com.javajman.hl7gen;

import io.codearte.jfairy.producer.person.Person;

import java.util.Random;

public class PV1
{
	public static Random r = new Random();
	//id^lName^fName^middle Initial^^^type^source (NPI vs Local)
	public static String generatePV1(Person doctor, String sourceFacility) {
		
		String doctorId = doctor.getNationalIdentificationNumber();
		String doctorName = doctor.getFullName().replace(" ", "^");
		
		String pv1 = "PV1|1|O|" + sourceFacility + "^" + r.nextInt(100) + "^" + r.nextInt(100) +
				"||||"+doctorId+"^"+doctorName+"^^^^^SourceId||||||||||||"+r.nextInt(99999999)+"\r";
		return pv1;
	}
	
}
