package ntu.phamgiakhiem.PhoneStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // 3. Tính tổng số điện thoại đã bán được giao thành công
    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.order.status = 'DELIVERED'")
    Long countTotalProductsSold();
}