function loadView(view) {
  const app = document.getElementById('app');

  if (view === 'upload') {
    fetch('/view/upload.html')
      .then(res => res.text())
      .then(html => {
        app.innerHTML = html;
        import('/js/upload.js');
      });
  }
}
