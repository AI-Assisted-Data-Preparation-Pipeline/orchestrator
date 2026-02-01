// feature/execute/execute.js
import { store, JobState } from '../../store.js';
import { executeJob, fetchJobDetail, getDownloadUrl } from './execute.api.js';

const POLLING_INTERVAL = 2000;

let pollingTimer = null;

export function initExecuteView() {
  executeJob(store.jobId);

  const jobIdEl = document.getElementById('job-id');
  const stateEl = document.getElementById('job-state');
  const logEl = document.getElementById('execution-log');
  const actionsEl = document.getElementById('execute-actions');
  const downloadBtn = document.getElementById('download-btn');

  jobIdEl.textContent = store.jobId;

  async function poll() {
    try {
      const job = await fetchJobDetail(store.jobId);

      stateEl.textContent = job.state;
      logEl.textContent = job.log || '';

      store.jobState = job.state;

      if (
        job.state === JobState.EXECUTE_SUCCESS ||
        job.state === JobState.EXECUTE_FAILED
      ) {
        stopPolling();

        if (job.state === JobState.EXECUTE_SUCCESS) {
          actionsEl.style.display = 'block';
          downloadBtn.href = getDownloadUrl(store.jobId);
        }
      }
    } catch (e) {
      console.error('Polling error', e);
      stopPolling();
    }
  }

  startPolling(poll);
}

function startPolling(pollFn) {
  pollFn(); // 즉시 1회 실행
  pollingTimer = setInterval(pollFn, POLLING_INTERVAL);
}

function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer);
    pollingTimer = null;
  }
}
