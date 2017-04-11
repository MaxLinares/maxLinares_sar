package cl.electrans.sar.core.manager;

import com.durrutia.ebean.BaseModelDAO;

import java.util.List;

import cl.electrans.sar.core.model.Requerimiento;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407173500
 */
@Slf4j
public final class RequerimientoManager {

    /**
     * DAO de acceso a la base de datos.
     */
    private BaseModelDAO dao;

    /**
     * @param baseModelDAO
     */
    public RequerimientoManager(final BaseModelDAO baseModelDAO) {
        this.dao = baseModelDAO;
    }

    /**
     * @return the {@link List} of {@link Requerimiento}.
     */
    public List<Requerimiento> getRequerimientos() {
        return this.dao.findList(Requerimiento.class);
    }

    /**
     * @param ordenProceso
     * @return the {@link Requerimiento}.
     */
    public Requerimiento getRequerimiento(final Long ordenProceso) {
        return this.dao.get(Requerimiento.class, "ordenProceso", ordenProceso);
    }

    /**
     * Shutdown the backend.
     */
    public void shutdown() {
        this.dao.shutdown();
    }
}
