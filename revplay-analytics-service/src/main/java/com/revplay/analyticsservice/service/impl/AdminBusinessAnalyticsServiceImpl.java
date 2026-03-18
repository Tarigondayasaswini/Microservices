package com.revplay.analyticsservice.service.impl;

import com.revplay.analyticsservice.repository.AdImpressionRepository;
import com.revplay.analyticsservice.dto.response.BusinessOverviewResponse;
import com.revplay.analyticsservice.dto.response.ConversionRateResponse;
import com.revplay.analyticsservice.dto.response.RevenueAnalyticsResponse;
import com.revplay.analyticsservice.dto.response.TopDownloadResponse;
import com.revplay.analyticsservice.dto.response.TopMixResponse;
import com.revplay.analyticsservice.service.AdminBusinessAnalyticsService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessAnalyticsServiceImpl implements AdminBusinessAnalyticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBusinessAnalyticsServiceImpl.class);

    private static final String COUNT_ACTIVE_PREMIUM_SQL = """
            SELECT COUNT(DISTINCT us.user_id)
            FROM revplay_analytics.user_subscriptions us
            WHERE us.status = 'ACTIVE' AND us.expires_at > ?
            """;

    private static final String SUM_REVENUE_IN_RANGE_SQL = """
            SELECT COALESCE(COUNT(DISTINCT us.user_id) * 9.99, 0.0)
            FROM revplay_analytics.user_subscriptions us
            WHERE us.status = 'ACTIVE' AND us.started_at >= ? AND us.started_at < ?
            """;

    private static final String SUM_TOTAL_REVENUE_SQL = """
            SELECT COALESCE(COUNT(DISTINCT us.user_id) * 9.99, 0.0)
            FROM revplay_analytics.user_subscriptions us
            WHERE us.status = 'ACTIVE'
            """;

    private static final String TOP_DOWNLOADS_SQL = """
            SELECT sd.song_id, COUNT(*) AS download_count
            FROM revplay_playback.play_history sd
            JOIN revplay_catalog.songs s ON s.song_id = sd.song_id
            WHERE s.is_active = true
            GROUP BY sd.song_id
            ORDER BY download_count DESC, sd.song_id ASC
            LIMIT ?
            """;

    private static final String TOP_MIXES_SQL = """
            SELECT sp.name AS playlist_name, COUNT(ph.play_id) AS total_play_count
            FROM revplay_playlists.system_playlists sp
            LEFT JOIN revplay_playlists.system_playlist_songs sps
                   ON sps.system_playlist_id = sp.system_playlist_id
            LEFT JOIN revplay_playback.play_history ph
                   ON ph.song_id = sps.song_id
            GROUP BY sp.system_playlist_id, sp.name
            ORDER BY total_play_count DESC, sp.name ASC
            """;

    private final AdImpressionRepository adImpressionRepository;
    private final JdbcTemplate jdbcTemplate;

    public AdminBusinessAnalyticsServiceImpl(
            AdImpressionRepository adImpressionRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.adImpressionRepository = adImpressionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessOverviewResponse getBusinessOverview() {
        long totalUsers = nvl(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM revplay_users.users", Long.class));
        long totalAdImpressions = adImpressionRepository.count();
        long totalDownloads = nvl(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM revplay_playback.play_history", Long.class));
        long totalSongPlays = nvl(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM revplay_playback.play_history", Long.class));
        long activePremiumUsers = getActivePremiumUsersCount();

        LOGGER.info("Business overview computed: users={}, premiumUsers={}, adImpressions={}, downloads={}, plays={}",
                totalUsers, activePremiumUsers, totalAdImpressions, totalDownloads, totalSongPlays);

        return new BusinessOverviewResponse(
                totalUsers,
                activePremiumUsers,
                totalAdImpressions,
                totalDownloads,
                totalSongPlays
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueAnalyticsResponse getRevenueAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        LocalDateTime yearStart = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yearEnd = yearStart.plusYears(1);

        double monthlyRevenue = queryRevenueInRange(monthStart, monthEnd);
        double yearlyRevenue = queryRevenueInRange(yearStart, yearEnd);
        double totalRevenue = queryTotalRevenue();

        LOGGER.info("Revenue analytics computed: monthly={}, yearly={}, total={}", monthlyRevenue, yearlyRevenue, totalRevenue);
        return new RevenueAnalyticsResponse(monthlyRevenue, yearlyRevenue, totalRevenue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopDownloadResponse> getTopDownloadedSongs(int limit) {
        int safeLimit = limit <= 0 ? 10 : Math.min(limit, 100);
        return jdbcTemplate.query(
                TOP_DOWNLOADS_SQL,
                (rs, rowNum) -> new TopDownloadResponse(
                        rs.getLong("song_id"),
                        rs.getLong("download_count")
                ),
                safeLimit
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopMixResponse> getTopMixes() {
        return jdbcTemplate.query(
                TOP_MIXES_SQL,
                this::mapTopMix
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ConversionRateResponse getPremiumConversionRate() {
        long totalUsers = nvl(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM revplay_users.users", Long.class));
        long activePremiumUsers = getActivePremiumUsersCount();
        double percentage = totalUsers == 0 ? 0.0 : (activePremiumUsers * 100.0) / totalUsers;
        return new ConversionRateResponse(roundToTwoDecimals(percentage));
    }

    private long getActivePremiumUsersCount() {
        LocalDateTime now = LocalDateTime.now();
        return nvl(jdbcTemplate.queryForObject(COUNT_ACTIVE_PREMIUM_SQL, Long.class, now));
    }
 
    private long nvl(Long value) {
        return value == null ? 0L : value;
    }

    private double queryRevenueInRange(LocalDateTime from, LocalDateTime to) {
        Double value = jdbcTemplate.queryForObject(SUM_REVENUE_IN_RANGE_SQL, Double.class, from, to);
        return value == null ? 0.0 : value;
    }

    private double queryTotalRevenue() {
        Double value = jdbcTemplate.queryForObject(SUM_TOTAL_REVENUE_SQL, Double.class);
        return value == null ? 0.0 : value;
    }

    private TopMixResponse mapTopMix(ResultSet rs, int rowNum) throws SQLException {
        return new TopMixResponse(
                rs.getString("playlist_name"),
                rs.getLong("total_play_count")
        );
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
