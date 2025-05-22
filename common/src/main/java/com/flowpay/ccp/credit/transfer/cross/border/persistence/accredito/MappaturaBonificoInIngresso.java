package com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito;

import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Entity;
import com.flowpay.ccp.persistence.Repository;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.UUID;
import java.util.function.Function;

@Table("mappatura_bonifico_in_ingresso")
public record MappaturaBonificoInIngresso(
        UUID id,

        String namespace,

        @Column("classe_qualificata_nome_completo")
        String classeQualificataNomeCompleto
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<MappaturaBonificoInIngresso, Repository> {

        @Override
        public Repository repository(SqlClient client) {
            return new Repository(client, this::from);
        }

        @Override
        public Class<MappaturaBonificoInIngresso> entityClass() {
            return MappaturaBonificoInIngresso.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<MappaturaBonificoInIngresso> {

        public Repository(SqlClient client, Function<Row, MappaturaBonificoInIngresso> decoder) {
            super(client, decoder);
        }

        public Uni<MappaturaBonificoInIngresso> getByNamespace(String namespace) {
            return single("""
                    SELECT *
                    FROM mappatura_bonifico_in_ingresso
                    WHERE
                        namespace = $1
                    """, Tuple.of(namespace));
        }

        public Uni<MappaturaBonificoInIngresso> getByID(UUID id) {
            return single("""
            SELECT *
            FROM mappatura_bonifico_in_ingresso
            WHERE
                id = $1
            """, Tuple.of(id));
        }
    }
}
