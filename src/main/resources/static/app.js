// app.js
import { initUpload } from './feature/upload/upload.js';
import { initPrompt } from './feature/prompt/submit_prompt.js';
import { initExecuteView } from './feature/execute/execute.js';
import { JobState, store } from './store.js';

// 최초 진입
loadView('upload');

export function goNextStep() {
  switch (store.jobState) {
    case JobState.UPLOADED:
      loadView('prompt');
      break;

    case JobState.EXECUTE_READY:
    case JobState.EXECUTING:
      loadView('execute');
      break;

    default:
      console.warn('Unknown job state:', store.jobState);
  }
}

function loadView(view) {
  const app = document.getElementById('app');

  const viewMap = {
    upload: {
      path: './feature/upload/upload.html',
      init: () => initUpload(goNextStep),
    },
    prompt: {
      path: './feature/prompt/submit_prompt.html',
      init: () => initPrompt(goNextStep),
    },
    execute: {
      path: './feature/execute/execute.html',
      init: initExecuteView,
    },
  };

  const config = viewMap[view];
  if (!config) {
    console.error('Unknown view:', view);
    return;
  }

  fetch(config.path)
    .then(res => res.text())
    .then(html => {
      app.innerHTML = html;
      config.init && config.init();
    });
}
