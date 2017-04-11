package com.durrutia.ebean;

import com.google.common.base.Preconditions;

import java.util.List;

import io.ebean.EbeanServer;
import lombok.extern.slf4j.Slf4j;

/**
 * Funcionalidad comun para todos los managers.
 *
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407172600
 */
@Slf4j
public final class BaseModelDAO {

    /**
     * Conexion con la libreria Ebean
     */
    private EbeanServer ebeanServer;

    /**
     * Se requere de {@link EbeanServer}.
     */
    public BaseModelDAO(final EbeanServer ebeanServer) {

        Preconditions.checkNotNull(ebeanServer, "EbeanServer can't be null");
        this.ebeanServer = ebeanServer;
    }

    /**
     * Dado el nombre de una clase, retorna una lista de esos objectos presentes en la base de datos.
     *
     * @param clazz de la clase a buscar.
     * @return the {@link List}.
     */
    public <T> List<T> findList(Class<T> clazz) {
        return this.ebeanServer.find(clazz)
                // .setUseCache(true)
                // .setUseQueryCache(true)
                .findList();
    }

    /**
     * Obtengo un T en particular dado el nombre de su propiedad y su valor.
     *
     * @param clazz        de la clase a buscar.
     * @param propertyName de la clase.
     * @param value        de la property.
     */
    public <T> T get(Class<T> clazz, String propertyName, Object value) {
        return this.ebeanServer.find(clazz)
                // .setUseQueryCache(true)
                // .setUseCache(true)
                .where()
                .eq(propertyName, value)
                .findUnique();
    }


    /**
     * Shutdown el backend.
     */
    public void shutdown() {

        log.debug("Shutting down EbeanServer ..");

        // Des-registrar el driver
        this.ebeanServer.shutdown(true, false);

    }

}
