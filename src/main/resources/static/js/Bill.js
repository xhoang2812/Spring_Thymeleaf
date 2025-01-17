// Hàm trả về tên status dựa trên mã trạng thái
let statusSearch = 10;

const {jsPDF} = window.jspdf;
selectLastDays(0)
getBills(11);
const getStatusLabel = (status) => {
    switch (status) {
        case -3:
            return "Lỗi vận chuyển"
        case -2:
            return "Đặt hàng"
        case -1:
            return "Hủy"
        case 0:
            return "Chờ xác nhận";
        case 1:
            return "Chờ đóng gói";
        case 2:
            return "Chờ vận chuyển";
        case 3:
            return "Đang vận chuyển";
        case 4:
            return "Đã nhận hàng";
        case 5:
            return "Hoàn tất";
        case 6:
            return "Trả hàng";
        case 7:
            return "Chờ hoàn tiền cho khách";
        case 14:
            return "Đã trả hàng";
        default:
            return "Không xác định";
    }
};

const getTimeLei = (status) => {
    switch (status) {
        case 0:
            return "Xác nhận";
        case 1:
            return "Đã đóng gói";
        case 2:
            return "Đã vận chuyển";
        case 3:
            return "Đã nhận hàng";
        case 4:
            return "Hoàn thành đơn";
        case 5:
            return "Trả hàng";
        case 6:
            return "Xem chi tiết";
        case 7:
            return "Hoàn tiền";
        case 14:
            return "Xem chi tiết";
        default:
            return "Không xác định";
    }
};

// Hàm trả về màu sắc cho mỗi trạng thái
const getStatusColor = (status) => {
    switch (status) {
        case -3:
            return '#144228'
        case -2:
            return '#2a352f'
        case -1:
            return '#D32F2F'
        case 0:
            return '#F57C00'; // Chờ xác nhận
        case 1:
            return '#FFB300'; // Đã xác nhận
        case 2:
            return '#1976D2'; // Đã đóng gói
        case 3:
            return '#1565C0'; // Đã vận chuyển
        case 4:
            return '#388E3C'; // Đã nhận hàng
        case 5:
            return '#1B5E20'; // Xác nhận nhận hàng
        case 6:
            return '#424242'; // Hoàn tất
        default:
            return '#424242'; // Mặc định
    }
};

function renderBill(data) {
    const orderSummaryContainer = document.querySelector(".col-md-4.order-summary");

    if (data.length === 0) { // Kiểm tra mảng rỗng
        orderSummaryContainer.innerHTML = `
        <p class="no-bill-selected" style="text-align: center; font-size: 18px; color: gray;text-align: center;">
            Không có hóa đơn
        </p>`;
    } else {
        orderSummaryContainer.innerHTML = ""; // Xóa nội dung nếu có dữ liệu
    }


    // Hàm định dạng giá trị tiền
    const formatPrice = (price) =>
        new Intl.NumberFormat("vi-VN", {style: "currency", currency: "VND"}).format(price);
    // Xử lý mỗi hóa đơn
    data.forEach(bill => {
        // Hàm trả về icon thanh toán dựa trên paymentType
        const paymentIcon = () => {
            if (bill.paymentType) {
                return '<i class=\'fas fa-globe-asia\'></i>'
            } else {
                return '<i class=\'fas fa-home\'></i>'
            }
        };
        const billHTML = `
                    <div class="order-item order-list" data-order="OFF${bill.id}" onclick="showBill(${bill.id}, ${bill.status}, '${formatDate(bill.dateCreate)}', '${bill.nameCustomer}', ${bill.totalPrice}, ${bill.reducedPrice || 0}, ${bill.shipPrice || 0},${bill.paymentMethodId},'${bill.addressDetail}',${bill.idCustomer},'${bill.recipientName}','${bill.recipientPhone}','${bill.descriptionBill}','${bill.descriptionShip}')">
                        <div style="display: block">
                            <p class="idBill" style="float: right;">
                                Hóa đơn #${bill.id} - 
                                ${paymentIcon()}
                            </p>
                            <p class="statusBill" style="float: left;">
                                <span class="status-label" style=" background-color: ${getStatusColor(bill.status)}">${getStatusLabel(bill.status)}</span>
                            </p>
                        </div>
                        ${bill.paymentType ?
            ` <p class="employeeName">Đơn hàng trực tuyến</p>` :
            ` <p class="employeeName">NV: ${bill.nameEmployee || "Không tồn tại"}</p>`}
                       
                        <p class="timePuy">Thời gian: ${formatDate(bill.dateCreate)}</p>
                        <p class="timePuy" style="float: right">${bill.shipPrice !== 0.0 || bill.shipPrice == null ? 'Đơn hàng vận chuyển\n' +
            '<i class=\'fas fa-shipping-fast\'></i>' : ""}</p>
                        <div style="display: flex">
                            <p class="nameCustomer">
                                <span class="customer-name">${bill.nameCustomer}</span>
                            </p>
                            <span class="payPrice">${formatPrice(bill.totalPrice - bill.reducedPrice + bill.shipPrice)}</span>
                        </div>
                    </div>
                `;

        orderSummaryContainer.insertAdjacentHTML("beforeend", billHTML);
    });
}


