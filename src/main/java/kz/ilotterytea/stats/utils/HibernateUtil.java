package kz.ilotterytea.stats.utils;

import kz.ilotterytea.stats.entities.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ilotterytea
 * @version 1.0
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        Logger log = LoggerFactory.getLogger(HibernateUtil.class.getSimpleName());
        try {
            return new Configuration()
                    .configure()
                    .addAnnotatedClass(Channel.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Couldn't create a session: ", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

