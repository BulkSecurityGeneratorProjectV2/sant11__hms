package com.sant.hms.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;


@SpringBootApplication
@EnableTurbine
@EnableDiscoveryClient
@EnableHystrixDashboard
public class TurbineServer {

//	public static void main(String[] args) {
//		boolean cloudEnvironment = new StandardEnvironment().acceptsProfiles("cloud");
//		new SpringApplicationBuilder(TurbineServer.class).web(!cloudEnvironment).run(args);
//	}
	
	  public static void main(String[] args) {
		    SpringApplication.run(TurbineServer.class, args);
	  }
}