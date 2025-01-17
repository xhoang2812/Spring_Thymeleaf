const api = "http://localhost:8080/api";
const listVoucher = document.getElementById("listVoucher");
document.addEventListener("DOMContentLoaded", function () {
    loadingPage();
    listenToSSE();

});

function loadingPage() {
    const message = sessionStorage.getItem("message");
    if (message !== null) {
        showToast(message, "success");
        sessionStorage.clear();
    }
    $.ajax({
        url: api + "/vouchers",
        type: "GET",
        success: function (data) {

            generateVoucher(data);
            $("#multi-filter-select").DataTable({
                pageLength: 5,
                order: [[0, 'desc']],

            });
        },
        error: function (error) {
            console.log(error)
        },
        complete: function () {
            let table = $("#multi-filter-select").DataTable();
            // $.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
            //     let min = parseFloat($("#minDiscount").val()) || 0; // Giá trị tối thiểu
            //     let max = parseFloat($("#maxDiscount").val()) || Infinity; // Giá trị tối đa
            //
            //     // Cột "discount" có index là 3 (theo thứ tự cột trong bảng HTML)
            //     let discount = data[3]
            //         .replace(/[^\d.,]/g, '') // Loại bỏ ký tự không phải số, `.` hoặc `,`
            //         .replace(/\./g, '')      // Loại bỏ dấu `.`
            //         .replace(',', '.');      // Chuyển dấu `,` thành dấu `.` nếu cần
            //     discount = parseFloat(discount) || 0;
            //
            //     // Kiểm tra điều kiện lọc
            //     return discount >= min && discount <= max;
            // });


            $.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
                // Lọc theo ngày
                let startDate = $("#startDate").val()
                let endDate = $("#endDate").val()
                console.log("Ngày lọc nhập vào:", startDate, endDate);

                let dateStartStr = data[8]; // Cột "dateStart"
                let dateEndStr = data[9];   // Cột "dateEnd"

                let dateStart = parseDate(dateStartStr);
                let dateEnd = parseDate(dateEndStr);

                console.log("Ngày lọc co san:", dateStart, dateEnd);

                let isDateValid = true;
                // if (filterDate) {
                //     isDateValid = dateStart <= filterDate && dateEnd >= filterDate;
                // }
                // console.log(`Kết quả lọc theo ngày cho dòng ${dataIndex}:`, isDateValid);
                if (startDate === "" && endDate === "") {
                    console.error("Ngày nhập không hợp lệ.");
                }else if(startDate === "" && endDate !== ""){
                    isDateValid = new Date(endDate + "T00:00:00") >= dateEnd;
                }else if(startDate !== "" && endDate === ""){
                    isDateValid = new Date(startDate + "T00:00:00") <= dateStart;
                }
                // Lọc theo discount
                let minDiscount = parseFloat($("#minDiscount").val()) || 0;
                let maxDiscount = parseFloat($("#maxDiscount").val()) || Infinity;

                let discountStr = data[3].replace(/[^0-9]/g, ""); // Lấy discount, loại bỏ ký tự không phải số
                let discount = parseFloat(discountStr) || 0;

                let isDiscountValid = discount >= minDiscount && discount <= maxDiscount;
                console.log(`Kết quả lọc theo discount cho dòng ${dataIndex}:`, isDiscountValid);

                // Kết hợp cả hai điều kiện
                return isDateValid && isDiscountValid;
            });


            // Áp dụng bộ lọc khi bấm nút "Lọc"
            $("#applyFilter").on("click", function () {
                console.log("Bắt đầu áp dụng bộ lọc...");
                table.draw();
                console.log("Bộ lọc đã được áp dụng.");
            });
        }
    })
}

// $.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
//     // Lọc theo ngày
//     let filterDate = $("#filterDate").val() ? new Date($("#filterDate").val()) : null;
//     console.log("Ngày lọc nhập vào:", filterDate);
//
//     let dateStartStr = data[8]; // Cột "dateStart"
//     let dateEndStr = data[9];   // Cột "dateEnd"
//
//     let dateStart = parseDate(dateStartStr);
//     let dateEnd = parseDate(dateEndStr);
//
//     let isDateValid = true;
//     if (filterDate) {
//         isDateValid = dateStart <= filterDate && dateEnd >= filterDate;
//     }
//     console.log(`Kết quả lọc theo ngày cho dòng ${dataIndex}:`, isDateValid);
//
//     // Lọc theo discount
//     let minDiscount = parseFloat($("#minDiscount").val()) || 0;
//     let maxDiscount = parseFloat($("#maxDiscount").val()) || Infinity;
//
//     let discountStr = data[3].replace(/[^0-9]/g, ""); // Lấy discount, loại bỏ ký tự không phải số
//     let discount = parseFloat(discountStr) || 0;
//
//     let isDiscountValid = discount >= minDiscount && discount <= maxDiscount;
//     console.log(`Kết quả lọc theo discount cho dòng ${dataIndex}:`, isDiscountValid);
//
//     // Kết hợp cả hai điều kiện
//     return isDateValid && isDiscountValid;
// });

