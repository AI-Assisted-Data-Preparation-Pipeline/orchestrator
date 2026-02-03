package orchestrator.service;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import orchestrator.common.dto.response.SubmitPromptResponse;
import orchestrator.common.dto.response.UploadResponse;
import orchestrator.domain.job.Job;
import orchestrator.domain.job.JobState;
import orchestrator.infra.client.AiClient;
import orchestrator.repository.JobRepository;
import orchestrator.stub.AiClientStub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("ci")
class AppServiceTest {

    @Autowired
    AppService appService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void 파일을_업로드하면_Job이_생성되고_상태가_FILE_UPLOADED가_된다() {
        // given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.csv",
            "text/csv",
            "id,name\n1,test".getBytes()
        );

        // when
        UploadResponse response = appService.uploadFile(file);

        // then
        assertThat(response.getJobId()).isNotNull();
        assertThat(response.getJobState()).isEqualTo(JobState.FILE_UPLOADED);

        entityManager.flush();
        entityManager.clear();

        Job job = jobRepository.findById(response.getJobId()).orElseThrow();
        assertThat(job.getState()).isEqualTo(JobState.FILE_UPLOADED);
        assertThat(job.getFileName()).isNotBlank();
    }

    @Test
    void 프롬프트를_제출하면_코드_생성후_상태가_CODE_GENERATED가_된다() {
        // given
        Job sample = fileUploadedJobSample();

        // when
        SubmitPromptResponse response = appService.generateCode(sample.getId(), "test-prompt");

        // then
        assertThat(response.getJobId()).isEqualTo(sample.getId());
        assertThat(response.getGeneratedCode()).isEqualTo("test-generated-code"); // stub result

        entityManager.flush();
        entityManager.clear();

        Job found = jobRepository.findById(response.getJobId()).orElseThrow();
        assertThat(found.getGeneratedCode()).isEqualTo("test-generated-code"); // stub result
        assertThat(found.getState()).isEqualTo(JobState.CODE_GENERATED);
    }

    private Job createdJobSample() {
        return jobRepository.save(Job.instance());
    }

    private Job fileUploadedJobSample() {
        Job created = jobRepository.save(Job.instance());
        created.fileUploaded("test-file.txt");
        return created;
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        public AiClient aiClient() {
            return new AiClientStub();
        }
    }
}