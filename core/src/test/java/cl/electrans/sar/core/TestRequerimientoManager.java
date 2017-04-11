package cl.electrans.sar.core;

import com.google.common.base.Stopwatch;

import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
     * Todos los test deben terminar antes de 120 segundos.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(120);

    /**
     * Configuracion de la base de datos:  h2, hsql, sqlite, sqlite-mem
     * WARN: hsql no soporta ENCRYPT
     */
    // private static final String DB = "pgsql";
    private static final String DB = "sqlite";
    // private static final String DB = "sqlite-mem";

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

        log.debug("Setting up Test ..");

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

    /**
     * Test de multithread
     */
    @Test
    public void benchmarkThreadInsert() throws InterruptedException {

        final Stopwatch sw = Stopwatch.createStarted();

        // Numero de inserts
        final int tasks = 1000;

        // Numero de nucleos a utilizar
        final int cores = Runtime.getRuntime().availableProcessors();

        // Executor
        final ExecutorService executorService = Executors.newFixedThreadPool(cores);

        log.debug("Starting multithreading with {} tasks in {} cores.", tasks, cores);

        IntStream.range(0, tasks).forEach(i -> executorService.execute(new InsertTask(this.service)));

        log.debug("Waiting for termination ..");
        executorService.shutdown();

        log.debug("Init shutdown in {}.", sw);

        // Espera de 10 segundos para terminar
        while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            log.debug("Waiting 10 second for finish ..");
        }

        log.debug("Tasks ended in {}.", sw);

        long millis = sw.elapsed(TimeUnit.MICROSECONDS);
        double microseconds = millis / tasks;
        // log.info("MicroS: {}", microseconds);
        log.info("i/s: {}", 1000000 / microseconds);

    }

    /**
     *
     */
    @Slf4j
    private static class InsertTask implements Runnable {

        /**
         *
         */
        private Service service;

        /**
         *
         * @param service
         */
        public InsertTask(final Service service) {
            this.service = service;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {

            try {

                final Long id = RandomUtils.nextLong();

                log.debug("Inserting id: {}", id);

                final Requerimiento requerimiento = Requerimiento.builder()
                        .ordenProceso(id)
                        .numeroCotizacion(id)
                        .detalle("Multithread " + id)
                        .prioridad(Requerimiento.Prioridad.URGENTE)
                        .build();
                requerimiento.save();

                log.debug("Inserted: {}", requerimiento);

            } catch (Exception ex) {
                log.error("Error", ex);
                Assertions.fail("Fail", ex);
            }

        }
    }

}
