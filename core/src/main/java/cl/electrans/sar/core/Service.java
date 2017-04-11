package cl.electrans.sar.core;

import com.durrutia.ebean.BaseModelDAO;

import java.util.List;

import cl.electrans.sar.core.manager.RequerimientoManager;
import cl.electrans.sar.core.model.Requerimiento;

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

        // Configuration de la base de datos.
        final BaseModelDAO baseModelDAO = BaseModelDAOFactory.buildBaseModelDAO(database);

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