function getBills(status) {
    let apiKey;
    let keyWord = document.getElementById("keyWord").value;
    let dateStart = document.getElementById("dateStart").value;
    let dateEnd = document.getElementById("dateEnd").value;
    let minPrice = document.getElementById("minPrice").value;
    let maxPrice = document.getElementById("maxPrice").value;
    let sort = document.getElementById("btnradio11").checked;
    console.log("check: " + sort)
    if (status === 11) {
        status = statusSearch;
    }
    apiKey = "http://localhost:8080/api/bill/get-filter?keyWord=" + keyWord + "&dateStart=" + dateStart + "&dateEnd=" + dateEnd + "&status=" + status + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice + "&sort=" + sort;
    statusSearch = status;
    fetch(apiKey)
        .then(response => response.json())
        .then(data => {
            renderBill(data)
        })
        .catch(error => {
            console.error("Lỗi khi gọi API:", error);
        });
}


function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getHours()}:${date.getMinutes()} ${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
}


let id0, status0, dateCreate0, nameCustomer0, totalPrice0, reducedPrice0, shipPrice0, paymentMethodId0, addressDetail0,
    idCustomer0, recipientName0, recipientPhone0, describeCustomerBill0, describeCustomerAddress0;

function showBill(id, status, dateCreate, nameCustomer, totalPrice, reducedPrice, shipPrice, paymentMethodId, addressDetail, idCustomer, recipientName, recipientPhone, describeCustomerBill, describeCustomerAddress) {
    id0 = id;
    status0 = status;
    dateCreate0 = dateCreate;
    nameCustomer0 = nameCustomer;
    totalPrice0 = totalPrice;
    reducedPrice0 = reducedPrice;
    shipPrice0 = shipPrice;
    paymentMethodId0 = paymentMethodId;
    addressDetail0 = addressDetail;
    idCustomer0 = idCustomer;
    recipientName0 = recipientName;
    recipientPhone0 = recipientPhone;
    describeCustomerBill0 = describeCustomerBill;
    describeCustomerAddress0 = describeCustomerAddress;
    console.log("Lấy được data show")
    fetch(`http://localhost:8080/api/bill/get-product-in-bill?idBill=${id}`)
        .then(response => response.json())
        .then(products => {
            // Render thông tin hóa đơn
            const orderDetailsContainer = document.querySelector(".col-md-8.order-details");
            const formatPrice = (price) =>
                new Intl.NumberFormat("vi-VN", {style: "currency", currency: "VND"}).format(price);

            const productRows = products.map((product, index) => `
                <tr>
                    <td>${index + 1}</td>
                    <td><a href="http://localhost:8080/san-pham-chi-tiet/detail/${product.idProductDetail}" target="_blank">${product.nameProduct}</a></td>
                    <td>${product.quantity}</td>
                    <td>${product.oldPrice === 0
                ? `${product.price.toLocaleString()} VNĐ`
                : `${product.price.toLocaleString()} <del style="color: red;font-weight: bold;" class="discount-price">${product.oldPrice.toLocaleString()}</del> VNĐ`}
                    </td>
                    <td>${formatPrice(product.intoMoney)}</td>
                </tr>
            `).join("");
            let clickDone = "";
            if (status0 !== -1 && status0 !== -3) {
                clickDone = `<button style="font-size: 23px;font-weight: bold;color: white; background-color: ${getStatusColor(status0 + 1)}" class="btn confirm-btn" onclick="handleDoneStatus()">
                                ${getTimeLei(status0)}
                            </button>`;
            }

            orderDetailsContainer.innerHTML = `
                <div class="order-summary-details">
                    <div>
                    <h5 class="order-title" style="float: right">
                    <div>
                        <button class="btn btn-white" onclick="exportToPDF()"><i class='fas fa-print' ></i> In</button> Hóa đơn <span class="order-id">#${id}</span>
                    </div>
                        <button onclick="cannelBill(true)" style="font-size: 12px;float: right;margin-top: 10px; color: white; background-color: #D32F2F;
                        display: ${status0 === 0 || status0 === 1 || status0 === 2 ? "block" : "none"}" class="btn cannel-bill">
                            Hủy đơn
                        </button>
                        <a  onclick="editbill(id0)" style="font-size: 12px;width:80px; float: right;margin-right:10px;margin-top: 10px; color: white; background-color: #ffb31f;
                        display: ${(status0 === 1 || status0 === 2) ? "block" : "none"}" class="btn cannel-bill">
                            Sửa đơn
                        </a>
                        <a onclick="errorShip()" style="font-size: 12px;float: right;margin-top: 10px; color: #fafafa; background-color: #D32F2F;
                        display: ${status0 === 3 ? "block" : "none"}" class="btn cannel-bill">
                            Lỗi quá trình vận chuyển
                        </a><br>
                        <a onclick="cannelBill(false)" style="font-size: 12px;float: right;margin-top: 10px; color: #fafafa; background-color: #000000;
                        display: ${status0 === 3 ? "block" : "none"}" class="btn cannel-bill">
                            Khách không nhận hàng
                        </a>
                    </div>
                    <div style="float: left;text-align: left">
                    <p class="statusBill" ><span class="status-label" style="background-color: ${getStatusColor(status0)};">${getStatusLabel(status0)}</span>
                    <button type="button" class="btn btn-white" id="btn-tradeInfo" style="font-size: 13px;" onclick="search('1')">
                      Chi tiết trả hàng <i class="fa-regular fa-file"></i></i>
                    </button>
                    <button type="button" class="btn btn-white" style="font-size: 13px" data-bs-toggle="modal" data-bs-target="#historyModal">
                      Lịch sử <i class='fas fa-history'></i>
                    </button>
                      
                     </p>
                        <p class="timePuy">
                            Thời gian: <span class="date-text">${dateCreate}</span>
                        </p>
                        ${idCustomer !== null ?
                `<a href="/customer/edit/${idCustomer}" target="_blank"  class="nameCustomer">
                            Khách hàng: <span class="customer-name">${nameCustomer + " - ID:" + idCustomer}</span>
                        </a>` :
                `<p class="nameCustomer">
                            Khách hàng: <span class="customer-name">Khách lẻ</span>
                        </p>`
            }
                        
                    </div>
                    </h5><br>
                    <div style="margin-top: 105px; overflow-y: auto;height: 235px; ">
                    <table class="table" style="border: black;border-radius: initial;">
                        <thead style="position: sticky; top: 0; ">
                            <tr>
                                <th style="width: 20px">#</th>
                                <th>Sản phẩm</th>
                                <th>Số lượng</th>
                                <th>Đơn giá</th>
                                <th>Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody >
                            ${productRows}
                        </tbody>
                    </table>
                    </div>
                    <div class="order-totals" id="bill-info" style="text-align: right; float: right; width: 400px">
                        <p class="total">
                            <span style="float: left;">Tổng:</span>
                            <span class="total-amount">${formatPrice(totalPrice)}</span>
                        </p>
                        <p class="voucher">
                            <span style="float: left;">Giảm:</span>
                            <span class="discount">- ${formatPrice(reducedPrice)}</span>
                        </p>
                        <div style="display: flex; justify-content: space-between;">
                            <span>Giao hàng:</span>
                            <span class="shipping-fee">${formatPrice(shipPrice)}</span>
                        </div>
                        
                        <p class="final">
                            <span style="font-size: 20px" class="final-amount">${formatPrice(totalPrice - reducedPrice + shipPrice)}</span>
                        </p>
                        <p class="timePuy" id="paymentInfo" style="float: right"></p>
                    </div>
                    
                    ${addressDetail === '' ? "" : `
                    <div>
                    Thông tin giao hàng:<br>
                    <span>Người nhận:        ${recipientName !== 'null' ? recipientName : ''}</span><br>
                    <span>Số điện thoại:     ${recipientPhone !== 'null' ? recipientPhone : ''}</span><br>
                    <span>Địa chỉ giao hàng:<br> ${addressDetail !== 'null' ? addressDetail : ''}</span><br>
                    <span>Ghi chú giao hàng:<br> ${describeCustomerAddress !== 'null' ? describeCustomerAddress : ''}</span>
                    </div>`}
                    <span>Lưu ý của đơn hàng:<br> ${describeCustomerBill !== 'null' ? describeCustomerBill : ''}</span>
                            ${clickDone}
                </div>
            `;
            const paymentInfo = document.getElementById("paymentInfo");

            if (paymentMethodId === 1) {
                paymentInfo.innerHTML = 'Thanh toán tiền mặt <i class="far fa-money-bill-alt"></i>';
            } else if (paymentMethodId === 2) {
                paymentInfo.innerHTML = 'Thanh toán chuyển khoản <i class="fa fa-qrcode"></i>';
            } else {
                paymentInfo.innerHTML = 'Thanh toán VN Pay <i class="far fa-credit-card"></i>';
            }
            if (status !== 7) {
                document.getElementById("btn-tradeInfo").style.display = 'none';
            }
            if (status === 7 || status === 14) {
                reloadBillInfo(id0, paymentMethodId)
            }
        })

        .catch(error => {
            console.error("Lỗi khi gọi API:", error);
        });
}


