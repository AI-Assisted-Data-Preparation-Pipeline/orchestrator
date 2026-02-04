package orchestrator.service;


import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import orchestrator.common.dto.response.JobResponse;
import orchestrator.common.dto.response.SubmitPromptResponse;
import orchestrator.common.dto.response.UploadResponse;
import orchestrator.domain.job.Job;
import orchestrator.domain.job.JobState;
import orchestrator.infra.client.ai_engine.AiClient;
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

    @Test
    void 실행에_성공한_job_조회시_결과정보가_반환된다() {
        // given
        Job sample = succeedJobSample();

        // when
        JobResponse response = appService.getJobResponse(sample.getId());

        // then
        assertThat(response.getId()).isEqualTo(sample.getId().toString());
        assertThat(response.getState()).isEqualTo(JobState.SUCCESS.toString());
        assertThat(response.getOutputUrl().toString())  // download url
            .contains(sample.getId().toString())
            .contains("/output");
    }

//    @Test
//    void output_요청시_file이_반환된다() {
//        // given (fileStorageService도 모킹 필요)
//        Job sample = succeedJobSample();
//
//        // when
//        File file = appService.getOutputFile(sample.getId());
//
//        // then
//        assertThat(file).isNotNull();
//        assertThat(file.exists()).isTrue();
//        assertThat(file.getName()).isNotBlank();
//    }

    private Job createdJobSample() {
        return jobRepository.save(Job.instance());
    }

    private Job fileUploadedJobSample() {
        Job created = jobRepository.save(Job.instance());
        created.fileUploaded("test-file.txt");
        return created;
    }

    private Job succeedJobSample() {
        Job created = jobRepository.save(Job.instance());
        created.fileUploaded("test-file.txt");
        created.setGeneratedCode("test-generated-code");
        created.startExecute();
        created.addExecutionLog("test-execute-log");
        created.executeSuccess("test-output-path");
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