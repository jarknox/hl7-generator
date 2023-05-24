package com.javajman.hl7gen;

import java.io.File;

/**
 * Creates very simple MDM^T02 HL7 messages with fake data for every field
 */
public class Main {


    public Main() {

    }

    public static void main(String[] args) throws Exception {
        
        int count = 1;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }
        
        String destDir = System.getProperty("user.dir") + File.separator  + "hl7-messages";
        System.out.println(destDir);
        if (args.length > 1) {
            destDir = args[1];
        }
        ADTGenerator.generateADT08Messages(count, destDir);
    }


}

