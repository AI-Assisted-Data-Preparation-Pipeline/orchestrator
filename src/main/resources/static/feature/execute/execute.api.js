// feature/execute/execute.api.js
import { API } from '../../api/endpoints.js';
import { apiRequest } from '../../api/client.js';

export function executeJob(jobId) {
  return apiRequest(API.EXECUTE_JOB(jobId), {
    method: 'POST'
  });
}

export function fetchJobDetail(jobId) {
  return apiRequest(API.VIEW_JOB_DETAIL(jobId), {
    method: 'GET',
  });
}

export function getDownloadUrl(jobId) {
  return API.DOWNLOAD_OUTPUT(jobId);
}