function handleDoneStatus() {
    if (status0 === 5 || status0 === 6) {
        search('2');
    } else if (status0 === 7) {
        setSessionTrade();
    } else if (status0 === 14) {
        setSessionBill();
    } else {
        updateStatus()
    }
}

function updateStatus() {
    Swal.fire({
        input: "textarea",
        inputLabel: "Mô tả",
        inputPlaceholder: "Mô tả trạng thái",
        inputAttributes: {
            "aria-label": "Type your message here"
        },
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Ok'
    }).then((result) => {
        if (result.isConfirmed) {
            let description = result.value;  // Lấy mô tả người dùng nhập vào
            if (description.length === 0) {
                description = getStatusLabel(status0 + 1);
            }
            // Gọi API để cập nhật trạng thái
            fetch(`http://localhost:8080/api/bill/next_status?idBill=${id0}&description=${encodeURIComponent(description)}&statusOld=${status0}`, {
                method: 'POST'
            })
                .then(response => response.json())
                .then(data => {
                    console.log("data" + data)
                    switch (data) {
                        case -3: {
                            Swal.fire('Thất bại', 'Trạng thái đã được cập nhật vui lòng chọn lại hoá đơn để thao tác!', 'error');
                            getBills(statusSearch);
                            break;
                        }
                        case -2: {
                            Swal.fire('Lỗi', 'Có lỗi xảy ra khi cập nhật số lượng', 'error');
                            break;
                        }
                        case -1: {
                            Swal.fire('Thành công', 'Trạng thái đã được cập nhật!', 'success');
                            getBills(statusSearch);
                            showBill(id0, status0 + 1, dateCreate0, nameCustomer0, totalPrice0, reducedPrice0, shipPrice0, paymentMethodId0, addressDetail0, idCustomer0, recipientName0, recipientPhone0, describeCustomerBill0, describeCustomerAddress0)
                            break;
                        }
                        case -3: {
                            Swal.fire('Thất bại', 'Trạng thái đã được cập nhật vui lòng chọn lại hoá đơn để thao tác!', 'error');
                            getBills(statusSearch);
                            break;
                        }
                        case 0: {
                            Swal.fire('Lỗi', 'Có lỗi xảy ra khi cập nhật trạng thái', 'error');
                            break;
                        }
                        default: {
                            Swal.fire('Kho chưa đủ', 'Sản phẩm ' + data + ' chưa đủ số lượng!', 'error');
                        }


                    }
                })
                .catch(error => {
                    Swal.fire('Lỗi', 'Không thể kết nối với server', 'error');
                    console.error(error);
                });
        }
    });
}

