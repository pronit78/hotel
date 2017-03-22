/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appconfig;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tops.hotelmanager.util.CommonConstant;
import com.tops.hotelmanager.util.CommonUtil;

/**
 *
 * @author omm
 */
@Configuration
@EnableWebMvc
@PropertySource("classpath:HotelManagerConfig.properties")
@ComponentScan(CommonConstant.SPRING_PACKAGE_SCAN)
@EnableJpaRepositories(basePackages = { CommonConstant.SPRING_PACKAGE_SCAN })
@EnableTransactionManagement
@Import(AppConfigCommon.class)
public class AppConfigWeb extends WebMvcConfigurerAdapter {

	/**
	 * properties object
	 */
	@Autowired
	private Environment env;

	/**
	 * Configure here the mechanism to handle web resources apart from Spring
	 * controllers
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String property = env.getProperty("STATIC_WEB_RESORCES_FOLDERS");
		System.out.println("**********Configuring ResourceHandlers value: "
				+ property);
		if (null != property && !property.isEmpty()) {
			registry.setOrder(Integer.MIN_VALUE);
			String[] arr = property.split(",");
			for (String string : arr) {
				registry.addResourceHandler(string + "**")
						.addResourceLocations(string);
			}
		}
		ServletContext servletContext = AppInitializer.getServletContext();
		if (servletContext != null) {
			System.out.println("**********Setting servlet contect object: "
					+ property);
			ApplicationContext applicationContext = AppInitializer
					.getApplicationContext();
			CommonUtil commonUtil = applicationContext
					.getBean(CommonUtil.class);
			servletContext.setAttribute(
					CommonConstant.APPLICATION_KEY_COMMONUTIL, commonUtil);

		}
		System.out.println("**********Completed Configuring ResourceHandlers");
	}

	/**
	 * Configure servlets handling mechanism if you have any servlets in your
	 * application
	 * 
	 * @return DataSource
	 */
	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		System.out.println("**********Configuring DefaultServletHandling");
		configurer.enable();
		System.out
				.println("**********Completed Configuring DefaultServletHandling");
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		// StringHttpMessageConverter stringConverter = new
		// StringHttpMessageConverter();
		// stringConverter.setSupportedMediaTypes(Arrays.asList(new
		// MediaType("text", "plain", Charset.forName("UTF-8"))));
		// converters.add(stringConverter);
		converters.add(jsonConverter());
	}

	/**
	 * Interceptors (Filters) for all requests handled by spring controllers
	 */

	/**
	 * View resolving mechanism for spring controllers
	 * 
	 * @return
	 */
	@Bean
	public ViewResolver configureViewResolver() {
		String property = env.getProperty("VIEWS_JSP_FOLDER");
		System.out.println("**********Configuring ViewResolver value: "
				+ property);
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix(property);
		viewResolver.setSuffix(".jsp");
		System.out.println("**********Completed Configuring ViewResolver");
		return viewResolver;
	}

	/**
	 * ByDefault dispatcher servlet looks for the bean with id [localeResolver]
	 * in the context, hence @Bean (name = "localeResolver")
	 *
	 * @return
	 */
	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver(CommonUtil commonUtil) {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();

		String language = env.getProperty("LOCALE_LANGUAGE");
		String country = env.getProperty("LOCALE_COUNTRY");
		String cookieName = env.getProperty("LOCALE_COOKIE_NAME");

		System.out.println("**********localResolver before language: "
				+ language + ", country: " + country + ", cookieName: "
				+ cookieName);

		if (null == language || language.isEmpty()) {
			language = "en";
			country = "US";
		} else {
			if (null == country || country.isEmpty()) {
				country = null;
			}
		}

		if (null == cookieName || cookieName.isEmpty()) {
			cookieName = "sujavtechlocal";
		}

		System.out.println("**********localResolver after language: "
				+ language + ", country: " + country + ", cookieName: "
				+ cookieName);

		Locale locale = new Locale(language, country);
		localeResolver.setDefaultLocale(locale);
		localeResolver.setCookieName(cookieName);
		localeResolver.setCookieHttpOnly(true);
		if (!commonUtil.isDevEnv()) {
			localeResolver.setCookieDomain("." + CommonConstant.SYSTEM_DOMAIN);
		}
		localeResolver.setCookieMaxAge(Integer.MAX_VALUE);

		return localeResolver;
	}

	// @Bean
	public LocaleChangeInterceptor localChangeInterceptor() {
		String localeParam = env.getProperty("LOCALE_REQUEST_PARAM");
		System.out.println("**********localChangeInterceptor localeParam: "
				+ localeParam);
		if (null == localeParam || localeParam.isEmpty()) {
			localeParam = "locale";
		}
		LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
		changeInterceptor.setParamName(localeParam);
		return changeInterceptor;
	}

	@Bean
	public ObjectMapper objectMapper() {
		Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder
				.json();
		builder.createXmlMapper(false);
		return builder.build();
	}

	@Bean
	public MappingJackson2HttpMessageConverter jsonConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
				objectMapper());
		converter.setSupportedMediaTypes(Arrays
				.asList(MediaType.APPLICATION_JSON));
		return converter;
	}

}
