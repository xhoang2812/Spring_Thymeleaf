package com.poly.du_an_tot_nghiep_f6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillHistoryRespone {
    Integer id;
    String nameEmployee;
    Date dataUpdate;
    int statusOld;
    int statusNew;
    String description;
}
