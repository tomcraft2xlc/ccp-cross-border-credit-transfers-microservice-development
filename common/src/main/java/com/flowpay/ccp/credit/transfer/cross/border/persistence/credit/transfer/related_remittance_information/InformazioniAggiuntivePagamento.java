package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.PrioritaTransazione;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni aggiuntive relative a un pagamento,<p>
 * secondo lo schema ISO 20022.<p>
 *
 * Questa classe memorizza dettagli come istruzioni per le banche riceventi e beneficiarie,
 * priorità della transazione e codici di classificazione del pagamento.<p>
 *
 * La tabella associata nel database è {@code informazioni_aggiuntive_pagamento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE informazioni_aggiuntive_pagamento (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     istruzioni_banca_ricevente_1 VARCHAR(255),
 *     istruzioni_banca_ricevente_2 VARCHAR(255),
 *     istruzioni_banca_ricevente_3 VARCHAR(255),
 *     istruzioni_banca_ricevente_4 VARCHAR(255),
 *     istruzioni_banca_ricevente_5 VARCHAR(255),
 *     istruzioni_banca_ricevente_6 VARCHAR(255),
 *     codice_istruzione_banca_del_beneficiario_1 VARCHAR(255),
 *     istruzione_banca_del_beneficiario_1 VARCHAR(255),
 *     codice_istruzione_banca_del_beneficiario_2 VARCHAR(255),
 *     istruzione_banca_del_beneficiario_2 VARCHAR(255),
 *     priorita_transazione VARCHAR(255),
 *     codice_livello_di_servizio VARCHAR(255),
 *     dettaglio_livello_di_servizio VARCHAR(255),
 *     classificazione_pagamento VARCHAR(255),
 *     dettaglio_classificazione_pagamento VARCHAR(255),
 *     codice_transazione VARCHAR(255),
 *     dettaglio_identificativo_transazione VARCHAR(255),
 *     codice_tipo_servizio VARCHAR(255),
 *     dettaglio_tipo_servizio VARCHAR(255),
 *     valore_cambio_istruito NUMERIC(18,6),
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                                  Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa                 Identificativo del bonifico extra SEPA associato.<p>
 * @param istruzioniBancaRicevente1           Prima istruzione per la banca ricevente.<p>Mappato in {@code CdtTrfTxInf/InstrForNxtAgt/InstrInf}.</p>
 * @param istruzioniBancaRicevente2           Seconda istruzione per la banca ricevente.<p>
 * @param istruzioniBancaRicevente3           Terza istruzione per la banca ricevente.<p>
 * @param istruzioniBancaRicevente4           Quarta istruzione per la banca ricevente.<p>
 * @param istruzioniBancaRicevente5           Quinta istruzione per la banca ricevente.<p>
 * @param istruzioniBancaRicevente6           Sesta istruzione per la banca ricevente.<p>
 * @param codiceIstruzioneBancaDelBeneficiario1 Codice della prima istruzione per la banca beneficiaria.<p>Mappato in {@code CdtTrfTxInf/InstrForCdtrAgt/Cd}.</p>
 * @param istruzioneBancaDelBeneficiario1     Prima istruzione per la banca beneficiaria.<p>Mappato in {@code CdtTrfTxInf/InstrForCdtrAgt/InstrInf}.</p>
 * @param codiceIstruzioneBancaDelBeneficiario2 Codice della seconda istruzione per la banca beneficiaria.<p>
 * @param istruzioneBancaDelBeneficiario2     Seconda istruzione per la banca beneficiaria.<p>
 * @param prioritaTransazione                 Priorità della transazione.<p>Mappato in {@code AppHdr/Prty e GrpHdr/PmtTpInf/InstrPrty}.</p>
 * @param codiceLivelloDiServizio             Codice del livello di servizio.<p>Mappato in {@code GrpHdr/PmtTpInf/SvcLvl/Cd}.</p>
 * @param dettaglioLivelloDiServizio          Dettaglio del livello di servizio.<p>Mappato in {@code GrpHdr/PmtTpInf/SvcLvl/Prtry}.</p>
 * @param classificazionePagamento            Classificazione del pagamento.<p>Mappato in {@code CdtTrfTxInf/Purp/Cd}.</p>
 * @param dettaglioClassificazionePagamento   Dettaglio della classificazione del pagamento.<p>Mappato in {@code CdtTrfTxInf/Purp/Prtry}.</p>
 * @param codiceTransazione                   Codice della transazione.<p>Mappato in {@code GrpHdr/PmtTpInf/CtgyPurp/Cd}.</p>
 * @param dettaglioIdentificativoTransazione  Dettaglio dell'identificativo della transazione.<p>Mappato in {@code GrpHdr/PmtTpInf/CtgyPurp/Prtry}.</p>
 * @param codiceTipoServizio                  Codice del tipo di servizio.<p>Mappato in {@code GrpHdr/PmtTpInf/LclInstrm/Cd}.</p>
 * @param dettaglioTipoServizio               Dettaglio del tipo di servizio.<p>Mappato in {@code GrpHdr/PmtTpInf/LclInstrm/Prtry}.</p>
 * @param valoreCambioIstruito                Valore del cambio istruito.<p>Mappato in {@code Document/FIToFICstmrCdtTrf/CdtTrfTxInf/XchgRate}.</p>
 *
 * @see BonificoExtraSepa
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_aggiuntive_pagamento")
public record InformazioniAggiuntivePagamento(
        UUID id,

        @Column("informazioni_documento_collegato")
        Boolean informazioniDocumentoCollegato,

        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        @Column("istruzioni_banca_ricevente_1")
        String istruzioniBancaRicevente1,

        @Column("istruzioni_banca_ricevente_2")
        String istruzioniBancaRicevente2,

        @Column("istruzioni_banca_ricevente_3")
        String istruzioniBancaRicevente3,

        @Column("istruzioni_banca_ricevente_4")
        String istruzioniBancaRicevente4,

        @Column("istruzioni_banca_ricevente_5")
        String istruzioniBancaRicevente5,

        @Column("istruzioni_banca_ricevente_6")
        String istruzioniBancaRicevente6,

        @Column("codice_istruzione_banca_del_beneficiario_1")
        String codiceIstruzioneBancaDelBeneficiario1,

        @Column("istruzione_banca_del_beneficiario_1")
        String istruzioneBancaDelBeneficiario1,

        @Column("codice_istruzione_banca_del_beneficiario_2")
        String codiceIstruzioneBancaDelBeneficiario2,

        @Column("istruzione_banca_del_beneficiario_2")
        String istruzioneBancaDelBeneficiario2,

        @Column("priorita_transazione")
        PrioritaTransazione prioritaTransazione,

        @Column("codice_livello_di_servizio")
        String codiceLivelloDiServizio,

        @Column("dettaglio_livello_di_servizio")
        String dettaglioLivelloDiServizio,

        @Column("classificazione_pagamento")
        String classificazionePagamento,

        @Column("dettaglio_classificazione_pagamento")
        String dettaglioClassificazionePagamento,

        @Column("codice_transazione")
        String codiceTransazione,

        @Column("dettaglio_identificativo_transazione")
        String dettaglioIdentificativoTransazione,

        @Column("codice_tipo_servizio")
        String codiceTipoServizio,

        @Column("dettaglio_tipo_servizio")
        String dettaglioTipoServizio,

        @Column("valore_cambio_istruito")
        BigDecimal valoreCambioIstruito
) {

    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<InformazioniAggiuntivePagamento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniAggiuntivePagamento> entityClass() {
            return InformazioniAggiuntivePagamento.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<InformazioniAggiuntivePagamento> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniAggiuntivePagamento> decoder) {
            super(client, decoder);
        }

        public Uni<InformazioniAggiuntivePagamento> getByBonificoExtraSepa(UUID idBonificoExtraSepa, Boolean documentoCollegato) {
            return singleOrNull("""
            SELECT * FROM informazioni_aggiuntive_pagamento
            WHERE id_bonifico_extra_sepa = $1 AND informazioni_documento_collegato = $2
            """, Tuple.of(idBonificoExtraSepa, documentoCollegato));
        }

    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa;

        @Override
        public InformazioniAggiuntivePagamento getEntity() {
            return InformazioniAggiuntivePagamento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            this.bonificoExtraSepa.informazioniAggiuntivePagamento = this;
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
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