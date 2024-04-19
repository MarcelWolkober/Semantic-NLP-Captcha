package com.nlpcaptcha.captcha;

import com.nlpcaptcha.captcha.model.ListChallenge;
import com.nlpcaptcha.captcha.model.Position;
import com.nlpcaptcha.captcha.model.Usage;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class HibernateUtil {

     private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    /**
     * Utility class
     */
    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    private static SessionFactory buildSessionFactory() {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
          .applySettings(dbSettings())
          .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
          .addAnnotatedClass(ListChallenge.class)
          .addAnnotatedClass(Usage.class)
          .addAnnotatedClass(Position.class)
          .buildMetadata();

        return metadata.buildSessionFactory();
    }
    private static Map<String, Object> dbSettings() {
        Map<String, Object> dbSettings = new HashMap<>();
        dbSettings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        dbSettings.put(Environment.SHOW_SQL, "true");
        dbSettings.put(Environment.HBM2DDL_AUTO, "create");
        return dbSettings;
    }
}