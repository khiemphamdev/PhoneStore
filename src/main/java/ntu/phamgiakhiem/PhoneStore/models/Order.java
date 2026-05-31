package ntu.phamgiakhiem.PhoneStore.models;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(length = 50)
    private String status = "CART"; // CART, PENDING, CONFIRMED...

    @CreationTimestamp 
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

	public Order(Integer id, User user, Double totalPrice, String status, LocalDateTime createdDate,
			String shippingAddress) {
		super();
		this.id = id;
		this.user = user;
		this.totalPrice = totalPrice;
		this.status = status;
		this.createdDate = createdDate;
		this.shippingAddress = shippingAddress;
	}

	public Order() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
    
    
}
