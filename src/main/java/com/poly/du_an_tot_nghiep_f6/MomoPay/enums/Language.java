package com.poly.du_an_tot_nghiep_f6.MomoPay.enums;

import com.poly.du_an_tot_nghiep_f6.MomoPay.shared.constants.Constants;
import com.google.gson.annotations.SerializedName;

public enum Language {

    @SerializedName("vi")
    VI(Constants.LANGUAGE_VI),

    @SerializedName("en")
    EN(Constants.LANGUAGE_EN);

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public static Language findByName(String name) {
        for (Language type : values()) {
            if (type.getLanguage().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getLanguage() { return value; }

}
