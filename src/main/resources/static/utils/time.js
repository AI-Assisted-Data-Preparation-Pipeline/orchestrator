// ./utils/time.js

function parseLocalDateTime(localDateTime) {
  if (!localDateTime) return null;

  // LocalDateTime → ISO처럼 변환
  return new Date(localDateTime + 'Z');
}

export function formatDateTime(localDateTime) {
  const date = parseLocalDateTime(localDateTime);
  if (!date) return '-';

  return date.toLocaleString();
}

export function formatDuration(startIso, endIso) {
  const start = parseLocalDateTime(startIso);
  const end = parseLocalDateTime(endIso);

  if (!start || !end) return '-';

  let diff = Math.floor((end - start) / 1000);

  const minutes = Math.floor(diff / 60);
  const seconds = diff % 60;

  return `${minutes}m ${seconds}s`;
}
