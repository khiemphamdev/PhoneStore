package ntu.phamgiakhiem.PhoneStore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ntu.phamgiakhiem.PhoneStore.models.Product;
import ntu.phamgiakhiem.PhoneStore.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 1. Lấy toàn bộ danh sách sản phẩm điện thoại
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // 2. Tìm kiếm sản phẩm theo ID
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }
    // 3. Thêm mới sản phẩm (Có kiểm tra trùng tên điện thoại)
    public Product saveProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }

        String formattedName = product.getName().trim();
        
        // Kiểm tra xem tên sản phẩm đã tồn tại trong DB chưa
        if (productRepository.existsByNameIgnoreCase(formattedName)) {
            throw new IllegalArgumentException("Sản phẩm '" + formattedName + "' đã tồn tại trên hệ thống!");
        }

        product.setName(formattedName);
        return productRepository.save(product);
    }

    // 4. Cập nhật thông tin sản phẩm (Kiểm tra trùng tên nhưng loại trừ chính nó)
    public Product updateProduct(Integer id, Product productDetails) {
        if (productDetails.getName() == null || productDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (productDetails.getPrice() == null || productDetails.getPrice() < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

        String newName = productDetails.getName().trim();
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(newName);

        // Nếu trùng tên với một điện thoại khác đang có trong DB
        if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên sản phẩm '" + newName + "' đã được sử dụng bởi một điện thoại khác!");
        }

        // Cập nhật các thông tin mới
        product.setName(newName);
        product.setPrice(productDetails.getPrice());
        product.setImage(productDetails.getImage());
        product.setDescription(productDetails.getDescription());
        product.setInventory(productDetails.getInventory());
        product.setCategory(productDetails.getCategory()); // Gán liên kết danh mục hãng mới

        return productRepository.save(product);
    }

    // 5. Xóa sản phẩm
    public void deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Sản phẩm không tồn tại để xóa.");
        }
    }

    public List<Product> searchProducts(String keyword, Integer categoryId) {
        // Trường hợp 1: Có cả từ khóa tên và có chọn danh mục hãng
        if (keyword != null && !keyword.trim().isEmpty() && categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryId(keyword.trim(), categoryId);
        }
        
        // Trường hợp 2: Chỉ tìm theo từ khóa tên điện thoại
        if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword.trim());
        }
        
        // Trường hợp 3: Chỉ lọc theo danh mục hãng
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        
        // Trường hợp 4: Không nhập gì cả -> Trả về tất cả sản phẩm
        return productRepository.findAll();
    }
}