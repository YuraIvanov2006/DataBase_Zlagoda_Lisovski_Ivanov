package org.lisovskyi_ivanov.backend.repository.category_repos;

import lombok.RequiredArgsConstructor;
import org.lisovskyi_ivanov.backend.entity.Category;
import org.lisovskyi_ivanov.backend.mapping.mapper.CategoryRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private static final String TABLE_NAME = "categories";
    private static final String SELECT_ALL =
            "SELECT category_number, category_name FROM " + TABLE_NAME;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final CategoryRowMapper categoryRowMapper;


    @Override
    public List<Category> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY category_name", categoryRowMapper);
    }

    @Override
    public List<Category> findAllByCategoryName(String categoryName) {
        String sql = SELECT_ALL + " WHERE category_name = ? ORDER BY category_name";
        return jdbc.query(sql, categoryRowMapper, categoryName);
    }

    @Override
    public Optional<Category> findByCategoryNumber(Long categoryNumber) {
        String sql = SELECT_ALL + " WHERE category_number = ?";
        return jdbc.query(sql, categoryRowMapper, categoryNumber)
                .stream()
                .findFirst();
    }

    @Override
    public Category save(Category category) {
        String sql =
            """
            INSERT INTO categories (category_name)
            VALUES (:category_name);
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, categoryParameters(category), keyHolder, new String[] {"category_number"});

        Long generatedId = keyHolder.getKeyAs(Long.class);
        return findByCategoryNumber(generatedId).orElseThrow();
    }

    @Override
    public int update(Category category) {
        String sql =
        """
        UPDATE categories SET
            category_name = :category_name
        WHERE category_number = :category_number;
        """;

        return namedJdbc.update(sql, categoryParameters(category));
    }

    @Override
    public int delete(Category category) {
        return deleteById(category.getCategoryNumber());
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE category_number = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_number = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


    private SqlParameterSource categoryParameters(Category category) {
        return new MapSqlParameterSource()
                .addValue("category_number", category.getCategoryNumber())
                .addValue("category_name", category.getCategoryName());
    }
}
