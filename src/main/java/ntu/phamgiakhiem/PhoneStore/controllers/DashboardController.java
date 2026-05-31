package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ntu.phamgiakhiem.PhoneStore.repositories.OrderRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.OrderDetailRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.UserRepository;

@Controller
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')") // Chỉ cho phép ADMIN truy cập vào trang thống kê này
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showDashboard(Model model) {
        
        // 1. Lấy số đơn hàng đã hoàn thành
        long completedOrdersCount = orderRepository.countByStatus("DELIVERED");
        
        // 2. Lấy tổng doanh thu (Nếu trống/chưa bán được đơn nào thì gán bằng 0)
        Double totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        
        // 3. Lấy tổng số máy điện thoại đã bán thành công
        Long totalProductsSold = orderDetailRepository.countTotalProductsSold();
        if (totalProductsSold == null) {
            totalProductsSold = 0L;
        }
        
        // 4. Lấy tổng số lượng khách hàng (ROLE_USER)
        long totalCustomersCount = userRepository.countTotalCustomers();

        // Đẩy tất cả dữ liệu ra Model của Thymeleaf
        model.addAttribute("completedOrders", completedOrdersCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("productsSold", totalProductsSold);
        model.addAttribute("totalCustomers", totalCustomersCount);
        
        return "admin/dashboard"; // Trả về giao diện: templates/admin/dashboard.html
    }
}