function editbill(id) {
    fetch(`http://localhost:8080/api/sale/edit-bill?idBill=${id}&statusOld=${status0}`, {
        method: 'PUT'
    })
        .then(response => response.json())
        .then(data => {
            if (data === -3) {
                Swal.fire('Thất bại', 'Trạng thái đã được cập nhật vui lòng chọn lại hoá đơn để thao tác!', 'error');
                getBills(statusSearch);
            } else if (data !== 0) {
                Swal.fire({
                    title: "Hoàn thành hóa đơn #" + data + " trước khi sửa",
                    text: "Khách hàng đang có hóa đơn #" + data + " chưa hoàn thành!",
                    icon: 'warning',
                    showCancelButton: true,       // Hiển thị nút "Hủy"
                    confirmButtonText: 'OK', // Nút xác nhận đăng xuất
                    cancelButtonText: 'Hủy'        // Nút hủy
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.open('http://localhost:8080/sale')
                    }
                });
            } else {
                window.open('http://localhost:8080/sale')
            }
        })
        .catch(error => {
            console.error(error);
        });
}

function cannelBill(status) {
    Swal.fire({
        input: "textarea",
        inputLabel: "Lý do " + (status ? 'hủy đơn' : 'không nhận hàng'),
        inputPlaceholder: "Mô tả lý do",
        inputAttributes: {
            "aria-label": "Type your message here"
        },
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Ok'
    }).then((result) => {
        if (result.isConfirmed) {
            const description = result.value;  // Lấy mô tả người dùng nhập vào
            if (description.length === 0) {
                cannelBill(status);
                return;
            }
            // Gọi API để cập nhật trạng thái
            fetch(`http://localhost:8080/api/bill/cancel_bill?idBill=${id0}&description=${encodeURIComponent(description)}&statusOld=${status0}`, {
                method: 'PUT'
            })
                .then(response => response.json())
                .then(data => {
                    if (data === 0) {
                        Swal.fire('Thành công', 'Đã hủy đơn hàng', 'success');
                        getBills(statusSearch);
                        showBill(id0, -1, dateCreate0, nameCustomer0, totalPrice0, reducedPrice0, shipPrice0, paymentMethodId0, addressDetail0, idCustomer0, recipientName0, recipientPhone0, describeCustomerBill0, describeCustomerAddress0)
                    } else if (data === -3) {
                        Swal.fire('Thất bại', 'Trạng thái đã được cập nhật vui lòng chọn lại hoá đơn để thao tác!', 'error');
                        getBills(statusSearch);

                    } else {
                        Swal.fire('Lỗi', 'Có lỗi xảy ra khi cập nhật trạng thái', 'error');
                    }
                })
                .catch(error => {
                    Swal.fire('Lỗi', 'Không thể kết nối với server', 'error');
                    console.error(error);
                });
        }
    });
}

