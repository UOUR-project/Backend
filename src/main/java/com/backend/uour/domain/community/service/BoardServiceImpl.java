package com.backend.uour.domain.community.service;

import com.backend.uour.domain.community.dto.BoardDetailDto;
import com.backend.uour.domain.community.dto.BoardListMicroDto;
import com.backend.uour.domain.community.dto.BoardPostDto;
import com.backend.uour.domain.community.dto.BoardListDto;
import com.backend.uour.domain.community.entity.Board;
import com.backend.uour.domain.community.entity.CATEGORY;
import com.backend.uour.domain.community.entity.LikeBoard;
import com.backend.uour.domain.community.entity.Scrap;
import com.backend.uour.domain.community.repository.ScrapRepository;
import com.backend.uour.domain.user.entity.User;
import com.backend.uour.domain.community.mapper.BoardMap;
import com.backend.uour.domain.community.repository.BoardRepository;
import com.backend.uour.domain.community.repository.LikeBoardRepository;
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

import java.time.LocalDateTime;
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

    @Override
    public void save(BoardPostDto board, String accessToken) throws Exception {
        User author = userRepository.findByEmail(jwtService.extractEmail(accessToken)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Board newboard = boardMap.ToEntity(board,author);
        boardRepository.save(newboard);
    }

    @Override
    public void update(Long boardId, BoardPostDto board, String accessToken) throws Exception {
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
        return boardMap.ToDetailDto(board);
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
    public void like(Long boardId, String authorization) throws Exception{
        User user = userRepository.findByEmail(jwtService.extractEmail(authorization)
                        .orElseThrow(WrongJwtException::new))
                .orElseThrow(NoUserException::new);
        Optional<LikeBoard> temp = likeBoardRepository.findByBoardIdAndUser(boardId, user);
        if(temp.isEmpty()){
            LikeBoard likeBoard = LikeBoard.builder()
                    .board(boardRepository.findById(boardId).get())
                    .user(user)
                    .build();
            likeBoardRepository.save(likeBoard);
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
            Scrap scrap = Scrap.builder()
                    .board(boardRepository.findById(boardId).get())
                    .user(user)
                    .build();
            scrapRepository.save(scrap);
        }
        else{
            scrapRepository.delete(temp.get());
        }
    }
}

