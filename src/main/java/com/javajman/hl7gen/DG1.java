package com.javajman.hl7gen;

import io.codearte.jfairy.producer.person.Person;

import java.util.Properties;
import java.util.Random;

public class DG1
{
	public static String generateRandomDG1(Person doctor, int setId)
	{
		
		String doctorId = doctor.getNationalIdentificationNumber();
		String doctorName = doctor.getFullName().replace(" ", "^");
		
		String randomDate =  Utilities.formatDate(Utilities.getRecentDate(), "YYYYMMddHHmmss");
		
		Properties prop = Utilities.loadProp("icd10cm_codes_2017.txt");
		
		Object[] keys = prop.stringPropertyNames().toArray();
		Object key = keys[new Random().nextInt(keys.length)];
		
		String seg = "DG1|";
		if(doctor != null)
		{
			seg += setId+"|0|"+key+"^"+prop.getProperty(key.toString())+"^I10|"+prop.getProperty(key.toString())+"|"+ randomDate + "|F||||||||||"+doctorId+"^"+doctorName+"||||||\r";
		}
		else
		{
			seg += setId+"|0|"+key+"^"+prop.getProperty(key.toString())+"^I10|"+prop.getProperty(key.toString())+"|"+ randomDate + "|F|||||||||||||||\r";
		}
		
		return seg;
	}
}