// Hàm chuyển đổi chuỗi ngày thành đối tượng Date

// Thêm custom filter
function listenToSSE() {
    const eventSource = new EventSource(api + "/stream/vouchers");

    eventSource.addEventListener("voucherUpdate", function (event) {
        const updatedVoucher = JSON.parse(event.data);
        updateVoucherInUI(updatedVoucher);
    });

    eventSource.onerror = function (err) {
        console.error("EventSource failed: ", err);
    };
}

function updateVoucherInUI(voucher) {
    const voucherRow = document.getElementById("row" + voucher.id);
    if (voucherRow) {
        const conditionButton = voucherRow.querySelector("td button");
        // console.log(conditionButton)
        if (conditionButton) {
            conditionButton.innerHTML = voucher.condition;
        }
    }
}

function generateVoucher(data) {
    listVoucher.innerHTML = ``;
    console.log(data);
    data.forEach(item => {
        voucherData(item);
    });
}

function voucherData(item) {
    let voucherItem = document.createElement("tr");
    voucherItem.setAttribute("id", "row" + item.id);
    let date_start = formatDate(item.dateStart);
    let date_end = formatDate(item.dateEnd);
    let formatFormVoucher;
    let statusVoucher;
    if (item.formVoucher === true) {
        formatFormVoucher = "Công khai";
    } else {
        formatFormVoucher = "Riêng tư";
    }
    if (item.status === true) {
        statusVoucher = "Hoạt động";
    } else {
        statusVoucher = "Đã huỷ";
    }
    let discount = "";
    if (item.discount <= 100) {
        discount = item.discount + "%";
    } else {
        discount = parseFloat(item.discount).toLocaleString('vi-VN') + " VND";
    }
    let minimumOrder = parseFloat(item.minimumOrder).toLocaleString('vi-VN') + " VND";
    let maximumReduction = parseFloat(item.maximumReduction).toLocaleString('vi-VN') + " VND";
    voucherItem.innerHTML = `
                                    <td>${item.id}</td>
                                    <td>${item.code}</td>
                                    <td>${item.name}</td>
                                    <td class="value-cell">${discount}</td>
                                    <td class="value-cell">${minimumOrder}</td>
                                    <td class="value-cell">${maximumReduction}</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.quantityUsed}</td>
                                    <td>${date_start}</td>
                                    <td>${date_end}</td>
                                    <td><button id="btn-${item.id}" style="color: white;" class="rounded-pill btn bg-primary">${item.condition}</button></td>
                                    <td><button style="color: white;" class="rounded-pill btn bg-primary">${formatFormVoucher}</button></td>
                                    <td><button style="color: white;" class="rounded-pill btn bg-primary">${statusVoucher}</button></td>
                                   <td>
                                        <button type="button" class="btn btn-success btn-sm px-2" onclick="updateData(${item.id})">
                                            <i class="fa-solid fa-pen-to-square"></i>
                                        </button>
                                        <button onclick="voucherDetail(${item.id})" type="button" class="btn btn-primary btn-sm px-2">
                                           <i class="fa-solid fa-eye"></i>
                                        </button>
                                        <button type="button" id="btnDelete" class="btn btn-danger btn-sm px-2" onclick="deleteVoucher(${item.id}, this)">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </td>
                                    
                                `;
    let tdElements = voucherItem.querySelectorAll('td');

    listVoucher.appendChild(voucherItem);
}

