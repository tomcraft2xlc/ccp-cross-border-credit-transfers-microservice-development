package com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.configuration;

import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Table("configurazioni_autorizzative")
public record ConfigurazioniAutorizzative(
        UUID id,
        @Column("id_sotto_tipologia_bonifico")
        UUID idSottoTipologiaBonifico,
        @Column("id_canale")
        UUID idCanale,
//        @Column("soglia_importo")
//        BigDecimal sogliaImporto,
        @Column("livelli_di_autorizzazione")
        Long livelliDiAutorizzazione,
        @Column("livello_di_autorizzazione_di_default")
        Long livelloDiAutorizzazioneDiDefault
) {

        public static final class Entity implements com.flowpay.ccp.persistence.Entity<ConfigurazioniAutorizzative, Repository> {

                @Override
                public Repository repository(SqlClient sqlClient) {
                        return new Repository(sqlClient, this::from);
                }

                @Override
                public Class<ConfigurazioniAutorizzative> entityClass() {
                        return ConfigurazioniAutorizzative.class;
                }
        }

        public static final class Repository extends com.flowpay.ccp.persistence.Repository<ConfigurazioniAutorizzative> {

                public Repository(SqlClient client, Function<Row, ConfigurazioniAutorizzative> decoder) {
                        super(client, decoder);
                }

                public Uni<Optional<ConfigurazioniAutorizzative>> search(UUID idSottotipologiaBonifico, UUID idCanale) {
                        return singleOrOptional("""
                        SELECT * FROM configurazioni_autorizzative
                        WHERE id_sotto_tipologia_bonifico = $1 AND id_canale = $2
                        """, Tuple.of(idSottotipologiaBonifico, idCanale));
                }
        }
}
