const API_BASE = '/api/showtimes';

const state = {
  showtimes: [],
  halls: [],
  filters: { search: '', hall: '', status: '', date: '' },
  editingId: null,
  pendingDeleteId: null,
};

const el = (id) => document.getElementById(id);

const grid = el('showtimeGrid');
const emptyState = el('emptyState');
const summaryBar = el('summaryBar');

const searchInput = el('searchInput');
const hallFilter = el('hallFilter');
const statusFilter = el('statusFilter');
const dateFilter = el('dateFilter');

const formOverlay = el('formOverlay');
const showtimeForm = el('showtimeForm');
const formTitle = el('formTitle');
const formTopError = el('formTopError');
const formEndPreview = el('formEndPreview');
const hallSelect = el('hallName');

const deleteOverlay = el('deleteOverlay');
const deleteMessage = el('deleteMessage');

const MONTHS = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC'];

console.log('Showtime Desk app.js loaded — build v2 (delete-dialog fix)');

init();

async function init() {
  try {
    bindStaticEvents();
  } catch (err) {
    console.error('Failed to bind UI event handlers — buttons may not respond:', err);
  }
  await loadHalls();
  await loadShowtimes();
}

function bindStaticEvents() {
  el('openCreateBtn').addEventListener('click', () => openForm());
  el('closeFormBtn').addEventListener('click', closeForm);
  el('cancelFormBtn').addEventListener('click', closeForm);
  formOverlay.addEventListener('click', (e) => { if (e.target === formOverlay) closeForm(); });

  el('closeDeleteBtn').addEventListener('click', closeDeleteDialog);
  el('cancelDeleteBtn').addEventListener('click', closeDeleteDialog);
  deleteOverlay.addEventListener('click', (e) => { if (e.target === deleteOverlay) closeDeleteDialog(); });
  el('confirmDeleteBtn').addEventListener('click', performDelete);

  showtimeForm.addEventListener('submit', handleSubmit);

  ['showTime', 'durationMinutes'].forEach((id) =>
    el(id).addEventListener('input', updateEndPreview));

  searchInput.addEventListener('input', () => { state.filters.search = searchInput.value.trim().toLowerCase(); render(); });
  hallFilter.addEventListener('change', () => { state.filters.hall = hallFilter.value; render(); });
  statusFilter.addEventListener('change', () => { state.filters.status = statusFilter.value; render(); });
  dateFilter.addEventListener('change', () => { state.filters.date = dateFilter.value; render(); });
  el('clearFiltersBtn').addEventListener('click', clearFilters);

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      if (!formOverlay.hidden) closeForm();
      if (!deleteOverlay.hidden) closeDeleteDialog();
    }
  });
}

function clearFilters() {
  state.filters = { search: '', hall: '', status: '', date: '' };
  searchInput.value = '';
  hallFilter.value = '';
  statusFilter.value = '';
  dateFilter.value = '';
  render();
}

/* ---------------- Data loading ---------------- */

async function loadHalls() {
  try {
    const res = await fetch(`${API_BASE}/halls`);
    state.halls = await res.json();
    const options = state.halls.map((h) => `<option value="${h}">${h}</option>`).join('');
    hallFilter.insertAdjacentHTML('beforeend', options);
    hallSelect.innerHTML = options;
  } catch (err) {
    showToast('Could not load hall list. Is the server running?', 'error');
  }
}

