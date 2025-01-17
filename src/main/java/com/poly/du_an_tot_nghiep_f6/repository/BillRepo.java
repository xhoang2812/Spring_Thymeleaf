package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Bill;
import com.poly.du_an_tot_nghiep_f6.entity.CustomerBillResponse;
import com.poly.du_an_tot_nghiep_f6.response.BillDetailOnlineResponse;
import com.poly.du_an_tot_nghiep_f6.response.BillOnlineResponse;
import com.poly.du_an_tot_nghiep_f6.response.BillResponse;
import com.poly.du_an_tot_nghiep_f6.response.TopSPBanChay;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import java.time.LocalDateTime;

@Repository
public interface BillRepo extends JpaRepository<Bill, Integer> {
    @Query("""
            SELECT new com.poly.du_an_tot_nghiep_f6.response.BillResponse(
                bill.id,
            COALESCE(bill.customer.name, ''),
            COALESCE(bill.employee.name, ''),
            bill.totalPrice,
            bill.paymentMethods.id,
            bill.voucher,
            bill.shipPrice,
            bill.dateCreate,
            bill.status,
            bill.descriptionShip,
            bill.paymentType,
            bill.address,
            bill.customer.id,
            bill.descriptionBill
            )
            FROM Bill bill
            LEFT JOIN bill.customer customer 
            LEFT JOIN bill.employee employee  
            LEFT JOIN bill.address address    
            LEFT JOIN bill.voucher voucher
                 ORDER BY 
                       bill.dateCreate DESC 
            """)
    List<BillResponse> getBills();



    @Query("""
            SELECT bill
            FROM Bill bill
            WHERE (:keyWord IS NULL 
              OR   (LOWER(bill.descriptionBill) LIKE LOWER(CONCAT('%', :keyWord, '%'))) 
              OR   (:idBill IS NULL OR bill.id = :idBill))
              AND (:dateStart IS NULL OR bill.dateCreate >= :dateStart)
              AND (:dateEnd IS NULL OR bill.dateCreate <= :dateEnd)
              AND (:status IS NULL OR
                   (:status = 6 AND bill.status >= 6) OR
                   (:status <> 6 AND bill.status = :status))
              AND (:minPrice IS NULL OR (COALESCE(bill.totalPrice, 0) + COALESCE(bill.shipPrice, 0) - COALESCE(bill.tienGiam, 0)) >= :minPrice)
              AND (:maxPrice IS NULL OR (COALESCE(bill.totalPrice, 0) + COALESCE(bill.shipPrice, 0) - COALESCE(bill.tienGiam, 0)) <= :maxPrice)
            ORDER BY\s
                CASE
                    WHEN :status IS NULL THEN
                        CASE
                            WHEN bill.status = 0 THEN 0
                            WHEN bill.status = 1 THEN 1
                            WHEN bill.status = 2 THEN 2
                            WHEN bill.status = 3 THEN 3
                            WHEN bill.status = 4 THEN 4
                            WHEN bill.status = 5 THEN 5
                            ELSE 6
                        END
                    ELSE NULL
                END ASC,
                CASE
                    WHEN bill.status BETWEEN 0 AND 5 THEN 0\s
                    ELSE 1\s
                END ASC
            """)
    List<Bill> getBillFilter(
            @Param("idBill") Integer idBill,
            @Param("keyWord") String keyWord,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd,
            @Param("status") Integer status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Sort sort);

//    AND (
//                    (bill.customer.name LIKE %:keyWord% OR :keyWord IS NULL) OR
//                    (bill.customer.username LIKE %:keyWord% OR :keyWord IS NULL) OR
//            (bill.customer.email LIKE %:keyWord% OR :keyWord IS NULL) OR
//                    (bill.customer.phone LIKE %:keyWord% OR :keyWord IS NULL) OR
//            (bill.descriptionBill LIKE %:keyWord% OR :keyWord IS NULL) OR
//                    (bill.descriptionShip LIKE %:keyWord% OR :keyWord IS NULL) OR
//            (bill.customer.phone LIKE %:keyWord% OR :keyWord IS NULL) OR
//                    (bill.employee.employeeCode LIKE %:keyWord% OR :keyWord IS NULL) OR
//            (bill.id = :idBill OR :idBill IS NULL)
//                    )

    @Query("""
            Select count(*) from Bill bill
            where bill.dateCreate between ?1 and ?2
            """)
    int totalBillInShift(LocalDateTime timeInShift, LocalDateTime timeOutShift);

    @Query("""
             Select SUM(bill.totalPrice + COALESCE(bill.shipPrice, 0)) from Bill bill
             where (bill.dateCreate between ?1 and ?2) and bill.paymentMethods.id = ?3
            """)
    Double totalPriceBillInShift(LocalDateTime timeInShift, LocalDateTime timeOutShift, Integer id);

