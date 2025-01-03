package es.labj.bugtracker.domain;

import static es.labj.bugtracker.domain.AttachmentTestSamples.*;
import static es.labj.bugtracker.domain.TicketTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import es.labj.bugtracker.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attachment.class);
        Attachment attachment1 = getAttachmentSample1();
        Attachment attachment2 = new Attachment();
        assertThat(attachment1).isNotEqualTo(attachment2);

        attachment2.setId(attachment1.getId());
        assertThat(attachment1).isEqualTo(attachment2);

        attachment2 = getAttachmentSample2();
        assertThat(attachment1).isNotEqualTo(attachment2);
    }

    @Test
    void ticketTest() {
        Attachment attachment = getAttachmentRandomSampleGenerator();
        Ticket ticketBack = getTicketRandomSampleGenerator();

        attachment.setTicket(ticketBack);
        assertThat(attachment.getTicket()).isEqualTo(ticketBack);

        attachment.ticket(null);
        assertThat(attachment.getTicket()).isNull();
    }
}
