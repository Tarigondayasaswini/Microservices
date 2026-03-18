package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.response.DiscoverWeeklyResponse;
import com.revplay.catalogservice.dto.response.DiscoveryFeedResponse;

public interface DiscoveryFeedService {

    DiscoverWeeklyResponse discoverWeekly(Long userId, int limit);

    DiscoveryFeedResponse homeFeed(Long userId, int sectionLimit);

    // Used by DiscoveryFeedController
    DiscoveryFeedResponse getFeed();
}




