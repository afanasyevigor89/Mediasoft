package service;

import entity.ProductEntity;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ProductService {
    private static final String TABLE = "product";
    private final ProductRepository repository;

    @Step("Проверяем запись в таблице " + TABLE + " по id")
    public ProductEntity findProductById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new AssertionError("Продукт не найден в БД"));
    }

    @Step("Проверяем что товар удален из таблицы " + TABLE)
    public boolean isProductDeleted(UUID id) {
        return repository.existsById(id);
    }

    @Step("Проверяем запись в таблице " + TABLE + " по артиклу")
    public ProductEntity findProductByArticle(UUID article) {
        return repository.findByArticle(article);
    }
}
