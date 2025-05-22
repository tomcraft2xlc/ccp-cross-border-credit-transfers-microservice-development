package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers;


import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "charSet", ignore = true)
@Mapping(target = "creDt", source = "entity.istanteCreazioneMessaggio")
@Mapping(target = "cpyDplct", ignore = true)
@Mapping(target = "pssblDplct", ignore = true)
@Mapping(target = "prty", ignore = true)
@Mapping(target = "sgntr", ignore = true)
@Mapping(target = "rltd", ignore = true)
@Mapping(target = "creationDate", ignore = true)
public @interface HeaderMapping {


}
