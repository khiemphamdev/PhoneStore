package ntu.phamgiakhiem.PhoneStore.models;

import jakarta.persistence.*;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(length = 500)
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer inventory = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
 // --- CÁC TRƯỜNG THÔNG SỐ KỸ THUẬT ĐƯỢC TÍCH HỢP TRỰC TIẾP ---
    @Column(name = "screen_technology", length = 255)
    private String screenTechnology;  // Công nghệ màn hình

    @Column(name = "screen_size", length = 50)
    private String screenSize;        // Kích thước màn hình

    @Column(name = "screen_features", columnDefinition = "TEXT")
    private String screenFeatures;    // Tính năng màn hình

    @Column(name = "cpu_type", length = 255)
    private String cpuType;           // Loại CPU (Chipset)

    @Column(length = 100)
    private String os;                // Hệ điều hành

    @Column(length = 50)
    private String ram;               // Dung lượng RAM

    @Column(length = 50)
    private String rom;               // Bộ nhớ trong

    @Column(name = "rear_camera", length = 255)
    private String rearCamera;        // Camera sau

    @Column(name = "front_camera", length = 255)
    private String frontCamera;       // Camera trước

    @Column(name = "sim_type", length = 100)
    private String simType;           // Loại SIM hỗ trợ

	

	public Product(Integer id, String name, Double price, String image, String description, Integer inventory,
			Category category, String screenTechnology, String screenSize, String screenFeatures, String cpuType,
			String os, String ram, String rom, String rearCamera, String frontCamera, String simType) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.inventory = inventory;
		this.category = category;
		this.screenTechnology = screenTechnology;
		this.screenSize = screenSize;
		this.screenFeatures = screenFeatures;
		this.cpuType = cpuType;
		this.os = os;
		this.ram = ram;
		this.rom = rom;
		this.rearCamera = rearCamera;
		this.frontCamera = frontCamera;
		this.simType = simType;
	}

	public Product() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getScreenTechnology() {
		return screenTechnology;
	}

	public void setScreenTechnology(String screenTechnology) {
		this.screenTechnology = screenTechnology;
	}

	public String getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(String screenSize) {
		this.screenSize = screenSize;
	}

	public String getScreenFeatures() {
		return screenFeatures;
	}

	public void setScreenFeatures(String screenFeatures) {
		this.screenFeatures = screenFeatures;
	}

	public String getCpuType() {
		return cpuType;
	}

	public void setCpuType(String cpuType) {
		this.cpuType = cpuType;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
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

	public String getRearCamera() {
		return rearCamera;
	}

	public void setRearCamera(String rearCamera) {
		this.rearCamera = rearCamera;
	}

	public String getFrontCamera() {
		return frontCamera;
	}

	public void setFrontCamera(String frontCamera) {
		this.frontCamera = frontCamera;
	}

	public String getSimType() {
		return simType;
	}

	public void setSimType(String simType) {
		this.simType = simType;
	}
    
	
    
}
