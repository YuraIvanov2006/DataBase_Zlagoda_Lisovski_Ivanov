package org.lisovskyi_ivanov.backend.repository.category_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.mapping.mapper.CategoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl {
    private final JdbcTemplate jdbc;
    private final CategoryRowMapper categoryRowMapper;


}
