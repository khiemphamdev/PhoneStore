package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Order;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;
import ntu.phamgiakhiem.PhoneStore.models.User;
import ntu.phamgiakhiem.PhoneStore.services.OrderService;
import ntu.phamgiakhiem.PhoneStore.repositories.UserRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    // Hàm trợ giúp: Lấy ID của người dùng đang đăng nhập dựa vào Username
    private Long getLoggedInUserId(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Bạn chưa đăng nhập hệ thống!");
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username) // Đảm bảo UserRepository của bạn có hàm này
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng!"));
        return user.getId();
    }

    // 1. Hiển thị trang giỏ hàng của người dùng đang đăng nhập
    @GetMapping
    public String viewCart(Principal principal, Model model) {
        try {
            Long userId = getLoggedInUserId(principal);
            
            Order cart = orderService.getOrCreateCart(userId);
            List<OrderDetail> cartItems = orderService.getOrderDetailsByOrderId(cart.getId());

            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cartItems);
            return "client/cart";
            
        } catch (Exception e) {
            return "redirect:/login"; // Nếu có lỗi đăng nhập, đẩy về trang login
        }
    }

    // 2. Thêm sản phẩm vào giỏ hàng cá nhân
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestParam("productId") Integer productId,
                                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                                            Principal principal) {
        try {
            Long userId = getLoggedInUserId(principal);
            orderService.addProductToCart(userId, productId, quantity);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // 3. Cập nhật số lượng sản phẩm trong giỏ hàng cá nhân
    @PostMapping("/update")
    public String updateCartItem(@RequestParam("productId") Integer productId,
                                 @RequestParam("quantity") int quantity,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            Long userId = getLoggedInUserId(principal);
            orderService.updateCartItemQuantity(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật số lượng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    // 4. Xóa hoàn toàn một dòng sản phẩm khỏi giỏ hàng cá nhân
    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Integer productId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            Long userId = getLoggedInUserId(principal);
            orderService.removeProductFromCart(userId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    // 5. Xử lý đặt hàng và điền địa chỉ giao nhận của chính user đó
    @PostMapping("/checkout")
    public String checkout(@RequestParam("shippingAddress") String shippingAddress,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            Long userId = getLoggedInUserId(principal);
            
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập địa chỉ nhận hàng chính xác!");
            }
            
            orderService.checkoutCart(userId, shippingAddress.trim());
            redirectAttributes.addFlashAttribute("successMessage", "🎉 Đặt hàng thành công! Đơn hàng đang chờ phê duyệt.");
            return "redirect:/";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}