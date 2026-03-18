package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.entity.Artist;
import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.audit.AlbumDeletedEvent;
import com.revplay.catalogservice.dto.request.AlbumCreateRequest;
import com.revplay.catalogservice.dto.request.AlbumUpdateRequest;
import com.revplay.catalogservice.dto.response.AlbumResponse;
import com.revplay.catalogservice.entity.Album;
import com.revplay.catalogservice.entity.Song;
import com.revplay.catalogservice.mapper.AlbumMapper;
import com.revplay.catalogservice.repository.AlbumRepository;
import com.revplay.catalogservice.repository.SongRepository;
import com.revplay.catalogservice.service.AlbumService;
import com.revplay.catalogservice.util.AccessValidator;
import com.revplay.catalogservice.util.SecurityUtil;
import com.revplay.catalogservice.exception.BadRequestException;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumMapper mapper;
    private final SecurityUtil securityUtil;
    private final AccessValidator accessValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AlbumResponse create(AlbumCreateRequest request) {
        Long currentUserId = securityUtil.getUserId();
        log.info("Creating album for currentUserId={}", currentUserId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Artist artist = getOwnedArtistByUserId();
        Album album = mapper.toEntity(request, artist.getArtistId());
        return mapper.toResponse(albumRepository.save(album));
    }

    @Override
    @Transactional
    public AlbumResponse update(Long albumId, AlbumUpdateRequest request) {
        log.info("Updating albumId={}", albumId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Album album = getOwnedAlbum(albumId);
        mapper.updateEntity(album, request);
        return mapper.toResponse(albumRepository.save(album));
    }

    @Override
    public AlbumResponse get(Long albumId) {
        log.info("Fetching albumId={}", albumId);
        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));
        if (Boolean.FALSE.equals(album.getIsActive())) {
            throw new ResourceNotFoundException("Album", albumId);
        }
        return mapper.toResponse(album);
    }

    @Override
    @Transactional
    public void delete(Long albumId) {
        log.info("Deleting albumId={}", albumId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Album album = getOwnedAlbum(albumId);
        long count = songRepository.countByAlbumId(album.getAlbumId());
        if (count > 0) {
            throw new BadRequestException("Cannot delete album with songs");
        }
        album.setIsActive(Boolean.FALSE);
        albumRepository.save(album);
        eventPublisher.publishEvent(new AlbumDeletedEvent(album.getAlbumId()));
    }

    @Override
    public Page<AlbumResponse> listByArtist(Long artistId, Pageable pageable) {
        log.info("Listing albums for artistId={} page={}", artistId, pageable.getPageNumber());
        return albumRepository.findByArtistIdAndIsActiveTrue(artistId, pageable)
            .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void addSongToAlbum(Long albumId, Long songId) {
        log.info("Adding songId={} to albumId={}", songId, albumId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Album album = getOwnedAlbum(albumId);
        Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song", songId));
        if (!song.getArtistId().equals(album.getArtistId())) {
            throw new BadRequestException("Song artist mismatch");
        }
        song.setAlbumId(albumId);
        songRepository.save(song);
    }

    @Override
    @Transactional
    public void removeSongFromAlbum(Long albumId, Long songId) {
        log.info("Removing songId={} from albumId={}", songId, albumId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Album album = getOwnedAlbum(albumId);
        Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song", songId));
        if (song.getAlbumId() == null || !song.getAlbumId().equals(album.getAlbumId())) {
            throw new BadRequestException("Song not in album");
        }
        song.setAlbumId(null);
        songRepository.save(song);
    }

    private void ensureOwnership(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
        String role = securityUtil.getUserRole();
        if (!UserRole.ADMIN.name().equalsIgnoreCase(role) && !artist.getUserId().equals(securityUtil.getUserId())) {
            throw new ResourceNotFoundException("Artist", artistId);
        }
    }

    private Artist getOwnedArtistByUserId() {
        return artistRepository.findByUserId(securityUtil.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Artist profile", securityUtil.getUserId()));
    }

    private Album getOwnedAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));
        if (Boolean.FALSE.equals(album.getIsActive())) {
            throw new ResourceNotFoundException("Album", albumId);
        }
        ensureOwnership(album.getArtistId());
        return album;
    }
}
