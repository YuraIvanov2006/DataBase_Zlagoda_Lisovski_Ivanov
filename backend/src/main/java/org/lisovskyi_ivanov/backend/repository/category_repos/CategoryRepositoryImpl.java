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
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final CategoryRowMapper categoryRowMapper;


    @Override
    public List<Category> findAll() {
        return jdbc.query(SELECT_ALL, categoryRowMapper);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = SELECT_ALL + " WHERE category_number = ?";
        return jdbc.query(sql, categoryRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public Category save(Category category) {
        String sql =
        """
        INSERT INTO categories (
            category_name
        ) VALUES (
            :categoryName
        );
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, categoryParameters(category), keyHolder, new String[] {"category_number"});

        Long generatedId = keyHolder.getKeyAs(Long.class);
        return findById(generatedId).orElseThrow();
    }

    @Override
    public void update(Category category) {
        String sql =
        """
        UPDATE categories SET
            category_name = :categoryName
        WHERE category_number = :categoryNumber;
        """;

        namedJdbc.update(sql, categoryParameters(category));
    }

    @Override
    public void delete(Category category) {
        deleteById(category.getCategoryNumber());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE category_number = ?";
        jdbc.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_number = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


    private SqlParameterSource categoryParameters(Category category) {
        return new MapSqlParameterSource()
                .addValue("categoryNumber", category.getCategoryNumber())
                .addValue("categoryName", category.getCategoryName());
    }
}
