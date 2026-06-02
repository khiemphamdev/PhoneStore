package ntu.phamgiakhiem.PhoneStore.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Order;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;
import ntu.phamgiakhiem.PhoneStore.models.Review;
import ntu.phamgiakhiem.PhoneStore.models.User;
import ntu.phamgiakhiem.PhoneStore.repositories.OrderRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.ReviewRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.UserRepository;
import ntu.phamgiakhiem.PhoneStore.services.OrderService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        // Lấy lịch sử các đơn hàng của riêng User này (Bỏ qua trạng thái đang là 'CART')
        List<Order> orders = orderRepository.findByUserId(user.getId()).stream()
                .filter(order -> !order.getStatus().equals("CART"))
                .toList();
                
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "client/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("fullname") String fullname,
                                @RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
        }
        return "redirect:/profile";
    }
    
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") Integer orderId, 
                                  Principal principal, 
                                  Model model, 
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1. Kiểm tra đăng nhập
            if (principal == null) {
                return "redirect:/login";
            }
            
            // 2. Lấy thông tin User hiện tại từ hệ thống danh tính
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

            // 3. Tìm thông tin đơn hàng theo ID từ URL
            Order order = orderService.getOrderById(orderId); // Đảm bảo OrderService đã có hàm tìm theo Id này

            // 4. BẢO MẬT: Kiểm tra xem đơn hàng này có thuộc về chính chủ đang đăng nhập không
            if (!order.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền truy cập đơn hàng này!");
                return "redirect:/profile";
            }

            // 5. Lấy danh sách các sản phẩm (chi tiết) nằm trong đơn hàng
            List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);

            // 6. Đẩy dữ liệu ra view
            model.addAttribute("newReview", new Review());
            model.addAttribute("reviewRepo", reviewRepository);
            model.addAttribute("order", order);
            model.addAttribute("orderDetails", orderDetails);
            
            return "client/order-detail"; // Trả về file giao diện: templates/client/order-detail.html

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng yêu cầu!");
            return "redirect:/profile";
        }
    }
}
