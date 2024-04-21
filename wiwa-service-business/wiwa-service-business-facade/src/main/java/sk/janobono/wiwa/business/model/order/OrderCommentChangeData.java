package sk.janobono.wiwa.business.model.order;

public record OrderCommentChangeData(Boolean notifyUser, Long parentId, String comment) {
}
