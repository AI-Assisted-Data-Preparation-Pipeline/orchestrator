import { store, JobState } from '../../store.js';
import { API } from '../../api/endpoints.js';
import { apiRequest } from '../../api/client.js';
import { goNextStep } from '../../app.js';

function submitPrompt(jobId, data) {
  return apiRequest(API.SUBMIT_PROMPT(jobId), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
  });
}

export function initPrompt() {
  const btn = document.getElementById('submitPromptBtn');
  const textarea = document.getElementById('promptInput');
  const status = document.getElementById('promptStatus');
  const preview = document.getElementById('generatedCodePreview');
  const runBtn = document.getElementById('runExecutionBtn');

  btn.addEventListener('click', async () => {
    const prompt = textarea.value.trim();

    if (!prompt) {
      alert('프롬프트를 입력하세요.');
      return;
    }

    if (!store.jobId) {
      alert('jobId가 없습니다.');
      return;
    }

    try {
      store.jobState = JobState.GENERATING_CODE;
      status.innerText = '코드 생성 중...';
      preview.innerText = '';

      const response = await submitPrompt(
        store.jobId,
        {prompt: prompt},
      )

      // response: { jobId, generatedCode }
      store.jobState = JobState.CODE_GENERATED;

      status.innerText = '코드 생성 완료';
      preview.innerText = response.generatedCode;

      runBtn.disabled = false;

    } catch (e) {
      console.error(e);
      store.jobState = JobState.FAILED;
      status.innerText = '코드 생성 실패';
      preview.innerText = '';
    }
  });

  runBtn.addEventListener('click', () => {
    if (store.jobState !== JobState.CODE_GENERATED) {
      alert('아직 코드가 생성되지 않았습니다.');
      return;
    }

    store.jobState = JobState.EXECUTE_READY
    goNextStep();
  });

}
