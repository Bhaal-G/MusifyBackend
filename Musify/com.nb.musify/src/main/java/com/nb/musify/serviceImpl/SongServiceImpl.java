package com.nb.musify.serviceImpl;

import com.nb.musify.dto.request.SongRequest;
import com.nb.musify.dto.response.PaginatedResponse;
import com.nb.musify.dto.response.SongResponse;
import com.nb.musify.entity.AppUser;
import com.nb.musify.entity.Song;
import com.nb.musify.repository.AppUserRepository;
import com.nb.musify.repository.PlaylistSongRepository;
import com.nb.musify.repository.SongRepository;
import com.nb.musify.service.SongService;
import com.nb.musify.util.FileHandlerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private FileHandlerUtil fileHandlerUtil;

    //Generic Gemini Service

    @Value("${app.base.url}")
    private String baseUrl;


    @Override
    public SongResponse addSong(SongRequest request, MultipartFile songFile, MultipartFile imageFile, String email) {
        AppUser appUser = getUserByEmail(email);
        String uniqueId = UUID.randomUUID().toString();

        Song song = new Song();
        song.setAppUser(appUser);
        updateSongMetadata(song,request);

        String songUrl =  processSongFile(songFile,uniqueId);
        song.setSongUrl(songUrl);

        String imageUrl = processImageFile(imageFile,uniqueId);
        song.setImageUrl(imageUrl);

        Song savedSong =songRepository.save(song);

        return SongResponse.fromEntity(savedSong,baseUrl);
    }


    @Override
    public Object getAllSongs(Long userId, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songPage;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasUserId = userId != null;

        if (hasUserId && hasSearch) {
            songPage = songRepository
                    .findByAppUserIdAndTitleContainingIgnoreCaseOrAppUserIdAndArtistContainingIgnoreCase(
                            userId, search.trim(),
                            userId, search.trim(),
                            pageable
                    );
        } else if (hasSearch) {
            songPage = songRepository
                    .findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(
                            search.trim(), search.trim(), pageable
                    );
        } else if (hasUserId) {
            songPage = songRepository.findByAppUserId(userId, pageable);
        } else {
            songPage = songRepository.findAll(pageable);
        }
        List<SongResponse> songResponses = songPage.getContent().stream()
                .map(song -> SongResponse.fromEntity(song, baseUrl))
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                songResponses,
                songPage.getNumber(),
                songPage.getSize(),
                songPage.getTotalElements(),
                songPage.getTotalPages(),
                songPage.isLast(),
                songPage.isFirst()
        );
    }


    private void updateSongMetadata(Song song, SongRequest request) {
        song.setTitle(request.getTitle());
        song.setArtist(request.getArtist());
    }

    private AppUser getUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    private String processSongFile(MultipartFile songFile, String uniqueId) {
        String songExtension = fileHandlerUtil.getFileExtension(songFile.getOriginalFilename());
        String songFilename = uniqueId+songExtension;
        fileHandlerUtil.saveSongFileWithName(songFile,songFilename);
        return "/api/file/song/"+songFilename;
    }

    private String processImageFile(MultipartFile imageFile, String uniqueId) {
        if (imageFile ==  null || imageFile.isEmpty()){
            return null;
        }
        String imageExtension = fileHandlerUtil.getFileExtension(imageFile.getOriginalFilename());
        String imageFileName = uniqueId + imageExtension;
        fileHandlerUtil.saveImageFileWithName(imageFile,imageFileName);
        return "/api/file/image/"+ imageFileName;
    }
}
