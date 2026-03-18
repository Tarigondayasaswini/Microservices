package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.entity.Song;
import com.revplay.catalogservice.entity.SongGenre;
import com.revplay.catalogservice.enums.UserRole;
import com.revplay.catalogservice.exception.BadRequestException;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.repository.GenreRepository;
import com.revplay.catalogservice.repository.SongGenreRepository;
import com.revplay.catalogservice.repository.SongRepository;
import com.revplay.catalogservice.service.SongGenreService;
import com.revplay.catalogservice.util.AccessValidator;
import com.revplay.catalogservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongGenreServiceImpl implements SongGenreService {
    private final SongGenreRepository songGenreRepository;
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SecurityUtil securityUtil;
    private final AccessValidator accessValidator;
    private final ArtistRepository artistRepository;

    @Override
    @Transactional
    public void addGenres(Long songId, List<Long> genreIds) {
        log.info("Adding genres for songId={} with {} genres", songId, genreIds.size());
        Song song = validateOwnedSongAndGenres(songId, genreIds);
        List<Long> uniqueRequested = genreIds.stream().distinct().toList();
        
        for (Long genreId : uniqueRequested) {
            if (!songGenreRepository.existsBySongIdAndGenreId(songId, genreId)) {
                SongGenre sg = new SongGenre();
                sg.setSongId(song.getSongId());
                sg.setGenreId(genreId);
                songGenreRepository.save(sg);
            }
        }
    }

    @Override
    @Transactional
    public void replaceGenres(Long songId, List<Long> genreIds) {
        log.info("Replacing genres for songId={} with {} genres", songId, genreIds.size());
        Song song = validateOwnedSongAndGenres(songId, genreIds);
        List<Long> uniqueGenreIds = genreIds.stream().distinct().toList();
        songGenreRepository.deleteBySongId(songId);
        
        for (Long genreId : uniqueGenreIds) {
            SongGenre sg = new SongGenre();
            sg.setSongId(song.getSongId());
            sg.setGenreId(genreId);
            songGenreRepository.save(sg);
        }
    }

    private Song validateOwnedSongAndGenres(Long songId, List<Long> genreIds) {
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song", songId));
        
        String role = securityUtil.getUserRole();
        if (!UserRole.ADMIN.name().equalsIgnoreCase(role)) {
            artistRepository.findById(song.getArtistId())
                .filter(a -> a.getUserId().equals(securityUtil.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Song", songId));
        }
        
        if (genreIds != null && !genreIds.isEmpty()) {
            long count = genreRepository.countByGenreIdIn(genreIds);
            if (count != genreIds.stream().distinct().count()) {
                throw new BadRequestException("One or more genre IDs are invalid");
            }
        }
        
        return song;
    }
}
