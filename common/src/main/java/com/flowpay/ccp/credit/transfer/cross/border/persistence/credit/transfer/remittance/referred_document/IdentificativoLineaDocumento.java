package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta l'identificativo di una linea di un documento di riferimento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id}.<p>
 *
 * Questa classe memorizza le informazioni necessarie per identificare una voce
 * specifica di una linea di documento, includendo codice, proprietario, emittente,
 * numero di identificazione e data di riferimento.<p>
 *
 * La tabella associata nel database è {@code identificativo_linea_documento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE identificativo_linea_documento (
 *     id UUID PRIMARY KEY,
 *     id_dettaglio_linea_documento_di_riferimento UUID NOT NULL,
 *     codice_voce VARCHAR(255),
 *     codice_proprietario_voce VARCHAR(255),
 *     emittente VARCHAR(255),
 *     numero VARCHAR(255),
 *     data DATE,
 *     CONSTRAINT fk_dettaglio_linea_documento_di_riferimento FOREIGN KEY (id_dettaglio_linea_documento_di_riferimento)
 *         REFERENCES dettaglio_linea_documento_di_riferimento(id)
 * );
 * }</pre>
 *
 * @param id                                  Identificativo univoco del record.<p>
 * @param idDettaglioLineaDocumentoDiRiferimento  Identificativo del dettaglio della linea di documento di riferimento.<p>
 * @param codiceVoce                          Codice della voce identificata.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/CdOrPrtry/Cd}.</p>
 * @param codiceProprietarioVoce              Codice proprietario della voce.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/CdOrPrtry/Prtry}.</p>
 * @param emittente                           Emittente della voce identificata.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/Issr}.</p>
 * @param numero                              Numero di identificazione della voce.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id/Nr}.</p>
 * @param data                                Data di riferimento per l'identificativo.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Id/Dt}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("identificativo_linea_documento")
public record IdentificativoLineaDocumento(
        UUID id,

        @Column("id_dettaglio_linea_documento_di_riferimento")
        UUID idDettaglioLineaDocumentoDiRiferimento,

        /// Codice della voce identificata.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/CdOrPrtry/Cd`.</p>
        @Column("codice_voce")
        String codiceVoce,

        /// Codice proprietario della voce.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/CdOrPrtry/Prtry`.</p>
        @Column("codice_proprietario_voce")
        String codiceProprietarioVoce,

        /// Emittente della voce identificata.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Id/Tp/Issr`.</p>
        String emittente,

        /// Numero di identificazione della voce.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Id/Nr`.</p>
        String numero,

        /// Data di riferimento per l'identificativo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/LineDtls/Id/Dt`.</p>
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate data
) {

    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<IdentificativoLineaDocumento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<IdentificativoLineaDocumento> entityClass() {
            return IdentificativoLineaDocumento.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<IdentificativoLineaDocumento> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, IdentificativoLineaDocumento> decoder) {
            super(client, decoder);
        }

        public Multi<IdentificativoLineaDocumento> getAllByDettaglioLineaDocumentoDiRiferimento(UUID idDettaglioLineaDocumentoDiRiferimento) {
            return multi("SELECT * FROM identificativo_linea_documento WHERE id_dettaglio_linea_documento_di_riferimento = $1", Tuple.of(idDettaglioLineaDocumentoDiRiferimento));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }
 
    public class WithLinkedEntities
            implements EntityWithLinkedEntities {
        public DettaglioLineaDocumentoDiRiferimento.WithLinkedEntities dettaglioLineaDocumentoDiRiferimento;

        @Override
        public IdentificativoLineaDocumento getEntity() {
            return IdentificativoLineaDocumento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettaglioLineaDocumentoDiRiferimento);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();


            if (this.dettaglioLineaDocumentoDiRiferimento == null) {
                multis.add(new DettaglioLineaDocumentoDiRiferimento.Entity()
                        .repository(sqlClient).getById(idDettaglioLineaDocumentoDiRiferimento())
                        .map(linked -> {
                            this.dettaglioLineaDocumentoDiRiferimento = linked.withLinkedEntities();
                            return this.dettaglioLineaDocumentoDiRiferimento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioLineaDocumentoDiRiferimento));
            }

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        public Uni<Void> insert(SqlClient sqlClient) {
            var entity = new Entity();
            return entity.repository(sqlClient).run(entity.insert(getEntity()));
        }
    }
}