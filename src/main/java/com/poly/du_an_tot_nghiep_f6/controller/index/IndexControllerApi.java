package com.poly.du_an_tot_nghiep_f6.controller.index;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.ErrorList;
import com.poly.du_an_tot_nghiep_f6.repository.ErrorListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index")
public class IndexControllerApi {
    private final ErrorListRepo errorListRepo;

    @GetMapping
    public void errorList(){
        errorListRepo.deleteAll();
        List<ErrorList> errorLists = new ArrayList<>();
        errorLists.add(new ErrorList(0, "Hàng nguyên vẹn nhưng không còn nhu cầu",true));
        errorLists.add(new ErrorList(0, "Sản phẩm không đúng với mô tả",true));
        errorLists.add(new ErrorList(0, "Sản phẩm không đúng kích cỡ",true));
        errorLists.add(new ErrorList(0, "Sản phẩm không đúng màu hoặc mẫu mã",true));
        errorLists.add(new ErrorList(0, "Sản phẩm bị nhăn, bẩn hoặc thiếu chăm sóc trong đóng gói",false));
        errorLists.add(new ErrorList(0, "Sản phẩm bị lỗi hoặc hư hỏng",false));
        errorListRepo.saveAll(errorLists);
    }
}
