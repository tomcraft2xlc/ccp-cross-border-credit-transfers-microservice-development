package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance;

import java.util.List;

public interface Causale {
    String causaleDescrittiva();
    default String getCausaleDescrittiva() { return causaleDescrittiva(); }

    List<CausaleStrutturata> causaleStrutturata();
    default List<CausaleStrutturata> getCausaleStrutturata() { return causaleStrutturata(); }
}
