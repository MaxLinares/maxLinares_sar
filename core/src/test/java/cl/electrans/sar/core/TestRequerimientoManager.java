package cl.electrans.sar.core;

import com.google.common.base.Stopwatch;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.List;

import javax.persistence.PersistenceException;

import cl.electrans.sar.core.model.Requerimiento;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407174400
 */
@Slf4j
public class TestRequerimientoManager {

    /**
     * Todos los test deben terminar antes de 60 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(60);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite, sqlite-mem
     * WARN: hsql no soporta ENCRYPT
     */
    private static final String DB = "sqlite";

    /**
     * Backend
     */
    private Service service;

    /**
     * Cronometro
     */
    private Stopwatch stopWatch;

    /**
     * Antes del test completo.
     */
    @BeforeClass
    public static void setupTest() {

        log.debug("Setting Test ..");

    }

    /**
     * Antes de cada test
     */
    @Before
    public void beforeTest() {

        stopWatch = Stopwatch.createStarted();
        log.debug("BeforeTest +++++++++++++++++++++++++++++++++ Initializing Test Suite with database: {} ..", DB);

        // Crear e inicializar
        this.service = new Service(DB);

        log.debug("BeforeTest --------------------------------- Done.");
    }

    /**
     * Despues del test
     */
    @After
    public void afterTest() {

        log.debug("AfterTest ++++++++++++++++++++++++++++++++++++ Shutting down the database ..");
        this.service.shutdown();

        log.debug("AfterTest ------------------------------------ Finished in {}.", stopWatch.toString());
    }

    /**
     * Test de {@link cl.electrans.sar.core.model.Requerimiento}.
     */
    @Test
    public void testRequerimiento() {

        // Saving ..
        {
            final Requerimiento requerimiento = Requerimiento.builder()
                    .ordenProceso(87242017L)
                    .numeroCotizacion(8724L)
                    .detalle("Motor de Camion")
                    .prioridad(Requerimiento.Prioridad.NORMAL)
                    .build();

            log.debug("Saving Requerimiento {} ..", requerimiento);
            requerimiento.save();

            Assertions.assertThat(requerimiento.getId()).isEqualTo(1L);
        }

        // List ..
        {
            final List<Requerimiento> requerimientos = this.service.getRequerimientos();
            Assertions.assertThat(requerimientos).hasSize(1);

            for (final Requerimiento r : requerimientos) {
                log.debug("Requerimiento: {}", r);
            }
        }

        // Get ..
        {
            final Requerimiento requerimiento = this.service.getRequerimiento(87242017L);
            Assertions.assertThat(requerimiento).isNotNull();
        }

        // Shutdown
        {
            this.service.shutdown();

            // Se debe caer en caso de tratar de obtener algo despues del shutdown.
            Assertions.assertThatThrownBy(() -> {
                this.service.getRequerimiento(1L);
            }).isInstanceOf(PersistenceException.class);

        }

    }

    /**
     * Test de benchmark.
     */
    @Test
    public void testBenchmark() {

        // Insert
        {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final int max = 1000;
            for (int i = 1; i <= max; i++) {
                final Requerimiento requerimiento = Requerimiento.builder()
                        .ordenProceso(87252017L + i)
                        .numeroCotizacion(8725L + i)
                        .detalle("Motor " + i)
                        .prioridad(Requerimiento.Prioridad.URGENTE)
                        .build();
                requerimiento.save();

            }
            log.info("{} inserts in {}.", max, stopwatch);
        }

        // List time
        {

            final Stopwatch stopwatch = Stopwatch.createStarted();
            final int max = 1000;
            for (int i = 0; i < max; i++) {
                this.service.getRequerimientos().size();
            }
            log.info("{} selects in {}.", max, stopwatch);

        }

    }

}
