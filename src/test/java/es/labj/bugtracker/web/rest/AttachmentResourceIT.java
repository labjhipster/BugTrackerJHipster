package es.labj.bugtracker.web.rest;

import static es.labj.bugtracker.domain.AttachmentAsserts.*;
import static es.labj.bugtracker.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.labj.bugtracker.IntegrationTest;
import es.labj.bugtracker.domain.Attachment;
import es.labj.bugtracker.repository.AttachmentRepository;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttachmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttachmentMockMvc;

    private Attachment attachment;

    private Attachment insertedAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createEntity() {
        return new Attachment().name(DEFAULT_NAME).file(DEFAULT_FILE).fileContentType(DEFAULT_FILE_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Attachment createUpdatedEntity() {
        return new Attachment().name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);
    }

    @BeforeEach
    public void initTest() {
        attachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAttachment != null) {
            attachmentRepository.delete(insertedAttachment);
            insertedAttachment = null;
        }
    }

    @Test
    @Transactional
    void createAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Attachment
        var returnedAttachment = om.readValue(
            restAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attachment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Attachment.class
        );

        // Validate the Attachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAttachmentUpdatableFieldsEquals(returnedAttachment, getPersistedAttachment(returnedAttachment));

        insertedAttachment = returnedAttachment;
    }

    @Test
    @Transactional
    void createAttachmentWithExistingId() throws Exception {
        // Create the Attachment with an existing ID
        attachment.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attachment)))
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attachment.setName(null);

        // Create the Attachment, which fails.

        restAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attachment)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttachments() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        // Get all the attachmentList
        restAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    void getAttachment() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        // Get the attachment
        restAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, attachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attachment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64.getEncoder().encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    void getNonExistingAttachment() throws Exception {
        // Get the attachment
        restAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttachment() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attachment
        Attachment updatedAttachment = attachmentRepository.findById(attachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAttachment are not directly saved in db
        em.detach(updatedAttachment);
        updatedAttachment.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAttachment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAttachment))
            )
            .andExpect(status().isOk());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttachmentToMatchAllProperties(updatedAttachment);
    }

    @Test
    @Transactional
    void putNonExistingAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attachment.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attachment using partial update
        Attachment partialUpdatedAttachment = new Attachment();
        partialUpdatedAttachment.setId(attachment.getId());

        partialUpdatedAttachment.name(UPDATED_NAME);

        restAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttachment))
            )
            .andExpect(status().isOk());

        // Validate the Attachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttachment, attachment),
            getPersistedAttachment(attachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attachment using partial update
        Attachment partialUpdatedAttachment = new Attachment();
        partialUpdatedAttachment.setId(attachment.getId());

        partialUpdatedAttachment.name(UPDATED_NAME).file(UPDATED_FILE).fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttachment))
            )
            .andExpect(status().isOk());

        // Validate the Attachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttachmentUpdatableFieldsEquals(partialUpdatedAttachment, getPersistedAttachment(partialUpdatedAttachment));
    }

    @Test
    @Transactional
    void patchNonExistingAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attachment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attachment.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attachment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Attachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttachment() throws Exception {
        // Initialize the database
        insertedAttachment = attachmentRepository.saveAndFlush(attachment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attachment
        restAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, attachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attachmentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Attachment getPersistedAttachment(Attachment attachment) {
        return attachmentRepository.findById(attachment.getId()).orElseThrow();
    }

    protected void assertPersistedAttachmentToMatchAllProperties(Attachment expectedAttachment) {
        assertAttachmentAllPropertiesEquals(expectedAttachment, getPersistedAttachment(expectedAttachment));
    }

    protected void assertPersistedAttachmentToMatchUpdatableProperties(Attachment expectedAttachment) {
        assertAttachmentAllUpdatablePropertiesEquals(expectedAttachment, getPersistedAttachment(expectedAttachment));
    }
}
