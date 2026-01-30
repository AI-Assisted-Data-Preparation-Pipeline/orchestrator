import { initUpload } from './feature/upload/upload.js';
import { initPrompt } from './feature/prompt/submit_prompt.js';
import { JobState, store } from './store.js'


loadView('upload', () => {
  initUpload(() => {
    goNextStep();
  });
});

function goNextStep() {
  if (store.jobState === JobState.UPLOADED) {
    loadView('prompt', () => {
      initPrompt(() => {
        goNextStep();
      });
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
    fetch('./feature/prompt/submit_prompt.html')
      .then(res => res.text())
      .then(html => {
        app.innerHTML = html;
        initPrompt();
        onLoaded && onLoaded();
      });
  }
}