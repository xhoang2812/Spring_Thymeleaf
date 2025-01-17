const api = "http://localhost:8080/api";
const div = document.getElementById("listUser");
const listCustomer = document.getElementById("listCustomer");
function showPage() {
    let selectedValue = document.querySelector('input[name="formVoucher"]:checked').value;
    const discount = document.getElementById("quantity");
    if (selectedValue === "false") {
        div.style.display = "block";
        discount.disabled = true;
        $("#customerTable").DataTable({
            pageLength: 6
        });
    } else {
        div.style.display = "none";
        discount.disabled = false;
        $("#customerTable").DataTable().destroy();
    }
}

let checkboxStates = {};

function dataCustomer(){
    $.ajax({
        url: api + "/vouchers/customerData",
        type: "GET",
        success: function (dataCustomer) {
            console.log(dataCustomer)
            listCustomer.innerHTML=``;
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
        },
        error: function (error) {
            console.log(error)
        },
        complete : function (){

        }
    })
}

function setQuantity(id){
    const checkbox = document.querySelector(`input[data-id="${id}"]`);
    checkboxStates[id] = checkbox.checked;
    const checkedCount = Object.values(checkboxStates).filter(state => state).length;
    const quantity =  document.getElementById("quantity");
    quantity.value = checkedCount;
}

function setMaximumReduction(){
    const styleVoucher = document.getElementById("styleVoucher1");
    const maximumReduction = document.getElementById("maximumReduction");
    const discount = document.getElementById("discount");
    if(styleVoucher.checked){
        maximumReduction.value = discount.value;
    }
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
    const form = document.getElementById('myForm');
    const formData = new FormData(form);
    let dataCheck = {};
    formData.forEach((value, key) => {
        dataCheck[key] = value;
    });
    dataCheck['customers'] = getCustomer();
    console.log(dataCheck)
    return dataCheck;
}

function randomCode(e) {
    e.preventDefault();
    const code = document.getElementById("code");
    code.value = Math.random().toString(36).slice(2, 8);
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
    else if (dataCheck.formVoucher === "true") {
        dataCheck.customers = [];
    }
}

function createVoucher() {
    if (validate(getData())) {

    } else {
        Swal.fire({
            title: 'Xác nhận cập nhật voucher',
            html: 'Bạn có chắc chắn thêm cập nhật voucher không ?<br><strong>Lưu ý</strong> : Hệ thống sẽ <strong>gửi email</strong> thông báo nhận được voucher đến khách hàng nếu đây là voucher <strong>riêng tư</strong>.',
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy'        // Nút hủy
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: api + "/vouchers",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(getData()),
                    success: function () {
                        sessionStorage.setItem("message","Thêm thành công !")
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
}
function checkStyle(){
    const style = document.getElementById("styleVoucher");
    const max = document.getElementById("maximumReduction");
    setMaximumReduction();
    max.disabled = !style.checked;
}
function closePage(){
    window.location.href="http://localhost:8080/voucher";
}

