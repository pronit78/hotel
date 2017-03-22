/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.tops.hotelmanager.util.CommonConstant;
/**
 *
 * @author omm
 */
@Configuration
// @PropertySource("file:tops/properties/classpath:HotelManagerConfig.properties")
@ComponentScan(CommonConstant.SPRING_PACKAGE_SCAN)
@EnableScheduling
@Import(AppConfigCommon.class)
public class AppConfigStandalone {
}
