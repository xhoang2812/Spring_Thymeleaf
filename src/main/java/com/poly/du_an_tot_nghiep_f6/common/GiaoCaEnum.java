package com.poly.du_an_tot_nghiep_f6.common;

public enum GiaoCaEnum {
    DANG_LAM_VIEC("Đang làm việc"),
    DA_BAN_GIAO_CA("Đã bàn giao ca"),
    DA_RESET_CA("Đã reset ca");

    private final String description;

    GiaoCaEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static GiaoCaEnum fromString(String status) {
        for (GiaoCaEnum giaoCaEnum : GiaoCaEnum.values()) {
            if (giaoCaEnum.description.equalsIgnoreCase(status)) {
                return giaoCaEnum;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
    }
}
