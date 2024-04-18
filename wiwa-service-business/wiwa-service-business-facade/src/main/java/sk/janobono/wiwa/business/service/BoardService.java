package sk.janobono.wiwa.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.board.BoardCategoryItemChangeData;
import sk.janobono.wiwa.business.model.board.BoardChangeData;
import sk.janobono.wiwa.business.model.board.BoardData;
import sk.janobono.wiwa.business.model.board.BoardSearchCriteriaData;

import java.util.List;

public interface BoardService {

    Page<BoardData> getBoards(BoardSearchCriteriaData criteria, Pageable pageable);

    BoardData getBoard(Long id);

    BoardData addBoard(BoardChangeData data);

    BoardData setBoard(Long id, BoardChangeData data);

    void deleteBoard(Long id);

    BoardData setBoardImage(Long boardId, MultipartFile multipartFile);

    BoardData deleteBoardImage(Long boardId, String fileName);

    BoardData setBoardCategoryItems(Long boardId, List<BoardCategoryItemChangeData> categoryItems);
}
