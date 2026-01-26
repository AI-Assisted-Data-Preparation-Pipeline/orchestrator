package orchestrator.service;


import static org.assertj.core.api.Assertions.assertThat;

import orchestrator.domain.job.Job;
import orchestrator.domain.job.JobState;
import orchestrator.dto.response.UploadResponse;
import orchestrator.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        Job job = jobRepository.findById(response.getJobId()).orElseThrow();
        assertThat(job.getState()).isEqualTo(JobState.FILE_UPLOADED);
        assertThat(job.getUploadPath()).isNotBlank();
    }

}