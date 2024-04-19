package com.nlpcaptcha.captcha;

import com.nlpcaptcha.captcha.controller.ListChallengeController;
import com.nlpcaptcha.captcha.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CaptchaApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(CaptchaApplication.class, args);







        /*
        //SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        //Session session = sessionFactory.getCurrentSession();
        LOGGER.info("Session created");

        try {
            // start transaction
            Transaction tx = session.beginTransaction();

            // Save the Model object
            session.persist(listChallenge);
            session.persist(listChallenge);


            // Commit transaction
            tx.commit();

            LOGGER.info("Cart ID={}, Foreign Key Cart ID={}", listChallenge.getId(), listChallenge.getReferenceUsage().getId());
            LOGGER.info("item1 ID={}", listChallenge.getId());


        } catch (Exception e) {
            //LOGGER.error("Exception occurred", e);
        } finally {
           if (!sessionFactory.isClosed()) {
                LOGGER.info("Closing SessionFactory");
                sessionFactory.close();
            }
        }*/

    }


}
