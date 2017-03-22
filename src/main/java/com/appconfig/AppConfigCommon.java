/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appconfig;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tops.hotelmanager.util.AppDatabase;
import com.tops.hotelmanager.util.CommonConstant;
import com.tops.hotelmanager.util.CommonUtil;

/**
 *
 * @author omm
 */
@Configuration
@PropertySource("classpath:HotelManagerConfig.properties")
@EnableJpaRepositories(basePackages = { CommonConstant.SPRING_PACKAGE_SCAN })
@EnableTransactionManagement
public class AppConfigCommon extends WebMvcConfigurerAdapter implements
		SchedulingConfigurer {

	/**
	 * properties object
	 */
	@Autowired
	private Environment env;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		String url = env.getProperty("DB_URL");
		String user = env.getProperty("DB_USER");
		String magicword = env.getProperty("DB_PASSWORD");
		String maxConn = env.getProperty("DB_MAX_CONNECTIONS");
		String minConn = env.getProperty("DB_INITIAL_CONNECTIONS");
		String maxIdle = env.getProperty("DB_MAX_IDLE_CONNECTIONS");

		AppDatabase appDatabase = new AppDatabase();
		appDatabase.setDbMagicword(magicword);
		appDatabase.setDbUser(user);
		appDatabase.setJdbcUrl(url);
		appDatabase.setMaxCon(Integer.parseInt(maxConn));
		appDatabase.setMinCon(Integer.parseInt(minConn));
		appDatabase.setMaxIdleCon(Integer.parseInt(maxIdle));

		return CommonUtil.getDataSource(appDatabase);
	}

	/**
	 * Spring application context bean declaration
	 * 
	 * @return ApplicationContext
	 */
	@Bean
	public ApplicationContext applicationContext() {
		System.out.println("**********applicationContext");
		return AppInitializer.getApplicationContext();
	}

	/**
	 * Entity manager to manage all our data base entities and repositories
	 * 
	 * @param dataSource
	 *            for database connection
	 * @return Entity manager factory bean
	 */
	@Bean(name = "entityManagerFactory", destroyMethod = "destroy")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPackagesToScan(CommonConstant.SPRING_PACKAGE_SCAN);
		emf.setPersistenceProvider(new HibernatePersistenceProvider());
		Properties jpaProperties = getHibernateProperties();
		emf.setJpaProperties(jpaProperties);
		emf.setDataSource(dataSource);
		System.out.println("**********entityManagerFactory " + emf);
		return emf;
	}

	/**
	 * Transaction manager to manage all our database transaction
	 * 
	 * @param emf
	 *            entity manager factory
	 * @return Transaction manager
	 */
	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		System.out
				.println("**********transactionManager " + transactionManager);
		return transactionManager;
	}

	/**
	 * Hibernate properties
	 * 
	 * @return Properties object initialised woth requred properties for
	 *         hibernate
	 */
	private Properties getHibernateProperties() {
		Properties prop = new Properties();
		prop.put("hibernate.hbm2ddl.auto",
				env.getProperty("hibernate.hbm2ddl.auto"));
		prop.put("hibernate.format_sql",
				env.getProperty("hibernate.format_sql"));
		prop.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		prop.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
		// prop.put("hibernate.enable_lazy_load_no_trans", "true");
		return prop;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

		String messageFolder = env.getProperty("MESSAGE_FOLDER");
		String messageFilePrefix = env.getProperty("MESSAGE_FILE_PREFIX");
		String charEncoading = env.getProperty("CHAR_ENCOADING");

		System.out.println("**********messageSource messageFolder: "
				+ messageFolder + ", messageFilePrefix: " + messageFilePrefix
				+ ", charEncoading: " + charEncoading);

		if (null == messageFolder || messageFolder.isEmpty()) {
			messageFolder = "/resources/";
		}
		if (null == messageFilePrefix || messageFilePrefix.isEmpty()) {
			messageFilePrefix = "messages";
		}
		if (null == charEncoading || charEncoading.isEmpty()) {
			charEncoading = "UTF-8";
		}

		messageSource.setBasename(messageFolder + messageFilePrefix);
		messageSource.setDefaultEncoding(charEncoading);
		messageSource.setCacheSeconds(1);
		return messageSource;
	}

	@Bean
	public CommonUtil commonUtil() {
		return new CommonUtil();
	}

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		System.out.println("*********Init thread pool for schedulers");
		return Executors.newScheduledThreadPool(5);
	}

	// @Bean
	// public JavaMailSender javaMailService() {
	// String host = env.getProperty("mail.host");
	// int port = Integer.valueOf(env.getProperty("mail.port"));
	// String userName = env.getProperty("mail.username");
	// String password = env.getProperty("mail.password");
	//
	// JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
	// javaMailSender.setHost(host);
	// javaMailSender.setPort(port);
	// javaMailSender.setUsername(userName);
	// javaMailSender.setPassword(password);
	//
	// javaMailSender.setJavaMailProperties(getMailProperties());
	//
	// return javaMailSender;
	// }

	private Properties getMailProperties() {
		Properties smtpServerProperties = new Properties();
		smtpServerProperties.put("mail.smtp.starttls.enable", "true");
		smtpServerProperties.put("mail.smtp.starttls.required", "true");
		smtpServerProperties.put("mail.smtp.port", 587);
		smtpServerProperties.put("mail.smtp.auth", "true");
		smtpServerProperties.put("mail.transport.protocol", "smtp");
		smtpServerProperties.put("mail.debug", "true");
		smtpServerProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		System.out.println("**********smtpServerProperties"
				+ smtpServerProperties);
		return smtpServerProperties;
	}

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub

	}

}
