/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.tesla.lifecycle.profiler.MojoProfile;
import io.tesla.lifecycle.profiler.PhaseProfile;
import io.tesla.lifecycle.profiler.ProjectProfile;
import io.tesla.lifecycle.profiler.SessionProfile;
import io.tesla.lifecycle.profiler.SessionProfileRenderer;
import io.tesla.lifecycle.profiler.Timer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

@Named
@Singleton
public class JsonSessionProfileRenderer implements SessionProfileRenderer {

    /**
     * Renders timing information in a JSON format
     *
     * @param sessionProfile The session profile containing timing information
     * @param filePath       The file path to render the JSON file to.
     *                       For whitespace or "-", the output is written to standard output.
     */
    public void render(SessionProfile sessionProfile, String filePath) {

        HashMap<String, Object> items = new HashMap<String, Object>();
        for (ProjectProfile pp : sessionProfile.getProjectProfiles()) {
            HashMap<String, Object> projectItems = new HashMap<String, Object>();
            items.put(pp.getProjectName(), projectItems);

            for (PhaseProfile phaseProfile : pp.getPhaseProfile()) {
                HashMap<String, Object> phaseItems = new HashMap<String, Object>();
                projectItems.put(phaseProfile.getPhase(), phaseItems);

                phaseItems.put("total_time", phaseProfile.getElapsedTime());

                for (MojoProfile mp : phaseProfile.getMojoProfiles()) {
                    phaseItems.put(mp.getId(), mp.getElapsedTime());
                }
            }
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // write the structure out
        java.io.OutputStream outfile = System.out;
        try {
            if(!filePath.trim().equals("") && !filePath.trim().equals("-")) {
                outfile = new FileOutputStream(filePath);
            }
            mapper.writeValue(outfile, items);
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Cannot serialize profiled projects.", ex);
        } finally {
            if(outfile != System.out) {
                try {
                    outfile.close();
                } catch(java.io.IOException ex) {
                    throw new RuntimeException("Could not close underlying file.", ex);
                }
            }
        }
    }

}
