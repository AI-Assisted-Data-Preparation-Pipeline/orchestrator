import { initUpload } from './feature/upload/upload.js';
import { JobState, store } from './store.js'

loadView('upload', () => {
  initUpload(() => {
    goNextStep();
  });
});

function goNextStep() {
  if (store.jobState === JobState.UPLOADED) {
    loadView('prompt', () => {

    });
  }
}

function loadView(view, onLoaded) {
  const app = document.getElementById('app');

  if (view === 'upload') {
    fetch('./feature/upload/upload.html')
      .then(res => res.text())
      .then(html => {
        app.innerHTML = html;
        onLoaded && onLoaded();
      });
  }

  if (view === 'prompt') {
    alert('프롬프트 입력 부분 개발준비 완료');
  }
}