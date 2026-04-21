package org.example.talktripproductclickservice.domain.click.repository;

import org.example.talktripproductclickservice.domain.click.entity.ProductClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickRepository extends JpaRepository<ProductClickEvent, Long> {
}

