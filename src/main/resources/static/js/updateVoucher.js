const api = "http://localhost:8080/api";
const data = JSON.parse(sessionStorage.getItem("voucher"));
const div = document.getElementById("listUser"); // Customer Table
const formVoucherPublic = document.getElementById('formVoucher'); // Public
const formVoucherPrivate = document.getElementById('formVoucher1'); // Private
const styleVoucherPercent = document.getElementById('styleVoucher'); // %
const styleVoucherCash = document.getElementById('styleVoucher1'); // VND
const listCustomer = document.getElementById("listCustomer");

async function loading() {
    await dataCustomer();
    console.log(data);
    data.formVoucher === false ? div.style.display = "block" : div.style.display = "none";
    const discount = document.getElementById("quantity");
    const max = document.getElementById("maximumReduction");
    data.formVoucher === false ? discount.readOnly = true : discount.readOnly = false
    data.styleVoucher === false ? max.disabled = true : max.disabled = false
    const form = document.getElementById('myFormUpdate');
    const formData = new FormData(form);
    let dataCheck = {};
    formData.forEach((value, key) => {
        dataCheck[key] = value;
    });

    for (const key in data) {
        if (dataCheck.hasOwnProperty(key)) {
            const input = form.querySelector(`[name="${key}"]`);
            if (key === "dateStart") {
                input.value = formatDateTimeForInput(data.dateStart);
            } else if (key === "dateEnd") {
                input.value = formatDateTimeForInput(data.dateEnd);
            } else if (key === "formVoucher") {
                data[key] === true ? formVoucherPublic.checked = true : formVoucherPrivate.checked = true;
            } else if (key === "styleVoucher") {
                data[key] === true ? styleVoucherPercent.checked = true : styleVoucherCash.checked = true;
            } else {
                console.log(input, data[key])
                input.value = data[key];
            }
        }
    }
    setMaximumReduction();
}

let checkboxStates = {};
function dataCustomer() {
    $.ajax({
        url: api + "/vouchers/customerData",
        type: "GET",
        success: function (dataCustomer) {
            dataCustomer.forEach(item => {
                let customerItem = document.createElement("tr");

                customerItem.innerHTML = `
                            <th scope="row"><input type="checkbox" onclick="setQuantity(${item.id})" class="select-row" data-id="${item.id}"></th>
                            <td>${item.id}</td>
                            <td>${item.name}</td>
                            <td>${item.phone}</td>
                            <td>${item.email}</td>`
                listCustomer.appendChild(customerItem);
            })
            const customerCheckboxes = document.querySelectorAll('#customerTable .select-row');
            customerCheckboxes.forEach(checkbox => {
                const customerId = checkbox.getAttribute('data-id');
                const isSelected = data?.customers?.some(customer => customer?.id == customerId);
                console.log(customerId)
                if (isSelected) {
                    checkbox.checked = true;
                }
                toggleCheckbox(customerId);
            });
            if (data.condition === "Đang diễn ra") {
                // Vô hiệu hóa tất cả các input trong form
                const inputs = document.querySelectorAll('input');
                inputs.forEach(input => {
                    console.log(input);
                    const btnRandom = document.getElementById("btnRandom");
                    const dateEnd = document.getElementById("dateEnd");
                    btnRandom.disabled = true;
                    input.disabled = true;
                    dateEnd.disabled = false;
                });
            }else if(data.condition === "Đã kết thúc"){
                const inputs = document.querySelectorAll('input');
                inputs.forEach(input => {
                    console.log(input);
                    const btnRandom = document.getElementById("btnRandom");
                    const dateEnd = document.getElementById("dateEnd");
                    btnRandom.disabled = true;
                    input.disabled = true;
                    dateEnd.disabled = true;
                    document.getElementById("saveBtn").disabled = true;
                });
            }
        },
        error: function (error) {
            console.log(error)
        },
        complete : function (){
            // setQuantity(-1);
            $("#customerTable").DataTable({
                pageLength: 6
            });
        }
    })
}


function setQuantity(id){
    if(id !== -1){
        toggleCheckbox(id)
    }
    const checkedCount = Object.values(checkboxStates).filter(state => state).length;
    const quantity =  document.getElementById("quantity");
    quantity.value = checkedCount;
}

function toggleCheckbox(id) {
    const checkbox = document.querySelector(`input[data-id="${id}"]`);
    checkboxStates[id] = checkbox.checked; // Lưu trạng thái
}