function errorShip() {
    Swal.fire({
        input: "textarea",
        inputLabel: "Lý do vận chuyển lỗi",
        inputPlaceholder: "Mô tả lý do",
        inputAttributes: {
            "aria-label": "Type your message here"
        },
        showCancelButton: true,
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Gửi lại đơn hàng'
    }).then((result) => {
        if (result.isConfirmed) {
            const description = result.value;  // Lấy mô tả người dùng nhập vào
            if (description.length === 0) {
                errorShip();
                return;
            }
            // Gọi API để cập nhật trạng thái
            fetch(`http://localhost:8080/api/bill/error_ship?idBill=${id0}&description=${encodeURIComponent(description)}&statusOld=${status0}`, {
                method: 'PUT'
            })
                .then(response => response.json())
                .then(data => {
                    switch (data) {
                        case -3: {
                            Swal.fire('Thất bại', 'Trạng thái đã được cập nhật vui lòng chọn lại hoá đơn để thao tác!', 'error');
                            getBills(statusSearch);
                            break;
                        }
                        case -1: {
                            Swal.fire('Thành công', 'Đã tiến hành trừ số lượng', 'success');
                            getBills(statusSearch);
                            showBill(id0, status0, dateCreate0, nameCustomer0, totalPrice0, reducedPrice0, shipPrice0, paymentMethodId0, addressDetail0, idCustomer0, recipientName0, recipientPhone0, describeCustomerBill0, describeCustomerAddress0)
                            break;
                        }
                        case 0: {
                            Swal.fire('Lỗi', 'Có lỗi xảy ra khi cập nhật trạng thái', 'error');
                            break;
                        }
                        case -2: {
                            Swal.fire('Lỗi', 'Có lỗi xảy ra khi cập nhật trạng thái', 'error');
                            break;
                        }
                        default: {
                            Swal.fire('Số lượng không đủ', 'Sản phẩm số ' + data + ' không đủ số lượng', 'error');
                            break;
                        }
                    }
                })
                .catch(error => {
                    Swal.fire('Lỗi', 'Không thể kết nối với server', 'error');
                    console.error(error);
                });
        }
    });
}

function search(status) {
    $.ajax({
        url: "/api/returnProduct/bill/" + id0,
        method: "GET",
        success: function (data) {
            if (data === null) {
                showToast("Hoá đơn không tồn tại !", "warning");
            } else {
                if (data.id === -1) {
                    showToast("Hoá đơn đã áp dụng voucher nên không thể trả hàng !", "warning");
                } else if (data.id === -2) {
                    showToast("Hoá đơn đang trong giai đoạn xử lý khác nên không thể trả hàng !", "warning");
                } else if (data.id === -3) {
                    showToast("Tất cả sản phẩm của hoá đơn đều mua trong đợt giảm giá không thể trả hàng !", "warning");
                } else if (data.id === -4) {
                    showToast("Hoá đơn đã quá 7 ngày kể từ ngày hoàn tất không thể trả hàng !", "warning");
                } else {
                    if (status === "2") {
                        sessionStorage.setItem("bill", JSON.stringify(data));
                        window.open('http://localhost:8080/returnProduct/billReturn', '_blank');
                    } else {
                        sessionStorage.setItem("bill", JSON.stringify(data));
                        window.open('http://localhost:8080/returnProduct/returnProductDetail', '_blank');
                    }

                    console.log("Đã lưu data:" + data)
                }
            }
        },
        error: function (error) {
            console.log(error)
        }
    })
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

// Hàm định dạng ngày giờ thành chuỗi yyyy-MM-ddTHH:mm (giờ địa phương)
function formatDateTimeLocal(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    // Trả về định dạng yyyy-MM-ddTHH:mm
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function selectDate() {
    switch (document.getElementById('select-date').value) {
        case "0":
            selectLastDays(0);
            break
        case "1":
            selectLastDays(1);
            break
        case "2":
            selectLastDays(7);
            break
        case "3":
            selectLastDays(30);
            break
        case "4":
            selectLastDays(90);
            break
    }
}

function selectLastDays(a) {
    const now = new Date();
    const startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - a, 0, 0); // a ngày trước, 00:00
    const endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59); // Hôm nay, 23:59

    document.getElementById("dateStart").value = formatDateTimeLocal(startDate);
    document.getElementById("dateEnd").value = formatDateTimeLocal(endDate);
    getBills(11);
}


