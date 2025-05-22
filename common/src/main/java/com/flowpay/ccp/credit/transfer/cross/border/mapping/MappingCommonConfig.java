package com.flowpay.ccp.credit.transfer.cross.border.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Qualifier;

/**
 * Central configuration for all the mappings
 */
@MapperConfig(
    // Configure the mappers for injection with cdi
    componentModel = ComponentModel.CDI,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    // Default to error if a target properties is not filled
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    // Warn if a type is converted to a smaller one
    typeConversionPolicy = ReportingPolicy.WARN
)
public interface MappingCommonConfig {

    /**
     * Mark a method that extracts the bare entity
     */
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface DtoToBareEntity {
    }
    
    /**
     * Mark a method that extracts the entity and all linked
     */
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface DtoToEntityWithLinkedEntitiesMainDocument {
    }

    /**
     * Mark a method that extracts the entity and all linked for a linked document (e.g. pacs9 COV)
     */
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface DtoToEntityWithLinkedEntitiesLinkedDocument {
    }
}
