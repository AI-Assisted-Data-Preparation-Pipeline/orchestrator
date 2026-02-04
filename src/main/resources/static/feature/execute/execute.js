// feature/execute/execute.js
import { store, JobState } from '../../store.js';
import { executeJob, fetchJobDetail, getDownloadUrl } from './execute.api.js';
import { formatDateTime, formatDuration } from '../../utils/time.js'

const POLLING_INTERVAL = 2000;

let pollingTimer = null;

export function initExecuteView() {
  executeJob(store.jobId);

  const jobIdEl = document.getElementById('job-id');
  const stateEl = document.getElementById('job-state');
  const logEl = document.getElementById('execution-log');
  const actionsEl = document.getElementById('execute-actions');
  const downloadBtn = document.getElementById('download-btn');

  const startedAtEl = document.getElementById('started-at');
  const finishedAtEl = document.getElementById('finished-at');
  const durationEl = document.getElementById('duration');

  jobIdEl.textContent = store.jobId;

  async function poll() {
    try {
      const job = await fetchJobDetail(store.jobId);

      stateEl.textContent = job.state;
      logEl.textContent = job.log || '';

      store.jobState = job.state;

      // ✅ start finish 시간 표시
      if (job.startedAt) {
        startedAtEl.textContent = formatDateTime(job.startedAt);
      }
      if (job.finishedAt) {
        finishedAtEl.textContent = formatDateTime(job.finishedAt);
      }

      // ✅ 소요 시간 계산
      if (job.startedAt && job.finishedAt) {
        durationEl.textContent = formatDuration(
          job.startedAt,
          job.finishedAt
        );
      }

      // ✅ 상태 완료 처리
      if (
        job.state === 'SUCCESS' ||
        job.state === 'FAILED'
      ) {
        stopPolling();

        if (job.state === 'SUCCESS') {
          actionsEl.style.display = 'block';
          downloadBtn.href = job.outputUrl;
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
  pollFn();
  pollingTimer = setInterval(pollFn, POLLING_INTERVAL);
}

function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer);
    pollingTimer = null;
  }
}
