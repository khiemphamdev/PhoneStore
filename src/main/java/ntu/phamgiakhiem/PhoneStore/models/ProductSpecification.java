package ntu.phamgiakhiem.PhoneStore.models;

import jakarta.persistence.*;

@Entity
@Table(name = "product_specifications")
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "screen_size", length = 100)
    private String screenSize; // Kích thước & Công nghệ màn hình

    @Column(length = 100)
    private String os; // Hệ điều hành (iOS, Android)

    @Column(length = 100)
    private String cpu; // Chip xử lý

    @Column(length = 50)
    private String ram; // Dung lượng RAM

    @Column(length = 50)
    private String rom; // Bộ nhớ trong

    @Column(name = "front_camera", length = 100)
    private String frontCamera; // Camera trước

    @Column(name = "rear_camera", length = 100)
    private String rearCamera; // Camera sau

    @Column(length = 100)
    private String battery; // Dung lượng pin & Công nghệ sạc

    // Kết nối 1-1 với bảng Product
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", unique = true)
    private Product product;

    // === CONSTRUCTORS ===
    public ProductSpecification() {
    }

    public ProductSpecification(String screenSize, String os, String cpu, String ram, String rom, 
                                String frontCamera, String rearCamera, String battery, Product product) {
        this.screenSize = screenSize;
        this.os = os;
        this.cpu = cpu;
        this.ram = ram;
        this.rom = rom;
        this.frontCamera = frontCamera;
        this.rearCamera = rearCamera;
        this.battery = battery;
        this.product = product;
    }

    // === GETTERS AND SETTERS ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getFrontCamera() {
        return frontCamera;
    }

    public void setFrontCamera(String frontCamera) {
        this.frontCamera = frontCamera;
    }

    public String getRearCamera() {
        return rearCamera;
    }

    public void setRearCamera(String rearCamera) {
        this.rearCamera = rearCamera;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}