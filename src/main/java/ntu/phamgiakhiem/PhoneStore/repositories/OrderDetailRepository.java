package ntu.phamgiakhiem.PhoneStore.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // 3. Tính tổng số điện thoại đã bán được giao thành công
    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.order.status = 'DELIVERED'")
    Long countTotalProductsSold();
    
    List<OrderDetail> findByOrderId(Integer orderId);
    
    // Tìm kiếm một sản phẩm cụ thể xem đã nằm trong đơn hàng đó chưa
    Optional<OrderDetail> findByOrderIdAndProductId(Integer orderId, Integer productId);
}