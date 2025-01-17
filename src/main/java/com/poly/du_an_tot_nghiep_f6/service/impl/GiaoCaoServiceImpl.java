package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Employee;
import com.poly.du_an_tot_nghiep_f6.entity.GiaoCa;
import com.poly.du_an_tot_nghiep_f6.repository.GiaoCaRepo;
import com.poly.du_an_tot_nghiep_f6.service.IGiaoCa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiaoCaoServiceImpl implements IGiaoCa {

    @Autowired
    GiaoCaRepo giaoCaRepo;

    @Override
    public Employee getEmployeeAreWorking() {
        return giaoCaRepo.getEmployee().get(0);
    }

    public GiaoCa getShift() {
        return giaoCaRepo.getCaLam().get(0);
    }

}
