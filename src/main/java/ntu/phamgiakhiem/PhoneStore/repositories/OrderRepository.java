package ntu.phamgiakhiem.PhoneStore.repositories;

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
}