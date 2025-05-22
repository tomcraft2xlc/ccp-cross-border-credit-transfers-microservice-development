package com.flowpay.ccp.credit.transfer.cross.border.persistence.config;

import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * This table contains the list of destination banks for which the creditor bank is not mandatory.
 *
 * @param id The primary key of the table
 * @param bic The BIC of the destination bank
 */
@Table("bic_banca_destinataria")
public record BICBancaDestinataria(
        UUID id,
        String bic
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<BICBancaDestinataria, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<BICBancaDestinataria> entityClass() {
            return BICBancaDestinataria.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<BICBancaDestinataria> {

        public Repository(SqlClient client, Function<Row, BICBancaDestinataria> decoder) {
            super(client, decoder);
        }

        public Uni<Boolean> exists(String bic) {
            return singleOrOptional("""
            SELECT *
            FROM bic_banca_destinataria
            WHERE bic = $1
            """, Tuple.of(bic))
            .map(Optional::isPresent);
        }
    }
}
