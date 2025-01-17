package com.poly.du_an_tot_nghiep_f6.response;

import com.poly.du_an_tot_nghiep_f6.entity.BillHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchBillWithoutLogin {
    BillOnlineResponse billOnlineResponse;
    List<BillDetailOnlineResponse> billDetailOnlineResponses;
    List<BillHistory> billHistories ;
}
