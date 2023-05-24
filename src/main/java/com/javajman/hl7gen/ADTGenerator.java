package com.javajman.hl7gen;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;

public class ADTGenerator
{
	
	public static void generateADT08Messages(int count, String destDir) {
		Fairy fairy = Fairy.create();
		Person person = null;
		Person doctor = null;
		
		for (int i = 1; i <= count; i++) {
			
			person = fairy.person();
			doctor = fairy.person();
			
			
			String sourceFacility =  Utilities.getRandomDepartment();
			
			String m =  Utilities.generateDemo(person, "A08", "ADT^A08");
			m +=  PV1.generatePV1(doctor, sourceFacility);
			
			
			for (int x = 0; x < 10; x++) {
				m +=  DG1.generateRandomDG1(doctor, x + 1);
			}
			for (int x = 0; x < 10; x++) {
				m +=  PR1.generateRandomPR1(doctor, x + 1);
			}
			
			m +=  Utilities.generateConSegment(sourceFacility);
			
			try {
				Utilities.saveFile(m, destDir, person.getFullName());
				System.out.println(m);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
	}
}
