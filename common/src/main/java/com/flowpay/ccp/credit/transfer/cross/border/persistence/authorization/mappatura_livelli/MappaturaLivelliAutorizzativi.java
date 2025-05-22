package com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization.mappatura_livelli;


import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Table("mappatura_livelli_autorizzativi")
public record MappaturaLivelliAutorizzativi(
        UUID id,
        String ruolo,
        Long livello
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<MappaturaLivelliAutorizzativi, Repository> {

       @Override
       public Repository repository(SqlClient sqlClient) {
           return new Repository(sqlClient, this::from);
       }

       @Override
       public Class<MappaturaLivelliAutorizzativi> entityClass() {
           return MappaturaLivelliAutorizzativi.class;
       }
   }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<MappaturaLivelliAutorizzativi> {

       public Repository(SqlClient client, Function<Row, MappaturaLivelliAutorizzativi> decoder) {
           super(client, decoder);
       }

       public Uni<List<MappaturaLivelliAutorizzativi>> getLevelByRuolo(String ruolo) {
           return multi("""
           SELECT * FROM mappatura_livelli_autorizzativi
           WHERE ruolo = $1
           """, Tuple.of(ruolo)).collect().asList();
       }
   }
}
