package com.example.shop;

import static com.example.shop.ApiDtos.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final OrderRepository orders;

    public CatalogService(ProductRepository products, CategoryRepository categories, OrderRepository orders) {
        this.products = products;
        this.categories = categories;
        this.orders = orders;
    }

    public Page<ProductDto> products(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return products.findAll(pageable).map(this::productDto);
        }
        category(categoryId);
        return products.findForCategory(categoryId, pageable).map(this::productDto);
    }

    public ProductDto getProduct(Long id) {
        return productDto(product(id));
    }

    @Transactional
    public ProductDto createProduct(ProductRequest request) {
        return saveProduct(new Product(), request);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductRequest request) {
        return saveProduct(product(id), request);
    }

    private ProductDto saveProduct(Product product, ProductRequest request) {
        product.category = category(request.categoryId());
        product.name = request.name().strip();
        product.price = request.price();
        return productDto(products.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        products.delete(product(id));
        products.flush();
    }

    public List<CategoryDto> categories() {
        return categories.findAll(Sort.by("id")).stream().map(this::categoryDto).toList();
    }

    public CategoryDto getCategory(Long id) {
        return categoryDto(category(id));
    }

    @Transactional
    public CategoryDto createCategory(CategoryRequest request) {
        Category category = new Category();
        category.name = request.name().strip();
        return categoryDto(categories.save(category));
    }

    public OrderDto getOrder(Long id, String owner) {
        Order order = required(orders, id, "Commande");
        if (owner != null && !owner.equals(order.ownerUsername)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable");
        }
        return orderDto(order);
    }

    public Page<OrderDto> orders(String owner, Pageable pageable) {
        return (owner == null ? orders.findAll(pageable) : orders.findByOwnerUsername(owner, pageable))
                .map(this::orderDto);
    }

    @Transactional
    public OrderDto createOrder(String owner, OrderRequest request) {
        Order order = new Order();
        order.ownerUsername = owner;
        for (OrderItemRequest line : request.items()) {
            Product product = product(line.productId());
            OrderItem item = new OrderItem();
            item.product = product;
            item.quantity = line.quantity();
            item.unitPrice = product.price;
            order.addItem(item);
        }
        return orderDto(orders.saveAndFlush(order));
    }

    @Transactional
    public void deleteOrder(Long id) {
        orders.delete(required(orders, id, "Commande"));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category category = category(id);
        category.name = request.name().strip();
        return categoryDto(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categories.delete(category(id));
        categories.flush();
    }

    private Product product(Long id) {
        return required(products, id, "Produit");
    }

    private Category category(Long id) {
        return required(categories, id, "Catégorie");
    }

    private <T> T required(org.springframework.data.repository.CrudRepository<T, Long> repository,
                           Long id, String resource) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " introuvable : " + id));
    }

    private ProductDto productDto(Product product) {
        return new ProductDto(product.id, product.name, product.price, product.category.getId());
    }

    private CategoryDto categoryDto(Category category) {
        return new CategoryDto(category.id, category.name);
    }

    private OrderDto orderDto(Order order) {
        return new OrderDto(order.id, order.createdAt, order.items.stream()
                .map(item -> new OrderItemDto(item.id, item.product.getId(), item.quantity, item.unitPrice))
                .toList());
    }
}
