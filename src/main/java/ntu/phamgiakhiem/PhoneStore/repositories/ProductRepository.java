package ntu.phamgiakhiem.PhoneStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.Product;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Tìm kiếm điện thoại theo tên (Không phân biệt hoa thường)
    Optional<Product> findByNameIgnoreCase(String name);
    
    // Kiểm tra nhanh xem tên sản phẩm đã tồn tại chưa
    boolean existsByNameIgnoreCase(String name);
}
