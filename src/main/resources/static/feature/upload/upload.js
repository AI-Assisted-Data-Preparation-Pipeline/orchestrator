import { apiRequest } from '../../api/client.js';
import { API } from '../../api/endpoints.js';
import { store, JobState } from '../../store.js'

export function uploadFile(formData) {
  return apiRequest(API.UPLOAD_FILE, {
    method: 'POST',
    body: formData,
  });
}

export function initUpload(onSuccess) {
  const form = document.getElementById('upload-form');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(form);

    try {
      const result = await uploadFile(formData);
      store.jobId = result.jobId;
      store.jobState = JobState.UPLOADED;
      console.log('업로드 성공', result.message);
      onSuccess?.()
    } catch (err) {
      console.error('업로드 실패', err);
      alert(err.message);
    }
  });
}