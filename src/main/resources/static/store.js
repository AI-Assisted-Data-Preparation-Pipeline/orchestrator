export const JobState = {
  INIT: 'INIT',
  UPLOADED: 'UPLOADED',
  PROMPT_READY: 'PROMPT_READY',
  RUNNING: 'RUNNING',
  DONE: 'DONE',
  FAILED: 'FAILED',
};

export const store = {
  jobId: null,
  jobState: JobState.INIT,
};
