package com.cinema.movie_reservation_system.repository;

import com.cinema.movie_reservation_system.model.Voucher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Voucher persistence using Spring JdbcTemplate.
 * Part of Loyalty Program & Discount Vouchers (IT24100907).
 */
@Repository
public class VoucherRepository {

    private final JdbcTemplate jdbcTemplate;

    public VoucherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Voucher> voucherRowMapper = (rs, rowNum) -> new Voucher(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getDouble("discount_amount_lkr"),
            rs.getBoolean("is_active")
    );

    public List<Voucher> findAll() {
        String sql = "SELECT id, code, discount_amount_lkr, is_active FROM vouchers ORDER BY id ASC";
        return jdbcTemplate.query(sql, voucherRowMapper);
    }

    public Optional<Voucher> findById(Long id) {
        String sql = "SELECT id, code, discount_amount_lkr, is_active FROM vouchers WHERE id = ?";
        List<Voucher> list = jdbcTemplate.query(sql, voucherRowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Voucher> findByCode(String code) {
        String sql = "SELECT id, code, discount_amount_lkr, is_active FROM vouchers WHERE UPPER(code) = UPPER(?)";
        List<Voucher> list = jdbcTemplate.query(sql, voucherRowMapper, code != null ? code.trim() : "");
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Voucher save(Voucher v) {
        if (v.getId() == null) {
            String sql = "INSERT INTO vouchers (code, discount_amount_lkr, is_active) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, v.getCode() != null ? v.getCode().toUpperCase().trim() : null);
                ps.setDouble(2, v.getDiscountAmountLkr() != null ? v.getDiscountAmountLkr() : 0.0);
                ps.setBoolean(3, v.isActive());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                v.setId(key.longValue());
            }
            return v;
        } else {
            String sql = "UPDATE vouchers SET code = ?, discount_amount_lkr = ?, is_active = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    v.getCode() != null ? v.getCode().toUpperCase().trim() : null,
                    v.getDiscountAmountLkr(),
                    v.isActive(),
                    v.getId()
            );
            return v;
        }
    }

    public int updateStatus(Long id, boolean isActive) {
        String sql = "UPDATE vouchers SET is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, isActive, id);
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
