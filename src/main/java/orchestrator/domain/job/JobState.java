package orchestrator.domain.job;

public enum JobState {
    CREATED,
    FILE_UPLOADED,
    CODE_GENERATED,
    RUNNING,
    SUCCESS,
    FAILED,
}
