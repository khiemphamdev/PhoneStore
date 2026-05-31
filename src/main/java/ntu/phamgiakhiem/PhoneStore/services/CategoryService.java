package ntu.phamgiakhiem.PhoneStore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ntu.phamgiakhiem.PhoneStore.models.Category;
import ntu.phamgiakhiem.PhoneStore.repositories.CategoryRepository; 

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống!");
        }
        String formattedName = category.getName().trim();
        Optional<Category> existingCategory = categoryRepository.findByName(formattedName);
        
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Danh mục '" + formattedName + "' đã tồn tại trong hệ thống!");
        } 
        category.setName(formattedName);
        return categoryRepository.save(category);
    }

    // 4. Cập nhật danh mục (Kiểm tra trùng tên nhưng bỏ qua chính nó)
    public Category updateCategory(Integer id, Category categoryDetails) {
        if (categoryDetails.getName() == null || categoryDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục mới không được để trống!");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có ID: " + id));
        
        String newName = categoryDetails.getName().trim();
        Optional<Category> existingCategory = categoryRepository.findByName(newName);     
        // Nếu tên mới trùng với 1 bản ghi đã có, và bản ghi đó KHÔNG PHẢI là bản ghi đang sửa hiện tại
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên danh mục '" + newName + "' đã được sử dụng bởi một hãng khác!");
        }

        category.setName(newName);
        return categoryRepository.save(category);
    }

    // 5. Xóa danh mục
    public void deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Danh mục không tồn tại để xóa.");
        }
    }
}