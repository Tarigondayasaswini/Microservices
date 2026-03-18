package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.dto.response.ImageUploadResponse;
import com.revplay.catalogservice.util.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.FILES)
@Tag(name = "Files", description = "Media streaming endpoints")
public class FileController {
    private static final MediaType AUDIO_MPEG = MediaType.parseMediaType("audio/mpeg");
    private static final int STREAM_BUFFER_SIZE = 64 * 1024;
    private final FileStorageService fileStorageService;

    @GetMapping("/songs/{fileName}")
    @Operation(summary = "Stream song audio by file name")
    public ResponseEntity<Resource> getSong(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadSong(fileName);
        return streamAudio(resource, fileName, request);
    }

    @GetMapping("/podcasts/{fileName}")
    @Operation(summary = "Stream podcast audio by file name")
    public ResponseEntity<Resource> getPodcast(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadPodcast(fileName);
        return streamAudio(resource, fileName, request);
    }

    @GetMapping("/images/{fileName}")
    @Operation(summary = "Fetch image by file name")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        log.debug("Serving image file {}", fileName);
        Resource resource = fileStorageService.loadImage(fileName);
        MediaType imageMediaType = MediaTypeFactory.getMediaType(fileName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(imageMediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload image file (.jpg, .jpeg, .png)")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(@RequestPart("file") MultipartFile file) {
        log.info("Uploading image file");
        String storedFileName = fileStorageService.storeImage(file);
        String imageUrl = ApiPaths.FILES + "/images/" + storedFileName;
        ImageUploadResponse response = new ImageUploadResponse(storedFileName, imageUrl);
        return ResponseEntity.ok(success(response, "Image uploaded successfully"));
    }

    private ResponseEntity<Resource> streamAudio(Resource resource, String fileName, HttpServletRequest request) throws IOException {
        Path filePath = resource.getFile().toPath();
        long fileLength = Files.size(filePath);
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);

        if (rangeHeader == null || rangeHeader.isBlank()) {
            return ResponseEntity.ok()
                    .contentType(AUDIO_MPEG)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentLength(fileLength)
                    .body(new InputStreamResource(Files.newInputStream(filePath)));
        }

        ByteRange byteRange = parseRange(rangeHeader, fileLength);
        if (byteRange == null) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                    .build();
        }

        long contentLength = byteRange.end() - byteRange.start() + 1;
        InputStreamResource partialStream = new InputStreamResource(new RangeInputStream(
                Files.newInputStream(filePath),
                byteRange.start(),
                contentLength
        ));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(AUDIO_MPEG)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE,
                        "bytes " + byteRange.start() + "-" + byteRange.end() + "/" + fileLength)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentLength(contentLength)
                .body(partialStream);
    }

    private ByteRange parseRange(String rangeHeader, long fileLength) {
        if (!rangeHeader.startsWith("bytes=")) {
            return null;
        }

        String rangeValue = rangeHeader.substring("bytes=".length()).trim();
        if (rangeValue.isEmpty() || rangeValue.contains(",")) {
            return null;
        }

        String[] parts = rangeValue.split("-", 2);
        if (parts.length != 2) {
            return null;
        }

        try {
            long start;
            long end;

            if (parts[0].isBlank()) {
                long suffixLength = Long.parseLong(parts[1]);
                if (suffixLength <= 0) {
                    return null;
                }
                suffixLength = Math.min(suffixLength, fileLength);
                start = fileLength - suffixLength;
                end = fileLength - 1;
            } else {
                start = Long.parseLong(parts[0]);
                if (start < 0 || start >= fileLength) {
                    return null;
                }

                if (parts[1].isBlank()) {
                    end = fileLength - 1;
                } else {
                    end = Long.parseLong(parts[1]);
                    if (end < start) {
                        return null;
                    }
                    end = Math.min(end, fileLength - 1);
                }
            }

            return new ByteRange(start, end);
        } catch (NumberFormatException ex) {
            log.warn("Invalid Range header {} for file {}", rangeHeader, fileLength);
            return null;
        }
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static final class ByteRange {
        private final long start;
        private final long end;

        private ByteRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        private long start() {
            return start;
        }

        private long end() {
            return end;
        }
    }

    private static final class RangeInputStream extends InputStream {
        private final InputStream delegate;
        private long remaining;

        private RangeInputStream(InputStream delegate, long start, long length) throws IOException {
            this.delegate = delegate;
            this.remaining = length;
            skipFully(start);
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }

            int nextByte = delegate.read();
            if (nextByte != -1) {
                remaining--;
            }
            return nextByte;
        }

        @Override
        public int read(byte[] buffer, int off, int len) throws IOException {
            if (remaining <= 0) {
                return -1;
            }

            int bytesRead = delegate.read(buffer, off, (int) Math.min(Math.min(len, STREAM_BUFFER_SIZE), remaining));
            if (bytesRead > 0) {
                remaining -= bytesRead;
            }
            return bytesRead;
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        private void skipFully(long bytesToSkip) throws IOException {
            long skipped = 0;
            while (skipped < bytesToSkip) {
                long current = delegate.skip(bytesToSkip - skipped);
                if (current > 0) {
                    skipped += current;
                    continue;
                }

                if (delegate.read() == -1) {
                    throw new IOException("Unexpected end of stream while seeking to range start");
                }
                skipped++;
            }
        }
    }
}