function updateData(idVoucher) {
    $.ajax({
        url: api + "/vouchers/" + idVoucher,
        type: "GET",
        success: function (data) {
            if(data === ""){
                showToast("Bạn không phải Admin", "warning");
                return;
            }
            sessionStorage.setItem("voucher", JSON.stringify(data));
            window.location.href = "http://localhost:8080/updateVoucher";
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function deleteVoucher(idVoucher, button) {
    const btn = document.getElementById("btn-" + idVoucher);
    if (btn.innerText === "Đang diễn ra" || btn.innerText === "Đã kết thúc") {
        showToast("Không thể 'huỷ' hoặc 'kích hoạt' voucher khi đang ở tình trạng 'Đang diễn ra' hoặc 'Đã kết thúc'", "error")
    } else {
        Swal.fire({
            title: 'Xác nhận huỷ voucher',
            html: 'Bạn có chắc chắn muốn huỷ voucher không?<br><strong>Lưu ý</strong> : Hệ thống sẽ <strong>gửi email</strong> thông báo thu hồi đến khách hàng nếu đây là voucher <strong>riêng tư</strong>.',
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy'        // Nút hủy
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: api + "/vouchers/" + idVoucher,
                    type: "DELETE",
                    success: function (item) {
                        if(item === ""){
                            showToast("Bạn không phải Admin", "warning");
                            return;
                        }
                        let voucherItem = document.createElement("tr");
                        voucherItem.setAttribute("id", "row" + item.id);
                        let date_start = formatDate(item.dateStart);
                        let date_end = formatDate(item.dateEnd);
                        let formatFormVoucher;
                        let statusVoucher;
                        if (item.formVoucher === true) {
                            formatFormVoucher = "Công khai";
                        } else {
                            formatFormVoucher = "Riêng tư";
                        }
                        if (item.status === true) {
                            statusVoucher = "Hoạt động";
                            showToast("Đã kích hoạt voucher", "success")
                        } else {
                            statusVoucher = "Đã huỷ";
                            showToast("Đã hủy voucher", "success")
                        }
                        let discount = "";
                        if (item.discount < 100) {
                            discount = item.discount + "%";
                        } else {
                            discount = parseFloat(item.discount).toLocaleString('vi-VN') + " VND";
                        }
                        let quantity = "";
                        if (item.quantity === 1 && item.formVoucher === false) {
                            quantity = "--";
                        } else {
                            quantity = item.quantity
                        }
                        let minimumOrder = parseFloat(item.minimumOrder).toLocaleString('vi-VN') + " VND";
                        let maximumReduction = parseFloat(item.maximumReduction).toLocaleString('vi-VN') + " VND";
                        voucherItem.innerHTML = `
                                    <td>${item.id}</td>
                                    <td>${item.code}</td>
                                    <td>${item.name}</td>
                                    <td class="value-cell">${discount}</td>
                                    <td class="value-cell">${minimumOrder}</td>
                                    <td class="value-cell">${maximumReduction}</td>
                                    <td>${quantity}</td>
                                    <td>0</td>
                                    <td>${date_start}</td>
                                    <td>${date_end}</td>
                                    <td><button style="color: white;" class="rounded-pill btn bg-primary">${item.condition}</button></td>
                                    <td><button style="color: white;" class="rounded-pill btn bg-primary">${formatFormVoucher}</button></td>
                                    <td><button style="color: white;" class="rounded-pill btn bg-primary">${statusVoucher}</button></td>
                                    <td>
                                        <button type="button" class="btn btn-success btn-sm px-2" onclick="updateData(${item.id})">
                                            <i class="fa-solid fa-pen-to-square"></i>
                                        </button>
                                      <button type="button" class="btn btn-primary btn-sm px-2">
                                            <i class="fa-solid fa-eye"></i>
                                       </button>  
                                        <button type="button" id="btn-${item.id}" class="btn btn-danger btn-sm px-2" onclick="deleteVoucher(${item.id}, this)">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </td>
                                `
                        const oldRow = document.getElementById("row" + item.id)
                        console.log(voucherItem, oldRow)
                        const tbody = document.getElementById("listVoucher");
                        tbody.replaceChild(voucherItem, oldRow);
                    },
                    error: function (error) {
                        console.log(error)
                    }
                })
            }
        });
    }
}

function formatDate(inputDate) {
    let date = new Date(inputDate);
    let day = String(date.getDate()).padStart(2, '0');
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let year = date.getFullYear();
    let hours = String(date.getHours()).padStart(2, '0');
    let minutes = String(date.getMinutes()).padStart(2, '0');
    let seconds = String(date.getSeconds()).padStart(2, '0');
    return `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;
}

function createVoucherPage() {
    $.ajax({
        url : api + "/vouchers/userData",
        method : "GET",
        success : function (data){
            if(data === ""){
                showToast("Bạn không phải Admin", "warning");
                return;
            }
            window.location.href = "http://localhost:8080/createVoucher";
        },
        error : function (error){
            console.log(error)
        }
    })
}

function voucherDetail(id) {
    $.ajax({
        url: api + "/vouchers/" + id,
        type: "GET",
        success: function (data) {
            sessionStorage.setItem("voucher", JSON.stringify(data));
            window.location.href = "http://localhost:8080/voucherDetail/" + id;
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function parseDate(dateString) {
    let parts = dateString.split(/[- :]/); // Tách chuỗi theo ký tự `-`, `:`, và khoảng trắng
    // parts = [day, month, year, hour, minute, second]
    return new Date(
        parseInt(parts[2]),    // Year
        parseInt(parts[1]) - 1, // Month (0-based)
        parseInt(parts[0]),    // Day
        parseInt(parts[3] || 0), // Hour (optional)
        parseInt(parts[4] || 0), // Minute (optional)
        parseInt(parts[5] || 0)  // Second (optional)
    );
}


function showToast(message, status) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });
    Toast.fire({
        icon: status,
        title: message
    });
}
