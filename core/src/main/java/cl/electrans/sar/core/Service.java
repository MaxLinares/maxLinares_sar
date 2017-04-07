package cl.electrans.sar.core;

import com.durrutia.ebean.BaseModelDAO;
import com.durrutia.ebean.EbeanServerBuilder;
import com.google.common.base.Preconditions;

import java.util.List;

import cl.electrans.sar.core.model.Requerimiento;
import io.ebean.EbeanServer;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407195100
 */
public class Service {

    /**
     * Manager de {@link Requerimiento}.
     */
    private RequerimientoManager requerimientoManager;

    /**
     * @param database
     */
    public Service(final String database) {

        Preconditions.checkNotNull(database, "Database name is null");

        // EbeanServer
        final EbeanServer ebeanServer = new EbeanServerBuilder(database)
                .addClass(Requerimiento.class)
                .build();

        // dao
        final BaseModelDAO baseModelDAO = new BaseModelDAO(ebeanServer);
        Preconditions.checkNotNull(baseModelDAO, "BaseModelDao is null");

        // Manager de requerimientos.
        this.requerimientoManager = new RequerimientoManager(baseModelDAO);

    }

    /**
     * @return the {@link List} of {@link Requerimiento}.
     */
    public List<Requerimiento> getRequerimientos() {
        return this.requerimientoManager.getRequerimientos();
    }

    /**
     * @param ordenProceso
     * @return the {@link Requerimiento}.
     */
    public Requerimiento getRequerimiento(final Long ordenProceso) {
        return this.requerimientoManager.getRequerimiento(ordenProceso);
    }

    /**
     * Apagar el sistema.
     */
    public void shutdown() {

        this.requerimientoManager.shutdown();

    }

}
