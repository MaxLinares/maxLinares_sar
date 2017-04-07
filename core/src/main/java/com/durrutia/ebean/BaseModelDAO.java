package com.durrutia.ebean;

import java.util.List;

import io.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407172600
 */
@Slf4j
public final class BaseModelDAO {

    /**
     * EbeanServer
     */
    private EbeanServer ebeanServer;

    /**
     *
     */
    public BaseModelDAO(final EbeanServer ebeanServer) {
        this.ebeanServer = ebeanServer;
    }

    /**
     *
     * @param clazz
     * @return the {@link List}.
     */
    public <T> List<T> findList(Class<T> clazz) {
        return this.ebeanServer.find(clazz)
                .setUseCache(true)
                .setUseQueryCache(true)
                .findList();
    }

    /**
     *
     * @param clazz
     * @param propertyName
     * @param value
     */
    public <E> E get(Class<E> clazz, String propertyName, Object value) {
        return this.ebeanServer.find(clazz)
                .setUseQueryCache(true)
                .setUseCache(true)
                .where()
                .eq(propertyName, value)
                .findUnique();
    }


    /**
     * Shutdown
     */
    public void shutdown() {

        log.debug("Shutting down EbeanServer ..");

        // Des-registrar el driver
        this.ebeanServer.shutdown(true, false);

    }

}
