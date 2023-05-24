package com.javajman.hl7gen;

import io.codearte.jfairy.producer.person.Person;

import java.util.Properties;
import java.util.Random;

public class PR1
{
	public static String generateRandomPR1(Person doctor, int setId)
	{
		
		String doctorId = doctor.getNationalIdentificationNumber();
		String doctorName = doctor.getFullName().replace(" ", "^");
		
		String randomDate =  Utilities.formatDate( Utilities.getRecentDate(), "YYYYMMddHHmmss");
		
		Properties prop =  Utilities.loadProp("icd10pcs_codes_2018.txt");
		
		Object[] keys = prop.stringPropertyNames().toArray();
		Object key = keys[new Random().nextInt(keys.length)];
		
		String seg = "PR1|";
		if(doctor != null)
		{
			seg += setId+"||"+key+"^"+prop.getProperty(key.toString())+"^I10||"+ randomDate + "|||||"+doctorId+"^"+doctorName+"|"+doctorId+"^"+doctorName+"|||||\r";
		}
		else
		{
			seg += setId+"||"+key+"^"+prop.getProperty(key.toString())+"^I10||"+ randomDate + "||||||||||||||||\r";
		}
		
		return seg;
	}
}
