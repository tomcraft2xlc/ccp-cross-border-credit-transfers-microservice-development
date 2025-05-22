package com.flowpay.ccp.credit.transfer.cross.border.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper
@DecoratedWith(OptionalMapper.Decorator.class)
public interface OptionalMapper {

    OptionalMapper INSTANCE = Mappers.getMapper(OptionalMapper.class);


    default <T> T unwrap(Optional<T> value) {
        return value.orElse(null);
    }

    abstract class Decorator implements OptionalMapper {

        private final OptionalMapper delegate;

        Decorator(OptionalMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public <T> T unwrap(Optional<T> value) {
            return Utils.allFieldsEmpty(delegate.unwrap(value));
        }
    }
}
