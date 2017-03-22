/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appconfig;


/**
 *
 * @author omm
 */
public class TestMain {

	public static void main(String[] str) throws Exception {

		AppInitializer appInitializer = new AppInitializer();
		appInitializer.initApplication(null);
		
		System.out.println("Done");
	}
}
