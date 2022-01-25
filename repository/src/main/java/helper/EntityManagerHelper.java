package helper;

import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;

public final class EntityManagerHelper {
    private EntityManagerHelper() {}

    private static class EntityManagerHelperHolder {
        private static final EntityManagerHelper holderEntityManagerHelper = new EntityManagerHelper();
    }

    public static EntityManagerHelper getInstance() {
        return EntityManagerHelper.EntityManagerHelperHolder.holderEntityManagerHelper;
    }

    public EntityManager getEntityManager(SessionFactory sessionFactory) {
        return sessionFactory.createEntityManager();
    }
}
