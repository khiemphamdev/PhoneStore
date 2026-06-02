package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Order;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;
import ntu.phamgiakhiem.PhoneStore.services.OrderService;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')") // Khóa toàn bộ các chức năng, chỉ cho phép quản trị viên truy cập
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. Hiển thị danh sách tất cả đơn hàng (Có hỗ trợ lọc theo trạng thái nếu truyền status)
    @GetMapping
    public String listOrders(@RequestParam(value = "status", required = false) String status, Model model) {
        List<Order> orders;
        
        // Nếu người dùng chọn lọc theo một trạng thái cụ thể (ví dụ: PENDING, DELIVERED)
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            // Cần bổ sung hàm findByStatus trong OrderService nếu muốn gọi trực tiếp,
            // hoặc tận dụng gọi từ Repository. Ở đây mặc định lấy hết hoặc bạn có thể tối ưu lọc.
            orders = orderService.getAllOrders().stream()
            		.filter(o -> !o.getStatus().equalsIgnoreCase("CART"))
                    .filter(o -> o.getStatus().equalsIgnoreCase(status.trim()))
                    .toList();
        } else {
        	orders = orderService.getAllOrders().stream()
                    .filter(o -> !o.getStatus().equalsIgnoreCase("CART"))
                    .toList();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        return "admin/order/list"; // Trả về file: templates/admin/order/list.html
    }

    // 2. Xem chi tiết một đơn hàng (Hiển thị thông tin khách mua và danh sách sản phẩm điện thoại đã đặt)
    @GetMapping("/view/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrderById(id);
            List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(id);

            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderDetails);
            return "admin/order/detail"; // Trả về file: templates/admin/order/detail.html
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng này.");
            return "redirect:/admin/orders";
        }
    }

    // 3. Xử lý cập nhật trạng thái đơn hàng (Duyệt đơn, xác nhận giao hàng, hủy đơn...)
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("orderId") Integer orderId, 
                               @RequestParam("status") String status, 
                               RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng sang '" + status + "' thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        // Sau khi xử lý xong, điều hướng ngược lại trang chi tiết của chính đơn hàng đó
        return "redirect:/admin/orders/view/" + orderId;
    }
}