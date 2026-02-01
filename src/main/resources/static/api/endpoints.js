export const API = {
  UPLOAD_FILE: () => `/api/jobs/upload`,
  SUBMIT_PROMPT: (jobId) => `/api/jobs/${jobId}/submit-prompt`,
  EXECUTE_JOB: (jobId) => `/api/jobs/${jobId}/execute`,
  VIEW_JOB_DETAIL: (jobId) => `/api/jobs/${jobId}`,
  DOWNLOAD_OUTPUT: (jobId) => `/api/jobs/${jobId}/output`,
};