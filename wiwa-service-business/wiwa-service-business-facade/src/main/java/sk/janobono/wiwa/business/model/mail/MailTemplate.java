package sk.janobono.wiwa.business.model.mail;

import lombok.Getter;

public enum MailTemplate {

    BASE("MailBaseTemplate", true);

    @Getter
    private final String template;
    private final boolean html;

    MailTemplate(final String template, final boolean html) {
        this.template = template;
        this.html = html;
    }

    public boolean getHtml() {
        return html;
    }
}
