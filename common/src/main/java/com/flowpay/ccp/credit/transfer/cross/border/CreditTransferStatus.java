package com.flowpay.ccp.credit.transfer.cross.border;

public enum CreditTransferStatus {

    /// The credit transfer has been received and is to be processed further.
    ///
    /// The next status is INSERTED or DELETED.
    ///
    /// The status transaction is a manual step
    ///
    /// Channels that can be used to reach this status are:
    /// 1. MITO & C.
    /// 2. MITO
    /// 3. Filiale Estero
    TO_BE_MANAGED,

    /// The credit transfer has been inserted in the system, background checks are being performed:
    ///  1. reachability
    ///  2. CIP-VERIFY-BON-OUT
    ///
    /// The next status is TO_BE_CONFIRMED or in case of errors ERRORED.
    ///
    /// This is often the initial status of a credit transfer.
    ///
    /// Channels that can be used to reach this status are:
    /// 1. CCP Frontend
    INSERITO,

    ///  The credit transfer must be confirmed. When confirmed it will transit in the status CONFIRMED, otherwise in the status CANCELLED.
    ///  This is a manual step
    DA_CONFERMARE,

    /// The first step of the confirmation process has been completed, but some checks requires manual intervention.
    CONFERMATO_STEP_1_COMPLETO,

    /// The second step of the confirmation process is running
    CONFERMATO_STEP_2,

    ///  The credit transfer has been confirmed. background checks are being performed:
    /// 1. WCL
    /// 2. CIP-CONFIRM-BON-OUT
    ///
    /// The next status is TO_BE_AUTHORIZED or in case of WCL related problems WCL_NOT_PASSED, for other kind of errors the status will be ERRORED.
    CONFERMATO,

    /// The credit transfer has been deleted.
    /// This is a final status.
    /// This status is reached if a credit transfer in the status TO_BE_MANAGED is deleted.
    ELIMINATO,

    /// The credit transfer has not passed the WCL check.
    /// An operator must authorize the credit transfer or deny it.
    ///
    /// The next status is TO_BE_AUTHORIZED or in case of denial the status will be WCL_NOT_GRANTED.
    WCL_NON_PASSATA,

    /// The credit transfer has been denied by the operator because of a WCL problem.
    /// This is a final status.
    WCL_NEGATA,

    /// The credit transfer must be authorized. The authorization process is a complex use case with its logic.
    ///
    /// If the credit transfer is authorized the status will transit in the intermediate status AUTHORIZED,
    /// otherwise if the operator doesn't authorize it the status will be NOT_AUTHORIZED_REIMBURSEMENT_SCHEDULED.,
    /// the operator can also cancel the credit transfer, in this case the status will be CANCELLED_REIMBURSEMENT_SCHEDULED.
    DA_AUTORIZZARE,

    /// The credit transfer has been authorized. Background operations are being performed:
    ///
    /// 1. The credit transfer is being sent on the network as an iso20022 message.
    ///
    /// The next status is SENT or in case of errors ERRORED.
    AUTORIZZATO,

    /// The credit transfer has been sent on the network as an iso20022 message.
    ///
    /// The next status is REGULATED or REJECTED
    /// If the message was sent to the CBPR+ network the status will be automatically set to REGULATED
    /// otherwise we wait for the response from the network. if we receive a pacs002 OK the status will be REGULATED
    /// in case of a pacs002 KO the status will be REJECTED.
    ///
    /// It's also possible for an operator to request a chargeback in this status in this case the status will be CHARGE_BACK_REQUESTED.
    INVIATO,

    /// The credit transfer has been regulated.
    /// This is not a final status because the operator can still request a chargeback. In this case the status will be CHARGE_BACK_REQUESTED.
    REGOLATO,

    /// An error occurred during the processing of the credit transfer.
    ///
    /// This is a final status.
    IN_ERRORE,

    /// The credit transfer has not been authorized by the operator. The reimbursement is being processed.
    /// The next status is NOT_AUTHORIZED
    NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO,

    /// The credit transfer has not been authorized by the operator.
    ///
    /// This is a final status. A chargeback must be done on the cabel system.
    NON_AUTORIZZATO,

    /// The credit transfer has been rejected by the network. The reimbursement is being processed.
    ///
    /// The next status is REJECTED.
    RIFIUTATO_RIMBORSO_PROGRAMMATO,

    /// The credit transfer has been rejected by the network.
    ///
    /// This is a final status. A chargeback must be done on the cabel system.
    RIFIUTATO,

    /// The operator has requested a chargeback. In the background the system is performing the step necessary to request the chargeback.
    ///
    /// The next status is CHARGE_BACK_SENT. It depends on the response from the network.
    STORNO_RICHIESTO,

    /// The chargeback request has been sent to the network.
    /// The next status is CHARGE_BACK_ACCEPTED or CHARGE_BACK_REJECTED. It depends on the response from the network.
    STORNO_INVIATO,


    /// The chargeback request has been accepted by the network.
    ///
    /// The next status is CHARGE_BACK_REIMBURSEMENT_SCHEDULED, the operator must manually confirm the reimbursement to the client.
    STORNO_ACCETTATO,

    /// The chargeback request has been rejected by the network.
    /// This is a final status.
    STORNO_RIFIUTATO,

    /// The operator has scheduled the reimbursement of the credit transfer. In the background the system is
    /// performing the step necessary to reimburse the client. The next status is REIMBURSED.
    STORNO_RIMBORSO_PROGRAMMATO,

    /// The credit transfer has been reimbursed.
    /// This is a final status.
    RIMBORSATO;

    /**
     * Checks if the transition to the new status is allowed.
     *
     * @param newStatus the new status to transition to
     * @return true if the transition is allowed, false otherwise
     */
    public Boolean isTransitionAllowed(CreditTransferStatus newStatus) {
        return switch (this) {
            case TO_BE_MANAGED -> newStatus == INSERITO || newStatus == ELIMINATO;
            case INSERITO -> false;
            case DA_CONFERMARE -> newStatus == CONFERMATO || newStatus == ELIMINATO; //TODO verificare stato DELETED
            case CONFERMATO_STEP_1_COMPLETO -> newStatus == CONFERMATO_STEP_2 || newStatus == ELIMINATO;
            case CONFERMATO_STEP_2 -> false;
            case CONFERMATO -> false;
            case ELIMINATO -> false;
            case WCL_NON_PASSATA -> newStatus == DA_AUTORIZZARE || newStatus == WCL_NEGATA;
            case WCL_NEGATA -> false;
            case DA_AUTORIZZARE -> newStatus == NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO || newStatus == AUTORIZZATO; //TODO verificare stato AUTHORIZED probabilmente si muove in base a lifecycle diverso
            case AUTORIZZATO -> false;
            case INVIATO -> newStatus == STORNO_RICHIESTO;
            case REGOLATO -> newStatus == STORNO_RICHIESTO;
            case NON_AUTORIZZATO_RIMBORSO_PROGRAMMATO -> false;
            case NON_AUTORIZZATO -> false;
            case RIFIUTATO_RIMBORSO_PROGRAMMATO -> false;
            case RIFIUTATO -> false;
            case STORNO_RICHIESTO -> false;
            case STORNO_INVIATO -> false;
            case STORNO_RIFIUTATO -> false;
            case STORNO_ACCETTATO -> newStatus == STORNO_RIMBORSO_PROGRAMMATO;
            case STORNO_RIMBORSO_PROGRAMMATO -> false;
            case RIMBORSATO -> false;
            case IN_ERRORE -> false;
        };
    }
}
