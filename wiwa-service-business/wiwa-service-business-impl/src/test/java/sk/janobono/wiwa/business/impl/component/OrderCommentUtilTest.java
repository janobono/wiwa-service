package sk.janobono.wiwa.business.impl.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.janobono.wiwa.business.model.order.OrderCommentData;
import sk.janobono.wiwa.business.model.order.OrderUserData;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {OrderCommentUtil.class}
)
class OrderCommentUtilTest {

    @Autowired
    public OrderCommentUtil orderCommentUtil;

    @Test
    void addComment_validData_NoException() {
        final OrderUserData user = OrderUserData.builder().id(1L).build();

        List<OrderCommentData> comments = orderCommentUtil.addComment(new ArrayList<>(), user, null, "comment01");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(1);

        comments = orderCommentUtil.addComment(comments, user, null, "comment02");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);

        comments = orderCommentUtil.addComment(comments, user, 1L, "comment01-1");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().size()).isEqualTo(1);

        comments = orderCommentUtil.addComment(comments, user, 3L, "comment01-1-1");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().size()).isEqualTo(1);
        assertThat(comments.get(0).subComments().get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().get(0).subComments().size()).isEqualTo(1);
    }
}
