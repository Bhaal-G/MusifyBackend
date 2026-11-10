package com.nb.musify.serviceImpl;

import com.nb.musify.dto.request.PlaylistRequest;
import com.nb.musify.dto.response.PlaylistResponse;
import com.nb.musify.entity.AppUser;
import com.nb.musify.entity.Playlist;
import com.nb.musify.repository.AppUserRepository;
import com.nb.musify.repository.PlaylistRepository;
import com.nb.musify.repository.PlaylistSongRepository;
import com.nb.musify.repository.SongRepository;
import com.nb.musify.service.PlaylistService;
import com.nb.musify.util.FileHandlerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private FileHandlerUtil fileHandlerUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public PlaylistResponse createPlaylist(PlaylistRequest request, MultipartFile imageFile, String email) {
        AppUser appUser = getUserByEmail(email);

        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setIsPublic(request.getIsPublic());
        playlist.setAppUser(appUser);
        if (imageFile !=null && !imageFile.isEmpty()){
            String uniqueId = UUID.randomUUID().toString();
            String imageExtension = fileHandlerUtil.getFileExtension(imageFile.getOriginalFilename());
            String imageFilename = uniqueId + imageExtension;
            fileHandlerUtil.saveImageFileWithName(imageFile,imageFilename);
            playlist.setImageUrl("/api/file/image/"+imageFilename);
        }
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return PlaylistResponse.fromEntity(savedPlaylist,baseUrl);
    }

    private AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }
}