async function loadShowtimes() {
  try {
    const res = await fetch(API_BASE);
    if (!res.ok) throw new Error('Failed to load showtimes');
    state.showtimes = await res.json();
    render();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

/* ---------------- Rendering ---------------- */

function applyFilters(list) {
  const { search, hall, status, date } = state.filters;
  return list.filter((s) => {
    if (search) {
      const haystack = `${s.movieTitle} ${s.hallName} ${s.genre} ${s.language}`.toLowerCase();
      if (!haystack.includes(search)) return false;
    }
    if (hall && s.hallName !== hall) return false;
    if (status && s.status !== status) return false;
    if (date && s.showDate !== date) return false;
    return true;
  });
}

function render() {
  const filtered = applyFilters(state.showtimes);

  summaryBar.textContent = filtered.length === 0
    ? ''
    : `Showing ${filtered.length} of ${state.showtimes.length} showtime${state.showtimes.length === 1 ? '' : 's'}`;

  grid.innerHTML = filtered.map(renderCard).join('');
  emptyState.hidden = filtered.length !== 0;
  grid.hidden = filtered.length === 0;

  grid.querySelectorAll('[data-edit-id]').forEach((btn) =>
    btn.addEventListener('click', () => openForm(Number(btn.dataset.editId))));
  grid.querySelectorAll('[data-delete-id]').forEach((btn) =>
    btn.addEventListener('click', () => openDeleteDialog(Number(btn.dataset.deleteId))));
}

function renderCard(s) {
  const d = new Date(s.showDate + 'T00:00:00');
  const day = d.getDate();
  const month = MONTHS[d.getMonth()];
  const statusClass = `badge--status-${s.status.toLowerCase()}`;
  const statusLabel = s.status.charAt(0) + s.status.slice(1).toLowerCase();

  return `
    <article class="ticket">
      <div class="ticket__stub">
        <span class="ticket__day">${day}</span>
        <span class="ticket__month">${month}</span>
        <span class="ticket__time">${formatTime(s.showTime)}</span>
      </div>
      <div class="ticket__body">
        <div class="ticket__top">
          <div>
            <h3 class="ticket__title">${escapeHtml(s.movieTitle)}</h3>
            <p class="ticket__meta">${escapeHtml(s.genre)} · ${escapeHtml(s.language)} · ${s.durationMinutes} min</p>
          </div>
          <span class="badge ${statusClass}">${statusLabel}</span>
        </div>
        <div>
          <span class="badge badge--hall">${escapeHtml(s.hallName)}</span>
        </div>
        <div class="ticket__footer">
          <span class="ticket__price">LKR ${Number(s.ticketPrice).toFixed(2)}</span>
          <div class="ticket__actions">
            <button type="button" data-edit-id="${s.id}">Edit</button>
            <button type="button" class="danger" data-delete-id="${s.id}">Delete</button>
          </div>
        </div>
      </div>
    </article>
  `;
}

function formatTime(hhmm) {
  const [h, m] = hhmm.split(':').map(Number);
  const period = h >= 12 ? 'PM' : 'AM';
  const hour12 = ((h + 11) % 12) + 1;
  return `${hour12}:${String(m).padStart(2, '0')} ${period}`;
}

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

/* ---------------- Create / Edit form ---------------- */

function openForm(id) {
  clearFormErrors();
  showtimeForm.reset();
  state.editingId = id ?? null;

  if (id) {
    const showtime = state.showtimes.find((s) => s.id === id);
    formTitle.textContent = 'Edit showtime';
    el('showtimeId').value = showtime.id;
    el('movieTitle').value = showtime.movieTitle;
    el('genre').value = showtime.genre;
    el('language').value = showtime.language;
    el('hallName').value = showtime.hallName;
    el('showDate').value = showtime.showDate;
    el('showTime').value = showtime.showTime;
    el('durationMinutes').value = showtime.durationMinutes;
    el('ticketPrice').value = showtime.ticketPrice;
    el('status').value = showtime.status;
  } else {
    formTitle.textContent = 'Schedule a showtime';
    el('showtimeId').value = '';
    el('showDate').value = new Date().toISOString().slice(0, 10);
    el('status').value = 'SCHEDULED';
  }

  updateEndPreview();
  formOverlay.hidden = false;
  el('movieTitle').focus();
}

function closeForm() {
  formOverlay.hidden = true;
  state.editingId = null;
}

function updateEndPreview() {
  const time = el('showTime').value;
  const duration = Number(el('durationMinutes').value);
  if (!time || !duration) {
    formEndPreview.textContent = '';
    return;
  }
  const [h, m] = time.split(':').map(Number);
  const end = new Date(2000, 0, 1, h, m + duration);
  const endStr = formatTime(`${String(end.getHours()).padStart(2, '0')}:${String(end.getMinutes()).padStart(2, '0')}`);
  formEndPreview.textContent = `Screening ends around ${endStr} (a 15-minute turnaround is reserved after that for cleaning and entry).`;
}

function clearFormErrors() {
  formTopError.hidden = true;
  formTopError.textContent = '';
  document.querySelectorAll('.error-text').forEach((s) => (s.textContent = ''));
}

async function handleSubmit(e) {
  e.preventDefault();
  clearFormErrors();

  const payload = {
    movieTitle: el('movieTitle').value.trim(),
    genre: el('genre').value.trim(),
    language: el('language').value.trim(),
    hallName: el('hallName').value,
    showDate: el('showDate').value,
    showTime: el('showTime').value,
    durationMinutes: Number(el('durationMinutes').value),
    ticketPrice: Number(el('ticketPrice').value),
    status: el('status').value,
  };

  const submitBtn = el('submitFormBtn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Saving…';

  try {
    const isEdit = Boolean(state.editingId);
    const url = isEdit ? `${API_BASE}/${state.editingId}` : API_BASE;
    const method = isEdit ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      if (res.status === 400 && err.fieldErrors) {
        Object.entries(err.fieldErrors).forEach(([field, message]) => {
          const target = document.querySelector(`[data-error-for="${field}"]`);
          if (target) target.textContent = message;
        });
        formTopError.textContent = err.message || 'Please fix the highlighted fields.';
      } else {
        formTopError.textContent = err.message || 'Something went wrong while saving.';
      }
      formTopError.hidden = false;
      return;
    }

    await loadShowtimes();
    closeForm();
    showToast(isEdit ? 'Showtime updated.' : 'Showtime scheduled.', 'success');
  } catch (err) {
    formTopError.textContent = 'Could not reach the server. Please try again.';
    formTopError.hidden = false;
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'Save showtime';
  }
}

/* ---------------- Delete ---------------- */

function openDeleteDialog(id) {
  const showtime = state.showtimes.find((s) => s.id === id);
  state.pendingDeleteId = id;
  deleteMessage.textContent = `This will permanently remove "${showtime.movieTitle}" (${showtime.hallName}, ${showtime.showDate} at ${formatTime(showtime.showTime)}) from the schedule.`;
  deleteOverlay.hidden = false;
}

function closeDeleteDialog() {
  deleteOverlay.hidden = true;
  state.pendingDeleteId = null;
}

async function performDelete() {
  if (!state.pendingDeleteId) return;
  const confirmBtn = el('confirmDeleteBtn');
  confirmBtn.disabled = true;
  confirmBtn.textContent = 'Deleting…';

  try {
    const res = await fetch(`${API_BASE}/${state.pendingDeleteId}`, { method: 'DELETE' });
    if (!res.ok && res.status !== 204) throw new Error('Could not delete this showtime.');
    await loadShowtimes();
    closeDeleteDialog();
    showToast('Showtime deleted.', 'success');
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    confirmBtn.disabled = false;
    confirmBtn.textContent = 'Delete permanently';
  }
}

/* ---------------- Toasts ---------------- */

function showToast(message, type = 'success') {
  const host = el('toastHost');
  const toast = document.createElement('div');
  toast.className = `toast toast--${type}`;
  toast.textContent = message;
  host.appendChild(toast);
  setTimeout(() => toast.remove(), 4200);
}