function setMaximumReduction(){
    const styleVoucher = document.getElementById("styleVoucher1");
    const maximumReduction = document.getElementById("maximumReduction");
    const discount = document.getElementById("discount");
    if(styleVoucher.checked){
        maximumReduction.value = discount.value;
    }
}

function showPage() {
    const selectedValue = document.querySelector('input[name="formVoucher"]:checked').value; // Radio formVoucher
    const discount = document.getElementById("quantity");
    if(selectedValue === "false"){
        div.style.display = "block"
        discount.disabled = true
    }else {
        div.style.display = "none"
        discount.disabled = false
        $("#customerTable").DataTable().destroy();
    }
}

function randomCode() {
    const code = document.getElementById("code");
    code.value = Math.random().toString(36).slice(2, 8);
}

function getCustomer() {
    const customer = [];
    for (const id in checkboxStates) {
        if (checkboxStates[id]) {
            customer.push({ id: parseInt(id) });
        }
    }
    return customer;
}

function getData() {
    const form = document.getElementById('myFormUpdate');
    const formData = new FormData(form);
    let dataCheck = {};
    formData.forEach((value, key) => {
        dataCheck[key] = value;
    });
    dataCheck['customers'] = getCustomer();
    console.log(dataCheck)
    return dataCheck;
}

function validate(dataCheck) {
    const currentDate = new Date();

    if(dataCheck.quantity > 1000000000 || dataCheck.discount > 1000000000 || dataCheck.minimumOrder > 10000000000 || dataCheck.maximumReduction > 1000000000){
        showToast("Giá trị số quá lớn");
        return "Không được bỏ trống tên mã giảm giá";
    }

    if (dataCheck.name.trim() === "") {
        showToast("Không được bỏ trống tên mã giảm giá");
        return "Không được bỏ trống tên mã giảm giá";
    } else if (dataCheck.code.trim() === "") {
        showToast("Không được bỏ trống mã giảm giá");
        return "Không được bỏ trống mã giảm giá";
    } else if (dataCheck.discount.trim() === "" || dataCheck.discount.trim() <= 0 || !Number.isInteger(+dataCheck.discount.trim())) {
        showToast("Giá trị giảm là số nguyên dương");
        return "Không được bỏ trống giá trị giảm";
    } else if (dataCheck.formVoucher === "true" && (dataCheck.quantity.trim() === "" || dataCheck.quantity.trim() <= 0 || !Number.isInteger(+dataCheck.quantity.trim()))) {
        showToast("Số lượng phải là số nguyên dương");
        return "Không được bỏ trống số lượng";
    } else if (dataCheck.minimumOrder.trim() === "" || dataCheck.minimumOrder < 1000 || !Number.isInteger(+dataCheck.minimumOrder)) {
        showToast("Đơn hàng tối thiểu phải là số nguyên dương và lớn hơn 1000");
        return "Không được bỏ giá giảm tối đa";
    } else if (dataCheck.styleVoucher === "true" && (dataCheck.maximumReduction.trim() === "" || parseFloat(dataCheck.maximumReduction) < 1000 || !Number.isInteger(+dataCheck.maximumReduction))) {
        console.log(dataCheck.styleVoucher);
        showToast("Giá giảm tối đa phải là số nguyên dương và lớn hơn 1000");
        return "Không được bỏ trống giá đơn hàng tối thiểu";
    } else if (dataCheck.dateStart.trim() === "") {
        showToast("Không được bỏ trống ngày bắt đầu");
        return "Không được bỏ trống ngày bắt đầu";
    } else if (dataCheck.dateEnd.trim() === "") {
        showToast("Không được bỏ trống ngày kết thúc");
        return "Không được bỏ trống ngày kết thú";
    }else if(new Date(dataCheck.dateStart) <= currentDate){
        showToast("Ngày bắt đầu phải lớn hơn ngày hiện tại");
        return "Ngày bắt đầu phải lớn hơn ngày hiện tại";
    } else if(new Date(dataCheck.dateEnd) <= new Date(dataCheck.dateStart)){
        showToast("Ngày kết thúc phải lớn hơn ngày bắt đầu");
        return "Ngày kết thúc phải lớn hơn ngày bắt đầu";
    }
    else if (dataCheck.description.trim() === "") {
        showToast("Không được bỏ trống mô tả");
        return "Không được bỏ trống mô tả";
    }
    else if (dataCheck.formVoucher === "false" && dataCheck.customers.length === 0) {
        showToast("Hãy chọn khách hàng");
        return "Hãy chọn khách hàng";
    } else if (dataCheck.styleVoucher === "true" && dataCheck.discount > 100) {
        showToast("Giá trị giảm phải nhỏ hơn 100%");
        return "Giá trị giảm phải nhỏ hơn 100%";
    } else if (dataCheck.styleVoucher === "false" && dataCheck.discount < 1000) {
        showToast("Giá trị giảm phải lớn hơn 1000VND");
        return "Giá trị giảm phải lớn hơn 1000VND";
    }
}

