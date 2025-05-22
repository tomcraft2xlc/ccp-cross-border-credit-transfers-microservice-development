package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account_to_account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante.InfoNDG;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante.InfoOrdinante;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.DettaglioBonificoAccountToAccount;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.InformazioniNdg;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account.TipoNdg;

@Mapper(config = MappingCommonConfig.class, imports = {UUID.class})
public interface InformazioniNdgMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "idDettaglioBonifico", source = "dettaglioBonificoAccountToAccount.entity.id")
    @DtoToBareEntity
    InformazioniNdg bareFromDto(InfoNDG infoNdg,
            DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount,
            TipoNdg tipo);

    void fillLinked(
            @MappingTarget InformazioniNdg.WithLinkedEntities informazioniAggiuntivePagamento,
            DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount);

    @DtoToEntityWithLinkedEntitiesMainDocument
    default InformazioniNdg.WithLinkedEntities fromDto(InfoNDG infoNdg,
            @Context DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount,
            TipoNdg tipo) {
        var entity =
                bareFromDto(infoNdg, dettaglioBonificoAccountToAccount, tipo).withLinkedEntities();
        fillLinked(entity, dettaglioBonificoAccountToAccount);
        return entity;
    }

    @DtoToEntityWithLinkedEntitiesMainDocument
    default Collection<InformazioniNdg.WithLinkedEntities> fromDto(InfoOrdinante infoOrdinante,
            @Context DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount) {
        if (infoOrdinante == null) {
            return null;
        }

        var infoNdg = new ArrayList<InformazioniNdg.WithLinkedEntities>();

        if (infoOrdinante.presentatore() != null) {
            infoOrdinante.presentatore().forEach(presentatore -> infoNdg.add(fromDto(presentatore,
                    dettaglioBonificoAccountToAccount, TipoNdg.PRESENTATORE)));
        }

        if (infoOrdinante.titolareEffettivo() != null) {
            infoOrdinante.titolareEffettivo()
                    .forEach(presentatore -> infoNdg.add(fromDto(presentatore,
                            dettaglioBonificoAccountToAccount, TipoNdg.TITOLARE_EFFETTIVO)));
        }

        return infoNdg;
    }

    @Mapping(target = ".", source = "entity")
    InfoNDG toDto(InformazioniNdg.WithLinkedEntities informazioniNdg);

    default List<InfoNDG> toDto(Collection<InformazioniNdg.WithLinkedEntities> informazioniNdg,
            @Context TipoNdg tipo) {
        if (informazioniNdg == null) {
            return null;
        }

        return informazioniNdg.stream().filter(infoNdg -> infoNdg.getEntity().tipo() == tipo).map(this::toDto)
                .toList();
    }

    @Named("toPresentatoreDto")
    public default List<InfoNDG> toPresentatoreDto(Collection<InformazioniNdg.WithLinkedEntities> informazioniNdg) {
        return toDto(informazioniNdg, TipoNdg.PRESENTATORE);
    }

    @Named("toTitolareEffettivoDto")
    public default List<InfoNDG> toTitolareEffettivoDto(Collection<InformazioniNdg.WithLinkedEntities> informazioniNdg) {
        return toDto(informazioniNdg, TipoNdg.TITOLARE_EFFETTIVO);
    }

}
