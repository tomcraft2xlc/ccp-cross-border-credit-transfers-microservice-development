package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.SqlStatement;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Rappresenta una sotto-tipologia di bonifico extra SEPA.<p>
 *
 * Questa classe definisce i dettagli di un sotto-tipo di bonifico,
 * inclusi il nome, la descrizione e le regole di elaborazione.<p>
 *
 * La tabella associata nel database è {@code sotto_tipologia_bonifico}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE sotto_tipologia_bonifico (
 *     id UUID PRIMARY KEY,
 *     nome VARCHAR(255) NOT NULL,
 *     descrizione TEXT,
 *     produci_mt999 BOOLEAN NOT NULL,
 *     campi_dto_obbligatori TEXT,
 *     banca_a_banca BOOLEAN NOT NULL,
 *     con_notifica BOOLEAN NOT NULL
 * );
 * }</pre>
 *
 * @param id                      Identificativo univoco del record.<p>
 * @param nome                    Nome del sotto-tipo di bonifico.<p>
 * @param descrizione             Descrizione del sotto-tipo di bonifico.<p>
 * @param produciMT999            Indica se deve essere generato un messaggio MT999.<p>Mappato in {@code produci_mt999}.</p>
 * @param campiDTOObbligatori     Campi obbligatori richiesti per la DTO.<p>Mappato in {@code campi_dto_obbligatori}.</p>
 *                                <p>Il formato di questo campo è una lista separata da virgole di campi annidati separati dal carattere {@code .}. Ad esempio {@code "foo.bar,foo.baz"} indica che è accettato un DTO nel formato</p>
 *                                <pre>
 *                                {@code
 *                                  {
 *                                      "foo": {
 *                                          "bar": 1,
 *                                          "baz": true
 *                                      }
 *                                  }
 *                                }
 *                                </pre>
 * @param bancaABanca             Indica se il bonifico è di tipo banca a banca.<p>Mappato in {@code banca_a_banca}.</p>
 */
@Table("sotto_tipologia_bonifico")
public record SottoTipologiaBonifico(
        /// Identificativo univoco del record.
        UUID id,

        /// Nome del sotto-tipo di bonifico.
        String nome,

        /// Descrizione del sotto-tipo di bonifico.
        String descrizione,

        /// Indica se deve essere generato un messaggio MT999.
        /// <p>Mappato in `produci_mt999`.</p>
        @Column("produci_mt999")
        Boolean produciMT999,

        /// Campi obbligatori richiesti per la DTO.
        /// <p>Mappato in `campi_dto_obbligatori`.</p>
        @Column("campi_dto_obbligatori")
        String campiDTOObbligatori,

        /// Indica se il bonifico è di tipo banca a banca.
        /// <p>Mappato in `banca_a_banca`.</p>
        @Column("banca_a_banca")
        Boolean bancaABanca,

        /// Indica se il bonifico causa la creazione di una notifica
        @Column("con_notifica")
        Boolean conNotifica
) {

    /**
     * Entità che fornisce l'interfaccia tra la classe e il repository,
     * permettendo l'accesso ai dati.
     */
    public static class Entity implements com.flowpay.ccp.persistence.Entity<SottoTipologiaBonifico, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<SottoTipologiaBonifico> entityClass() {
            return SottoTipologiaBonifico.class;
        }
    }

    public class WithLinkedEntitiesDeepened extends WithLinkedEntities {
//        public Collection<BonificoExtraSepaListaCanaliAbilitati.WithLinkedEntities> canaliAbilitati;
        public Collection<SottoTipologiaBonificoMappatura.WithLinkedEntities> mappatureMessaggi;

        WithLinkedEntitiesDeepened(WithLinkedEntities base) {
            this.bonificoExtraSepa = base.bonificoExtraSepa;
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.mappatureMessaggi == null) {
                this.mappatureMessaggi = new ArrayList<>();
                multis.add(
                        new SottoTipologiaBonificoMappatura.Entity().repository(sqlClient)
                                .getAllBySottoTipologiaBonifico(id())
                                .map(linked -> {
                                    var withLinked = linked.withLinkedEntities();
                                    withLinked.sottoTipologiaBonifico = this;
                                    this.mappatureMessaggi.add(withLinked);
                                    return withLinked;
                                })
                );
            } else {
                multis.add(
                        Multi.createFrom().iterable(this.mappatureMessaggi).onItem().castTo(EntityWithLinkedEntities.class)
                );
            }

            return Multi.createBy().merging().streams(multis);
        }
    }

    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {

        public BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa;

        public WithLinkedEntitiesDeepened deepened() {
            return new WithLinkedEntitiesDeepened(this);
        }

        @Override
        public SottoTipologiaBonifico getEntity() {
            return SottoTipologiaBonifico.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        /// Non dobbiamo mai creare questo tipo di entità
        public Uni<Void> insert(SqlClient sqlClient) {
            return Uni.createFrom().voidItem();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {

        }
    }
    /**
     * Repository per la gestione dei dati di {@code SottoTipologiaBonifico}.<p>
     * Fornisce metodi per l'interazione con il database utilizzando un client SQL.<p>
     */
    public static class Repository extends com.flowpay.ccp.persistence.Repository<SottoTipologiaBonifico> {

        public Repository(SqlClient client, Function<Row, SottoTipologiaBonifico> decoder) {
            super(client, decoder);
        }

        public Multi<SottoTipologiaBonifico> list(String channelID, WireTransferType tipoLogiaBonifico) {
            var statement = switch (tipoLogiaBonifico) {
                case BANCA, CLIENTE -> new SqlStatement("""
                SELECT wtk.*
                FROM sotto_tipologia_bonifico wtk, bonifico_extra_sepa_lista_canali_abilitati wtcal, canale cc
                WHERE wtk.id = wtcal.id_sottotipo_bonifico_extra_sepa AND wtcal.id_canale = cc.id
                AND cc.id_canale = $1 AND wtk.banca_a_banca = $2
                """, Tuple.of(channelID, tipoLogiaBonifico == WireTransferType.BANCA));
                case ENTRAMBI -> new SqlStatement("""
                SELECT wtk.*
                FROM sotto_tipologia_bonifico wtk, bonifico_extra_sepa_lista_canali_abilitati wtcal, canale cc
                WHERE wtk.id = wtcal.id_sottotipo_bonifico_extra_sepa AND wtcal.id_canale = cc.id
                AND cc.id_canale = $1
                """, Tuple.of(channelID));
            };
            return this.multi(statement);
        }

        public Uni<Optional<SottoTipologiaBonifico>> isAllowed(String channelID, String creditTransferKind, Boolean isUserPresentInBody) {
            return this.singleOrOptional("""
                SELECT wtk.*
                FROM sotto_tipologia_bonifico wtk, bonifico_extra_sepa_lista_canali_abilitati wtcal, canale cc
                WHERE wtk.id = wtcal.id_sottotipo_bonifico_extra_sepa AND wtcal.id_canale = cc.id
                AND cc.id_canale = $1
                AND wtk.nome = $2
                AND cc.utente_richiesto = $3
                LIMIT 1
            """, Tuple.of(channelID, creditTransferKind, isUserPresentInBody));
        }

        public Uni<SottoTipologiaBonifico> getByName(String name) {
            return this.single("""
                SELECT *
                FROM sotto_tipologia_bonifico
                WHERE nome = $1
            """, Tuple.of(name));
        }

        public Uni<SottoTipologiaBonifico> getByID(UUID id) {
            return this.single("""
                SELECT *
                FROM sotto_tipologia_bonifico
                WHERE id = $1
            """, Tuple.of(id));
        }
    }
}