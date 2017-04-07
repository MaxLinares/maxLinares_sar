package com.durrutia.ebean;

import com.google.common.base.Stopwatch;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.EncryptKey;
import io.ebean.config.EncryptKeyManager;
import io.ebean.config.ServerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407165500
 */
@Slf4j
public final class EbeanServerBuilder {

    /**
     * Configuracion del server.
     */
    private ServerConfig serverConfig;

    /**
     * Builder!
     */
    public EbeanServerBuilder(final String database) {

        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName(database);
        serverConfig.setDefaultServer(true);
        serverConfig.loadFromProperties();
        serverConfig.setRegister(true);

        // http://ebean-orm.github.io/docs/query/autotune
        serverConfig.getAutoTuneConfig().setProfiling(false);
        serverConfig.getAutoTuneConfig().setQueryTuning(false);

        // Generador de llave de escriptacion
        serverConfig.setEncryptKeyManager(new EncryptKeyManager() {

            @Override
            public void initialise() {
                log.debug("Initializing EncryptKey ..");
            }

            @Override
            public EncryptKey getEncryptKey(final String tableName, final String columnName) {

                log.debug("gettingEncryptKey for {} in {}.", columnName, tableName);

                // Return the encrypt key
                return () -> tableName + columnName;
            }
        });

        this.serverConfig = serverConfig;

    }

    /**
     * Agrega una clase a la configuracion.
     *
     * @param clazz a agregar.
     * @return the {@link EbeanServerBuilder}.
     */
    public EbeanServerBuilder addClass(final Class clazz) {

        this.serverConfig.addClass(clazz);

        return this;
    }

    /**
     *
     * @return the {@link EbeanServer}.
     */
    public EbeanServer build() {

        final Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Initializing EbeanServer ..");

        final EbeanServer ebeanServer = EbeanServerFactory.create(this.serverConfig);

        log.info("EBeanServer initialized in {}.", stopwatch);

        return ebeanServer;

    }


}
