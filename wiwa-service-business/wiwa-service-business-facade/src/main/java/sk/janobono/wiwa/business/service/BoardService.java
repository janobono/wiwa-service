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

    BoardData getBoard(long id);

    BoardData addBoard(BoardChangeData data);

    BoardData setBoard(long id, BoardChangeData data);

    void deleteBoard(long id);

    BoardData setBoardImage(long boardId, MultipartFile multipartFile);

    BoardData deleteBoardImage(long boardId, String fileName);

    BoardData setBoardCategoryItems(long boardId, List<BoardCategoryItemChangeData> categoryItems);
}
