/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appconfig;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.tops.hotelmanager.util.*;

/**
 * This class is used for the initialisation of the application
 * 
 * @author omm
 */
public class AppInitializer implements WebApplicationInitializer {

	private static ApplicationContext applicationContext = null;

	private static ServletContext servletContext = null;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * Method to initialise spring application context based on environment
	 * [WEB|STANDALONE]
	 * 
	 * @param servletContext
	 */
	public void initApplication(ServletContext servletContext) {
		if (servletContext != null) {
			String environmentMode = System
					.getProperty(CommonConstant.ENVIRONMENT_MODE);
			if (environmentMode == null) {
				System.out
						.println("EnvironmentMode is not set in the jvm argument. system exit.\n1. Eclipse: Project > Right Click > Run As > Run Configurations > Apache Tomcat > Tomcat XX > (X)= Argument > VM Arguments > Add argument -DEnvironmentMode=DEV\n2. Netbeans: Servers > Tomcat XXX > Platform > VM Options > Add argument -DEnvironmentMode=DEV");
				System.exit(0);
			} else if (!environmentMode.equals(CommonConstant.ENVIRONMENT_DEV)
					&& !environmentMode.equals(CommonConstant.ENVIRONMENT_TEST)
					&& !environmentMode.equals(CommonConstant.ENVIRONMENT_PROD)) {
				System.out
						.println("Invalid EnvironmentMode set in the jvm argument: "
								+ System.getProperty(CommonConstant.ENVIRONMENT_MODE)
								+ ". system exit.");
				System.exit(0);
			} else {
				System.out.println("*****onStartup user.dir: "
						+ System.getProperty("user.dir") + ", context path: "
						+ servletContext.getContextPath() + ", app dir: "
						+ servletContext.getRealPath(File.separator));
				AppInitializer.servletContext = servletContext;
				System.out
						.println("*****Initializing Web Application context ");
				applicationContext = new AnnotationConfigWebApplicationContext();
				((AnnotationConfigRegistry) applicationContext)
						.register(AppConfigWeb.class);
			}
		} else {
			System.out.println("*****onStartup user.dir: "
					+ System.getProperty("user.dir"));
			System.out.println("*****Initializing Local Application context ");
			applicationContext = new AnnotationConfigApplicationContext();
			((AnnotationConfigRegistry) applicationContext)
					.register(AppConfigStandalone.class);
			((AnnotationConfigApplicationContext) applicationContext).refresh();
		}
	}

	/**
	 * Overridden method to listen the event of web context initialisation for
	 * the application
	 */

	public void onStartup(ServletContext servletContext)
			throws ServletException {

		initApplication(servletContext);
		// Adding the listener for the rootContext
		servletContext.addListener(new ContextLoaderListener(
				(WebApplicationContext) applicationContext));
		// Registering the dispatcher servlet mappings.
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				"dispatcher", new DispatcherServlet(
						(WebApplicationContext) applicationContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

	}

}
