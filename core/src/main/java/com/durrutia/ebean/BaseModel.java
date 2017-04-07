package com.durrutia.ebean;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheBeanTuning;
import io.ebean.annotation.CacheQueryTuning;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Diego Urrutia Astorga <durrutia@ucn.cl>
 * @version 20170407154500
 */
@Cache(enableQueryCache = true, enableBeanCache = true, naturalKey = "id", readOnly = true)
@CacheQueryTuning(maxSecsToLive = 30)
@CacheBeanTuning(maxSecsToLive = 30)
// @History
@MappedSuperclass
@Slf4j
public class BaseModel extends Model {

    /**
     * Serializador Gson.
     */
    private static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new BaseModelExclusionStrategy())
            .setPrettyPrinting()
            .create();

    /**
     * Identificador
     */
    @Getter
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Version
     */
    @Getter
    @Version
    private Long version;

    /**
     * Softdeletes :D
     */
    @Getter
    @SoftDelete
    private boolean deleted;

    /**
     * Cuando fue creado
     */
    @Getter
    @WhenCreated
    private LocalDateTime whenCreated;

    /**
     * Cuando fue modificado
     */
    @Getter
    @WhenModified
    private LocalDateTime whenModified;

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        // FIXME: Deep serialization
        return GSON.toJson(this);

        // return JsonStream.serialize(this);

    }

    /**
     * Exclusion
     */
    @Slf4j
    private static final class BaseModelExclusionStrategy implements ExclusionStrategy {

        /**
         * @param f the field object that is under test
         * @return true if the field should be ignored; otherwise false
         */
        @Override
        public boolean shouldSkipField(FieldAttributes f) {

            final String name = f.getName();

            return name.startsWith("log")
                    || name.startsWith("_")
                    || name.startsWith("when")
                    || name.startsWith("deleted")
                    || name.startsWith("version")
                    || f.getAnnotation(Exclude.class) != null;

        }

        /**
         * @param clazz the class object that is under test
         * @return true if the class should be ignored; otherwise false
         */
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

}
