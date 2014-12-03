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

import java.util.HashMap;

@Named
@Singleton
public class JsonSessionProfileRenderer implements SessionProfileRenderer {

  public void render(SessionProfile sessionProfile) {

    HashMap<String, Object> items = new HashMap<String, Object>();
    for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
        HashMap<String, Object> projectItems = new HashMap<String, Object>();
        items.put(pp.getProjectName(), projectItems);

      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
          HashMap<String, Object> phaseItems = new HashMap<String, Object>();
          projectItems.put(phaseProfile.getPhase(), phaseItems);

          phaseItems.put("total_time", phaseProfile.getElapsedTime());

        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
            phaseItems.put(mp.getId(), mp.getElapsedTime());
        }
      }
    }

    ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      try {
          mapper.writeValue(System.out, items);
      } catch(java.io.IOException ex) {
          throw new RuntimeException("Cannot serialize profiled projects.", ex);
      }
  }

}
