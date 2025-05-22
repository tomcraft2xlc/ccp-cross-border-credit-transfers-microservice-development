package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.BonificoDaAutorizzare;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.BonificoInUscita;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.ParametriRicercaAutorizzazioneBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.ParametriRicercaBonificiInUscita;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapportoBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.DettaglioBonificoAccountToAccount;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.bank_to_bank.DettaglioBonificoBancaABanca;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.CommissioneAccountToAccount;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.CommissioneBanca;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.InformazioniAggiuntivePagamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RegulatoryReporting;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RiferimentiAggiuntiviPagamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.InformazioniSistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.history.StoriaStatiBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonifico;
import com.flowpay.ccp.pagination.persistence.Page;
import com.flowpay.ccp.persistence.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Rappresenta un bonifico extra-SEPA nel sistema di pagamento.
 * <p>
 * Questa classe mappa la tabella {@code bonifico_extra_sepa} nel database e
 * contiene
 * informazioni dettagliate sulla transazione, inclusi identificativi, dati di
 * regolamento,
 * dettagli della banca di copertura e stato della transazione.
 * <p>
 * La classe segue il modello di dati dei bonifici transfrontalieri e supporta
 * il
 * tracciamento delle date di creazione, esecuzione e regolamento secondo le
 * normative ISO 20022.
 * <p>
 * SQL:
 *
 * <pre>{@code
 * CREATE TABLE bonifico_extra_sepa (
 *     id UUID PRIMARY KEY,
 *     tid VARCHAR(255) NOT NULL,
 *     id_canale UUID NOT NULL,
 *     id_sotto_tipologia_bonifico UUID NOT NULL,
 *     sistema_di_regolamento sistema_di_regolamento NOT NULL,
 *     codice_filiale VARCHAR(255),
 *     denominazione_filiale VARCHAR(255),
 *     utente VARCHAR(255),
 *     iban_conto_banca_di_copertura VARCHAR(34),
 *     divisa_conto_banca_di_copertura VARCHAR(3),
 *     bic_banca_di_copertura VARCHAR(11),
 *     intestazione_banca_di_copertura VARCHAR(255),
 *     data_di_creazione DATE NOT NULL,
 *     data_di_esecuzione DATE NOT NULL,
 *     data_valuta_ordinante DATE NOT NULL,
 *     data_regolamento_banca_beneficiario DATE NOT NULL,
 *     stato credit_transfer_status NOT NULL,
 *     in_gestione BOOLEAN NOT NULL,
 *     istante_creazione_messaggio TIMESTAMP WITH TIME ZONE,
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL
 * );
 * }</pre>
 *
 * @param id                               Identificativo univoco del bonifico
 * @param tid                              Identificativo della transazione
 * @param idCanale                         Identificativo del canale di
 *                                         pagamento
 * @param idSottoTipologiaBonifico         Identificativo della sotto-tipologia
 *                                         del bonifico
 * @param sistemaDiRegolamento             Sistema di regolamento utilizzato per
 *                                         la transazione
 * @param codiceFiliale                    Codice della filiale di esecuzione
 * @param denominazioneFiliale             Nome della filiale di esecuzione
 * @param utente                           Nome dell'utente che ha avviato la
 *                                         transazione
 * @param ibanContoBancaDiCopertura        IBAN del conto della banca di
 *                                         copertura
 * @param divisaContoBancaDiCopertura      Divisa del conto della banca di
 *                                         copertura (ISO 4217)
 * @param bicBancaDiCopertura              BIC della banca di copertura
 * @param intestazioneBancaDiCopertura     Nome della banca di copertura
 * @param dataDiCreazione                  Data di creazione del bonifico
 * @param dataDiEsecuzione                 Data di esecuzione della transazione
 * @param dataValutaOrdinante              Data valuta per l'ordinante
 * @param dataRegolamentoBancaBeneficiario Data di regolamento presso la banca
 *                                         del beneficiario,
 *                                         corrispondente al campo ISO 20022 XML
 *                                         {@code CdtTrfTxInf/IntrBkSttlmDt}.
 * @param stato                            Stato del bonifico extra-SEPA
 * @param inGestione                       Indica se il bonifico è in gestione
 * @param istanteCreazioneMessaggio        Istante in cui è stato creato il
 *                                         messagio XML
 * @param createdAt                        Timestamp di creazione della
 *                                         registrazione
 */
