package orchestrator.repository;

import java.util.List;
import java.util.UUID;
import orchestrator.domain.job.Job;
import orchestrator.domain.job.JobState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByState(JobState state);
}