function updateVoucher() {
    const currentDate = new Date();
    const dateEnd = new Date(document.getElementById("dateEnd").value);
    if(data.condition === "Đang diễn ra" && dateEnd >= currentDate){
        Swal.fire({
            title: 'Xác nhận cập nhật voucher',
            html: 'Bạn có chắc chắn muốn cập nhật voucher không ?<br>Lưu ý khi cập nhật voucher sẽ ở trạng thái <strong>kích hoạt</strong> và <strong>gửi email</strong> thông báo đến khách hàng nếu <strong>cần thiết</strong>.',
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy'        // Nút hủy
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: api + "/vouchers/" + data?.id,
                    type: "PUT",
                    contentType: "application/json",
                    data: JSON.stringify(getData()),
                    success: function () {
                        sessionStorage.setItem("message", "Cập nhật thành công !")
                        window.location.href = "http://localhost:8080/voucher";
                    },
                    error: function (error) {
                        console.log(error)
                    }
                })
            }
        });

    }else if(data.condition === "Đang diễn ra" && dateEnd <= currentDate){
        showToast("Ngày kết thúc phải lớn hơn ngày hiện tại");
    }
    else {
        if (validate(getData())) {

        } else {
            Swal.fire({
                title: 'Xác nhận cập nhật voucher',
                html: 'Bạn có chắc chắn muốn cập nhật voucher không ?<br><strong>Lưu ý</strong> : Khi cập nhật voucher sẽ ở trạng thái <strong>kích hoạt</strong> và <strong>gửi email</strong> thông báo đến khách hàng nếu <strong>cần thiết</strong>.',
                icon: 'warning',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: api + "/vouchers/" + data?.id,
                        type: "PUT",
                        contentType: "application/json",
                        data: JSON.stringify(getData()),
                        success: function () {
                            sessionStorage.setItem("message", "Cập nhật thành công !")
                            window.location.href = "http://localhost:8080/voucher";
                        },
                        error: function (error) {
                            console.log(error)
                        }
                    })
                }
            });
        }
    }
}

function formatDateTimeForInput(dateTime) {
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function showToast(message) {
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
        icon: "error",
        title: message
    });
    // const toastContainer = document.getElementById('toastContainer');
    // const newToast = document.createElement('div');
    // newToast.className = 'toast show';
    // const toastId = `toast-${Date.now()}`;
    // newToast.innerHTML = `
    //     <span id="toastMessage">${message}</span>
    //     <div class="closeToast">
    //       <span id="closeToast" class="close">&times;</span>
    //       <div id="countdown">5s</div>
    //     </div>
    //     <div class="progress"></div>
    //   `;
    // toastContainer.appendChild(newToast);
    // const countdown = newToast.querySelector('#countdown');
    // const closeToast = newToast.querySelector('.close');
    // const progress = newToast.querySelector('.progress');
    // progress.style.animation = 'none';
    // setTimeout(() => {
    //     progress.style.animation = '';
    // }, 10);
    // let remainingTime = 5;
    // countdown.textContent = `${remainingTime}s`;
    // const countdownInterval = setInterval(() => {
    //     remainingTime -= 1;
    //     countdown.textContent = `${remainingTime}s`;
    //     if (remainingTime === 0) {
    //         clearInterval(countdownInterval);
    //         toastContainer.removeChild(newToast);
    //     }
    // }, 1000);
    // closeToast.onclick = function () {
    //     clearInterval(countdownInterval);
    //     toastContainer.removeChild(newToast);
    // };
}
function checkStyle(){
    const style = document.getElementById("styleVoucher");
    const max = document.getElementById("maximumReduction");
    max.disabled = !style.checked;
}
function closePage(){
    window.location.href="http://localhost:8080/voucher";
}