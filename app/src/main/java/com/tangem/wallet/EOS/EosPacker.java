package com.tangem.wallet.EOS;

import io.jafka.jeos.convert.Packer;
import io.jafka.jeos.core.common.transaction.TransactionAction;
import io.jafka.jeos.core.common.transaction.TransactionAuthorization;
import io.jafka.jeos.util.Raw;
import io.jafka.jeos.util.ecc.Hex;

public class EosPacker extends Packer {
    public static Raw packPackedTransaction(String chainId, EosPackedTransaction t) {
        Raw raw = new Raw();
        //chain
        raw.pack(Hex.toBytes(chainId));
        //expiration
        raw.packUint32(t.getExpirationSec());
        //ref_block_num
        raw.packUint16(t.getRefBlockNum().intValue());
        //ref_block_prefix
        raw.packUint32(t.getRefBlockPrefix());
        //max_net_usage_words
        raw.packVarint32(t.getMaxNetUsageWords());
        //max_cpu_usage_ms
        raw.packUint8(t.getMaxCpuUsageMs());//TODO: what the type?
        //delay_sec
        raw.packVarint32(t.getDelaySec());
        //context_free_actions
        raw.packVarint32(t.getContextFreeActions().size());
        //TODO: getContextFreeActions

        //actions
        raw.packVarint32(t.getActions().size());

        for (TransactionAction a : t.getActions()) {
            //action.account
            raw.packName(a.getAccount())//
                    .packName(a.getName())//
                    .packVarint32(a.getAuthorization().size())//
            ;
            //action.authorization
            for (TransactionAuthorization au : a.getAuthorization()) {
                raw.packName(au.getActor())//
                        .packName(au.getPermission());
            }

            //action.data
            byte[] dat = Hex.toBytes(a.getData());
            raw.packVarint32(dat.length);
            raw.pack(dat);
        }
        //transaction_extensions
        //raw.packVarint32(t.getTransactionExtensions().size());
        //TODO: getTransactionExtensions

        //context_free_data
        //raw.packVarint32(t.getContextFreeActions().size());
        return raw;
    }
}