package cl.electrans.sar.core;

import com.durrutia.ebean.BaseModelDAO;
import com.durrutia.ebean.EbeanServerBuilder;
import com.google.common.base.Preconditions;

import cl.electrans.sar.core.model.Requerimiento;
import io.ebean.EbeanServer;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170411140000
 */
public final class BaseModelDAOFactory {

    /**
     * @param database
     * @return the {@link BaseModelDAO} de la aplicacion.
     */
    public static BaseModelDAO buildBaseModelDAO(final String database) {

        Preconditions.checkNotNull(database, "Database name is null");

        // EbeanServer
        final EbeanServer ebeanServer = new EbeanServerBuilder(database)
                .addClass(Requerimiento.class)
                // TODO: Agregar aca las demas clases
                .build();

        // Construccion del baseModelDAO
        final BaseModelDAO baseModelDAO = new BaseModelDAO(ebeanServer);
        Preconditions.checkNotNull(baseModelDAO, "BaseModelDao is null");

        return baseModelDAO;

    }
}
