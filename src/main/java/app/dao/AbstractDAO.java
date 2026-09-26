package app.dao;

import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDAO <T, ID> implements IDAO<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDAO.class);

    protected final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    @Override
    public T create(T entity){
        return executeInTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    @Override
    public Optional<T> getById(ID id){
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(entityClass, id));
        }
    }

    @Override
    public List<T> getAll(){
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        }
    }

    @Override
    public T update (T entity){
        return executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(ID id){
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null){
                em.remove(entity);
            }
            return null;
        });
    }

    protected T executeInTransaction(Function<EntityManager, T> action) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction emTransaction = em.getTransaction();
            try {
                emTransaction.begin();
                T result = action.apply(em);
                emTransaction.commit();
                return result;
            } catch (RuntimeException e){
                if (emTransaction.isActive()) {
                    emTransaction.rollback();
                }
                logger.error("Transaction failed, rolled back: {}", e.getMessage(), e);
                throw e;
            }
        }
    }
}
