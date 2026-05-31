package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.services.ProductService;

import java.util.List;

@Controller
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    // Xem chi tiết điện thoại theo mã ID ngoài trang chủ
    @GetMapping("/product/detail/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 1. Tìm thông tin chi tiết của sản phẩm theo ID
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

            // 2. Lấy các sản phẩm cùng hãng liên quan (Loại trừ máy hiện tại và lấy tối đa 4 máy)
            List<Product> relatedProducts = productService.searchProducts(null, product.getCategory().getId())
                    .stream()
                    .filter(p -> !p.getId().equals(id))
                    .limit(4)
                    .toList();

            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            
            return "client/product-detail"; // Trả về file giao diện: templates/client/product-detail.html

        } catch (Exception e) {
            // Nếu gõ ID bậy hoặc sản phẩm đã bị xóa, đẩy ngược về trang chủ kèm thông báo
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm điện thoại bạn yêu cầu!");
            return "redirect:/";
        }
    }
}