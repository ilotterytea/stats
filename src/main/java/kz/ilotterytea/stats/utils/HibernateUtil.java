package kz.ilotterytea.stats.utils;

import kz.ilotterytea.stats.entities.Channel;
import kz.ilotterytea.stats.entities.Command;
import kz.ilotterytea.stats.entities.Hashtag;
import kz.ilotterytea.stats.entities.User;
import kz.ilotterytea.stats.entities.emotes.Emote;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            return configuration
                    .configure()
                    .addAnnotatedClass(Channel.class)
                    .addAnnotatedClass(Emote.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Command.class)
                    .addAnnotatedClass(Hashtag.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
