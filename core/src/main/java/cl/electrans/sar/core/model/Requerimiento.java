package cl.electrans.sar.core.model;

import com.durrutia.ebean.BaseModel;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407154400
 */
@Builder
@Entity
public class Requerimiento extends BaseModel {

    /**
     * Numero de orden de proceso. No pueden haber 2 iguales.
     */
    @Getter
    @Basic(optional = false)
    @Column(unique = true)
    private Long ordenProceso;

    /**
     * Numero de cotizacion asociado
     */
    @Getter
    @Basic(optional = false)
    @Column(unique = true)
    private Long numeroCotizacion;

    /**
     * Detalle
     */
    @Getter
    @Basic(optional = true)
    private String detalle;

    /**
     * Prioridad
     */
    @Getter
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    /**
     *
     */
    public enum Prioridad {
        NORMAL,
        URGENTE,
        EQUIPO_DETENIDO;
    }
}
