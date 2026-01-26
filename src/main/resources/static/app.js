import { initUpload } from './feature/upload/upload.js';

loadView('upload', () => {
  initUpload((result) => {
    loadView('prompt_input');
  });
});

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

  if (view === 'prompt_input') {
    alert('프롬프트 입력 부분 개발준비 완료');
  }
}