    //truy vấn theo lọc
    @Query(value = """
           SELECT COALESCE(SUM(CASE WHEN status IN (5, 14,7) THEN total_price - tien_giam ELSE 0 END), 0) AS totalPriceSum,
                    COALESCE (SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END),0) AS slHoaDonTC,
                   COALESCE(SUM(CASE WHEN payment_type = 0 AND status IN (5, 14,7)  THEN total_price - tien_giam ELSE 0 END), 0) AS doanhThuTaiQuay,
                   COALESCE(SUM(CASE WHEN payment_type = 1 AND status IN (5, 14,7)  THEN total_price - tien_giam ELSE 0 END), 0) AS doanhThuOnline,
                     COALESCE (SUM(CASE WHEN status = -1 THEN 1 ELSE 0 END),0) AS slHoaDonBiHuy
            FROM Bill
            WHERE  (:date IS NULL OR CAST(date_create AS DATE) = :date)
                   AND (:month IS NULL OR :year IS NULL
                        OR MONTH(CAST(date_create AS DATE)) = :month
                        AND YEAR(CAST(date_create AS DATE)) = :year)
                   AND (:quarter IS NULL OR :year IS NULL
                        OR DATEPART(QUARTER, CAST(date_create AS DATE)) = :quarter
                        AND YEAR(CAST(date_create AS DATE)) = :year)
                   AND (:year IS NULL OR YEAR(CAST(date_create AS DATE)) = :year)
                   AND (:quarter IS NULL OR :endDate IS NULL OR date_create BETWEEN :startDate AND :endDate)
                   AND (:date IS NOT NULL OR :month IS NOT NULL
                        OR :year IS NOT NULL OR :quarter IS NOT NULL
                        OR :startDate IS NOT NULL OR :endDate IS NOT NULL
                        OR CAST(date_create AS DATE) = CAST(GETDATE() AS DATE)) """, nativeQuery = true)
    List<Object[]> calculateRevenue(
            @Param("date") LocalDate date,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("quarter") Integer quarter,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // truy vấn năm để so sánh biểu đồ
    @Query(value = "WITH Months AS (\n" +
            "    SELECT 1 AS month_number\n" +
            "    UNION ALL\n" +
            "    SELECT 2\n" +
            "    UNION ALL\n" +
            "    SELECT 3\n" +
            "    UNION ALL\n" +
            "    SELECT 4\n" +
            "    UNION ALL\n" +
            "    SELECT 5\n" +
            "    UNION ALL\n" +
            "    SELECT 6\n" +
            "    UNION ALL\n" +
            "    SELECT 7\n" +
            "    UNION ALL\n" +
            "    SELECT 8\n" +
            "    UNION ALL\n" +
            "    SELECT 9\n" +
            "    UNION ALL\n" +
            "    SELECT 10\n" +
            "    UNION ALL\n" +
            "    SELECT 11\n" +
            "    UNION ALL\n" +
            "    SELECT 12\n" +
            ")\n" +
            "SELECT \n" +
            "    COALESCE(SUM(b.total_price - b.tien_giam), 0) AS total_price_sum\n" +
            "FROM Months m\n" +
            "LEFT JOIN [Du_An_Tot_Nghiep_F6].[dbo].[bill] b\n" +
            "    ON MONTH(b.date_create) = m.month_number \n" +
            "       AND b.status IN (5, 14, 7) \n" +
            "       AND YEAR(b.date_create) = ?1\n" +
            "GROUP BY m.month_number\n" +
            "ORDER BY m.month_number;", nativeQuery = true)
    List<Integer> getTotalMoney12Month(Integer year);

    // loc theo thang theo ngay
    @Query(value = "\n" +
            "-- Tạo danh sách các ngày trong tháng\n" +
            "WITH Days AS (\n" +
            "    SELECT CAST(DATEADD(DAY, n.number, DATEFROMPARTS(?2, ?1, 1)) AS DATE) AS [day]\n" +
            "    FROM master.dbo.spt_values n\n" +
            "    WHERE n.type = 'P'\n" +
            "    AND n.number < DAY(EOMONTH(DATEFROMPARTS(?2, ?1, 1)))\n" +
            ")\n" +
            "-- Tính tổng `total_price` cho từng ngày\n" +
            "SELECT \n" +
            "    ISNULL(SUM(b.[total_price] - b.[tien_giam]), 0) AS [total_price_sum]\n" +
            "FROM \n" +
            "    Days d\n" +
            "LEFT JOIN \n" +
            "    [Du_An_Tot_Nghiep_F6].[dbo].[bill] b\n" +
            "ON \n" +
            "    d.[day] = CAST(b.[date_create] AS DATE)\n" +
            "    AND b.[status]  IN (5, 14, 7) \n" +
            "WHERE \n" +
            "    YEAR(d.[day]) = ?2 AND MONTH(d.[day]) = ?1\n" +
            "GROUP BY \n" +
            "    d.[day]\n" +
            "ORDER BY \n" +
            "    d.[day];\n" +
            "\n" +
            "\n", nativeQuery = true)
    List<Integer> getTotalMoneyForMonth(Integer month, Integer year);


    // thống kê tỉ lệ mua hàng 3 loại
    @Query(value = "WITH TotalBills AS (\n" +
            "    SELECT COUNT(*) AS total_count\n" +
            "    FROM [Du_An_Tot_Nghiep_F6].[dbo].[bill]\n" +
            "    WHERE [status] IN (5, 14) \n" +
            ")\n" +
            "SELECT \n" +
            "    CAST(SUM(CASE WHEN [payment_type] = 1 THEN 1 ELSE 0 END) * 100.0 / (SELECT total_count FROM TotalBills) AS DECIMAL(5, 2)) AS online_percentage, -- % hóa đơn online\n" +
            "    CAST(SUM(CASE WHEN [payment_type] = 0 AND [ship_price] = 0 THEN 1 ELSE 0 END) * 100.0 / (SELECT total_count FROM TotalBills) AS DECIMAL(5, 2)) AS store_pickup_percentage, -- % đơn tại quầy\n" +
            "    CAST(SUM(CASE WHEN [payment_type] = 0 AND [ship_price] > 0 THEN 1 ELSE 0 END) * 100.0 / (SELECT total_count FROM TotalBills) AS DECIMAL(5, 2)) AS store_delivery_percentage -- % đơn tại quầy có giao hàng\n" +
            "FROM \n" +
            "    [Du_An_Tot_Nghiep_F6].[dbo].[bill]\n" +
            "WHERE \n" +
            "    [status] IN (5, 14) ", nativeQuery = true)
    List<Object[]> tiLeDonHang();

    // thống kê tỉ lệ thành công và tỉ lệ hủy
    @Query(value = "SELECT \n" +
            "    CAST(ROUND(COUNT(CASE WHEN status IN (5, 14)   THEN 1 END) * 100.0 / COUNT(*), 2) AS DECIMAL(10, 2)) AS percentage_completed,\n" +
            "    CAST(ROUND(COUNT(CASE WHEN status = -1 THEN 1 END) * 100.0 / COUNT(*), 2) AS DECIMAL(10, 2)) AS percentage_canceled\n" +
            "FROM [Du_An_Tot_Nghiep_F6].[dbo].[bill]\n" +
            "WHERE status IN (5, 14, -1);", nativeQuery = true)
    List<Object[]> tiLeTrangThaiDonHang();

    // lay ra top 5 sp
    @Query("""
    SELECT new com.poly.du_an_tot_nghiep_f6.response.TopSPBanChay(
        CONCAT(p.name, ' - ', s.name, ' - ', c.name) AS productDetailName,
        SUM(bd.quantity) AS totalQuantity
    )
    FROM BillDetail bd
    JOIN bd.bill b
    JOIN bd.productDetail pd
    JOIN pd.product p
    LEFT JOIN pd.size s
    LEFT JOIN pd.color c
    WHERE b.status = 5
    GROUP BY p.name, s.name, c.name
    ORDER BY totalQuantity DESC LIMIT 5
""")
    List<TopSPBanChay> findTop5ProductDetails();

    // tiền trả hàng
    @Query(value = """
                SELECT COALESCE(SUM(pay_money), 0)
                FROM trade
                WHERE status = 0
                  AND (:inputDate IS NULL OR CAST(response_date AS DATE) = :inputDate)
                  AND (:inputMonth IS NULL OR :inputYear IS NULL 
                       OR (MONTH(CAST(response_date AS DATE)) = :inputMonth AND YEAR(CAST(response_date AS DATE)) = :inputYear))
                  AND (:inputQuarter IS NULL OR :inputYear IS NULL 
                       OR (DATEPART(QUARTER, CAST(response_date AS DATE)) = :inputQuarter AND YEAR(CAST(response_date AS DATE)) = :inputYear))
                  AND (:inputYear IS NULL OR YEAR(CAST(response_date AS DATE)) = :inputYear)
                  AND (:startDate IS NULL OR :endDate IS NULL OR response_date BETWEEN :startDate AND :endDate)
                  AND (:inputDate IS NOT NULL OR :inputMonth IS NOT NULL 
                       OR :inputYear IS NOT NULL OR :inputQuarter IS NOT NULL 
                       OR :startDate IS NOT NULL OR :endDate IS NOT NULL
                       OR CAST(response_date AS DATE) = CAST(GETDATE() AS DATE))
            """, nativeQuery = true)
    BigDecimal tienTraHang(
            @Param("inputDate") LocalDate inputDate,
            @Param("inputMonth") Integer inputMonth,
            @Param("inputYear") Integer inputYear,
            @Param("inputQuarter") Integer inputQuarter,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query(value = "select new CustomerBillResponse(b.id, img.url1, p.name, s.name, c.name,bd.quantity,bd.price, b.totalPrice, b.status, b.shipPrice, bd.oldPrice, b.tienGiam) " +
            "from " +
            "Bill b join BillDetail bd on b.id = bd.bill.id " +
            "join ProductDetail pd on bd.productDetail.id = pd.id " +
            "join Image img on pd.image.id = img.id " +
            "join Product p on pd.product.id = p.id " +
            "join Size s on pd.size.id = s.id " +
            "join Color c on pd.color.id = c.id " +
            "where b.status = :idSatus and b.customer.id = :idCustomer ")
    List<CustomerBillResponse> findAllCustomerBillResponse(@Param("idSatus") Integer status, @Param("idCustomer") Integer idCustomer);


    @Query(value ="select new com.poly.du_an_tot_nghiep_f6.response.BillOnlineResponse(b.id, b.status, ad.nameRecipient, " +
            "ad.phone,concat(ad.addreseDetail, ', ', ad.province, ', ', ad.district, ', ', ad.ward), b.totalPrice, " +
            "b.shipPrice, vc.discount, pm.name, b.tienGiam ) " +
            "from " +
            "Bill b " +
            "join Address ad on b.address.id = ad.id " +
            "left join Voucher vc on b.voucher.id = vc.id " +
            "join PaymentMethods pm on b.paymentMethods.id = pm.id " +
            "where b.customer.id = :idCustomer and b.id = :idBill")
    BillOnlineResponse getBillOnlineByCustomerAndId(Integer idCustomer, Integer idBill);

    @Query(value ="select new com.poly.du_an_tot_nghiep_f6.response.BillOnlineResponse(b.id, b.status, ad.nameRecipient, " +
            "ad.phone,concat(ad.addreseDetail, ', ', ad.province, ', ', ad.district, ', ', ad.ward), b.totalPrice, " +
            "b.shipPrice, vc.discount, pm.name, b.tienGiam ) " +
            "from " +
            "Bill b " +
            "left join Address ad on b.address.id = ad.id " +
            "left join Voucher vc on b.voucher.id = vc.id " +
            "join PaymentMethods pm on b.paymentMethods.id = pm.id " +
            "where b.id = :idBill")
    BillOnlineResponse getBillOnlineById(Integer idBill);


    @Query(value = "select new  com.poly.du_an_tot_nghiep_f6.response.BillDetailOnlineResponse(bd.id, pd.id, concat('[', s.name, ' + ', c.name, '] '), p.name, bd.quantity, img.url1, bd.price, bd.oldPrice) " +
            "from " +
            "BillDetail bd " +
            "join ProductDetail pd on bd.productDetail.id = pd.id " +
            "join Product p on pd.product.id = p.id " +
            "join Size s on pd.size.id = s.id " +
            "join Color c on pd.color.id = c.id " +
            "join Image img on pd.image.id = img.id " +
            "where bd.bill.id = :idBill")
    List<BillDetailOnlineResponse> getBillDetailOnlineById(Integer idBill);



//    @Query(value = "WITH Days AS (\n" +
//            "    SELECT CAST(DATEADD(DAY, n.number, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0)) AS DATE) AS [day]\n" +
//            "    FROM master.dbo.spt_values n\n" +
//            "    WHERE n.type = 'P'\n" +
//            "    AND n.number < DAY(EOMONTH(GETDATE()))\n" +
//            ")\n" +
//            "-- Tính tổng `total_price` cho từng ngày\n" +
//            "SELECT \n" +
//            "    ISNULL(SUM(b.[total_price]), 0) AS [total_price_sum]\n" +
//            "FROM \n" +
//            "    Days d\n" +
//            "LEFT JOIN \n" +
//            "    [Du_An_Tot_Nghiep_F6].[dbo].[bill] b\n" +
//            "ON \n" +
//            "    d.[day] = CONVERT(DATE, b.[date_create])\n" +
//            "    AND b.[status] = 5\n" +
//            "GROUP BY \n" +
//            "    d.[day]\n" +
//            "ORDER BY \n" +
//            "    d.[day];", nativeQuery = true)
//    List<Integer> getTotalMoneyForMonth();
}

