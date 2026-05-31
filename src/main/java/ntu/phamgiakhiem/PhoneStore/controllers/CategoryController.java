package ntu.phamgiakhiem.PhoneStore.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.phamgiakhiem.PhoneStore.models.Category;
import ntu.phamgiakhiem.PhoneStore.services.CategoryService;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')") // Chỉ tài khoản có quyền ROLE_ADMIN mới truy cập được nhóm link này
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1. Hiển thị danh sách danh mục hãng điện thoại
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/list"; // Trả về file: src/main/resources/templates/admin/category/list.html
    }

    // 2. Hiển thị Form thêm danh mục mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Thêm Danh Mục Mới");
        return "admin/category/form"; // Trả về file: src/main/resources/templates/admin/category/form.html
    }

    // 3. Xử lý Thêm mới hoặc Cập nhật danh mục
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (category.getId() == null) {
                // Trường hợp ID trống -> Thêm mới
                categoryService.saveCategory(category);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm mới danh mục thành công!");
            } else {
                // Trường hợp có ID -> Cập nhật
                categoryService.updateCategory(category.getId(), category);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            }
            return "redirect:/admin/categories";
            
        } catch (IllegalArgumentException e) {
            // Hứng lỗi trùng tên từ Service ném ra và gửi lại form để hiển thị cảnh báo
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", category.getId() == null ? "Thêm Danh Mục Mới" : "Sửa Danh Mục");
            return "admin/category/form";
        }
    }

    // 4. Hiển thị Form sửa danh mục (Dùng chung file form.html với hàm thêm mới)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        return categoryService.getCategoryById(id).map(category -> {
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Sửa Danh Mục (ID: " + id + ")");
            return "admin/category/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục yêu cầu.");
            return "redirect:/admin/categories";
        });
    }

    // 5. Xử lý Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
