package ntu.phamgiakhiem.PhoneStore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.services.ProductService;
import ntu.phamgiakhiem.PhoneStore.services.CategoryService; // Tiêm thêm để lấy danh sách hãng

@Controller
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ các tính năng này, chỉ cho phép ADMIN truy cập
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // 1. Hiển thị danh sách tất cả điện thoại
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/product/list";
    }

    // 2. Hiển thị Form thêm điện thoại mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        // Nạp danh sách hãng điện thoại để hiển thị vào thẻ <select> trên giao diện
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Thêm Điện Thoại Mới");
        return "admin/product/form"; // Trả về file: templates/admin/product/form.html
    }

    // 3. Xử lý lưu dữ liệu (Thêm mới hoặc Cập nhật)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (product.getId() == null) {
                // Thêm mới
                productService.saveProduct(product);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sản phẩm thành công!");
            } else {
                // Cập nhật
                productService.updateProduct(product.getId(), product);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm thành công!");
            }
            return "redirect:/admin/products";

        } catch (IllegalArgumentException e) {
            // Nếu trùng tên hoặc dữ liệu lỗi, nạp lại dữ liệu cũ và danh sách danh mục để sửa lại
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("pageTitle", product.getId() == null ? "Thêm Điện Thoại Mới" : "Sửa Thông Tin Điện Thoại");
            return "admin/product/form";
        }
    }

    // 4. Hiển thị Form chỉnh sửa điện thoại
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id).map(product -> {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories()); 
            model.addAttribute("pageTitle", "Sửa Thông Tin Điện Thoại (ID: " + id + ")");
            return "admin/product/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm yêu cầu.");
            return "redirect:/admin/products";
        });
    }

    // 5. Xử lý xóa điện thoại
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}