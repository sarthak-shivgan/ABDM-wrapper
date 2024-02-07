/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.database.mongo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This class can be used to provide Transactional capabilities to our database operations if we
 * have set up mongo database with replica sets. We can then annotate methods with Transactional
 * annotation, and then those methods should honor the transactional properties.
 *
 * <p>By default, Transactional Management is disabled, and this config enables it but this config
 * does not come into play until you add @Transactional to the desired method. Again, this will only
 * work if your mongo setup has replica sets.
 */
@Configuration
@EnableTransactionManagement
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.database}")
  public String databaseName;

  @Bean
  MongoTransactionManager transactionManager(
      @Qualifier("mongoDbFactory") MongoDatabaseFactory mongoDatabaseFactory) {
    return new MongoTransactionManager(mongoDatabaseFactory);
  }

  @Override
  protected String getDatabaseName() {
    return databaseName;
  }
}
