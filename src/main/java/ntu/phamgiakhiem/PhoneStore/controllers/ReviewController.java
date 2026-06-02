package ntu.phamgiakhiem.PhoneStore.controllers;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.models.Review;
import ntu.phamgiakhiem.PhoneStore.models.User;
import ntu.phamgiakhiem.PhoneStore.repositories.ReviewRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.UserRepository;
import ntu.phamgiakhiem.PhoneStore.services.ProductService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addReview(@RequestParam("productId") Integer productId,
    						@RequestParam("orderId") Integer orderId,
                            @RequestParam("rating") Integer rating,
                            @RequestParam("content") String content,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        
        // 1. Kiểm tra xem người dùng đã đăng nhập chưa
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để gửi đánh giá!");
            return "redirect:/login";
        }

        try {
            // 2. Tìm sản phẩm cần đánh giá
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

            // 3. Tìm thông tin User đang đăng nhập
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không hợp lệ!"));
            
            boolean hasReviewed = reviewRepository.existsByUserIdAndProductId(user.getId(), productId);
            if (hasReviewed) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã đánh giá sản phẩm này trước đó rồi!");
                return "redirect:/profile/orders/detail/" + orderId;
            }

            // 4. Khởi tạo thực thể Review và gán dữ liệu
            Review review = new Review();
            review.setProduct(product);
            review.setUser(user);
            review.setRating(rating);
            review.setContent(content);

            // 5. Lưu vào Database
            reviewRepository.save(review);

            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã đánh giá sản phẩm!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi đánh giá thất bại: " + e.getMessage());
        }

        // Quay trở lại đúng trang chi tiết sản phẩm vừa đánh giá
        return "redirect:/profile/detail/" + orderId;
    }
}