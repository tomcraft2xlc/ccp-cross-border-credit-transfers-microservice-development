package com.flowpay.ccp.credit.transfer.cross.border.services;


import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.Accredito;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.MappaturaBonificoInIngresso;
import com.flowpay.ccp.persistence.DataSources;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.UUID;

@RequestScoped
public class AccreditiService {

    private static final Logger LOG = Logger.getLogger(AccreditiService.class);

    @Inject
    public AccreditiService(DataSources dataSources) {
        this.dataSources = dataSources;
    }

    DataSources dataSources;

    public Uni<Accredito> getAccredito(
            UUID id,
            SecurityIdentity identity
    ) {
        var connection = this.dataSources.dataSource(identity);
        var credential = identity.getCredential(CabelForwardedCredential.class);
        var repository = new BonificoInIngresso.Entity().repository(connection);
        return repository.getById(id).flatMap(data -> {
            if (!credential.isBranchVisible(data.codiceFiliale().toString())) {
                return Uni.createFrom().failure(new NotFoundException("bonifico con id %s non presente nel sistema".formatted(id)));
            }
            var mappaturaEntity = new MappaturaBonificoInIngresso.Entity();
            var mappaturaRepository = mappaturaEntity.repository(connection);
            return mappaturaRepository.getByID(data.idMappaturaBonificoInIngresso())
                    .map(mappaturaBonificoInIngresso -> {
                        var mapper = Utils.loadAccreditoMapper(mappaturaBonificoInIngresso.classeQualificataNomeCompleto());
                        var mx = AbstractMX.parse(data.rawXML());

                        return mapper.fromXmlToJson().map(mx, new AccreditoMappingContext(
                                data.sistemaDiRegolamento(),
                                data.sottoTipologiaBonifico()
                        ));
                    });
        });
    }

}
