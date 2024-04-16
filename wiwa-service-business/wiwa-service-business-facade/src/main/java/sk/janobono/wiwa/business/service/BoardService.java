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

    Page<BoardData> getBoards(final BoardSearchCriteriaData criteria, final Pageable pageable);

    BoardData getBoard(final Long id);

    BoardData addBoard(final BoardChangeData data);

    BoardData setBoard(final Long id, final BoardChangeData data);

    void deleteBoard(final Long id);

    BoardData setBoardImage(final Long boardId, final MultipartFile multipartFile);

    BoardData deleteBoardImage(final Long boardId, final String fileName);

    BoardData setBoardCategoryItems(final Long boardId, final List<BoardCategoryItemChangeData> categoryItems);
}