// Function to fetch data from the API and render it in the table
async function loadHistoryData() {
    try {
        const response = await fetch(`http://localhost:8080/api/bill/get-history-bill?idBill=${id0}`);
        const data = await response.json();

        // Get table body where we want to render the data
        const tableBody = document.getElementById('historyTableBody');

        // Clear any existing rows in the table
        tableBody.innerHTML = '';

        // Loop through the data and add rows to the table
        data.forEach((item, index) => {
            const row = document.createElement('tr');

            // Create each cell and append to the row
            row.innerHTML = `
        <td>${index + 1}</td>
        <td>${item.nameEmployee}</td>
        <td>${new Date(item.dataUpdate).toLocaleString()}</td>
        <td><p class="statusBill" ><span class="status-label" style="background-color: ${getStatusColor(item.statusOld)};">${getStatusLabel(item.statusOld)}</span></td>
        <td><p class="statusBill" ><span class="status-label" style="background-color: ${getStatusColor(item.statusNew)};">${getStatusLabel(item.statusNew)}</span></td>
        <td>${item.description}</td>
      `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching history data:', error);
    }
}

async function exportToPDF() {
    if (status0 === 14) {
        $.ajax({
            url: "/api/returnProduct/pdf/" + id0,
            method: "GET",
            success: function (returnLists) {
                console.log(returnLists);
                let i = 1;
                let j = 1;
                const bill = returnLists.billDetails[0].bill;
                console.log(bill)
                const pdf = document.createElement('div');
                let billDetails = ``;
                let tradeProductItems = ``;

                returnLists.billDetails.forEach(billDetail => {
                    billDetails += `<tr>
                            <th scope="row">${i++}</th>
                            <td>${billDetail.productDetail.product.name} [ ${billDetail.productDetail.size.name} - ${billDetail.productDetail.color.name} ]</td>
                            <td>${billDetail.price.toLocaleString()}<sup>đ</sup></td>
                            <td>${billDetail.quantity}</td>
                            <td>${billDetail.intoMoney.toLocaleString()} <sup>đ</sup></td>
                        </tr>`;
                })
                returnLists.tradeProductItemResponses.forEach(tradeProductItem => {
                    tradeProductItems += `<tr>
                                <th scope="row">${j++}</th>
                                <td style="white-space: nowrap">${tradeProductItem.productDetail.product.name} [ ${tradeProductItem.productDetail.size.name} - ${tradeProductItem.productDetail.color.name} ]</td>
                                <td style="white-space: nowrap">${tradeProductItem.price.toLocaleString()} <sup>đ</sup></td>
                                <td style="white-space: nowrap">${tradeProductItem.description}</td>
                            </tr>`;
                })

                pdf.innerHTML = `<div class="container-fluid invoice-box row mt-4">
            <div class="col-6">
                <h3 style="font-weight: bold;">F6 SHOP</h3>
            </div>
            <div class="col-6 text-end">
                <p>
                    <strong>Địa chỉ:</strong> 123 Đường ABC, Quận XYZ <br>
                    <strong>Số điện thoại:</strong> 0123456789 <br> <strong>Email:</strong> info@f6shop.com <br>
                    <strong>Website:</strong> www.f6shop.com
                </p>
            </div>
            <div class="col-12 text-center">
                <h5><strong>HOÁ ĐƠN #20</strong></h5>
                <h5 class="mt-2"><strong>CHI TIẾT ĐƠN MUA</strong></h5>
            </div>
            <div class="col-12 d-flex justify-content-between mt-4">
                <div class="col-6">
                    <div class="">
                        <strong style="font-size: 18px;">Người thực hiện</strong>
                    </div>
                    <div class="col-12">
                        <strong>Tên nhân viên:</strong> ${bill.employee.name} <br>
                        <strong>Thời gian:</strong> ${formatDate(bill.dateCreate)} <br>
                    </div>
                </div>
                <div class="col-6">
                    <div class="">
                        <strong style="font-size: 18px;">Thông tin khách hàng</strong>
                    </div>
                    <div class="col-12">
                        <strong>Họ tên:</strong> ${bill.customer === null ? 'Khách lẻ' : bill.customer.name} <br>
                        <strong>Số điện thoại:</strong> ${bill.customer === null ? 'Không có' : bill.customer.phone} <br>
                    </div>
                </div>
            </div>
            <div class="mt-4">
                <table class="table border">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Sản phẩm</th>
                            <th scope="col">Giá</th>
                            <th scope="col">Số lượng</th>
                            <th scope="col">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${billDetails}
                    </tbody>
                </table>
            </div>
            <div class="col-12 d-flex justify-content-between mt-4">
                <div class="col-6">
                    <div class="">
                        <strong style="font-size: 18px;">Địa chỉ nhận hàng</strong>
                    </div>
                    <div class="col-12">
                        ${bill.address === null ? `không có` : `<strong>Tên người nhận:</strong> ${bill.address.nameRecipient} <br>
                        <strong>Số điện thoại:</strong> ${bill.address.phone} <br>
                        <strong>Địa chỉ:</strong> ${bill.address.addreseDetail}, ${bill.address.ward}, ${bill.address.district}, ${bill.address.province} <br>
                        <strong>Ghi chú giao hàng:</strong> ${bill.descriptionShip} <br>
                        <strong>Ghi chú hoá đơn:</strong> ${bill.descriptionBill}`}
                    </div>
                </div>
                <div class="col-4">
                    <div class="col-12 text-center">
                        <strong style="font-size: 18px;">Tổng tiền hoá đơn</strong>
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <strong>Tổng tiền:</strong>
                        </div>
                        <div class="col-6 text-end">
                            <strong>${(returnLists.trade.productDetailMoney).toLocaleString()} <sup>đ</sup></strong>
                        </div>
                        <div class="col-6">
                            <strong>Giảm giá:</strong>
                        </div>
                        <div class="col-6 text-end">
                            <strong>${returnLists.trade.oldVoucherMoney} <sup>đ</sup></strong>
                        </div>
                        <div class="col-8">
                            <strong>Phí vận chuyển:</strong>
                        </div>
                        <div class="col-4 text-end">
                            <strong>${bill.shipPrice.toLocaleString()} <sup>đ</sup></strong>
                        </div>
                        <div class="col-6">
                            <strong>Thanh toán:</strong>
                        </div>
                        <div class="col-6 text-end">
                            <strong>${(returnLists.trade.productDetailMoney + bill.shipPrice).toLocaleString()} <sup>đ</sup></strong>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-12">
                <h5 class="mt-2 col-12 text-center"><strong>CHI TIẾT TRẢ HÀNG <br> ${formatDate(returnLists.tradeProductItemResponses[0].createDate)}</strong></h5>
                <div class="mt-4">
                    <table class="table border">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Sản phẩm</th>
                                <th scope="col">Giá</th>
                                <th scope="col">Lý do trả hàng</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${tradeProductItems}
                        </tbody>
                    </table>
                    <div class="row d-flex justify-content-end mt-4">
                        <div class="col-4">
                            <div class="col-12 text-center">
                                <strong style="font-size: 18px;">Tổng tiền hoá đơn</strong>
                            </div>
                            <div class="row text-start">
                                <div class="col-6">
                                    <strong>Trả hàng:</strong>
                                </div>
                                <div class="col-6 text-end">
                                    <strong>${returnLists.trade.oldProductDetailMoney.toLocaleString()} <sup>đ</sup></strong>
                                </div>
                                <div class="col-6">
                                    <strong>Giảm giá cũ:</strong>
                                </div>
                                <div class="col-6 text-end">
                                    <strong>${returnLists.trade.oldVoucherMoney.toLocaleString()} <sup>đ</sup></strong>
                                </div>
                                <div class="col-8">
                                    <strong>Giảm giá mới:</strong>
                                </div>
                                <div class="col-4 text-end">
                                    <strong>${returnLists.trade.newVoucherMoney.toLocaleString()} <sup>đ</sup></strong>
                                </div>
                                <div class="col-6">
                                    <strong>Hoàn trả:</strong>
                                </div>
                                <div class="col-6 text-end">
                                    <strong>${returnLists.trade.payMoney.toLocaleString()} <sup>đ</sup></strong>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>`;

                let opt = {
                    margin: 0,
                    filename: 'pageContent_' + new Date().getTime() + '.pdf',
                    image: {type: 'jpeg', quality: 0.98},
                    html2canvas: {scale: 2, useCORS: true},  // Sử dụng CORS cho hình ảnh ngoài
                    jsPDF: {unit: 'in', format: 'letter', orientation: 'portrait'}
                };

                // New Promise-based usage:
                html2pdf().set(opt).from(pdf).save();
            },
            error: function (error) {
                console.log(error);
            }
        });
        return;
    }
    const billResponse = await fetch(`http://localhost:8080/api/bill/get-bill?idBill=` + id0);
    const bill = await billResponse.json();

    const productResponse = await fetch(`http://localhost:8080/api/bill/get-product-in-bill?idBill=` + id0);
    const products = await productResponse.json();

    const date = new Date(bill.dateCreate);
    const formattedDate = date.toLocaleString('vi-VN', {
        year: 'numeric', month: 'long', day: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });

    const tableBody = [
        ['Sản phẩm', 'Giá', 'Số lượng', 'Thành tiền'],
        ...products.map(p => [p.nameProduct, p.price.toLocaleString(), p.quantity, p.intoMoney.toLocaleString()])
    ];

    const docDefinition = {
        content: [

            {text: 'F6 SHOP', fontSize: 18, bold: true, alignment: 'left', margin: [0, 0, 0, 10]}, {
                text: [
                    {text: 'Địa chỉ: ', bold: true}, '123 Đường ABC, Quận XYZ\n',
                    {text: 'Số điện thoại: ', bold: true}, '0123456789 | ',
                    {text: 'Email: ', bold: true}, 'info@f6shop.com | ',
                    {text: 'Website: ', bold: true}, 'www.f6shop.com'
                ],
                alignment: 'right',
                fontSize: 10,
                margin: [0, 0, 0, 30]
            },
            {text: `HÓA ĐƠN #${bill.id}`, fontSize: 16, bold: true, alignment: 'center', margin: [0, 0, 0, 20]},

            // Thông tin khách hàng và nhân viên
            {
                columns: [
                    {
                        text: [
                            {text: 'ID Nhân viên: ', bold: true}, `${bill.employee?.id || ''}\n`,
                            {text: 'Nhân viên: ', bold: true}, `${bill.employee?.name || ''}\n`,
                            {text: 'Ngày mua: ', bold: true}, `${formattedDate}\n`,
                            {text: 'Nơi mua: ', bold: true}, `${bill.paymentType ? 'Trực tuyến' : 'Tại cửa hàng'}`
                        ]
                    },
                    {
                        text: [
                            {text: 'ID Khách hàng: ', bold: true}, `${bill.customer?.id || ''}\n`,
                            {text: 'Khách hàng: ', bold: true}, `${bill.customer?.name || 'Khách lẻ'}\n`,
                            {text: 'Số điện thoại: ', bold: true}, `${bill.customer?.phone || ''}`
                        ]
                    }
                ],
                columnGap: 30,
                margin: [0, 0, 0, 10]
            },

            // Bảng sản phẩm
            {
                table: {
                    headerRows: 1,
                    widths: ['*', 'auto', 'auto', 'auto'],
                    body: tableBody
                },
                layout: 'lightHorizontalLines', // Tùy chọn viền nhẹ
                margin: [0, 10, 0, 20]
            }, {
                columns: [
                    {
                        text: [
                            {text: 'Người nhận: ', bold: true}, `${bill.address?.nameRecipient || 'N/A'}\n`,
                            {text: 'Số điện thoại: ', bold: true}, `${bill.address?.phone || 'N/A'}\n`,
                            {text: 'Địa chỉ: ', bold: true},
                            `${bill.address?.addreseDetail || ''}, ${bill.address?.ward || ''}, ${bill.address?.district || ''}, ${bill.address?.province || ''}\n`,
                            {text: 'Ghi chú giao hàng: ', bold: true}, `${bill.descriptionShip || 'N/A'}\n\n`,
                            {text: 'Ghi chú hóa đơn: ', bold: true}, `${bill.descriptionBill || 'N/A'}`
                        ]
                    }
                ],
                margin: [0, 0, 20, 30] // Thêm khoảng cách dưới 20px để cách với phần tiếp theo
            },
// Thông tin thanh toán chia 2 cột 4 hàng không viền
            {
                columns: [
                    [
                        {text: 'Tổng tiền: ', bold: true},
                        {text: 'Giảm giá: ', bold: true},
                        {text: 'Phí vận chuyển: ', bold: true},
                        {text: 'Thanh toán: ', bold: true}
                    ],
                    [
                        {text: `${bill.totalPrice.toLocaleString()} VND`},
                        {text: `-${bill.tienGiam?.toLocaleString() || '0'} VND`},
                        {text: `${bill.shipPrice?.toLocaleString() || '0'} VND`},
                        {text: `${(bill.totalPrice + bill.shipPrice - (bill.tienGiam || 0)).toLocaleString()} VND`}
                    ]
                ],
                columnGap: 50,
                margin: [30, 0, 0, 30]
            }
            // Thông tin shop ở cuối trang

        ]
    };

    pdfMake.createPdf(docDefinition).open();
}

// Trigger the function when the modal is shown
document.getElementById('historyModal').addEventListener('show.bs.modal', function () {
    loadHistoryData();
});


function reloadBillInfo(idBill, paymentMethodId) {
    $.ajax({
        url: "/api/returnProduct/findTradeByBillId/" + idBill,
        method: "GET",
        success: function (data) {
            console.log(data)
            const formatPrice = (price) =>
                new Intl.NumberFormat("vi-VN", {style: "currency", currency: "VND"}).format(price);
            const billInfo = document.getElementById("bill-info");
            billInfo.innerHTML = `<p class="total">
                            <span style="float: left;">Tổng:</span>
                            <span class="total-amount">${formatPrice(data.oldProductDetailMoney)}</span>
                        </p>
                        <div style="display: flex; justify-content: space-between;">
                            <span>Giảm giá trước khi trả:</span>
                            <span class="shipping-fee">${formatPrice(data.oldVoucherMoney)}</span>
                        </div>
                        <p class="voucher">
                            <span style="float: left;">Hoàn lại:</span>
                            <span class="discount">${formatPrice(data.payMoney)}</span>
                        </p>
                        <div style="display: flex; justify-content: space-between;">
                            <span>Giảm giá sau khi trả:</span>
                            <span class="shipping-fee">${formatPrice(data.newVoucherMoney)}</span>
                        </div>
                        <div class="mt-2" style="display: flex; justify-content: space-between;">
                            <span>Giao hàng:</span>
                            <span class="shipping-fee">${formatPrice(data.bill.shipPrice)}</span>
                        </div>
                        
                        <p class="final">
                            <span style="font-size: 20px" class="final-amount">${formatPrice(data.oldProductDetailMoney - data.payMoney + data.bill.shipPrice - data.oldVoucherMoney + data.newVoucherMoney)}</span>
                        </p>
                        <p class="timePuy mb-3" id="paymentInfo" style="float: left; margin-top: -37px"></p>`;
            if (paymentMethodId === 1) {
                paymentInfo.innerHTML = 'Thanh toán tiền mặt <i class="far fa-money-bill-alt"></i>';
            } else if (paymentMethodId === 2) {
                paymentInfo.innerHTML = 'Thanh toán chuyển khoản <i class="fa fa-qrcode"></i>';
            } else {
                paymentInfo.innerHTML = 'Thanh toán VN Pay <i class="far fa-credit-card"></i>';
            }
        }
    })
}

function setSessionTrade() {
    $.ajax({
        url: "/api/returnProduct/" + id0,
        method: "GET",
        success: function (data) {
            if (data === "") {
                showToast("Bạn không phải là Admin", "warning");
                return;
            }
            sessionStorage.setItem('trade', JSON.stringify(data));
            window.location.href = 'returnProduct/returnMoney';
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function setSessionBill() {
    $.ajax({
        url: "/api/returnProduct/getBillById/" + id0,
        method: "GET",
        success: function (data) {
            sessionStorage.setItem('bill', JSON.stringify(data));
            window.location.href = "http://localhost:8080/returnProduct/returnProductDetail";
        }
    })
}


