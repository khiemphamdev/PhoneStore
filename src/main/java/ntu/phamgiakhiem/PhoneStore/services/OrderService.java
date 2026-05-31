package ntu.phamgiakhiem.PhoneStore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ntu.phamgiakhiem.PhoneStore.models.Order;
import ntu.phamgiakhiem.PhoneStore.models.OrderDetail;
import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.models.User;
import ntu.phamgiakhiem.PhoneStore.repositories.OrderRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.OrderDetailRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.ProductRepository;
import ntu.phamgiakhiem.PhoneStore.repositories.UserRepository;

import java.util.List;

@Service
@Transactional // Đảm bảo tính toàn vẹn dữ liệu (Rollback nếu xảy ra lỗi giữa chừng)
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== QUẢN LÝ CHO ADMIN & ĐƠN HÀNG CHUNG ====================

    // Lấy tất cả các đơn hàng hiện có
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Xem chi tiết một đơn hàng theo ID
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));
    }

    // Lấy danh sách sản phẩm chi tiết của một đơn hàng
    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    // Thay đổi trạng thái đơn hàng (Ví dụ từ PENDING -> CONFIRMED -> DELIVERED)
    public Order updateOrderStatus(Integer orderId, String newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus.toUpperCase());
        return orderRepository.save(order);
    }


    // ==================== XỬ LÝ NGHIỆP VỤ GIỎ HÀNG (CLIENT) ====================

    // 1. Lấy hoặc Tạo mới Giỏ hàng ('CART') cho người dùng đang đăng nhập
    public Order getOrCreateCart(Long userId) {
        return orderRepository.findByUserIdAndStatus(userId, "CART")
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
                    Order newCart = new Order();
                    newCart.setUser(user);
                    newCart.setTotalPrice(0.0);
                    newCart.setStatus("CART");
                    newCart.setShippingAddress(""); // Tạm thời để trống khi là giỏ hàng
                    return orderRepository.save(newCart);
                });
    }

    // 2. Thêm sản phẩm điện thoại vào giỏ hàng
    public void addProductToCart(Long userId, Integer productId, int quantity) {
        Order cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        // Kiểm tra xem sản phẩm này đã có trong giỏ hàng chưa
        OrderDetail orderDetail = orderDetailRepository.findByOrderIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (orderDetail == null) {
            // Nếu chưa có -> Tạo dòng chi tiết mới
            orderDetail = new OrderDetail();
            orderDetail.setOrder(cart);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(product.getPrice());
        } else {
            // Nếu đã có -> Cộng dồn số lượng thêm vào
            orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
        }

        orderDetailRepository.save(orderDetail);
        
        // Cập nhật lại tổng tiền của giỏ hàng
        updateCartTotalPrice(cart);
    }

    // 3. Cập nhật số lượng của một sản phẩm trong giỏ hàng (Khi khách bấm tăng/giảm số lượng)
    public void updateCartItemQuantity(Long userId, Integer productId, int newQuantity) {
        Order cart = orderRepository.findByUserIdAndStatus(userId, "CART")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        OrderDetail orderDetail = orderDetailRepository.findByOrderIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng!"));

        if (newQuantity <= 0) {
            // Nếu số lượng chỉnh về 0 hoặc âm -> Tiến hành xóa luôn sản phẩm khỏi giỏ
            orderDetailRepository.delete(orderDetail);
        } else {
            orderDetail.setQuantity(newQuantity);
            orderDetailRepository.save(orderDetail);
        }

        updateCartTotalPrice(cart);
    }

    // 4. Xóa hoàn toàn một sản phẩm ra khỏi giỏ hàng
    public void removeProductFromCart(Long userId, Integer productId) {
        Order cart = orderRepository.findByUserIdAndStatus(userId, "CART")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        OrderDetail orderDetail = orderDetailRepository.findByOrderIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng!"));

        orderDetailRepository.delete(orderDetail);
        updateCartTotalPrice(cart);
    }

    // 5. Khách hàng bấm THÀNH TOÁN (Đổi từ 'CART' sang 'PENDING')
    public void checkoutCart(Long userId, String shippingAddress) {
        Order cart = orderRepository.findByUserIdAndStatus(userId, "CART")
                .orElseThrow(() -> new RuntimeException("Giỏ hàng của bạn đang trống!"));

        List<OrderDetail> details = orderDetailRepository.findByOrderId(cart.getId());
        if (details.isEmpty()) {
            throw new RuntimeException("Không thể thanh toán giỏ hàng trống!");
        }

        // Kiểm tra và trừ số lượng sản phẩm trong kho (Inventory)
        for (OrderDetail item : details) {
            Product product = item.getProduct();
            if (product.getInventory() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho!");
            }
            // Trừ kho
            product.setInventory(product.getInventory() - item.getQuantity());
            productRepository.save(product);
        }

        // Cập nhật thông tin đơn hàng để chuyển sang trạng thái chờ duyệt
        cart.setShippingAddress(shippingAddress);
        cart.setStatus("PENDING"); // Đổi trạng thái sang chờ xử lý
        orderRepository.save(cart);
    }

    // ==================== HÀM TRỢ GIÚP (INTERNAL HELPER) ====================
    
    // Tự động tính toán và cập nhật lại tổng tiền của hóa đơn
    private void updateCartTotalPrice(Order cart) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(cart.getId());
        double totalPrice = 0.0;
        for (OrderDetail detail : details) {
            totalPrice += detail.getPrice() * detail.getQuantity();
        }
        cart.setTotalPrice(totalPrice);
        orderRepository.save(cart);
    }
}