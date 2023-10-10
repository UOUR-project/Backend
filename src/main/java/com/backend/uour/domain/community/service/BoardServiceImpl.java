package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.dto.*;
import com.backend.uour.domain.community.entity.*;
import com.backend.uour.domain.community.repository.*;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.community.mapper.BoardMap;
import com.backend.uour.domain.user.repository.UserRepository;
import com.backend.uour.global.exception.NoPostingException;
import com.backend.uour.global.exception.NoUserException;
import com.backend.uour.global.exception.NotSameAuthorException;
import com.backend.uour.global.exception.WrongJwtException;
import com.backend.uour.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class BoardServiceImpl implements BoardService {
    private final LikeBoardRepository likeBoardRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtService jwtService;
    private final BoardMap boardMap;
    private final ScrapRepository scrapRepository;
    private final PhotoHandler photoHandler;
    private final PhotoRepository photoRepository;
    private final PhotoService photoService;
    private final CommentRepository commentRepository;

    @Override
    public void save(BoardPostDto board, List<MultipartFile> photos, String accessToken) throws Exception {
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Board newboard = boardMap.ToEntity(board,author);
        List<Photo> photoList = photoHandler.parseFileInfo(newboard,photos);
        System.out.println(photoList);
        if (!photoList.isEmpty()){
            for(Photo photo : photoList){
                newboard.addPhoto(photoRepository.save(photo));
            }
        }
        boardRepository.save(newboard);
    }

    @Override
    public void update(Long boardId, BoardPostDto board, List<MultipartFile> multipartList, String accessToken) throws Exception {
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Board oldboard = boardRepository.findById(boardId).orElseThrow(NoPostingException::new);
        if (oldboard.getAuthor().getId().equals(author.getId())) {
            System.out.println("같은 유저");
            oldboard.setCategory(board.getCategory());
            oldboard.setContent(board.getContent());
            oldboard.setTitle(board.getTitle());
            oldboard.updateUpdateTime();
            List<MultipartFile> addFileList = new ArrayList<>();
            List<Photo> dbPhotoList = photoRepository.findAllByBoardId(boardId);

            if(CollectionUtils.isEmpty(dbPhotoList)) { // DB에 아예 존재 x
                if(!CollectionUtils.isEmpty(multipartList)) { // 전달되어온 파일이 하나라도 존재
                    // 저장할 파일 목록에 추가
                    addFileList.addAll(multipartList);
                }
            }
            else {  // DB에 한 장 이상 존재
                if(CollectionUtils.isEmpty(multipartList)) { // 전달되어온 파일 아예 x
                    // 파일 삭제
                    for(Photo dbPhoto : dbPhotoList)
                        photoService.deletePhoto(dbPhoto);
                }
                else {  // 전달되어온 파일 한 장 이상 존재

                    // DB에 저장되어있는 파일 원본명 목록
                    List<String> dbOriginNameList = new ArrayList<>();

                    // DB의 파일 원본명 추출
                    for(Photo dbPhoto : dbPhotoList) {
                        // file id로 DB에 저장된 파일 정보 얻어오기
                        PhotoDto dbPhotoDto = photoService.findByFileId(dbPhoto.getId());
                        // DB의 파일 원본명 얻어오기
                        String dbOrigFileName = dbPhotoDto.getOrigFileName();

                        if(!multipartList.contains(dbOrigFileName))  // 서버에 저장된 파일들 중 전달되어온 파일이 존재하지 않는다면
                            photoService.deletePhoto(dbPhoto);  // 파일 삭제
                        else  // 그것도 아니라면
                            dbOriginNameList.add(dbOrigFileName);	// DB에 저장할 파일 목록에 추가
                    }

                    for (MultipartFile multipartFile : multipartList) { // 전달되어온 파일 하나씩 검사
                        // 파일의 원본명 얻어오기
                        String multipartOrigName = multipartFile.getOriginalFilename();
                        if(!dbOriginNameList.contains(multipartOrigName)){   // DB에 없는 파일이면
                            addFileList.add(multipartFile); // DB에 저장할 파일 목록에 추가
                        }
                    }
                }
            }
            System.out.println(addFileList);
            List<Photo> newPhoto = photoHandler.parseFileInfo(oldboard,addFileList);
            if(!newPhoto.isEmpty()){
                photoRepository.saveAll(newPhoto);
            }
        } else {
            throw new NotSameAuthorException();
        }
    }

    @Override
    public void delete(Long boardId, String accessToken) throws Exception{
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(NoPostingException::new);
        if (board.getAuthor().equals(author)) {
            photoRepository.deleteAllByBoardId(boardId);
            boardRepository.delete(board);
        } else {
            throw new NotSameAuthorException();
        }
    }

    @Override
    public BoardDetailDto get(Long boardId) throws Exception{
        Board board = boardRepository.findById(boardId).orElseThrow(NoPostingException::new);
        board.addView();
        boardRepository.save(board);
        List<PhotoResponseDto> prds = photoService.findAllByBoard(boardId);
        List<Long> photoId = new ArrayList<>();
        for (PhotoResponseDto prd: prds){
            photoId.add(prd.getFileId());
        }
        return boardMap.ToDetailDto(board,photoId);
    }

    @Override
    public Slice<BoardListDto> getByCategory(CATEGORY category, int page) {
        Slice<Board> boards = boardRepository.findByCategory(category, PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }

    @Override
    public Slice<BoardListDto> getBySearch(String search, int page) {
        Slice<Board> boards = boardRepository.findByTitleContaining(search,PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }
    @Override
    public Slice<BoardListDto> getByCategoryAndSearch(CATEGORY category, String search, int page) {
        Slice<Board> boards = boardRepository.findByCategoryAndTitleContaining(category,search,PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }
    @Override
    public Slice<BoardListDto> getByUser(Long userId,int page) {
        Slice<Board> boards = boardRepository.findByAuthorId(userId,PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }

    // Entity find
    @Override
    public Slice<BoardListDto> getByLike(String accessToken, int page) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Slice<Board> boards = boardRepository.findByLikedUserId(user,PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }

    @Override
    public Slice<BoardListDto> getByScrap(String accessToken, int page) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Slice<Board> boards = boardRepository.findByScrapedUserId(user,PageRequest.of(page,10));
        return boards.map(boardMap::toListDto);
    }

    // Sorting
    @Override
    public Slice<BoardListMicroDto> getByPopular(int page){
        LocalDateTime start  = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();
        Slice<Board> boards = boardRepository.findByWriteTimeBetween(start,end,PageRequest.of(page,10,Sort.by(Sort.Direction.DESC,"view")) );
        return boards.map(boardMap::toListMicroDto);
    }
    @Override
    public Slice<BoardListMicroMicroDto> getByPopularMicroMicro(int page){
        LocalDateTime start  = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();
        Slice<Board> boards = boardRepository.findByWriteTimeBetween(start,end,PageRequest.of(page,10,Sort.by(Sort.Direction.DESC,"view")) );
        return boards.map(boardMap::toListMicroMicroDto);
    }

    @Override
    public void like(Long boardId, String authorization) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(authorization)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Optional<LikeBoard> temp = likeBoardRepository.findByBoardIdAndUser(boardId, user);
        if(temp.isEmpty()){
            Board board = boardRepository.findById(boardId).get();
            LikeBoard likeBoard = LikeBoard.builder()
                    .board(board)
                    .user(user)
                    .build();
            likeBoardRepository.save(likeBoard);
            user.addLikeBoard(likeBoard);
        }
        else{
            likeBoardRepository.delete(temp.get());
        }
    }
    @Override
    public void scrap(Long boardId, String authorization) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(authorization)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Optional<Scrap> temp = scrapRepository.findByBoardIdAndUser(boardId, user);
        if(temp.isEmpty()){
            Board board = boardRepository.findById(boardId).get();
            Scrap scrap = Scrap.builder()
                    .board(board)
                    .user(user)
                    .build();
            scrapRepository.save(scrap);
            user.addScrapBoard(scrap);
        }
        else{
            scrapRepository.delete(temp.get());
        }
    }
}
