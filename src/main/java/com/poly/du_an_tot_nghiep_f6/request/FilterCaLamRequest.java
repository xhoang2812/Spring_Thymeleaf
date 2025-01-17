package com.poly.du_an_tot_nghiep_f6.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCaLamRequest {
    private Date dateStart;
    private Date dateEnd;
}
