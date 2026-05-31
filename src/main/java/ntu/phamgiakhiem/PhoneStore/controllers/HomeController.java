package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.services.ProductService;
import ntu.phamgiakhiem.PhoneStore.services.CategoryService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Trang chủ hệ thống: đón nhận tham số tìm kiếm và lọc danh mục từ Client
    @GetMapping({"/", "/home"})
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        
        // 1. Lấy danh sách sản phẩm theo bộ lọc (nếu có) để hiển thị ngoài trang chủ
        List<Product> products = productService.searchProducts(keyword, categoryId);
        model.addAttribute("products", products);
        
        // 2. Lấy toàn bộ danh mục hãng để hiển thị làm Menu Lọc cho khách hàng
        model.addAttribute("categories", categoryService.getAllCategories());
        
        // 3. Giữ lại trạng thái tìm kiếm trên thanh tìm kiếm của khách
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        
        return "client/index"; // Trả về file giao diện khách hàng: templates/client/index.html
    }
    
    
}