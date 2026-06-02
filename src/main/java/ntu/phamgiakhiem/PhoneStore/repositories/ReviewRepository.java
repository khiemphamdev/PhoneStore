package ntu.phamgiakhiem.PhoneStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Lấy tất cả đánh giá của một sản phẩm và sắp xếp theo ngày mới nhất lên đầu
    List<Review> findByProductIdOrderByCreatedDateDesc(Integer productId);
    boolean existsByUserIdAndProductId(Long userId, Integer productId);
}