@Table("bonifico_extra_sepa")
public record BonificoExtraSepa(

        /// Identificativo univoco del bonifico
        UUID id,

        /// Identificativo della transazione
        String tid,

        @Column("tid_documento_collegato")
        String tidDocumentoCollegato,

        @Column("numero_transazione")
        Long numeroTransazione,

        /// Identificativo del canale di pagamento
        @Column("id_canale")
        UUID idCanale,

        /// Identificativo della sotto-tipologia del bonifico
        @Column("id_sotto_tipologia_bonifico")
        UUID idSottoTipologiaBonifico,

        /// Sistema di regolamento utilizzato per la transazione
        @Column("sistema_di_regolamento")
        SistemaDiRegolamento sistemaDiRegolamento,

        /// Codice della filiale di esecuzione
        @Column("codice_filiale")
        Long codiceFiliale,

        /// Nome della filiale di esecuzione
        // TODO: Remove this field
        @Column("denominazione_filiale")
        String denominazioneFiliale,

        /// Nome dell'utente che ha avviato la transazione
        String utente,

        /// IBAN del conto della banca di copertura
        @Column("iban_conto_banca_di_copertura")
        String ibanContoBancaDiCopertura,

        /// Divisa del conto della banca di copertura (ISO 4217)
        @Column("divisa_conto_banca_di_copertura")
        String divisaContoBancaDiCopertura,

        /// BIC della banca di copertura
        @Column("bic_banca_di_copertura")
        String bicBancaDiCopertura,

        /// Nome della banca di copertura
        @Column("intestazione_banca_di_copertura")
        String intestazioneBancaDiCopertura,

        /// Data di creazione del bonifico
        @DateKind(DateKind.DateKindEnum.DATE)
        @Column("data_di_creazione")
        LocalDate dataDiCreazione,

        /// Data di esecuzione della transazione
        @DateKind(DateKind.DateKindEnum.DATE)
        @Column("data_di_esecuzione")
        LocalDate dataDiEsecuzione,

        /// Data valuta per l'ordinante
        @DateKind(DateKind.DateKindEnum.DATE)
        @Column("data_valuta_ordinante")
        LocalDate dataValutaOrdinante,

        /// Data di regolamento presso la banca del beneficiario (ISO 20022 XML: {@code
        /// CdtTrfTxInf/IntrBkSttlmDt})
        @DateKind(DateKind.DateKindEnum.DATE)
        @Column("data_regolamento_banca_beneficiario")
        LocalDate dataRegolamentoBancaBeneficiario,

        /// Stato del bonifico extra-SEPA
        @EnumKind CreditTransferStatus stato,

        /// Indica se il bonifico è in gestione
        @Column("in_gestione")
        Boolean inGestione,

        @Column("istante_creazione_messaggio")
        @DateKind OffsetDateTime istanteCreazioneMessaggio,

        /// Timestamp di creazione della registrazione
        @CreationTimeStamp @Column("created_at")
        @DateKind Instant createdAt) {

    /**
     * Implementazione dell'entità associata al bonifico extra-SEPA.
     * <p>
     * Questa classe fornisce il repository per l'accesso ai dati relativi ai
     * bonifici extra-SEPA.
     */
    public static final class Entity implements com.flowpay.ccp.persistence.Entity<BonificoExtraSepa, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<BonificoExtraSepa> entityClass() {
            return BonificoExtraSepa.class;
        }
    }

    /**
     * Repository per la gestione dei bonifici extra-SEPA nel database.
     * <p>
     * Fornisce i metodi per eseguire operazioni di persistenza sui bonifici
     * extra-SEPA
     * utilizzando il client SQL asincrono di Vert.x Mutiny.
     */
    public static final class Repository extends com.flowpay.ccp.persistence.Repository<BonificoExtraSepa> {

        private static final Logger LOG = Logger.getLogger(Repository.class);

        public Repository(SqlClient client,
                          Function<Row, BonificoExtraSepa> decoder) {
            super(client, decoder);
        }

        public Uni<BonificoExtraSepa> getById(UUID idBonificoExtraSepa) {
            return single("SELECT * FROM bonifico_extra_sepa WHERE id = $1", Tuple.of(idBonificoExtraSepa));
        }

        public Uni<Void> updateStatus(UUID idBonificoExtraSepa, CreditTransferStatus newStatus) {
            return run("""
                    UPDATE bonifico_extra_sepa
                    SET stato = $1
                    WHERE id = $2
                    """, Tuple.of(newStatus, idBonificoExtraSepa));
        }

        private Uni<Void> doUpdateStatus(CreditTransferStatus status, UUID idBonificoExtraSepa) {
            return run("""
                    UPDATE bonifico_extra_sepa
                    SET stato = $1, in_gestione = false
                    WHERE id = $2
                    """, Tuple.of(status, idBonificoExtraSepa));
        }

        public Uni<Void> updateStatusAndUnlock(BonificoExtraSepa bonifico, CreditTransferStatus newStatus) {
            return updateStatusAndUnlock(bonifico, newStatus, null);
        }

        public Uni<Void> updateStatusAndUnlock(BonificoExtraSepa bonifico, CreditTransferStatus newStatus,
                                               String note) {
            Uni<Void> result;
            if (bonifico.stato() != newStatus) {
                result = addNewStatusHistory(bonifico, newStatus.name(), note);
            } else {
                result = Uni.createFrom().voidItem();
            }

            return result
                    .call(() -> doUpdateStatus(newStatus, bonifico.id()));
        }

        public Uni<BonificoExtraSepa> getByIdAndLock(UUID idBonificoExtraSepa) {
            return single("""
                    UPDATE bonifico_extra_sepa
                    SET in_gestione = true
                    WHERE id = $1 AND in_gestione = false
                    RETURNING *
                    """, Tuple.of(idBonificoExtraSepa));
        }

        public Uni<BonificoExtraSepa> getByIdLocked(UUID idBonificoExtraSepa) {
            return single("""
                    SELECT * FROM bonifico_extra_sepa
                    WHERE id = $1 AND in_gestione = true
                    """, Tuple.of(idBonificoExtraSepa));
        }

        /**
         * Recupera il bonifico extra-SEPA con l'identificativo specificato, a
         * condizione che sia
         * già marcato come "in gestione", e aggiorna il campo
         * `istante_creazione_messaggio`
         * al timestamp corrente.
         * <p>
         * Questo metodo è utilizzato durante la generazione del messaggio XML per
         * registrare il momento esatto in cui il messaggio viene creato per la
         * trasmissione.
         * Inoltre, assicura che l'entità sia effettivamente in gestione, evitando
         * conflitti
         * concorrenziali tra processi che potrebbero tentare di accedere
         * contemporaneamente
         * alla stessa entità.
         *
         * @param idBonificoExtraSepa L'identificativo univoco del bonifico da
         *                            recuperare.
         * @return Una {@link Uni} contenente l'entità {@link BonificoExtraSepa}
         * aggiornata, se trovata.
         */
        public Uni<BonificoExtraSepa> getByIdLockedForSending(UUID idBonificoExtraSepa) {
            return single("""
                    UPDATE bonifico_extra_sepa
                    SET istante_creazione_messaggio = CURRENT_TIMESTAMP
                    WHERE id = $1 and in_gestione = true
                    RETURNING *
                    """, Tuple.of(idBonificoExtraSepa));
        }

        public Uni<Void> unlock(UUID idBonificoExtraSepa) {
            return run("""
                    UPDATE bonifico_extra_sepa
                    SET in_gestione = false
                    WHERE id = $1 AND in_gestione = true
                    """, Tuple.of(idBonificoExtraSepa));
        }

        public Uni<Void> addNewStatusHistory(BonificoExtraSepa bonifico, String newStatus, String note) {
            var entity = new StoriaStatiBonificoExtraSepa.Entity();
            var repository = entity.repository(this.client);
            var statusHistory = new StoriaStatiBonificoExtraSepa(
                    UUID.randomUUID(),
                    bonifico.id(),
                    bonifico.stato().toString(),
                    newStatus,
                    note,
                    null);

            return repository.run(entity.insert(statusHistory));
        }

        public Uni<Void> setNumeroTransazione(UUID idBonificoExtraSepa, Long numeroTransazione) {

            return run("""
            UPDATE bonifico_extra_sepa
            SET
                numero_transazione = $1
            WHERE
                id = $2
            """, Tuple.of(numeroTransazione, idBonificoExtraSepa));
        }

        public Uni<Page<BonificoDaAutorizzare>> searchBonificiDaAutorizzare(ParametriRicercaAutorizzazioneBonifico dtoParametri, int page, int pageSize) {
            ArrayList<Object> params = new ArrayList<>();
            var selectClause = """
                    
                    WITH max_auth AS (
                         SELECT
                             aut.id_bonifico_extra_sepa as id_bonifico_extra_sepa, max(aut.livello_autorizzazione) as max_livello_autorizzazione
                             FROM autorizzazione aut
                             LEFT JOIN bonifico_extra_sepa bon ON aut.id_bonifico_extra_sepa = bon.id
                             group by aut.id_bonifico_extra_sepa
                    )
                    
                    SELECT
                        bes.id as id,
                        bes.tid as tid,
                        st.descrizione as sotto_tipologia_bonifico,
                        bes.sistema_di_regolamento as sistema_di_regolamento,
                        COALESCE(daa.divisa, dbb.divisa) as divisa,
                        COALESCE(daa.importo, dbb.importo) as importo,
                        COALESCE(ia_ordinante.intestazione, i_ordinante.intestazione) as intestazione_ordinante,
                        i_ordinante.bic as bic_ordinante,
                        COALESCE(ia_ordinante_ir.intestazione_conto, i_ordinante_ir.intestazione_conto) as intestazione_conto_ordinante,
                        COALESCE(i_banca_beneficiario.intestazione, ia_beneficiario.intestazione) as intestazione_beneficiario,
                        i_banca_beneficiario.bic as bic_beneficiario,
                        i_banca_del_beneficiario.bic as bic_banca_del_beneficiario,
                        COALESCE(ia_beneficiario_ir.intestazione_conto, i_banca_beneficiario_ir.intestazione_conto) as intestazione_conto_beneficiario,
                        bes.data_regolamento_banca_beneficiario as data_regolamento_banca,
                        bes.codice_filiale as codice_filiale,
                        bes.stato as stato,
                        coalesce(a.max_livello_autorizzazione, 0) as max_livello_autorizzazione
                    """;
            StringBuilder fromClause = new StringBuilder();
            fromClause.append("""
                    FROM
                        bonifico_extra_sepa bes
                    
                        -- sotto tipologia bonifico
                        LEFT JOIN sotto_tipologia_bonifico st ON st.id = bes.id_sotto_tipologia_bonifico
                    
                        -- dettaglio bonifico
                        LEFT JOIN dettaglio_bonifico_banca_a_banca dbb
                            ON
                                dbb.id_bonifico_extra_sepa = bes.id AND
                                st.banca_a_banca = true
                        LEFT JOIN dettaglio_bonifico_account_to_account daa
                            ON
                                daa.id_bonifico_extra_sepa = bes.id AND
                                st.banca_a_banca = false
                    
                        -- dati ordinante banca
                        LEFT JOIN informazioni_intermediario i_ordinante
                            ON
                                i_ordinante.id_bonifico_extra_sepa = bes.id AND
                                i_ordinante.tipo_intermediario = 'ORDINANTE' AND
                                st.banca_a_banca = true
                        LEFT JOIN informazioni_rapporto i_ordinante_ir
                            ON
                                i_ordinante.id_info_rapporto = i_ordinante_ir.id
                    
                        -- dati ordinante attore
                            LEFT JOIN informazioni_attore ia_ordinante
                                ON
                                    ia_ordinante.id_bonifico_extra_sepa = bes.id AND
                                    ia_ordinante.tipo_attore = 'ORDINANTE' and
                                    st.banca_a_banca = false
                            LEFT JOIN informazioni_rapporto ia_ordinante_ir
                                ON
                                    ia_ordinante.id_info_rapporto = ia_ordinante_ir.id
                    
                        -- dati beneficiario banca
                        LEFT JOIN informazioni_intermediario i_banca_beneficiario
                            ON
                                i_banca_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                i_banca_beneficiario.tipo_intermediario = 'BANCA_BENEFICIARIA' AND
                                st.banca_a_banca = true
                        LEFT JOIN informazioni_rapporto i_banca_beneficiario_ir
                            ON
                                i_banca_beneficiario.id_info_rapporto = i_banca_beneficiario_ir.id
                    
                        -- dati beneficiario attore
                        LEFT JOIN informazioni_attore ia_beneficiario
                            ON
                                ia_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                ia_beneficiario.tipo_attore = 'BENEFICIARIO' AND
                                st.banca_a_banca = false
                        LEFT JOIN informazioni_rapporto ia_beneficiario_ir
                            ON
                                ia_beneficiario.id_info_rapporto = ia_beneficiario_ir.id
                    
                        -- dati banca del beneficiario
                        LEFT JOIN informazioni_intermediario i_banca_del_beneficiario
                            ON
                                i_banca_del_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                i_banca_del_beneficiario.tipo_intermediario = 'BANCA_DEL_BENEFICIARIO'
                    
                        -- dati banca destinataria
                        LEFT JOIN informazioni_intermediario i_banca_destinataria
                            ON
                            i_banca_destinataria.id_bonifico_extra_sepa = bes.id AND
                            i_banca_destinataria.tipo_intermediario = 'BANCA_DESTINATARIA'
                    """);
            String joinWithAutClause = """
                    -- join per il massimo livello autorizzativo
                       LEFT JOIN max_auth a on
                            bes.id = a.id_bonifico_extra_sepa
                    """;
            StringBuilder conditionsString = new StringBuilder();
            conditionsString.append("WHERE bes.stato = 'DA_AUTORIZZARE'");
            String paginationString = " LIMIT " + pageSize + " OFFSET " + (page * pageSize);
            int paramIndex = 0;
            if (dtoParametri.ndg != null) {
                paramIndex++;
                conditionsString.append("""
                                AND EXISTS (
                                    SELECT *
                                    FROM informazioni_ndg i_ndg
                                    WHERE
                                    i_ndg.id_dettaglio_bonifico = daa.id
                                    AND i_ndg.ndg = $""").append(paramIndex)
                        .append(")");
                params.add(String.format("%7s", dtoParametri.ndg).replace(' ', '0'));
            }
            if (dtoParametri.tid != null) {
                paramIndex++;
                conditionsString.append(" AND bes.tid = $").append(paramIndex);
                params.add(dtoParametri.tid);
            }
            if (dtoParametri.uetr != null) {
                paramIndex++;
                conditionsString.append(" AND bes.id = $").append(paramIndex);
                params.add(dtoParametri.uetr);
            }
            if (dtoParametri.bicOrdinante != null) {
                paramIndex++;
                if (dtoParametri.bicOrdinante.length() == 8) {
                    conditionsString.append(" AND i_ordinante.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicOrdinante + "%");
                } else if (dtoParametri.bicOrdinante.length() == 11) {
                    conditionsString.append(" AND i_ordinante.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicOrdinante);
                }
            }
            if (dtoParametri.rapportoOrdinante != null) {
                paramIndex++;
                conditionsString.append("AND (i_ordinante_ir.numero = $").append(paramIndex)
                        .append(" OR ia_ordinante_ir.numero = $").append(paramIndex).append(")");
                params.add(dtoParametri.rapportoOrdinante);
            }
            if (dtoParametri.dataRegolamentoBancaBeneficiarioDa != null) {
                paramIndex++;
                conditionsString.append(" AND bes.data_regolamento_banca_beneficiario >= $").append(paramIndex);
                params.add(dtoParametri.dataRegolamentoBancaBeneficiarioDa);
            }
            if (dtoParametri.dataRegolamentoBancaBeneficiarioA != null) {
                paramIndex++;
                conditionsString.append(" AND bes.data_regolamento_banca_beneficiario <= $").append(paramIndex);
                params.add(dtoParametri.dataRegolamentoBancaBeneficiarioA);
            }
            if (dtoParametri.idCanale != null) {
                paramIndex++;
                conditionsString.append("""
                                AND EXISTS (
                                    SELECT *
                                    FROM canale
                                    WHERE
                                        bes.id_canale = canale.id AND
                                        canale.id_canale = $""").append(paramIndex)
                        .append(")");
                params.add(dtoParametri.idCanale);
            }
            if (dtoParametri.idSottoTipologia != null) {
                paramIndex++;
//                dato che filtro su nome Es: client_extra_sepa_credit_transfer magari è giusto cambiare chiave dell'attributo al dto
                conditionsString.append(" AND st.nome = $").append(paramIndex);
                params.add(dtoParametri.idSottoTipologia);
            }
            if (dtoParametri.sistemaDiRegolamento != null) {
                paramIndex++;
                conditionsString.append(" AND bes.sistema_di_regolamento = $").append(paramIndex);
                params.add(dtoParametri.sistemaDiRegolamento);
            }
            if (dtoParametri.divisa != null) {
                // Prima si controlla se il bonifico è banca a banca
                paramIndex++;
                conditionsString.append("AND (dbb.divisa = $")
                        .append(paramIndex)
                        .append(" OR daa.divisa = $").append(paramIndex)
                        .append(")");
                params.add(dtoParametri.divisa);
            }
            if (dtoParametri.ordinante != null) {
                paramIndex++;
                conditionsString.append(" AND (");
                conditionsString.append("      ia_ordinante.intestazione LIKE $").append(paramIndex);
                conditionsString.append("      OR");
                conditionsString.append("      i_ordinante.intestazione LIKE $").append(paramIndex);
                conditionsString.append("      )");
                params.add(dtoParametri.ordinante + "%");
            }
            if (dtoParametri.importoDa != null) {
                paramIndex++;
                conditionsString.append("""
                        AND (
                        (st.banca_a_banca IS TRUE AND dbb.importo >= $""").append(paramIndex).append(")").append("""
                           OR
                        (st.banca_a_banca IS FALSE AND daa.importo >= $""").append(paramIndex).append(")").append(")");
                params.add(dtoParametri.importoDa);
            }
            if (dtoParametri.importoA != null) {
                paramIndex++;
                conditionsString.append("""
                        AND (
                        (st.banca_a_banca IS TRUE AND dbb.importo <= $""").append(paramIndex).append(")").append("""
                           OR
                        (st.banca_a_banca IS FALSE AND daa.importo <= $""").append(paramIndex).append(")").append(")");
                params.add(dtoParametri.importoA);
            }
            if (dtoParametri.bicBancaBeneficiario != null && (dtoParametri.bicBancaBeneficiario.length() == 11 || dtoParametri.bicBancaBeneficiario.length() == 8)) {
                paramIndex++;
                if (dtoParametri.bicBancaBeneficiario.length() == 11) {
                    conditionsString.append("  AND i_banca_del_beneficiario.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicBancaBeneficiario);
                } else if (dtoParametri.bicBancaBeneficiario.length() == 8) {
                    conditionsString.append("  AND i_banca_del_beneficiario.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicBancaBeneficiario + "%");
                }
            }
            if (dtoParametri.bicBancaDestinataria != null && (dtoParametri.bicBancaDestinataria.length() == 11 || dtoParametri.bicBancaDestinataria.length() == 8)) {
                paramIndex++;
                if (dtoParametri.bicBancaDestinataria.length() == 11) {
                    conditionsString.append("  AND i_banca_destinataria.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDestinataria);
                } else if (dtoParametri.bicBancaDestinataria.length() == 8) {
                    conditionsString.append("  AND i_banca_destinataria.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDestinataria + "%");
                }
            }
            if (dtoParametri.codiceFiliale != null) {
                paramIndex++;
                conditionsString.append(" AND bes.codice_filiale = $").append(paramIndex);
                params.add(dtoParametri.codiceFiliale);
            }
            String queryTotalEments = "SELECT count(*) as total_elements " + fromClause + conditionsString;
            LOG.infof("Query dei total elements: %s", queryTotalEments);
            return this.client.preparedQuery(queryTotalEments).execute(Tuple.from(params)).flatMap(countRowSet -> {
                // Trovo il totale dei record ai fini della paginazione.
                if (!countRowSet.iterator().hasNext()) {
                    throw new NoSuchElementException();
                }
                int totalElements = countRowSet.iterator().next().getInteger("total_elements");
                LOG.infof("Numero totale di elementi trovati: %n", totalElements);
                ArrayList<BonificoDaAutorizzare> dataList = new ArrayList<>();
                StringBuilder searchQuery = new StringBuilder();
                searchQuery.append(selectClause);
                searchQuery.append(fromClause);
                searchQuery.append(joinWithAutClause);
                searchQuery.append(conditionsString);
                searchQuery.append(paginationString);
                LOG.infof("Query di ricerca: %s", searchQuery);
                return this.client.preparedQuery(searchQuery.toString()).execute(Tuple.from(params)).map(rowSet -> {

                    for (Row row : rowSet) {
                        LOG.debug("decodifico riga");
                        LOG.debug(row);
                        dataList.add(new BonificoDaAutorizzare(
                                row.getUUID("id"),
                                row.getString("tid"),
                                row.get(SistemaDiRegolamento.class, "sistema_di_regolamento"),
                                row.getString("sotto_tipologia_bonifico"),
                                row.getString("divisa"),
                                row.getBigDecimal("importo"),
                                row.getString("intestazione_ordinante"),
                                row.getString("bic_ordinante"),
                                row.getString("intestazione_conto_ordinante"),
                                row.getString("intestazione_beneficiario"),
                                row.getString("bic_beneficiario"),
                                row.getString("intestazione_conto_beneficiario"),
                                row.getString("bic_banca_del_beneficiario"),
                                row.getLocalDate("data_regolamento_banca"),
                                row.getLong("codice_filiale"),
                                row.get(CreditTransferStatus.class, "stato"),
                                row.getLong("max_livello_autorizzazione")
                        ));
                    }
                    return new Page<>(totalElements, dataList);
                });
            });
        }

        public Uni<Page<BonificoInUscita>> searchBonificiInUscita(ParametriRicercaBonificiInUscita dtoParametri, int page, int pageSize) {
            String selectClause = """
                    SELECT
                        bes.id as id,
                        bes.tid as tid,
                        st.descrizione as sotto_tipologia_bonifico,
                        bes.sistema_di_regolamento as sistema_di_regolamento,
                        COALESCE(daa.divisa, dbb.divisa) as divisa,
                        COALESCE(daa.importo, dbb.importo) as importo,
                        COALESCE(ia_ordinante.intestazione, i_ordinante.intestazione) as intestazione_ordinante,
                        i_ordinante.bic as bic_ordinante,
                        COALESCE(ia_ordinante_ir.intestazione_conto, i_ordinante_ir.intestazione_conto) as intestazione_conto_ordinante,
                        COALESCE(i_banca_beneficiario.intestazione, ia_beneficiario.intestazione) as intestazione_beneficiario,
                        i_banca_beneficiario.bic as bic_beneficiario,
                        i_banca_del_beneficiario.bic as bic_banca_del_beneficiario,
                        COALESCE(ia_beneficiario_ir.intestazione_conto, i_banca_beneficiario_ir.intestazione_conto) as intestazione_conto_beneficiario,
                        bes.data_regolamento_banca_beneficiario as data_regolamento_banca,
                        bes.codice_filiale as codice_filiale,
                        bes.stato as stato
                    """;
            String fromClause = """
                    FROM
                        bonifico_extra_sepa bes
                    
                        -- sotto tipologia bonifico
                        LEFT JOIN sotto_tipologia_bonifico st ON st.id = bes.id_sotto_tipologia_bonifico
                    
                        -- dettaglio bonifico
                        LEFT JOIN dettaglio_bonifico_banca_a_banca dbb
                            ON
                                dbb.id_bonifico_extra_sepa = bes.id AND
                                st.banca_a_banca = true
                        LEFT JOIN dettaglio_bonifico_account_to_account daa
                            ON
                                daa.id_bonifico_extra_sepa = bes.id AND
                                st.banca_a_banca = false
                    
                        -- dati ordinante banca
                        LEFT JOIN informazioni_intermediario i_ordinante
                            ON
                                i_ordinante.id_bonifico_extra_sepa = bes.id AND
                                i_ordinante.tipo_intermediario = 'ORDINANTE' AND
                                st.banca_a_banca = true
                        LEFT JOIN informazioni_rapporto i_ordinante_ir
                            ON
                                i_ordinante.id_info_rapporto = i_ordinante_ir.id
                    
                        -- dati ordinante attore
                            LEFT JOIN informazioni_attore ia_ordinante
                                ON
                                    ia_ordinante.id_bonifico_extra_sepa = bes.id AND
                                    ia_ordinante.tipo_attore = 'ORDINANTE' and
                                    st.banca_a_banca = false
                            LEFT JOIN informazioni_rapporto ia_ordinante_ir
                                ON
                                    ia_ordinante.id_info_rapporto = ia_ordinante_ir.id
                    
                        -- dati beneficiario banca
                        LEFT JOIN informazioni_intermediario i_banca_beneficiario
                            ON
                                i_banca_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                i_banca_beneficiario.tipo_intermediario = 'BANCA_BENEFICIARIA' AND
                                st.banca_a_banca = true
                        LEFT JOIN informazioni_rapporto i_banca_beneficiario_ir
                            ON
                                i_banca_beneficiario.id_info_rapporto = i_banca_beneficiario_ir.id
                    
                        -- dati beneficiario attore
                        LEFT JOIN informazioni_attore ia_beneficiario
                            ON
                                ia_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                ia_beneficiario.tipo_attore = 'BENEFICIARIO' AND
                                st.banca_a_banca = false
                        LEFT JOIN informazioni_rapporto ia_beneficiario_ir
                            ON
                                ia_beneficiario.id_info_rapporto = ia_beneficiario_ir.id
                    
                        -- dati banca del beneficiario
                        LEFT JOIN informazioni_intermediario i_banca_del_beneficiario
                            ON
                                i_banca_del_beneficiario.id_bonifico_extra_sepa = bes.id AND
                                i_banca_del_beneficiario.tipo_intermediario = 'BANCA_DEL_BENEFICIARIO'
                    
                        -- dati banca destinataria
                        LEFT JOIN informazioni_intermediario i_banca_destinataria
                            ON
                            i_banca_destinataria.id_bonifico_extra_sepa = bes.id AND
                            i_banca_destinataria.tipo_intermediario = 'BANCA_DESTINATARIA'
                    """;
            ArrayList<Object> params = new ArrayList<>();
            StringBuilder conditionsString = new StringBuilder();
            conditionsString.append("WHERE bes.stato NOT IN (" +
                                    "'TO_BE_MANAGED', 'INSERITO', 'DA_CONFERMARE', 'CONFERMATO', 'ELIMINATO', 'WCL_NON_PASSATA', 'WCL_NEGATA', 'IN_ERRORE'" +
                                    ")");
            String orderString = " ORDER BY bes.data_regolamento_banca_beneficiario DESC ";
            String paginationString = " LIMIT " + pageSize + " OFFSET " + (page * pageSize);
            int paramIndex = 0;
            if (dtoParametri.ndg != null) {
                paramIndex++;
                conditionsString.append("""
                                AND EXISTS (
                                    SELECT *
                                    FROM informazioni_ndg i_ndg
                                    WHERE
                                    i_ndg.id_dettaglio_bonifico = daa.id
                                    AND i_ndg.ndg = $""").append(paramIndex)
                        .append(")");
                params.add(String.format("%7s", dtoParametri.ndg).replace(' ', '0'));
            }
            if (dtoParametri.tid != null) {
                paramIndex++;
                conditionsString.append(" AND bes.tid = $").append(paramIndex);
                params.add(dtoParametri.tid);
            }
            if (dtoParametri.uetr != null) {
                paramIndex++;
                conditionsString.append(" AND bes.id = $").append(paramIndex);
                params.add(dtoParametri.uetr);
            }
            if (dtoParametri.bicOrdinante != null) {
                paramIndex++;
                if (dtoParametri.bicOrdinante.length() == 8) {
                    conditionsString.append(" AND i_ordinante.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicOrdinante + "%");
                } else if (dtoParametri.bicOrdinante.length() == 11) {
                    conditionsString.append(" AND i_ordinante.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicOrdinante);
                }
            }
            if (dtoParametri.rapportoOrdinante != null) {
                paramIndex++;
                conditionsString.append("AND (i_ordinante_ir.numero = $").append(paramIndex)
                        .append(" OR ia_ordinante_ir.numero = $").append(paramIndex).append(")");
                params.add(dtoParametri.rapportoOrdinante);
            }
            if (dtoParametri.dataInvioDa != null) {
                paramIndex++;
                conditionsString.append(" AND bes.istante_creazione_messaggio >= $").append(paramIndex);
                params.add(OffsetDateTime.of(LocalDateTime.of(LocalDate.parse(dtoParametri.dataInvioDa), LocalTime.MIDNIGHT), ZoneOffset.UTC));
            }
            if (dtoParametri.dataInvioA != null) {
                paramIndex++;
                conditionsString.append(" AND bes.istante_creazione_messaggio <= $").append(paramIndex);
                params.add(OffsetDateTime.of(LocalDateTime.of(LocalDate.parse(dtoParametri.dataInvioA), LocalTime.MAX), ZoneOffset.UTC));
            }
            if (dtoParametri.idCanale != null) {
                paramIndex++;
                conditionsString.append("""
                                AND EXISTS (
                                    SELECT *
                                    FROM canale
                                    WHERE
                                        bes.id_canale = canale.id AND
                                        canale.id_canale = $""").append(paramIndex)
                        .append(")");
                params.add(dtoParametri.idCanale);
            }
            if (dtoParametri.nomeSottoTipologia != null) {
                paramIndex++;
                conditionsString.append(" AND st.nome = $").append(paramIndex);
                params.add(dtoParametri.nomeSottoTipologia);
            }
            if (dtoParametri.sistemaDiRegolamento != null) {
                paramIndex++;
                conditionsString.append(" AND bes.sistema_di_regolamento = $").append(paramIndex);
                params.add(dtoParametri.sistemaDiRegolamento);
            }
            if (dtoParametri.divisa != null) {
                // Prima si controlla se il bonifico è banca a banca
                paramIndex++;
                conditionsString.append("AND (dbb.divisa = $")
                        .append(paramIndex)
                        .append(" OR daa.divisa = $").append(paramIndex)
                        .append(")");
                params.add(dtoParametri.divisa);
            }
            if (dtoParametri.importoDa != null) {
                paramIndex++;
                conditionsString.append("""
                        AND (
                        (st.banca_a_banca IS TRUE AND dbb.importo >= $""").append(paramIndex).append(")").append("""
                           OR
                        (st.banca_a_banca IS FALSE AND daa.importo >= $""").append(paramIndex).append(")").append(")");
                params.add(dtoParametri.importoDa);
            }
            if (dtoParametri.importoA != null) {
                paramIndex++;
                conditionsString.append("""
                        AND (
                        (st.banca_a_banca IS TRUE AND dbb.importo <= $""").append(paramIndex).append(")").append("""
                           OR
                        (st.banca_a_banca IS FALSE AND daa.importo <= $""").append(paramIndex).append(")").append(")");
                params.add(dtoParametri.importoA);
            }
            if (dtoParametri.ordinante != null) {
                paramIndex++;
                conditionsString.append(" AND (");
                conditionsString.append("      ia_ordinante.intestazione LIKE $").append(paramIndex);
                conditionsString.append("      OR");
                conditionsString.append("      i_ordinante.intestazione LIKE $").append(paramIndex);
                conditionsString.append("      )");
                params.add(dtoParametri.ordinante + "%");
            }
            if (dtoParametri.bicBancaDelBeneficiario != null && (dtoParametri.bicBancaDelBeneficiario.length() == 11 || dtoParametri.bicBancaDelBeneficiario.length() == 8)) {
                paramIndex++;
                if (dtoParametri.bicBancaDelBeneficiario.length() == 11) {
                    conditionsString.append("  AND i_banca_del_beneficiario.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDelBeneficiario);
                } else {
                    conditionsString.append("  AND i_banca_del_beneficiario.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDelBeneficiario + "%");
                }
            }
            if (dtoParametri.bicBancaDestinataria != null && (dtoParametri.bicBancaDestinataria.length() == 11 || dtoParametri.bicBancaDestinataria.length() == 8)) {
                paramIndex++;
                if (dtoParametri.bicBancaDestinataria.length() == 11) {
                    conditionsString.append("  AND i_banca_destinataria.bic = $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDestinataria);
                } else {
                    conditionsString.append("  AND i_banca_destinataria.bic LIKE $").append(paramIndex);
                    params.add(dtoParametri.bicBancaDestinataria + "%");
                }
            }
            if (dtoParametri.dataRegolamentoBancaBeneficiarioDa != null) {
                paramIndex++;
                conditionsString.append(" AND bes.data_regolamento_banca_beneficiario >= $").append(paramIndex);
                params.add(dtoParametri.dataRegolamentoBancaBeneficiarioDa);
            }
            if (dtoParametri.dataRegolamentoBancaBeneficiarioA != null) {
                paramIndex++;
                conditionsString.append(" AND bes.data_regolamento_banca_beneficiario <= $").append(paramIndex);
                params.add(dtoParametri.dataRegolamentoBancaBeneficiarioA);
            }
            if (dtoParametri.codiceFiliale != null) {
                paramIndex++;
                conditionsString.append(" AND bes.codice_filiale = $").append(paramIndex);
                params.add(dtoParametri.codiceFiliale);
            }
            if (dtoParametri.stato != null) {
                paramIndex++;
                conditionsString.append(" AND bes.stato = $").append(paramIndex);
                params.add(dtoParametri.stato);
            }
            String queryTotalEments = "SELECT count(*) as total_elements " + fromClause + conditionsString;
            LOG.debugf("Query dei total elements: %s", queryTotalEments);
            return this.client.preparedQuery(queryTotalEments).execute(Tuple.from(params)).flatMap(countRowSet -> {
                // Trovo il totale dei record ai fini della paginazione.
                if (!countRowSet.iterator().hasNext()) {
                    throw new NoSuchElementException();
                }
                int totalElements = countRowSet.iterator().next().getInteger("total_elements");
                LOG.debugf("Numero totale di elementi trovati: %n", totalElements);
                ArrayList<BonificoInUscita> dataList = new ArrayList<>();
                StringBuilder searchQuery = new StringBuilder();
                searchQuery.append(selectClause);
                searchQuery.append(fromClause);
                searchQuery.append(conditionsString);
                searchQuery.append(orderString);
                searchQuery.append(paginationString);
                LOG.debugf("Query di ricerca: %s", searchQuery);
                return this.client.preparedQuery(searchQuery.toString()).execute(Tuple.from(params)).map(rowSet -> {
                    for (Row row : rowSet) {
                        LOG.debug("decodifico riga");
                        LOG.debug(row);
                        dataList.add(new BonificoInUscita(
                                row.getUUID("id"),
                                row.getString("tid"),
                                row.get(SistemaDiRegolamento.class, "sistema_di_regolamento"),
                                row.getString("sotto_tipologia_bonifico"),
                                row.getString("divisa"),
                                row.getBigDecimal("importo"),
                                row.getString("intestazione_ordinante"),
                                row.getString("bic_ordinante"),
                                row.getString("intestazione_conto_ordinante"),
                                row.getString("intestazione_beneficiario"),
                                row.getString("bic_beneficiario"),
                                row.getString("intestazione_conto_beneficiario"),
                                row.getString("bic_banca_del_beneficiario"),
                                row.getLocalDate("data_regolamento_banca"),
                                row.getLong("codice_filiale"),
                                row.get(CreditTransferStatus.class, "stato")
                        ));
                    }
                    return new Page<>(totalElements, dataList);
                });
            });
        }

        public Uni<Void> updateDataDiRegolamentoAndUnlock(UUID idBonificoExtraSepa, LocalDate newDate) {
            return run("""
                    UPDATE bonifico_extra_sepa
                    SET data_regolamento_banca_beneficiario = $1, in_gestione = false
                    WHERE id = $2 AND in_gestione = true
                    """, Tuple.of(newDate, idBonificoExtraSepa));
        }

    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    /**
     * Bonifico extra-SEPA con tutte le entità collegate.
     */
    public class WithLinkedEntities
            implements EntityWithLinkedEntities {

        /// Dati tecnici legati al bonifico
        public SottoTipologiaBonifico.WithLinkedEntities sottoTipologiaBonifico;

        /// Dati di business legati al bonifico
        public Collection<InformazioniRapportoBonificoExtraSepa.WithLinkedEntities> informazioniRapportiBonificoExtraSepa;
        public InformazioniSistemaDiRegolamento.WithLinkedEntities informazioniSistemaDiRegolamento;
        public InformazioniSistemaDiRegolamento.WithLinkedEntities informazioniSistemaDiRegolamentoDocumentoCollegato;
        public DettaglioBonificoBancaABanca.WithLinkedEntities dettaglioBonificoBancaABanca;
        public CommissioneBanca.WithLinkedEntities commissioneBanca;
        public DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount;
        public Collection<CommissioneAccountToAccount.WithLinkedEntities> commissioniAccountToAccount;
        public Collection<InformazioniIntermediario.WithLinkedEntities> informazioniIntermediari;
        public Collection<InformazioniAttore.WithLinkedEntities> informazioniAttori;
        public Collection<RegulatoryReporting.WithLinkedEntities> regulatoryReportings;
        public InformazioniAggiuntivePagamento.WithLinkedEntities informazioniAggiuntivePagamento;
        public InformazioniAggiuntivePagamento.WithLinkedEntities informazioniAggiuntivePagamentoDocumentoCollegato;
        public Collection<RiferimentiAggiuntiviPagamento.WithLinkedEntities> riferimentiAggiuntiviPagamento;
        public Collection<InformazioniCausale.WithLinkedEntities> informazioniCausale;
        public Collection<InformazioniCausale.WithLinkedEntities> informazioniCausaleDocumentoCollegato;

        public DatiVerificaBonifico.WithLinkedEntities datiVerificaBonifico;
        public DatiConfermaBonifico.WithLinkedEntities datiConfermaBonifico;

        @Override
        public BonificoExtraSepa getEntity() {
            return BonificoExtraSepa.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        private Multi<EntityWithLinkedEntities> loadTechnicalData(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.sottoTipologiaBonifico == null) {
                multis.add(
                        new SottoTipologiaBonifico.Entity().repository(sqlClient)
                                .getByID(getEntity().idSottoTipologiaBonifico())
                                .map(linked -> {
                                    this.sottoTipologiaBonifico = linked.withLinkedEntities();
                                    this.sottoTipologiaBonifico.bonificoExtraSepa = this;
                                    return this.sottoTipologiaBonifico;
                                })
                                .onItem().castTo(EntityWithLinkedEntities.class)
                                .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.sottoTipologiaBonifico));
            }

            return Multi.createBy().merging().streams(multis);
        }

        private Multi<EntityWithLinkedEntities> loadFunctionalData(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniRapportiBonificoExtraSepa == null) {
                this.informazioniRapportiBonificoExtraSepa = new ArrayList<>();
                multis.add(new InformazioniRapportoBonificoExtraSepa.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.informazioniRapportiBonificoExtraSepa.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniRapportiBonificoExtraSepa).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.informazioniSistemaDiRegolamento == null) {
                multis.add(new InformazioniSistemaDiRegolamento.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id(), false)
                        .map(linked -> {
                            this.informazioniSistemaDiRegolamento = linked.withLinkedEntities();
                            this.informazioniSistemaDiRegolamento.bonificoExtraSepa = this;
                            return this.informazioniSistemaDiRegolamento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniSistemaDiRegolamento));
            }
            if (this.informazioniSistemaDiRegolamentoDocumentoCollegato == null) {
                multis.add(new InformazioniSistemaDiRegolamento.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id(), true)
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.informazioniSistemaDiRegolamentoDocumentoCollegato = linked.withLinkedEntities();
                            this.informazioniSistemaDiRegolamentoDocumentoCollegato.bonificoExtraSepa = this;
                            return this.informazioniSistemaDiRegolamentoDocumentoCollegato;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniSistemaDiRegolamentoDocumentoCollegato));
            }
            if (this.dettaglioBonificoBancaABanca == null) {
                multis.add(new DettaglioBonificoBancaABanca.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.dettaglioBonificoBancaABanca = linked.withLinkedEntities();
                            this.dettaglioBonificoBancaABanca.bonificoExtraSepa = this;
                            return this.dettaglioBonificoBancaABanca;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioBonificoBancaABanca));
            }
            if (this.commissioneBanca == null) {
                multis.add(new CommissioneBanca.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.commissioneBanca = linked.withLinkedEntities();
                            this.commissioneBanca.bonificoExtraSepa = this;
                            return this.commissioneBanca;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.commissioneBanca));
            }
            if (this.dettaglioBonificoAccountToAccount == null) {
                multis.add(new DettaglioBonificoAccountToAccount.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.dettaglioBonificoAccountToAccount = linked.withLinkedEntities();
                            this.dettaglioBonificoAccountToAccount.bonificoExtraSepa = this;
                            return this.dettaglioBonificoAccountToAccount;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioBonificoAccountToAccount));
            }
            if (this.commissioniAccountToAccount == null) {
                this.commissioniAccountToAccount = new ArrayList<>();
                multis.add(new CommissioneAccountToAccount.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.commissioniAccountToAccount.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.commissioniAccountToAccount).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.informazioniIntermediari == null) {
                this.informazioniIntermediari = new ArrayList<>();
                multis.add(new InformazioniIntermediario.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.informazioniIntermediari.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniIntermediari).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.informazioniAttori == null) {
                this.informazioniAttori = new ArrayList<>();
                multis.add(new InformazioniAttore.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.informazioniAttori.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniAttori).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.regulatoryReportings == null) {
                this.regulatoryReportings = new ArrayList<>();
                multis.add(new RegulatoryReporting.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.regulatoryReportings.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.regulatoryReportings).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.informazioniAggiuntivePagamento == null) {
                multis.add(new InformazioniAggiuntivePagamento.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id(), false)
                        .map(linked -> {
                            this.informazioniAggiuntivePagamento = linked.withLinkedEntities();
                            this.informazioniAggiuntivePagamento.bonificoExtraSepa = this;
                            return this.informazioniAggiuntivePagamento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniAggiuntivePagamento));
            }
            if (this.informazioniAggiuntivePagamentoDocumentoCollegato == null) {
                multis.add(new InformazioniAggiuntivePagamento.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id(), true)
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.informazioniAggiuntivePagamentoDocumentoCollegato = linked.withLinkedEntities();
                            this.informazioniAggiuntivePagamentoDocumentoCollegato.bonificoExtraSepa = this;
                            return this.informazioniAggiuntivePagamentoDocumentoCollegato;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniAggiuntivePagamentoDocumentoCollegato));
            }
            if (this.riferimentiAggiuntiviPagamento == null) {
                this.riferimentiAggiuntiviPagamento = new ArrayList<>();
                multis.add(new RiferimentiAggiuntiviPagamento.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.riferimentiAggiuntiviPagamento.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.riferimentiAggiuntiviPagamento).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.informazioniCausale == null) {
                this.informazioniCausale = new ArrayList<>();
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id(), false)
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.informazioniCausale.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniCausale).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }

            if (this.informazioniCausaleDocumentoCollegato == null) {
                this.informazioniCausaleDocumentoCollegato = new ArrayList<>();
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getAllByBonificoExtraSepa(id(), true)
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.bonificoExtraSepa = this;
                            this.informazioniCausaleDocumentoCollegato.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniCausaleDocumentoCollegato).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }

            if (this.datiVerificaBonifico == null) {
                multis.add(new DatiVerificaBonifico.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id())
                        .map(linked -> {
                            if (linked != null) {
                                this.datiVerificaBonifico = linked.withLinkedEntities();
                                this.datiVerificaBonifico.bonificoExtraSepa = this;
                            }
                            return this.datiVerificaBonifico;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.datiVerificaBonifico));
            }
            if (this.datiConfermaBonifico == null) {
                multis.add(new DatiConfermaBonifico.Entity()
                        .repository(sqlClient).getByBonificoExtraSepa(id())
                        .map(linked -> {
                            if (linked != null) {
                                this.datiConfermaBonifico = linked.withLinkedEntities();
                                this.datiConfermaBonifico.bonificoExtraSepa = this;
                            }
                            return this.datiConfermaBonifico;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.datiConfermaBonifico));
            }

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            var technical = this.loadTechnicalData(sqlClient);
            var functional = this.loadFunctionalData(sqlClient);

            return Multi.createBy().merging().streams(technical, functional);
        }

        @Override
        public Uni<Void> insert(SqlClient sqlClient) {
            var entity = new Entity();
            return entity.repository(sqlClient).run(entity.insert(getEntity()));
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {

            if (informazioniRapportiBonificoExtraSepa != null)
                informazioniRapportiBonificoExtraSepa.forEach(consumer);

            consumer.accept(informazioniSistemaDiRegolamento);
            consumer.accept(informazioniAggiuntivePagamentoDocumentoCollegato);
            consumer.accept(dettaglioBonificoBancaABanca);
            consumer.accept(commissioneBanca);
            consumer.accept(dettaglioBonificoAccountToAccount);
            if (commissioniAccountToAccount != null)
                commissioniAccountToAccount.forEach(consumer);
            if (informazioniIntermediari != null)
                informazioniIntermediari.forEach(consumer);
            if (informazioniAttori != null)
                informazioniAttori.forEach(consumer);
            if (regulatoryReportings != null)
                regulatoryReportings.forEach(consumer);
            consumer.accept(informazioniAggiuntivePagamento);
            consumer.accept(informazioniAggiuntivePagamentoDocumentoCollegato);
            if (riferimentiAggiuntiviPagamento != null)
                riferimentiAggiuntiviPagamento.forEach(consumer);
            if (informazioniCausale != null)
                informazioniCausale.forEach(consumer);
            if (informazioniCausaleDocumentoCollegato != null) {
                informazioniCausaleDocumentoCollegato.forEach(consumer);
            }


            consumer.accept(datiVerificaBonifico);
            consumer.accept(datiConfermaBonifico);
        }

        public DettaglioBonificoCommon dettaglioBonifico() {
            if (Boolean.TRUE.equals(sottoTipologiaBonifico.getEntity().bancaABanca())) {
                return dettaglioBonificoBancaABanca.getEntity();
            } else {
                return dettaglioBonificoAccountToAccount.getEntity();
            }
        }
    }

}
