package ntu.phamgiakhiem.PhoneStore.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 1. Đếm số lượng đơn hàng đã hoàn thành (DELIVERED)
    long countByStatus(String status);

    // 2. Tính tổng doanh thu từ các đơn hàng đã hoàn thành
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'DELIVERED'")
    Double calculateTotalRevenue();
    
 // Tìm danh sách đơn hàng của một người dùng cụ thể
    List<Order> findByUserId(Long userId);
    
    // Tìm đơn hàng theo trạng thái của một người dùng (Rất quan trọng để tìm đúng đơn hàng dạng 'CART')
    Optional<Order> findByUserIdAndStatus(Long userId, String status);
    
    // Tìm tất cả các đơn hàng dựa theo trạng thái (Dùng cho Admin lọc đơn PENDING, SHIPPING...)
    List<Order> findByStatus(String status);
}