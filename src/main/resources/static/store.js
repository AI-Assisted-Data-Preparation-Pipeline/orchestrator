export const JobState = {
  INIT: 'INIT',
  UPLOADED: 'UPLOADED',
  GENERATING_CODE: 'GENERATING_CODE',
  CODE_GENERATED: 'CODE_GENERATED',
  PROMPT_READY: 'PROMPT_READY',
  RUNNING: 'RUNNING',
  DONE: 'DONE',
  FAILED: 'FAILED',
};

export const store = {
  jobId: null,
  jobState: JobState.INIT,
};
