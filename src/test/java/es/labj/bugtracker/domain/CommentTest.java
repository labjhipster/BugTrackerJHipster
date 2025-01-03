package es.labj.bugtracker.domain;

import static es.labj.bugtracker.domain.CommentTestSamples.*;
import static es.labj.bugtracker.domain.CommentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import es.labj.bugtracker.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void parentTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        comment.addParent(commentBack);
        assertThat(comment.getParents()).containsOnly(commentBack);
        assertThat(commentBack.getChild()).isEqualTo(comment);

        comment.removeParent(commentBack);
        assertThat(comment.getParents()).doesNotContain(commentBack);
        assertThat(commentBack.getChild()).isNull();

        comment.parents(new HashSet<>(Set.of(commentBack)));
        assertThat(comment.getParents()).containsOnly(commentBack);
        assertThat(commentBack.getChild()).isEqualTo(comment);

        comment.setParents(new HashSet<>());
        assertThat(comment.getParents()).doesNotContain(commentBack);
        assertThat(commentBack.getChild()).isNull();
    }

    @Test
    void childTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        comment.setChild(commentBack);
        assertThat(comment.getChild()).isEqualTo(commentBack);

        comment.child(null);
        assertThat(comment.getChild()).isNull();
    }
}
