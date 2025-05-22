package com.flowpay.ccp.credit.transfer.cross.border.persistence.authorization;

import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.*;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Table("autorizzazione")
public record Autorizzazione(
        UUID id,
        @Column("id_bonifico_extra_sepa") 
        UUID idBonificoExtraSepa,

        String utente,

        @Column("nome_utente") 
        String nomeUtente,
        String ruolo,
        String profilo,

        @EnumKind AutorizzazioneActionEnum azione,

        @Column("livello_autorizzazione") 
        Long livelloAutorizzazione,

        @Column("data_di_regolamento_precedente") @DateKind(DateKind.DateKindEnum.DATE) 
        LocalDate dataDiRegolamentoPrecedente,

        @Column("autorizzazione_messaggio") 
        Boolean autorizzazioneMessaggio,

        @Column("autorizzazione_notifica")
        Boolean autorizzazioneNotifica,

        String note,

        @CreationTimeStamp 
        @DateKind 
        @Column("created_at") 
        Instant createdAt
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<Autorizzazione, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<Autorizzazione> entityClass() {
            return Autorizzazione.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<Autorizzazione> {

        public Repository(SqlClient client, Function<Row, Autorizzazione> decoder) {
            super(client, decoder);
        }

        public Uni<Autorizzazione> getById(UUID id) {
            return single("SELECT * FROM autorizzazione WHERE id = $1", Tuple.of(id));
        }

        public Uni<Optional<Autorizzazione>> getLast(UUID idBonificoExtraSepa, AutorizzazioneActionEnum azione) {
            return singleOrOptional("""
                    SELECT * FROM autorizzazione
                    WHERE id_bonifico_extra_sepa = $1 AND azione = $2
                    ORDER BY created_at DESC
                    LIMIT 1
                    """, Tuple.of(idBonificoExtraSepa, azione));
        }

        public Uni<Optional<Autorizzazione>> findNotAuthorizingNotification(UUID idBonificoExtraSepa) {
            return singleOrOptional("""
                SELECT * FROM autorizzazione
                WHERE
                    id_bonifico_extra_sepa = $1
                    AND azione = 'AUTORIZZAZIONE'
                    AND autorizzazione_notifica IS FALSE
                ORDER BY created_at ASC
                LIMIT 1
                """, Tuple.of(idBonificoExtraSepa));
        }


        public Multi<Autorizzazione> getAllByBonifico(UUID idBonificoExtraSepa) {
            return multi("""
                SELECT * FROM autorizzazione
                WHERE
                    id_bonifico_extra_sepa = $1
            """, Tuple.of(idBonificoExtraSepa));
        }
        
    }

    public static AutorizzazioneBuilder buildAutorizzazione() {
        return new AutorizzazioneBuilder(AutorizzazioneActionEnum.AUTORIZZAZIONE);
    }

    public static AutorizzazioneBuilder buildModificaData() {
        return new AutorizzazioneBuilder(AutorizzazioneActionEnum.MODIFICA_DATA_REGOLAMENTO_BANCA);
    }

    public static class AutorizzazioneBuilder {
        private UUID idBonificoExtraSepa;

        private String utente;
        private String nomeUtente;
        private String ruolo;
        private String profilo;

        private final AutorizzazioneActionEnum azione;
        private Long livelloAutorizzazione;

        private LocalDate dataDiRegolamentoPrecedente;

        private Boolean autorizzazioneMessaggio;
        private Boolean autorizzazioneNotifica;

        private String note;

        private AutorizzazioneBuilder(AutorizzazioneActionEnum azione) {
            this.azione = azione;
        }

        public AutorizzazioneBuilder bonificoExtraSepa(BonificoExtraSepa bonificoExtraSepa) {
            this.idBonificoExtraSepa = bonificoExtraSepa.id();
            if (this.azione == AutorizzazioneActionEnum.MODIFICA_DATA_REGOLAMENTO_BANCA) {
                this.dataDiRegolamentoPrecedente = bonificoExtraSepa.dataRegolamentoBancaBeneficiario();
            }
            return this;
        }

        public AutorizzazioneBuilder credentials(CabelForwardedCredential credentials) {
            this.utente = credentials.profile();
            this.nomeUtente = credentials.fullName();
            this.ruolo = credentials.roleDescription();
            this.profilo = credentials.role();
            return this;
        }

        public AutorizzazioneBuilder livelloAutorizzazione(Long livelloAutorizzazione) {
            this.livelloAutorizzazione = livelloAutorizzazione;
            return this;
        }

        public AutorizzazioneBuilder autorizzazioneMessaggio(Boolean autorizzazioneMessaggio) {
            this.autorizzazioneMessaggio = autorizzazioneMessaggio;
            return this;
        }

        public AutorizzazioneBuilder autorizzazioneNotifica(Boolean autorizzazioneNotifica) {
            this.autorizzazioneNotifica = autorizzazioneNotifica;
            return this;
        }

        public AutorizzazioneBuilder note(String note) {
            this.note = note;
            return this;
        }

        public Autorizzazione build() {
            return new Autorizzazione(UUID.randomUUID(), this.idBonificoExtraSepa, utente, nomeUtente, ruolo, profilo,
                    azione, livelloAutorizzazione, dataDiRegolamentoPrecedente, autorizzazioneMessaggio, autorizzazioneNotifica, note,
                    null);
        }
    }
}
