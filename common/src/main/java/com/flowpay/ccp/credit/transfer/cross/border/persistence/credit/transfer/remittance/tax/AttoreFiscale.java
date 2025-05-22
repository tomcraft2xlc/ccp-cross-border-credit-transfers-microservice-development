package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.TipoAttoreFiscale;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.EnumKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni fiscali di un attore coinvolto nella transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/TaxRmt}.<p>
 *
 * Questa classe memorizza le informazioni fiscali, inclusi il tipo di attore,
 * l'identificativo fiscale, il tipo di contribuente e altre informazioni rilevanti.<p>
 *
 * La tabella associata nel database è {@code attore_fiscale}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE attore_fiscale (
 *     id UUID PRIMARY KEY,
 *     id_dettagli_fiscali UUID NOT NULL,
 *     tipo_attore_fiscale tipo_attore_fiscale NOT NULL,
 *     identificativo_fiscale VARCHAR(255),
 *     identificativo VARCHAR(255),
 *     tipo_contribuente VARCHAR(255),
 *     titolo VARCHAR(255),
 *     intestazione VARCHAR(255),
 *     CONSTRAINT fk_dettagli_fiscali FOREIGN KEY (id_dettagli_fiscali)
 *         REFERENCES dettagli_fiscali(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco del record.<p>
 * @param idDettagliFiscali          Identificativo dei dettagli fiscali associati.<p>
 * @param tipoAttoreFiscale          Tipo di attore fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>}.</p>
 * @param identificativoFiscale      Identificativo fiscale dell'attore.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>/TaxId}.</p>
 * @param identificativo             Identificativo di registrazione dell'attore.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>/RegnId}.</p>
 * @param tipoContribuente           Tipo di contribuente.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>/TaxTp}.</p>
 * @param titolo                     Titolo dell'attore fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>/Authstn/Titl}.</p>
 * @param intestazione               Intestazione dell'attore fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/<tipo>/Authstn/Nm}.</p>
 *
 * @see TipoAttoreFiscale
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("attore_fiscale")
public record AttoreFiscale(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo dei dettagli fiscali associati.
        @Column("id_dettagli_fiscali")
        UUID idDettagliFiscali,

        /// Tipo di attore fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>`.</p>
        @Column("tipo_attore_fiscale")
        @EnumKind
        TipoAttoreFiscale tipoAttoreFiscale,

        /// Identificativo fiscale dell'attore.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>/TaxId`.</p>
        @Column("identificativo_fiscale")
        String identificativoFiscale,

        /// Identificativo di registrazione dell'attore.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>/RegnId`.</p>
        @Column("identificativo")
        String identificativo,

        /// Tipo di contribuente.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>/TaxTp`.</p>
        @Column("tipo_contribuente")
        String tipoContribuente,

        /// Titolo dell'attore fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>/Authstn/Titl`.</p>
        @Column("titolo")
        String titolo,

        /// Intestazione dell'attore fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/<tipo>/Authstn/Nm`.</p>
        @Column("intestazione")
        String intestazione
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<AttoreFiscale, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<AttoreFiscale> entityClass() {
            return AttoreFiscale.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<AttoreFiscale> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, AttoreFiscale> decoder) {
            super(client, decoder);
        }

        public Multi<AttoreFiscale> getAllByDettagliFiscali(UUID idDettagliFiscali) {
            return multi("SELECT * FROM attore_fiscale WHERE id_dettagli_fiscali = $1",
                    Tuple.of(idDettagliFiscali));
        }

        public Uni<AttoreFiscale> getById(UUID id) {
            return single("SELECT * FROM attore_fiscale WHERE id = $1",
                    Tuple.of(id));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DettagliFiscali.WithLinkedEntities dettagliFiscali;

        @Override
        public AttoreFiscale getEntity() {
            return AttoreFiscale.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettagliFiscali);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();
           
            if (this.dettagliFiscali == null) {
                multis.add(new DettagliFiscali.Entity()
                        .repository(sqlClient).getById(idDettagliFiscali())
                        .map(linked -> {
                            this.dettagliFiscali = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as
                            // the sibling would need to be loaded, and loading is
                            // done en-masse.
                            return this.dettagliFiscali;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettagliFiscali));
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