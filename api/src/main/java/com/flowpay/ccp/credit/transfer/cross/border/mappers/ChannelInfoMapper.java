package com.flowpay.ccp.credit.transfer.cross.border.mappers;


import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.bank.ConfigurazioneBanca;
import org.mapstruct.Mapper;

@Mapper
public interface ChannelInfoMapper {

    ConfigurazioneBanca.InfoCanale toDTO(BanksConfig.BankConfig.SettlementInfo settlementInfo);
}
