package com.poly.du_an_tot_nghiep_f6.controller;

import com.poly.du_an_tot_nghiep_f6.common.GiaoCaEnum;
import com.poly.du_an_tot_nghiep_f6.entity.*;
import com.poly.du_an_tot_nghiep_f6.repository.*;
import com.poly.du_an_tot_nghiep_f6.service.EmployeeService;
import com.poly.du_an_tot_nghiep_f6.service.GiaoCaService;
import com.poly.du_an_tot_nghiep_f6.service.IBillService;
import com.poly.du_an_tot_nghiep_f6.service.impl.CartInCounterServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class GiaoCaController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    private GiaoCaService giaoCaService;

    @Autowired
    GiaoCaRepo giaoCaRepo;

    @Autowired
    IBillService iBillService;
    @Autowired
    CartInCounterServiceImpl cartInCounterServiceImpl;

    private final CaLamRepo caLamRepo;
    private final CaLamChiTietRepo caLamChiTietRepo;

    private Long idGiaoca;


    @GetMapping("/giaoca")
    public String listEmployees(Model model, Authentication authentication, HttpSession session) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());

        Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
        Employee employee = new Employee();
        if (user.isPresent()) {
            var userObj = user.get();
            employee = userObj;
            model.addAttribute("user", userObj);
//            System.out.println("dũng in " + userObj);
        }

        // Lấy thông tin giao ca từ giaoCaRepo
        Optional<GiaoCa> optionalGiaoCa = giaoCaRepo.findById(idGiaoca);
        GiaoCa giaoCa = null;
        if (optionalGiaoCa.isPresent()) {
            giaoCa = optionalGiaoCa.get();
            giaoCa.setSlhoadondathanhtoan(iBillService.getTotalBillInShift(giaoCa));

            Double tienmattrongca = 0.;
//            Double chuyenkhoantrongca = 0.;
            try {
                tienmattrongca = iBillService.getTotalPriceBillInShift(giaoCa, 1);
//                chuyenkhoantrongca = iBillService.getTotalPriceBillInShift(giaoCa, 2);
            } catch (Exception e) {

            }
            giaoCa.setTienmattrongca(tienmattrongca);
//            giaoCa.setChuyenkhoantrongca(chuyenkhoantrongca);
            giaoCa.setChuyenkhoantrongca(iBillService.getTotalPriceBillInShift(giaoCa, 2));
            giaoCa.setSlhoadonchuathanhtoan(cartInCounterServiceImpl.getCartInCounters().size());
            String formattedThoiGianVaoCa = giaoCaService.formatDateTime(giaoCa.getThoigianvaoca());
            model.addAttribute("thoigianvaoca", formattedThoiGianVaoCa);

// Định dạng `tienmatdauca` với dấu phẩy ngăn cách hàng nghìn
            DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
            String formattedTienMatDauCa = formatter.format(giaoCa.getTienmatdauca());
            String formattedTongTienMatTrongCa = formatter.format(giaoCa.getTienmattrongca());
            String formattedTongTienCKTrongCa = formatter.format(giaoCa.getChuyenkhoantrongca());
            // Add attributes to the model
            model.addAttribute("chuyenkhoantrongca", formattedTongTienCKTrongCa);
            model.addAttribute("tienmattrongca", formattedTongTienMatTrongCa);
            model.addAttribute("slhoadondathanhtoan", giaoCa.getSlhoadondathanhtoan());
            model.addAttribute("slhoadonchuathanhtoan", giaoCa.getSlhoadonchuathanhtoan());
            model.addAttribute("magiaoca", giaoCa.getMagiaoca());
            model.addAttribute("thoigianvaoca", formattedThoiGianVaoCa);  // Thời gian đã format
            model.addAttribute("tienmatdauca", formattedTienMatDauCa);
            model.addAttribute("tenca", giaoCa.getTenca());

        } else {
            model.addAttribute("error", "Không tìm thấy thông tin giao ca.");
        }

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CaLam currentCaLam = new CaLam();
//        System.out.println("day la ten ca" + giaoCa.getTenca());
        for (CaLam caLam : caLamRepo.findAll()) {
            LocalDate localDateNgayLam = caLam.getNgayLam().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (localDate.isEqual(localDateNgayLam)) {
                currentCaLam = caLam;
                break;
            }
        }

        System.out.println(currentCaLam);
        if (currentCaLam.getId() != null) {
            List<CaLamChiTiet> caLamChiTiets = caLamChiTietRepo.findByCalam(currentCaLam);

            LocalDateTime now = LocalDateTime.now();

//            LocalTime startMorning = LocalTime.of(7, 0);
//            LocalTime endMorning = LocalTime.of(12, 0);
//            LocalTime startAfternoon = LocalTime.of(12, 0);
//            LocalTime endAfternoon = LocalTime.of(17, 0);
//            LocalTime startEvening = LocalTime.of(17, 0);
//            LocalTime endEvening = LocalTime.of(23, 59);

            LocalTime currentTime = now.toLocalTime();
            System.out.println(currentTime);
            if (giaoCa.getTenca().contains("Ca 1")) {
                System.out.println("day la ten ca 1 da lay nhan vien ca 2 ra");
                if (caLamChiTiets.get(1).getEmployees().isEmpty()) { //Khong co nhan vien => ca trong => null
                    model.addAttribute("employeesPage", new ArrayList<Employee>());
                } else {
                    List<Employee> employees = new ArrayList<>();
                    if (caLamChiTiets.get(0).getEmployees().get(0).equals(employee)) {
                        //check nhan vien dang nhap neu trung voi nhan vien trong ca => hien nhan vien ca tiep theo luon
                        // lưu trạng thais nhận ca
                        CaLamChiTiet caLamChiTiet = caLamChiTiets.get(0);
                        caLamChiTiet.setTrangThai("Đã nhận ca");
                        caLamChiTietRepo.save(caLamChiTiet);

                        employees.add(caLamChiTiets.get(1).getEmployees().get(0));
                        model.addAttribute("employeesPage", employees);
//                        model.addAttribute("calam", currentCaLam);
//                        model.addAttribute("tenca", caLamChiTiet.getTenCa());
//                        model.addAttribute("calamchitiet", caLamChiTiet);
                    } else {
                        //con khong thi no cu hien nv ca hien tai o duoi cung tuong tu
                        employees.add(caLamChiTiets.get(0).getEmployees().get(0));
                        model.addAttribute("employeesPage", employees);
                    }
                }
            } else if (giaoCa.getTenca().contains("Ca 2")) {
                System.out.println("day la ten ca 2 da lay nhan vien ca 3 ra");
                if (caLamChiTiets.get(2).getEmployees().isEmpty()) {
                    model.addAttribute("employeesPage", new ArrayList<Employee>());
                } else {
                    List<Employee> employees = new ArrayList<>();
                    if (caLamChiTiets.get(1).getEmployees().get(0).equals(employee)) {
                        //                khi nv ca 2 vao thi set trang thai da nhan ca
                        CaLamChiTiet caLamChiTiet = caLamChiTiets.get(1);
                        caLamChiTiet.setTrangThai("Đã nhận ca");

                        caLamChiTietRepo.save(caLamChiTiet);

                        employees.add(caLamChiTiets.get(2).getEmployees().get(0));
                        model.addAttribute("employeesPage", employees);
                    } else {
                        employees.add(caLamChiTiets.get(1).getEmployees().get(0));
                        model.addAttribute("employeesPage", employees);
                    }
                }
            } else if (giaoCa.getTenca().contains("Ca 3")) {
                System.out.println("day la ten ca 3 da lay admin ra");
//                khi nv ca 3 vao thi set trang thai da nhan ca
                CaLamChiTiet caLamChiTiet = caLamChiTiets.get(2);
                caLamChiTiet.setTrangThai("Đã nhận ca");
                caLamChiTietRepo.save(caLamChiTiet);

                List<Employee> employees = employeeRepo.findAll().stream().filter(employee1 -> employee1.getPosition().equals("Admin")).toList();
                model.addAttribute("employeesPage", employees);
                System.out.println("XXXXXXXXX ne");
            } else {
                model.addAttribute("employeesPage", new ArrayList<Employee>());
            }
        } else {
            model.addAttribute("employeesPage", new ArrayList<Employee>());
        }
        String tBDungGio = (String) session.getAttribute("dunggio");
        if (tBDungGio != null) {
            model.addAttribute("tBDungGio", tBDungGio);
        }
        return "admin/giaoca/giaoca";
    }

    @GetMapping("/lichsu-giaoca")
    public String listGiaoCa(Model model, Authentication authentication) {
        model.addAttribute("listGiaoCa", giaoCaService.findAll());
        return "admin/giaoca/lichsu-giaoca";
    }

    @GetMapping("/giaoca/detail/{id}")
    public String lichSuGiaoCaDetail(@PathVariable("id") Long id, Model model) {
        GiaoCa giaoCaCt = giaoCaService.findById(id);
        if (giaoCaCt != null) {
            model.addAttribute("giaoCaCt", giaoCaCt);
            return "admin/giaoca/giaoca-deatail";
        } else {
            model.addAttribute("error", "Không tìm thông tin giao ca!");
            return "admin/giaoca/lichsu-giaoca";
        }
    }

    @GetMapping("/xacnhantien")
    public String listEmployees2(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println(userDetails.getUsername());
        Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            var userObj = user.get();
            model.addAttribute("user", userObj);
            System.out.println(userObj);
        }
        model.addAttribute("employeesPage", employeeRepo.findAll());
        GiaoCa giaoCa = new GiaoCa();
        model.addAttribute("giaoCa", giaoCa);
        return "admin/giaoca/xacnhantiendauca";
    }


    @PostMapping("/xacnhantien")
    public String addGiaoCa(@RequestParam("tienmatdauca") Double tienmatdauca, Authentication authentication, Model model, HttpSession session) {
        String a = (String) session.getAttribute("notification");
        if (a != null) {
            if (a.equals("true")) {
                model.addAttribute("error", "Hãy nhập đúng số tiền bàn giao của ca trước");
            } else if (a.equals("false")) {
                // đăng nhập khi ca đang trống
                model.addAttribute("error", "Bạn đang đăng nhập khi không phải ca làm của mình ");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDateTime Ngayvaoca = giaoCaService.getCurrentShiftTime2();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // định dạng ngày giờ tùy chọn
        String formattedNgayvaoca = Ngayvaoca.format(formatter);
        String Ca1 = "Ca 1 ngày " + formattedNgayvaoca + " (7h - 12h)";
        String Ca2 = "Ca 2 ngày " + formattedNgayvaoca + " (12h - 17h)";
        String Ca3 = "Ca 3 ngày " + formattedNgayvaoca + " (17h - 22h)";
        LocalTime startMorning = LocalTime.of(7, 0);
        LocalTime endMorning = LocalTime.of(12, 0);
        LocalTime startAfternoon = LocalTime.of(12, 0);
        LocalTime endAfternoon = LocalTime.of(17, 0);
        LocalTime startEvening = LocalTime.of(17, 0);
        LocalTime endEvening = LocalTime.of(22, 0);
        LocalTime TrueCa1 = LocalTime.of(7, 15);
        long minutesLateCa1 = Duration.between(TrueCa1, currentTime).toMinutes();
        LocalTime TrueCa2 = LocalTime.of(12, 15);
        long minutesLateCa2 = Duration.between(TrueCa2, currentTime).toMinutes();
        LocalTime TrueCa3 = LocalTime.of(17, 15);
        long minutesLateCa3 = Duration.between(TrueCa3, currentTime).toMinutes();
        String diLamMuonCa2 = "Đi muộn " + minutesLateCa2 + " phút";
        String diLamMuonCa1 = "Đi muộn " + minutesLateCa1 + " phút";
        String diLamMuonCa3 = "Đi muộn " + minutesLateCa3 + " phút";
        String diLamDungGio = "Đúng giờ";
        GiaoCa giaoCa = new GiaoCa();
        if (currentTime.isAfter(startMorning) && currentTime.isBefore(endMorning)) {
            giaoCa.setTenca(Ca1);
            // test di lam muon
            if (currentTime.isAfter(startMorning) && currentTime.isBefore(TrueCa1)) {
                giaoCa.setLichSuGiaoCa(diLamDungGio);
                session.setAttribute("dunggio", "Chúc mừng bạn đi làm đúng giờ");
                System.out.println("Bạn đang ở Ca 1 và đi làm đúng giờ.");
            } else if (currentTime.isAfter(TrueCa1) && currentTime.isBefore(endMorning)) {

                System.out.println("Bạn đang ở Ca 1 và đi làm muộn " + minutesLateCa1 + " phút.");
                giaoCa.setLichSuGiaoCa(diLamMuonCa1);
            } else {
                System.out.println("Bạn không ở trong khoảng thời gian của Ca 1.");
            }
            // hết test
        } else if (currentTime.isAfter(startAfternoon) && currentTime.isBefore(endAfternoon)) {
            giaoCa.setTenca(Ca2);
            // test di lam muon
            if (currentTime.isAfter(startAfternoon) && currentTime.isBefore(TrueCa2)) {
                giaoCa.setLichSuGiaoCa(diLamDungGio);
                System.out.println("Bạn đang ở Ca 2 và đi làm đúng giờ.");
            } else if (currentTime.isAfter(TrueCa2) && currentTime.isBefore(endAfternoon)) {
                System.out.println("Bạn đang ở Ca 2 và đi làm muộn " + minutesLateCa2 + " phút.");
                giaoCa.setLichSuGiaoCa(diLamMuonCa2);
            } else {
                System.out.println("Bạn không ở trong khoảng thời gian của Ca 2.");
            }
            // hết test
        } else if (currentTime.isAfter(startEvening) && currentTime.isBefore(endEvening)) {
            giaoCa.setTenca(Ca3);

            // test di lam muon
            if (currentTime.isAfter(startEvening) && currentTime.isBefore(TrueCa3)) {
                giaoCa.setLichSuGiaoCa(diLamDungGio);
                System.out.println("Bạn đang ở Ca 3 và đi làm đúng giờ.");
            } else if (currentTime.isAfter(TrueCa3) && currentTime.isBefore(endEvening)) {
                System.out.println("Bạn đang ở Ca 3 và đi làm muộn " + minutesLateCa3 + " phút.");
                giaoCa.setLichSuGiaoCa(diLamMuonCa3);
            } else {
                System.out.println("Bạn không ở trong khoảng thời gian của Ca 3.");
            }
            // hết test
        } else {
            System.out.println("Không nằm trong khoảng giờ làm việc.");
        }

//        session.removeAttribute("notification");
        // Lấy thông tin nhân viên đăng nhập
        if (giaoCaRepo.getCaLam().isEmpty()) {
            System.out.println("day la ca 1 khong can kiem tra tien");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
            // Kiểm tra nếu nhân viên tồn
            // tại thì gán id_nhan_vien cho giaoCa
//            GiaoCa giaoCa = new GiaoCa();
            giaoCa.setTienmatdauca(tienmatdauca);
            String maGiaoCa = giaoCaService.generateMaGiaoCa();
            giaoCa.setMagiaoca(maGiaoCa);

            LocalDateTime thoiGianVaoCa = giaoCaService.getCurrentShiftTime();
            giaoCa.setThoigianvaoca(thoiGianVaoCa); // Gán thời gian vào ca hiện tại

            giaoCa.setTrang_thai(GiaoCaEnum.DANG_LAM_VIEC);

            if (user.isPresent()) {
                Employee userObj = user.get();
                giaoCa.setEmployee(userObj);  // Gán id nhân viên vào giaoCa
            }
            // Lưu đối tượng giaoCa
            idGiaoca = giaoCaService.addGiaoCa(giaoCa).getId();
            return "redirect:/giaoca";  // Điều hướng đến trang khác sau khi thêm thành công
        }
        GiaoCa caLamTruoc = giaoCaRepo.getCaLam().get(0);

        GiaoCa giaoCaHienTai = new GiaoCa();
//        GiaoCa ca3 = giaoCaRepo.getCaLam().get(4);
        // lấy thông tin nhân viên ca
//        LocalDateTime now = LocalDateTime.now();
//
//        LocalTime startMorning = LocalTime.of(7, 0);
//        LocalTime endMorning = LocalTime.of(12, 0);
//        LocalTime currentTime = now.toLocalTime();
//        LocalDateTime Ngayvaoca = giaoCaService.getCurrentShiftTime2();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // định dạng ngày giờ tùy chọn
//        String formattedNgayvaoca = Ngayvaoca.format(formatter);
        if (currentTime.isAfter(startMorning) && currentTime.isBefore(endMorning)) {
            System.out.println("day la ca 1 khong can kiem tra tien");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
            // Kiểm tra nếu nhân viên tồn
            // tại thì gán id_nhan_vien cho giaoCa
//
//            GiaoCa giaoCa = new GiaoCa();
//
//            String Ca1 = "Ca 1 ngày " + formattedNgayvaoca;

            giaoCa.setTienmatdauca(tienmatdauca);
            String maGiaoCa = giaoCaService.generateMaGiaoCa();
            giaoCa.setMagiaoca(maGiaoCa);

            LocalDateTime thoiGianVaoCa = giaoCaService.getCurrentShiftTime();
            giaoCa.setThoigianvaoca(thoiGianVaoCa); // Gán thời gian vào ca hiện tại
//            giaoCa.setTenca(Ca1);
            giaoCa.setTrang_thai(GiaoCaEnum.DANG_LAM_VIEC);

            if (user.isPresent()) {
                Employee userObj = user.get();
                giaoCa.setEmployee(userObj);  // Gán id nhân viên vào giaoCa
            }
            // Lưu đối tượng giaoCa
            idGiaoca = giaoCaService.addGiaoCa(giaoCa).getId();
            return "redirect:/giaoca";  // Điều hướng đến trang khác sau khi thêm thành công
        } else if (caLamTruoc.getSotienthucnhan() == null || caLamTruoc.getSotienthucnhan() == 0) {
            System.out.println("id cua ca lam" + caLamTruoc.getId() + "ma cua ca lam " + caLamTruoc.getMagiaoca());
            System.out.println("So tien ca lam truoc" + caLamTruoc.getSotienthucnhan());
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
            // Kiểm tra nếu nhân viên tồn
            // tại thì gán id_nhan_vien cho giaoCa
//            GiaoCa giaoCa = new GiaoCa();
            giaoCa.setTienmatdauca(tienmatdauca);
            String maGiaoCa = giaoCaService.generateMaGiaoCa();
            giaoCa.setMagiaoca(maGiaoCa);

            LocalDateTime thoiGianVaoCa = giaoCaService.getCurrentShiftTime();
            giaoCa.setThoigianvaoca(thoiGianVaoCa); // Gán thời gian vào ca hiện tại

            giaoCa.setTrang_thai(GiaoCaEnum.DANG_LAM_VIEC);

            if (user.isPresent()) {
                Employee userObj = user.get();
                giaoCa.setEmployee(userObj);  // Gán id nhân viên vào giaoCa
            }
            // Lưu đối tượng giaoCa
            idGiaoca = giaoCaService.addGiaoCa(giaoCa).getId();
            return "redirect:/giaoca";  // Điều hướng đến trang khác sau khi thêm thành công

        } else if (tienmatdauca.equals(caLamTruoc.getSotienthucnhan())) {
            System.out.println("nhap dung so tien ca truoc roi, phai vao ca");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
            // Kiểm tra nếu nhân viên tồn
            // tại thì gán id_nhan_vien cho giaoCa
//            GiaoCa giaoCa = new GiaoCa();
            giaoCa.setTienmatdauca(tienmatdauca);
            String maGiaoCa = giaoCaService.generateMaGiaoCa();
            giaoCa.setMagiaoca(maGiaoCa);

            LocalDateTime thoiGianVaoCa = giaoCaService.getCurrentShiftTime();
            giaoCa.setThoigianvaoca(thoiGianVaoCa); // Gán thời gian vào ca hiện tại

            giaoCa.setTrang_thai(GiaoCaEnum.DANG_LAM_VIEC);

            if (user.isPresent()) {
                Employee userObj = user.get();
                giaoCa.setEmployee(userObj);  // Gán id nhân viên vào giaoCa
            }
            // Lưu đối tượng giaoCa
            idGiaoca = giaoCaService.addGiaoCa(giaoCa).getId();
            return "redirect:/giaoca";  // Điều hướng đến trang khác sau khi thêm thành công
        } else if (!caLamTruoc.getSotienthucnhan().equals(tienmatdauca)) {

            System.out.println("so tien nhap vao" + tienmatdauca);
            System.out.println("phai nhap so tien la" + caLamTruoc.getSotienthucnhan());
            System.out.println("Nhap sai tien roi");
            model.addAttribute("error", "Hãy nhập đúng số tiền bàn giao của ca trước");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println(userDetails.getUsername());
            Optional<Employee> user = employeeRepo.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                var userObj = user.get();
                model.addAttribute("user", userObj);
                System.out.println(userObj);
            }
            model.addAttribute("employeesPage", employeeRepo.findAll());
//            GiaoCa giaoCa = new GiaoCa();
            model.addAttribute("giaoCa", giaoCa);
            return "admin/giaoca/xacnhantiendauca";
        }
        return "redirect:/giaoca";
    }

    @GetMapping("/clear")
    public String clearTB(HttpSession session) {
        session.setAttribute("dunggio", "");
        return "redirect:/giaoca";
    }

    @PostMapping("/giaoca")
    public String saveGiaoCaNhanVien(
            @RequestParam("tienphatsinh") Double tienphatsinh,
            @RequestParam("magiaoca") String magiaoca,
            @RequestParam("tienmattrongca") Double tienmattrongca,
            @RequestParam("chuyenkhoantrongca") Double chuyenkhoantrongca,
            @RequestParam("xacnhantienmat") Double xacnhantienmat,
            @RequestParam("xacnhantienchuyenkhoan") Double xacnhantienchuyenkhoan,
            @RequestParam("sotienthucnhan") Double sotienthucnhan,
            @RequestParam("sotienconthieu") Double sotienconthieu,
            @RequestParam("ghichu") String ghichu,
            @RequestParam("employee") Integer employee,
            @RequestParam("slhoadondathanhtoan") Integer slhoadondathanhtoan,
            @RequestParam("slhoadonchuathanhtoan") Integer slhoadonchuathanhtoan
    ) {
        System.out.println(magiaoca);
        System.out.println("XXXXXXXXXXXXx" + employee);
        System.out.println(tienphatsinh + '/' + tienmattrongca + '/' + chuyenkhoantrongca + '/' + xacnhantienchuyenkhoan + '/' + xacnhantienmat + '/' + ghichu);
        GiaoCa giaoCa = giaoCaRepo.findByMagiaoca(magiaoca);
        giaoCa.setTienphatsinh(tienphatsinh);
        giaoCa.setTienmattrongca(tienmattrongca);
        giaoCa.setChuyenkhoantrongca(chuyenkhoantrongca);
        giaoCa.setXacnhantienmat(xacnhantienmat);
        giaoCa.setXacnhantienchuyenkhoan(xacnhantienchuyenkhoan);
        giaoCa.setSotienthucnhan(sotienthucnhan);
        giaoCa.setSotienconthieu(sotienconthieu);
        giaoCa.setGhichu(ghichu);
        //test
        giaoCa.setSlhoadonchuathanhtoan(slhoadonchuathanhtoan);
        giaoCa.setSlhoadondathanhtoan(slhoadondathanhtoan);
        System.out.println("Sl hoa don CHUA TT" + slhoadonchuathanhtoan);
        System.out.println("Sl hoa don DA TT" + slhoadondathanhtoan);

        LocalDateTime thoiGianGiaoCa = giaoCaService.getCurrentShiftTime();
        giaoCa.setThoigianketthucca(thoiGianGiaoCa); // Gán thời gian kết thúc ca
        Employee employee1 = employeeRepo.getReferenceById(employee);
        giaoCa.setNhan_vien_ban_giao("Mã :" + employee1.getEmployeeCode() + " - Tên: " + employee1.getName() + " - SDT: " + employee1.getPhone());

        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalTime ca1 = LocalTime.of(7, 0);
        LocalTime ca2 = LocalTime.of(21, 0);
        LocalTime endEvening = LocalTime.of(22, 0);
        LocalTime endDay = LocalTime.of(23, 59);
        if (currentTime.isAfter(ca1) && currentTime.isBefore(ca2)) {
            System.out.println("day la ca 1 va 2 set trang thai da ban giao ca");
            giaoCa.setTrang_thai(GiaoCaEnum.DA_BAN_GIAO_CA);
        }
        if (currentTime.isAfter(endEvening) && currentTime.isBefore(endDay)) {
            System.out.println("day la ca 3 trang thai da resetca");
            giaoCa.setTrang_thai(GiaoCaEnum.DA_RESET_CA);
        }

//        if (giaoCaService.getCurrentShiftTime())
        giaoCaService.addGiaoCa(giaoCa);
        return "redirect:/logout";
    }


}
