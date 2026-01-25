document.addEventListener('submit', async (e) => {
  if (e.target.id !== 'upload-form') return;

  e.preventDefault();

  const fileInput = document.getElementById('file');
  if (!fileInput.files.length) {
    alert('Select a file');
    return;
  }

  const formData = new FormData();
  formData.append('file', fileInput.files[0]);

  try {
    const res = await fetch('/api/v1/jobs/upload', {
      method: 'POST',
      body: formData
    });

    if (!res.ok) throw new Error('Upload failed');

    const result = await res.json();
    alert('Uploaded: ' + result.jobId);
  } catch (err) {
    alert(err.message);
  }
});
