package ntu.phamgiakhiem.PhoneStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.Product;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    Optional<Product> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    // ✨ THÊM MỚI: Tìm kiếm theo tên HOẶC theo ID danh mục hãng
    // Tìm sản phẩm mà tên chứa từ khóa (không phân biệt hoa thường)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm sản phẩm dựa vào ID của danh mục hãng
    List<Product> findByCategoryId(Integer categoryId);

    // Tìm kiếm kết hợp: Tên chứa từ khóa VÀ thuộc danh mục hãng cụ thể
    List<Product> findByNameContainingIgnoreCaseAndCategoryId(String name, Integer categoryId);
}