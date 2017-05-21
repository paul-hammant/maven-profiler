/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.MojoProfile;
import io.tesla.lifecycle.profiler.PhaseProfile;
import io.tesla.lifecycle.profiler.ProjectProfile;
import io.tesla.lifecycle.profiler.SessionProfile;
import io.tesla.lifecycle.profiler.SessionProfileRenderer;
import io.tesla.lifecycle.profiler.Timer;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class DefaultSessionProfileRenderer implements SessionProfileRenderer {

  private Timer timer;
  
  public DefaultSessionProfileRenderer(Timer timer) {
    this.timer = timer;
  }
    public void render(SessionProfile sessionProfile, String filePath) {
	Map<String, Long> cumulativepluginTime = new HashMap<String, Long>();
	Long totalTime = 0l;
	render("#################################################");
	render("#			Build time breakup					#");
	render("#################################################");
    render("Project Name,Phase,Plugin,Time,Formatted Time");
    for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
      for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
        for(MojoProfile mp : phaseProfile.getMojoProfiles()) {
        	Long timeElapsed = mp.getElapsedTime();
          render(pp.getProjectName()+","+phaseProfile.getPhase()+","+mp.getId()+","+mp.getElapsedTime()+","+timer.format(mp.getElapsedTime()));
          populateCumulativepluginTime(cumulativepluginTime, mp);
          totalTime = totalTime + timeElapsed;
        }
      }
    }
    render(",,Total,"+totalTime+","+timer.format(totalTime));
    
    render("#################################################");
	render("#			Cumulative Plugin time breakup		#");
	render("#################################################");
    render("Plugin Name,Time,Formatted Time");
    for(String pluginName : cumulativepluginTime.keySet()){
    	Long pluginTime = cumulativepluginTime.get(pluginName);
    	String formattedTime = timer.format(pluginTime);
    	render(pluginName+","+pluginTime+","+formattedTime);
    }
    render("Total,"+totalTime+","+timer.format(totalTime));
  }
  
  private void populateCumulativepluginTime(Map<String, Long> cumulativepluginTime, MojoProfile mp) {
	  Long pluginTime = cumulativepluginTime.get(mp.getId());
	  Long elapsedTime = mp.getElapsedTime();
	  if(pluginTime == null){
		  pluginTime = elapsedTime;
	  }
	  else{
		  pluginTime = pluginTime + elapsedTime;
	  }
	  cumulativepluginTime.put(mp.getId(), pluginTime);
	
}

private void render(String s) {
    System.out.println(s);
  }
}
