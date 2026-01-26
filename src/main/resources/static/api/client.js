export async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    //credentials: 'same-origin',
    ...options,
  });

  const contentType = response.headers.get('content-type');
  const body = contentType?.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    throw {
      status: response.status,
      message: body?.message || body || 'API Error',
      body,
    };
  }

  return body;
}