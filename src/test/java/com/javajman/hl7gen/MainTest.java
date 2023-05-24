package com.javajman.hl7gen;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MainTest {


    @Test
    public void testMain()
            throws Exception
    {
        Main.main(new String[]{"1", "./target"});
    }

    @Test
    public void testDoctorNote() throws Exception {
        String note = new Main().getRandomDoctorsNote();
        assertNotNull(note);
        System.out.println(note);


    }
}