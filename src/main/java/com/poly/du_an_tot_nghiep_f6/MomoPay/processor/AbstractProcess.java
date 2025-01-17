package com.poly.du_an_tot_nghiep_f6.MomoPay.processor;

import com.poly.du_an_tot_nghiep_f6.MomoPay.config.Environment;
import com.poly.du_an_tot_nghiep_f6.MomoPay.config.PartnerInfo;
import com.poly.du_an_tot_nghiep_f6.MomoPay.shared.exception.MoMoException;
import com.poly.du_an_tot_nghiep_f6.MomoPay.shared.utils.Execute;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author hainguyen
 * Documention: https://developers.momo.vn
 */

public abstract class AbstractProcess<T, V> {

    protected PartnerInfo partnerInfo;
    protected Environment environment;
    protected Execute execute = new Execute();

    public AbstractProcess(Environment environment) {
        this.environment = environment;
        this.partnerInfo = environment.getPartnerInfo();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    public abstract V execute(T request) throws MoMoException;
}
