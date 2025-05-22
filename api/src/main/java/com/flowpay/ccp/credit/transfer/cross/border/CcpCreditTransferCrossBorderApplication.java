package com.flowpay.ccp.credit.transfer.cross.border;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title="CCP Disposizione Bonifico",
        version = "1.0.0",
        description = """
        Interfaccia per la disposizione di un bonifico cross border (fuori dall'area SEPA).
        
        ## Macchina a stati bonifico

        La seguente macchina a stati descrive i possibili stati in cui si trova un bonifico.

        Lo stato inserted e to be confirmed sono previsti per il flusso di inserimento da frontend di backoffice.

        [![](https://mermaid.ink/img/pako:eNplUk1PAyEU_CsvHE17sN_hYFKrBxN7qomJrofnwrYk7KNhoUab_ndxd8HScmKGGd5A5shKIyTjrHHo5IPCrcV6eBgVVBCEtbnl8ESNtE6KnhlxeDFwL2FlqFK2TgdjDo_WGpuISVQuvdsZq37SyZTD6-oZyDjYY9MkfsZhhVRKrRM157CR5Hq04NeXzWA4vIP3m48YucV_OZfkFFQWvQAkAQfUSqBThvKpvWGcGYw9118lHV8MHXV3hCenb2lnfpW6Mza5cHou_PSuFV5N6ZPNcm-EkxxOczi_SNirF_EnOzjPzSH_Gsmjjuna58c2sAGrZeCUCI05_nEFcztZy4LxsBWyQq9dwQo6BSl6ZzbfVDLurJcD5vfiv2OMV6ibwEqhnLHrroVtGQfMGr_dJcUe6c2Y6Dj9AtBuzIo?type=png)](https://mermaid.live/edit#pako:eNplUk1PAyEU_CsvHE17sN_hYFKrBxN7qomJrofnwrYk7KNhoUab_ndxd8HScmKGGd5A5shKIyTjrHHo5IPCrcV6eBgVVBCEtbnl8ESNtE6KnhlxeDFwL2FlqFK2TgdjDo_WGpuISVQuvdsZq37SyZTD6-oZyDjYY9MkfsZhhVRKrRM157CR5Hq04NeXzWA4vIP3m48YucV_OZfkFFQWvQAkAQfUSqBThvKpvWGcGYw9118lHV8MHXV3hCenb2lnfpW6Mza5cHou_PSuFV5N6ZPNcm-EkxxOczi_SNirF_EnOzjPzSH_Gsmjjuna58c2sAGrZeCUCI05_nEFcztZy4LxsBWyQq9dwQo6BSl6ZzbfVDLurJcD5vfiv2OMV6ibwEqhnLHrroVtGQfMGr_dJcUe6c2Y6Dj9AtBuzIo)

        ### Esempi

        Secondo questo schema se un canale ad esempio MITO prevede che lo stato iniziale di default sia To Be Authorized, allora è possibile indicare come stato iniziale lo stato Inserted. L'effetto è che il bonifico sarà in automatico sottoposto ai controlli anti frode e di validazione, nel caso tali controlli passino, in automatico verrà anche eseguito la conferma e la verifica WCL.


        Se lo stato di default di un canale è Sent significa che il ciclo di vita e di invio sulla rete viene gestito direttamente da tale canale, quindi l'inserimento nello stato di default ha effetto di aggiungere il bonifico allo storico, nel caso invece venga richiesto che lo stato iniziale sia Authorized, il canale sta richiedendo che il messaggio xml legato al bonifico venga inviato in rete direttamente da CCP
        
        """
        )
)
public class CcpCreditTransferCrossBorderApplication extends Application {

}
