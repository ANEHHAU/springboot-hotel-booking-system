package com.maitrunghau.hotelbookingsystem.model;

public enum Role {
    Admin("Quản trị viên"),
    Customer("Khách hàng");
    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


//RECEPTIONIST("Lễ tân"),
//HOUSEKEEPING("Dọn phòng"),
//FINANCE("Thu ngân/ Kế toán"),
//SERVICE_MANAGER("Quản lý dịch vụ");  // hoặc STAFF chung

//Admin: người của công ty phần mềm (CMS) quản lý cấu hình hệ thống, tài khoản nhân viên.
//Service Manager: trưởng bộ phận khách sạn — có thể thêm/sửa dịch vụ, quản lý doanh thu từng ngày. quản lý theo từng hotel mà có nó có quyền
//Receptionist: nhân viên lễ tân xử lý đặt phòng và check-in.
//        Customer: khách hàng đặt phòng qua web.
// dọn phòng nhận và duy trì