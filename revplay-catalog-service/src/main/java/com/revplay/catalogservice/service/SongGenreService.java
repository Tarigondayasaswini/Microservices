package com.revplay.catalogservice.service;

import java.util.List;

public interface SongGenreService {

    void addGenres(Long songId, List<Long> genreIds);


    void replaceGenres(Long songId, List<Long> genreIds);
}
