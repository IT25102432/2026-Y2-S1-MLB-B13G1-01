/**
 * CineBook - Web-Based Cinema Ticket Reservation System
 * Consolidated Frontend JavaScript Application across all 6 Modules
 */

const API_BASE = ''; // Same origin

// Global State
let selectedBookingSeats = [];
let currentShowtimeDetails = null;
let appliedVoucher = null;
let currentHallSeats = [];

// ======================================================================
// ======================================================================
// 1. HARDENED APPLICATION INITIALIZATION & NAVIGATION
// ======================================================================
window.addEventListener('DOMContentLoaded', () => {
    initApp().catch(err => console.error('Critical failure in initApp():', err));
});

async function initApp() {
    // 1. Initialize UI event bindings first so navigation and forms work regardless of API status
    try {
        initNavigationTabs();
    } catch (err) {
        console.error('Error initializing navigation tabs:', err);
    }

    try {
        initFormListeners();
    } catch (err) {
        console.error('Error initializing form listeners:', err);
    }

    try {
        initSeatTooltip();
    } catch (err) {
        console.error('Error initializing seat tooltip:', err);
    }

    // 2. Load dashboard statistics
    try {
        await loadDashboardStats();
    } catch (err) {
        console.error('Error loading dashboard stats:', err);
    }

    // 3. Load all modules with defensive try/catch blocks
    try {
        await loadHalls();
    } catch (err) {
        console.error('Error loading halls module:', err);
    }

    try {
        await loadMovies();
    } catch (err) {
        console.error('Error loading movies module:', err);
    }

    try {
        await loadShowtimes();
    } catch (err) {
        console.error('Error loading showtimes module:', err);
    }

    try {
        await loadReservations();
    } catch (err) {
        console.error('Error loading reservations module:', err);
    }

    try {
        await loadRefunds();
    } catch (err) {
        console.error('Error loading refunds module:', err);
    }

    try {
        await loadVouchers();
    } catch (err) {
        console.error('Error loading vouchers module:', err);
    }

    // Auto-refresh dashboard stats every 20 seconds
    setInterval(() => {
        loadDashboardStats().catch(err => console.error('Auto-refresh dashboard stats failed:', err));
    }, 20000);
}

function initNavigationTabs() {
    const tabs = document.querySelectorAll('.nav-tab, .nav-tabs button, .nav-tabs a');
    tabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = tab.getAttribute('data-tab') || tab.getAttribute('href');
            switchTab(targetId);
        });
    });
}

function switchTab(tabId) {
    if (!tabId) return;
    const cleanId = String(tabId).replace(/^#/, '');

    const aliasMap = {
        'tab-halls': 'halls-section',
        'halls': 'halls-section',
        'halls-section': 'halls-section',
        'tab-movies': 'movies-section',
        'movies': 'movies-section',
        'movies-section': 'movies-section',
        'tab-showtimes': 'showtimes-section',
        'showtimes': 'showtimes-section',
        'showtimes-section': 'showtimes-section',
        'tab-reservations': 'reservations-section',
        'reservations': 'reservations-section',
        'reservations-section': 'reservations-section',
        'tab-refunds': 'refunds-section',
        'refunds': 'refunds-section',
        'refunds-section': 'refunds-section',
        'tab-vouchers': 'vouchers-section',
        'vouchers': 'vouchers-section',
        'vouchers-section': 'vouchers-section'
    };

    const sectionId = aliasMap[cleanId] || cleanId;
    const targetPane = document.getElementById(sectionId) ||
                       document.getElementById('tab-' + cleanId.replace('-section', '')) ||
                       document.getElementById(cleanId);

    // Remove active class from all section panels
    document.querySelectorAll('.tab-pane, main[id$="-section"], main[id^="tab-"], section[id$="-section"]').forEach(pane => {
        pane.classList.remove('active');
    });

    // Remove active class from all navigation tab buttons
    document.querySelectorAll('.nav-tab, .nav-tabs button, .nav-tabs a').forEach(btn => {
        btn.classList.remove('active');
        const btnTab = btn.getAttribute('data-tab') || (btn.getAttribute('href') ? btn.getAttribute('href').replace(/^#/, '') : '');
        if (btnTab && (btnTab === cleanId || btnTab === sectionId || aliasMap[btnTab] === sectionId)) {
            btn.classList.add('active');
        }
    });

    // Add active class to corresponding container section
    if (targetPane) {
        targetPane.classList.add('active');
    }

    // Trigger corresponding module load function when a tab is selected
    try {
        if (sectionId === 'halls-section' || cleanId.includes('hall')) {
            if (typeof loadHalls === 'function') loadHalls();
        } else if (sectionId === 'movies-section' || cleanId.includes('movie')) {
            if (typeof loadMovies === 'function') loadMovies();
        } else if (sectionId === 'showtimes-section' || cleanId.includes('showtime')) {
            if (typeof loadShowtimes === 'function') loadShowtimes();
        } else if (sectionId === 'reservations-section' || cleanId.includes('reservation')) {
            if (typeof loadReservations === 'function') {
                loadReservations();
            }
        } else if (sectionId === 'refunds-section' || cleanId.includes('refund')) {
            if (typeof loadRefunds === 'function') loadRefunds();
        } else if (sectionId === 'vouchers-section' || cleanId.includes('voucher')) {
            if (typeof loadVouchers === 'function') loadVouchers();
        }
    } catch (err) {
        console.error('Error during tab switch module loading:', err);
    }
}
window.switchTab = switchTab;

function initFormListeners() {
    const allocateHallForm = document.getElementById('allocateHallForm') || document.getElementById('createHallForm');
    if (allocateHallForm) {
        allocateHallForm.addEventListener('submit', handleAllocateHall);
    }

    const movieModalForm = document.getElementById('movieModalForm');
    if (movieModalForm) {
        movieModalForm.addEventListener('submit', handleSaveMovie);
    }

    const createShowtimeForm = document.getElementById('createShowtimeForm');
    if (createShowtimeForm) {
        createShowtimeForm.addEventListener('submit', handleCreateShowtime);
    }

    const createRefundForm = document.getElementById('createRefundForm');
    if (createRefundForm) {
        createRefundForm.addEventListener('submit', handleCreateRefund);
    }

    const createVoucherForm = document.getElementById('createVoucherForm');
    if (createVoucherForm) {
        createVoucherForm.addEventListener('submit', handleCreateVoucher);
    }

    const editHallForm = document.getElementById('editHallForm');
    if (editHallForm) {
        editHallForm.addEventListener('submit', handleSaveEditHall);
    }
}

async function loadReservations() {
    try {
        if (typeof loadShowtimeDropdownForBooking === 'function') {
            loadShowtimeDropdownForBooking();
        }
        if (typeof loadBookings === 'function') {
            await loadBookings();
        }
    } catch (err) {
        console.error('Error in loadReservations():', err);
    }
}
window.loadReservations = loadReservations;

// ======================================================================
// 2. DASHBOARD LIVE STATISTICS
// ======================================================================
async function loadDashboardStats() {
    try {
        const res = await fetch(`${API_BASE}/api/dashboard/stats`);
        if (!res.ok) throw new Error('Failed to fetch stats');
        const stats = await res.json();

        document.getElementById('statTotalCapacity').textContent = stats.totalCapacity ?? 0;
        document.getElementById('statAvailableSeats').textContent = stats.availableSeats ?? 0;
        document.getElementById('statMaintenanceSeats').textContent = stats.underMaintenanceSeats ?? 0;
        document.getElementById('statVipCount').textContent = stats.vipCount ?? 0;
        document.getElementById('statStandardCount').textContent = stats.standardCount ?? 0;

        const badge = document.getElementById('backendStatusBadge');
        if (badge) {
            badge.className = 'status-badge badge-online';
            badge.textContent = '● Backend Online';
        }
    } catch (err) {
        const badge = document.getElementById('backendStatusBadge');
        if (badge) {
            badge.className = 'status-badge badge-error';
            badge.textContent = '● Backend Offline';
        }
    }
}

// ======================================================================
// 3. MODULE 1: SEATING LAYOUT & HALL ALLOCATION (IT25102154)
// ======================================================================
function renderHallsTable(halls) {
    try {
        const hallsTableBody = document.getElementById('hallsTableBody');
        if (!hallsTableBody) {
            console.warn('renderHallsTable: hallsTableBody element not found in DOM.');
            return;
        }

        if (!Array.isArray(halls) || halls.length === 0) {
            hallsTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No registered halls found. Create one using the form on the right.</td></tr>`;
            return;
        }

        hallsTableBody.innerHTML = halls.map(h => {
            const id = h.id ?? '';
            const name = h.name ?? 'Unnamed Hall';
            const totalRows = h.totalRows ?? h.total_rows ?? 0;
            const seatsPerRow = h.seatsPerRow ?? h.seats_per_row ?? 0;
            const totalCapacity = h.totalCapacity ?? h.total_capacity ?? (totalRows * seatsPerRow);
            const hallType = h.hallType ?? h.hall_type ?? 'STANDARD';
            const basePrice = h.basePrice ?? h.base_price ?? 1200;

            return `
                <tr>
                    <td><b>#${id}</b></td>
                    <td><b>${escapeHtml(name)}</b></td>
                    <td>${totalRows} × ${seatsPerRow} (${totalCapacity} seats)</td>
                    <td><span class="badge">${escapeHtml(hallType)}</span></td>
                    <td>Rs. ${formatCurrency(basePrice)}</td>
                    <td>
                        <div class="action-btns">
                            <button class="btn btn-secondary btn-sm" onclick="openEditHallModal(${id}, '${escapeHtml(name)}', '${hallType}', ${basePrice})">✏️ Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteHall(${id})">🗑️</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        console.error('Error rendering halls table:', err);
        const hallsTableBody = document.getElementById('hallsTableBody');
        if (hallsTableBody) {
            hallsTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No registered halls found. Create one using the form on the right.</td></tr>`;
        }
    }
}

async function loadHalls() {
    const hallsTableBody = document.getElementById('hallsTableBody');
    const hallSelect = document.getElementById('hallSelect') || document.getElementById('layoutHallSelect');
    const showtimeHallSelect = document.getElementById('showtimeHallSelect');
    const badge = document.getElementById('hallsBadge');

    try {
        const res = await fetch(`${API_BASE}/api/halls`);
        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            const errMsg = errData.error || `HTTP ${res.status}: ${res.statusText || 'Could not fetch halls'}`;
            console.error('Error loading halls:', errMsg);

            if (hallsTableBody) {
                hallsTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No registered halls found. Create one using the form on the right.</td></tr>`;
            }
            if (hallSelect) {
                hallSelect.innerHTML = `<option value="">-- Select a Hall --</option>`;
            }
            if (showtimeHallSelect) {
                showtimeHallSelect.innerHTML = `<option value="">-- Choose Hall --</option>`;
            }
            if (badge) {
                badge.textContent = '0 halls';
            }
            return;
        }

        const halls = await res.json();

        if (!Array.isArray(halls) || halls.length === 0) {
            if (hallsTableBody) {
                hallsTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No registered halls found. Create one using the form on the right.</td></tr>`;
            }
            if (hallSelect) {
                hallSelect.innerHTML = `<option value="">-- Select a Hall --</option>`;
            }
            if (showtimeHallSelect) {
                showtimeHallSelect.innerHTML = `<option value="">-- Choose Hall --</option>`;
            }
            if (badge) {
                badge.textContent = '0 halls';
            }
            return;
        }

        if (badge) {
            badge.textContent = `${halls.length} halls`;
        }

        // Safely render table
        renderHallsTable(halls);

        // Safely populate hallSelect dropdown
        if (hallSelect) {
            const currentSelected = hallSelect.value;
            let optionsHtml = `<option value="">-- Select a Hall --</option>`;
            optionsHtml += halls.map(h => {
                const totalRows = h.totalRows ?? h.total_rows ?? 0;
                const seatsPerRow = h.seatsPerRow ?? h.seats_per_row ?? 0;
                const totalCap = h.totalCapacity ?? h.total_capacity ?? (totalRows * seatsPerRow);
                const hType = h.hallType ?? h.hall_type ?? 'STANDARD';
                const name = h.name ?? 'Unnamed Hall';
                return `<option value="${h.id}">${escapeHtml(name)} (${escapeHtml(hType)} - ${totalCap} seats)</option>`;
            }).join('');
            hallSelect.innerHTML = optionsHtml;

            const hallToLoad = currentSelected && halls.some(h => String(h.id) === String(currentSelected))
                ? currentSelected
                : halls[0].id;
            hallSelect.value = hallToLoad;

            if (typeof loadHallSeatingLayout === 'function') {
                loadHallSeatingLayout(hallToLoad);
            }
        }

        // Safely populate showtimeHallSelect
        if (showtimeHallSelect) {
            const hType = (h) => h.hallType ?? h.hall_type ?? 'STANDARD';
            showtimeHallSelect.innerHTML = `<option value="">-- Choose Hall --</option>` + halls.map(h => `
                <option value="${h.id}">${escapeHtml(h.name ?? 'Hall')} (${escapeHtml(hType(h))})</option>
            `).join('');
        }

    } catch (err) {
        console.error('Error in loadHalls():', err);
        if (hallsTableBody) {
            hallsTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No registered halls found. Create one using the form on the right.</td></tr>`;
        }
        if (hallSelect) {
            hallSelect.innerHTML = `<option value="">-- Select a Hall --</option>`;
        }
        if (badge) {
            badge.textContent = '0 halls';
        }
    }
}

async function handleAllocateHall(e) {
    if (e && e.preventDefault) {
        e.preventDefault();
    }

    const form = document.getElementById('allocateHallForm') || document.getElementById('createHallForm');
    const nameInput = document.getElementById('hallName');
    const totalRowsInput = document.getElementById('totalRows');
    const seatsPerRowInput = document.getElementById('seatsPerRow');
    const hallTypeInput = document.getElementById('hallType');
    const basePriceInput = document.getElementById('basePrice');

    const name = nameInput ? nameInput.value.trim() : '';
    const totalRows = totalRowsInput ? parseInt(totalRowsInput.value, 10) : 0;
    const seatsPerRow = seatsPerRowInput ? parseInt(seatsPerRowInput.value, 10) : 0;
    const hallType = hallTypeInput ? hallTypeInput.value : 'STANDARD';
    const basePrice = basePriceInput ? parseFloat(basePriceInput.value) : 1200;

    // Validation
    if (!name) {
        showToast('Hall name cannot be empty.', 'warning');
        return;
    }
    if (isNaN(totalRows) || totalRows < 1 || totalRows > 26) {
        showToast('Total rows must be between 1 and 26.', 'warning');
        return;
    }
    if (isNaN(seatsPerRow) || seatsPerRow < 1 || seatsPerRow > 30) {
        showToast('Seats per row must be between 1 and 30.', 'warning');
        return;
    }
    if (isNaN(basePrice) || basePrice <= 0) {
        showToast('Base price must be greater than 0 LKR.', 'warning');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/halls`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, totalRows, seatsPerRow, hallType, basePrice })
        });
        const data = await res.json().catch(() => ({}));

        if (!res.ok) {
            const errorMsg = data.error || (res.status === 400 || res.status === 409 ? 'Hall name already exists' : 'Failed to allocate hall');
            if (res.status === 400 || res.status === 409 || /already exists/i.test(errorMsg)) {
                showToast(`⚠️ ${errorMsg}`, 'warning');
            } else {
                showToast(`Error: ${errorMsg}`, 'error');
            }
            return;
        }

        const capacity = data.totalCapacity || data.total_capacity || (totalRows * seatsPerRow);
        showToast(`🎉 Hall "${data.name || name}" allocated successfully with ${capacity} seats!`, 'success');

        if (form) form.reset();
        if (totalRowsInput) totalRowsInput.value = 5;
        if (seatsPerRowInput) seatsPerRowInput.value = 8;
        if (basePriceInput) basePriceInput.value = 1200;

        await loadHalls();
        if (typeof loadDashboardStats === 'function') {
            loadDashboardStats();
        }
    } catch (err) {
        console.error('Error submitting allocate hall form:', err);
        showToast(`Error: ${err.message}`, 'error');
    }
}
window.handleAllocateHall = handleAllocateHall;
window.handleCreateHall = handleAllocateHall;

async function loadHallSeatingLayout(hallId) {
    if (!hallId) return;
    const container = document.getElementById('seatMatrixContainer');
    if (!container) return;
    container.innerHTML = `<div class="empty-matrix-msg">Generating seating matrix...</div>`;

    try {
        const res = await fetch(`${API_BASE}/api/halls/${hallId}/seats`);
        if (!res.ok) throw new Error('Could not load seats for this hall');
        const seats = await res.json();
        currentHallSeats = seats;

        renderSeatingMatrix(seats, container, async (seat) => {
            // Admin mode: toggle maintenance
            try {
                const patchRes = await fetch(`${API_BASE}/api/seats/${seat.id}/toggle`, { method: 'PATCH' });
                if (!patchRes.ok) throw new Error('Failed to toggle seat status');
                const updated = await patchRes.json();
                showToast(`Seat ${updated.seatLabel} is now ${updated.isActive ? 'AVAILABLE' : 'UNDER MAINTENANCE'}`, 'info');
                loadHallSeatingLayout(hallId);
                loadDashboardStats();
            } catch (err) {
                console.error('Error toggling seat maintenance:', err);
                showToast(err.message, 'error');
            }
        });
    } catch (err) {
        console.error('Error loading hall seating layout:', err);
        if (container) {
            container.innerHTML = `<div class="empty-matrix-msg" style="color:var(--danger)">${escapeHtml(err.message)}</div>`;
        }
    }
}

function renderSeatingMatrix(seats, container, onSeatClick) {
    if (!seats || seats.length === 0) {
        container.innerHTML = `<div class="empty-matrix-msg">No seats found for this hall layout.</div>`;
        return;
    }

    // Group seats by row
    const rowsMap = new Map();
    seats.forEach(s => {
        const row = s.seatRow;
        if (!rowsMap.has(row)) rowsMap.set(row, []);
        rowsMap.get(row).push(s);
    });

    container.innerHTML = '';
    rowsMap.forEach((rowSeats, rowLabel) => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'matrix-row';

        const labelSpan = document.createElement('span');
        labelSpan.className = 'row-label';
        labelSpan.textContent = rowLabel;
        rowDiv.appendChild(labelSpan);

        rowSeats.sort((a, b) => a.seatNumber - b.seatNumber);
        rowSeats.forEach(seat => {
            const seatBtn = document.createElement('button');
            seatBtn.className = 'seat-btn';
            seatBtn.textContent = seat.seatNumber;

            const isVip = (seat.seatType || '').toUpperCase() === 'VIP';
            const price = seat.priceLkr || (isVip ? 2000 : 1200);

            if (!seat.isActive || seat.status === 'MAINTENANCE') {
                seatBtn.classList.add('seat-maintenance');
            } else if (seat.status === 'BOOKED') {
                seatBtn.classList.add('seat-booked');
                seatBtn.disabled = true;
            } else if (isVip) {
                seatBtn.classList.add('seat-vip');
            } else {
                seatBtn.classList.add('seat-standard');
            }

            // Tooltip events
            seatBtn.addEventListener('mouseenter', (e) => {
                const statusStr = (!seat.isActive || seat.status === 'MAINTENANCE')
                    ? '🛠️ Under Maintenance'
                    : (seat.status === 'BOOKED' ? '🔒 Booked / Reserved' : '🟢 Available');
                showTooltip(e, `Row ${seat.seatRow} - Seat ${seat.seatNumber} | ${seat.seatType} | Rs. ${formatCurrency(price)} [${statusStr}]`);
            });
            seatBtn.addEventListener('mousemove', moveTooltip);
            seatBtn.addEventListener('mouseleave', hideTooltip);

            seatBtn.addEventListener('click', () => {
                if (onSeatClick) onSeatClick(seat, seatBtn);
            });

            rowDiv.appendChild(seatBtn);
        });

        container.appendChild(rowDiv);
    });
}

function openEditHallModal(id, name, type, price) {
    document.getElementById('editHallId').value = id;
    document.getElementById('editHallName').value = name;
    document.getElementById('editHallType').value = type;
    document.getElementById('editHallBasePrice').value = price;
    document.getElementById('editHallModal').classList.add('show');
}

function closeEditHallModal() {
    document.getElementById('editHallModal').classList.remove('show');
}

async function handleSaveEditHall(e) {
    e.preventDefault();
    const id = document.getElementById('editHallId').value;
    const name = document.getElementById('editHallName').value.trim();
    const hallType = document.getElementById('editHallType').value;
    const basePrice = parseFloat(document.getElementById('editHallBasePrice').value);

    try {
        const res = await fetch(`${API_BASE}/api/halls/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, hallType, basePrice })
        });
        if (!res.ok) throw new Error('Failed to update hall');
        showToast(`Hall #${id} updated successfully!`, 'success');
        closeEditHallModal();
        loadHalls();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteHall(id) {
    if (!confirm(`Are you sure you want to delete Cinema Hall #${id} and ALL its generated seats?`)) return;
    try {
        const res = await fetch(`${API_BASE}/api/halls/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete hall');
        showToast(`Cinema hall #${id} and associated seats deleted.`, 'info');
        loadHalls();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ======================================================================
// 4. MODULE 2: MOVIE CATALOG & DETAILS MANAGEMENT (IT25101311)
// ======================================================================
let allMoviesList = [];
let movieFilterStatus = null;

async function loadMovies() {
    const container = document.getElementById('movieGridContainer');
    const showtimeMovieSelect = document.getElementById('showtimeMovieSelect');

    try {
        let url = `${API_BASE}/api/movies`;
        if (movieFilterStatus) {
            url += `?status=${movieFilterStatus}`;
        }
        const res = await fetch(url);
        if (!res.ok) throw new Error('Failed to load movies');
        const movies = await res.json();
        allMoviesList = movies;

        if (movies.length === 0) {
            container.innerHTML = `<div class="loading-placeholder">No movies found under this filter.</div>`;
            return;
        }

        container.innerHTML = movies.map(m => {
            const isArchived = m.status === 'ARCHIVED';
            const poster = m.posterUrl && m.posterUrl.startsWith('http')
                ? m.posterUrl
                : 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=400&q=80';

            return `
                <div class="movie-card">
                    <div class="movie-poster-box">
                        <img src="${poster}" alt="${escapeHtml(m.title)}" class="movie-poster-img" onerror="this.src='https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=400&q=80'">
                        <span class="movie-rating-badge">${escapeHtml(m.rating || 'PG')}</span>
                        <span class="movie-status-pill ${isArchived ? 'status-archived' : 'status-active'}">${escapeHtml(m.status)}</span>
                    </div>
                    <div class="movie-body">
                        <h4 class="movie-title">${escapeHtml(m.title)}</h4>
                        <div class="movie-meta">⏱️ ${m.durationMins} mins | 🎭 ${escapeHtml(m.genre)}</div>
                        <p class="movie-desc">${escapeHtml(m.description || 'No description provided.')}</p>
                        <div class="movie-actions">
                            <button class="btn btn-secondary btn-sm" onclick="openEditMovieModal(${m.id})">✏️ Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteMovie(${m.id})">🗑️</button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        // Populate showtime movie select (only active movies)
        const activeMovies = movies.filter(m => m.status === 'ACTIVE');
        showtimeMovieSelect.innerHTML = `<option value="">-- Choose Movie --</option>` + activeMovies.map(m => `
            <option value="${m.id}">${escapeHtml(m.title)} (${m.durationMins}m)</option>
        `).join('');

    } catch (err) {
        container.innerHTML = `<div class="loading-placeholder" style="color:var(--danger)">Error: ${err.message}</div>`;
    }
}

function filterMovies(status, btn) {
    movieFilterStatus = status;
    document.querySelectorAll('.btn-filter').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
    loadMovies();
}

function openAddMovieModal() {
    document.getElementById('movieModalTitle').textContent = 'Add New Movie';
    document.getElementById('movieModalForm').reset();
    document.getElementById('modalMovieId').value = '';
    document.getElementById('modalMovieDuration').value = 120;
    document.getElementById('movieModal').classList.add('show');
}

function openEditMovieModal(id) {
    const movie = allMoviesList.find(m => m.id === id);
    if (!movie) return;
    document.getElementById('movieModalTitle').textContent = `Edit Movie #${id}`;
    document.getElementById('modalMovieId').value = movie.id;
    document.getElementById('modalMovieTitle').value = movie.title;
    document.getElementById('modalMovieGenre').value = movie.genre;
    document.getElementById('modalMovieDuration').value = movie.durationMins;
    document.getElementById('modalMovieRating').value = movie.rating;
    document.getElementById('modalMovieStatus').value = movie.status;
    document.getElementById('modalMoviePoster').value = movie.posterUrl || '';
    document.getElementById('modalMovieDesc').value = movie.description || '';
    document.getElementById('movieModal').classList.add('show');
}

function closeMovieModal() {
    document.getElementById('movieModal').classList.remove('show');
}

async function handleSaveMovie(e) {
    e.preventDefault();
    const id = document.getElementById('modalMovieId').value;
    const title = document.getElementById('modalMovieTitle').value.trim();
    const genre = document.getElementById('modalMovieGenre').value.trim();
    const durationMins = parseInt(document.getElementById('modalMovieDuration').value);
    const rating = document.getElementById('modalMovieRating').value;
    const status = document.getElementById('modalMovieStatus').value;
    const posterUrl = document.getElementById('modalMoviePoster').value.trim();
    const description = document.getElementById('modalMovieDesc').value.trim();

    if (!title) {
        showToast('Movie title cannot be empty.', 'warning');
        return;
    }
    if (isNaN(durationMins) || durationMins <= 0) {
        showToast('Movie duration must be greater than zero minutes.', 'warning');
        return;
    }

    const payload = { title, genre, durationMins, rating, status, posterUrl, description };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/api/movies/${id}` : `${API_BASE}/api/movies`;

        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to save movie');

        showToast(`Movie "${data.title}" saved successfully!`, 'success');
        closeMovieModal();
        loadMovies();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteMovie(id) {
    if (!confirm(`Delete movie #${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/api/movies/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete movie');
        showToast(`Movie #${id} was deleted.`, 'info');
        loadMovies();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ======================================================================
// 5. MODULE 3: SHOWTIME SCHEDULING (IT25103071)
// ======================================================================
let allShowtimesList = [];

async function loadShowtimes() {
    const tbody = document.getElementById('showtimesTableBody');
    try {
        const res = await fetch(`${API_BASE}/api/showtimes`);
        if (!res.ok) throw new Error('Could not fetch showtimes');
        const showtimes = await res.json();
        allShowtimesList = showtimes;

        document.getElementById('showtimesBadge').textContent = `${showtimes.length} sessions`;

        if (showtimes.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="loading-td">No scheduled screenings. Add a showtime session.</td></tr>`;
            return;
        }

        tbody.innerHTML = showtimes.map(s => `
            <tr>
                <td><b>#${s.id}</b></td>
                <td><b>${escapeHtml(s.movieTitle || 'Movie #' + s.movieId)}</b></td>
                <td><span class="badge">${escapeHtml(s.hallName || 'Hall #' + s.hallId)}</span></td>
                <td>${s.showDate}</td>
                <td><b>${s.startTime}</b></td>
                <td>${s.durationMins || 120} mins</td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="deleteShowtime(${s.id})">🗑️ Delete</button>
                </td>
            </tr>
        `).join('');

        loadShowtimeDropdownForBooking();
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="loading-td" style="color:var(--danger)">${err.message}</td></tr>`;
    }
}

async function handleCreateShowtime(e) {
    e.preventDefault();
    const movieId = parseInt(document.getElementById('showtimeMovieSelect').value);
    const hallId = parseInt(document.getElementById('showtimeHallSelect').value);
    const showDate = document.getElementById('showDate').value;
    const timeVal = document.getElementById('startTime').value;

    if (!movieId || !hallId || !showDate || !timeVal) {
        showToast('Please fill out all showtime scheduling fields.', 'warning');
        return;
    }

    const showDateTime = new Date(`${showDate}T${timeVal}`);
    if (showDateTime < new Date()) {
        showToast('⚠️ Cannot schedule showtimes for dates or times in the past.', 'warning');
        return;
    }

    const startTime = timeVal.length === 5 ? timeVal + ":00" : timeVal;

    try {
        const res = await fetch(`${API_BASE}/api/showtimes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ movieId, hallId, showDate, startTime })
        });
        const data = await res.json();

        if (res.status === 409 || !res.ok) {
            // Overlap conflict or validation error
            throw new Error(data.error || 'Showtime scheduling conflict detected.');
        }

        showToast(`🎉 Showtime scheduled successfully for ${data.movieTitle}!`, 'success');
        document.getElementById('createShowtimeForm').reset();
        loadShowtimes();
        loadDashboardStats();
    } catch (err) {
        showToast(`⚠️ ${err.message}`, 'error');
    }
}

async function deleteShowtime(id) {
    if (!confirm(`Delete showtime session #${id}? Any linked bookings will be removed.`)) return;
    try {
        const res = await fetch(`${API_BASE}/api/showtimes/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete showtime');
        showToast(`Showtime session #${id} deleted.`, 'info');
        loadShowtimes();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ======================================================================
// 6. MODULE 4: SEAT RESERVATIONS & BOOKING (IT25100266)
// ======================================================================
function loadShowtimeDropdownForBooking() {
    const select = document.getElementById('bookingShowtimeSelect');
    if (allShowtimesList.length === 0) {
        select.innerHTML = `<option value="">No showtimes available</option>`;
        return;
    }
    const currentVal = select.value;
    select.innerHTML = `<option value="">-- Choose Showtime Session --</option>` + allShowtimesList.map(s => `
        <option value="${s.id}">#${s.id} - ${escapeHtml(s.movieTitle)} in ${escapeHtml(s.hallName)} (${s.showDate} @ ${s.startTime})</option>
    `).join('');

    if (currentVal && allShowtimesList.some(s => s.id == currentVal)) {
        select.value = currentVal;
    } else if (allShowtimesList.length > 0) {
        select.value = allShowtimesList[0].id;
        loadBookingLayout(allShowtimesList[0].id);
    }
}

async function loadBookingLayout(showtimeId) {
    selectedBookingSeats = [];
    appliedVoucher = null;
    document.getElementById('bookingVoucherInput').value = '';
    document.getElementById('voucherMsg').textContent = '';
    updateBookingSummary();

    if (!showtimeId) {
        document.getElementById('sessionBrief').innerHTML = `<p class="text-muted">No showtime selected.</p>`;
        document.getElementById('bookingMatrixContainer').innerHTML = `<div class="empty-matrix-msg">Please select a showtime session to view live available seats.</div>`;
        return;
    }

    const container = document.getElementById('bookingMatrixContainer');
    container.innerHTML = `<div class="empty-matrix-msg">Loading real-time showtime seat availability...</div>`;

    try {
        const res = await fetch(`${API_BASE}/api/showtimes/${showtimeId}/seats`);
        if (!res.ok) throw new Error('Could not load seats for this showtime');
        const data = await res.json();
        currentShowtimeDetails = data;

        const st = data.showtime;
        const hl = data.hall;

        document.getElementById('sessionBrief').innerHTML = `
            <div><b>🎬 ${escapeHtml(st.movieTitle)}</b></div>
            <div>🏛️ ${escapeHtml(hl.name)} (${hl.hallType})</div>
            <div>📅 ${st.showDate} | 🕒 ${st.startTime}</div>
            <div class="text-muted" style="margin-top:4px;">Base: Rs. ${formatCurrency(hl.basePrice)} | VIP: Rs. 2,000.00</div>
        `;

        renderBookingSeatingMatrix(data.seats, container);
    } catch (err) {
        container.innerHTML = `<div class="empty-matrix-msg" style="color:var(--danger)">${err.message}</div>`;
    }
}

function renderBookingSeatingMatrix(seats, container) {
    if (!seats || seats.length === 0) {
        container.innerHTML = `<div class="empty-matrix-msg">No seats found for this session.</div>`;
        return;
    }

    const rowsMap = new Map();
    seats.forEach(s => {
        const row = s.seatRow;
        if (!rowsMap.has(row)) rowsMap.set(row, []);
        rowsMap.get(row).push(s);
    });

    container.innerHTML = '';
    rowsMap.forEach((rowSeats, rowLabel) => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'matrix-row';

        const labelSpan = document.createElement('span');
        labelSpan.className = 'row-label';
        labelSpan.textContent = rowLabel;
        rowDiv.appendChild(labelSpan);

        rowSeats.sort((a, b) => a.seatNumber - b.seatNumber);
        rowSeats.forEach(seat => {
            const seatBtn = document.createElement('button');
            seatBtn.className = 'seat-btn';
            seatBtn.textContent = seat.seatNumber;

            const isVip = (seat.seatType || '').toUpperCase() === 'VIP';
            const price = seat.priceLkr || (isVip ? 2000 : 1200);

            if (seat.status === 'MAINTENANCE') {
                seatBtn.classList.add('seat-maintenance');
                seatBtn.disabled = true;
            } else if (seat.status === 'BOOKED') {
                seatBtn.classList.add('seat-booked');
                seatBtn.disabled = true;
            } else if (isVip) {
                seatBtn.classList.add('seat-vip');
            } else {
                seatBtn.classList.add('seat-standard');
            }

            // Tooltip
            seatBtn.addEventListener('mouseenter', (e) => {
                let statusText = '🟢 Available';
                if (seat.status === 'MAINTENANCE') statusText = '🛠️ Under Maintenance (Locked)';
                if (seat.status === 'BOOKED') statusText = '🔒 Reserved / Booked';
                showTooltip(e, `Row ${seat.seatRow} - Seat ${seat.seatNumber} | ${seat.seatType} | Rs. ${formatCurrency(price)} [${statusText}]`);
            });
            seatBtn.addEventListener('mousemove', moveTooltip);
            seatBtn.addEventListener('mouseleave', hideTooltip);

            // Click interaction (User Booking Selection with 10-seat limit)
            seatBtn.addEventListener('click', () => {
                if (seat.status === 'MAINTENANCE') {
                    showToast(`⚠️ Seat ${seat.seatLabel} is under maintenance and cannot be booked.`, 'warning');
                    return;
                }
                if (seat.status === 'BOOKED') {
                    showToast(`⚠️ Seat ${seat.seatLabel} is already reserved for this session.`, 'warning');
                    return;
                }

                const existingIdx = selectedBookingSeats.findIndex(s => s.id === seat.id);
                if (existingIdx >= 0) {
                    // Deselect
                    selectedBookingSeats.splice(existingIdx, 1);
                    seatBtn.classList.remove('seat-selected');
                } else {
                    // Enforce Maximum 10 seats restriction
                    if (selectedBookingSeats.length >= 10) {
                        showToast('⚠️ Booking limit reached! You can select a maximum of 10 seats per transaction.', 'warning');
                        return;
                    }
                    selectedBookingSeats.push(seat);
                    seatBtn.classList.add('seat-selected');
                }

                updateBookingSummary();
            });

            rowDiv.appendChild(seatBtn);
        });

        container.appendChild(rowDiv);
    });
}

function updateBookingSummary() {
    const countSpan = document.getElementById('selectedSeatsCount');
    const tagsDiv = document.getElementById('selectedSeatsTags');
    const subtotalSpan = document.getElementById('priceSubtotal');
    const discountSpan = document.getElementById('priceDiscount');
    const totalSpan = document.getElementById('priceTotal');
    const confirmBtn = document.getElementById('confirmBookingBtn');

    countSpan.textContent = selectedBookingSeats.length;

    if (selectedBookingSeats.length === 0) {
        tagsDiv.innerHTML = `<span class="empty-tag">No seats selected</span>`;
        subtotalSpan.textContent = `Rs. 0.00`;
        discountSpan.textContent = `- Rs. 0.00`;
        totalSpan.textContent = `Rs. 0.00`;
        confirmBtn.disabled = true;
        return;
    }

    confirmBtn.disabled = false;
    tagsDiv.innerHTML = selectedBookingSeats.map(s => `
        <span class="seat-tag">${s.seatLabel} (${s.seatType} - Rs. ${formatCurrency(s.priceLkr || 1200)})</span>
    `).join('');

    let subtotal = 0;
    selectedBookingSeats.forEach(s => {
        subtotal += (s.priceLkr || 1200);
    });

    let discount = 0;
    if (appliedVoucher && appliedVoucher.discountAmountLkr) {
        discount = appliedVoucher.discountAmountLkr;
    }

    const total = Math.max(0, subtotal - discount);

    subtotalSpan.textContent = `Rs. ${formatCurrency(subtotal)}`;
    discountSpan.textContent = `- Rs. ${formatCurrency(discount)}`;
    totalSpan.textContent = `Rs. ${formatCurrency(total)}`;
}

async function applyVoucher() {
    const code = document.getElementById('bookingVoucherInput').value.trim();
    const msgDiv = document.getElementById('voucherMsg');

    if (!code) {
        msgDiv.className = 'voucher-msg error';
        msgDiv.textContent = 'Please enter a voucher code.';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/vouchers/validate/${encodeURIComponent(code)}`);
        const data = await res.json();
        if (!res.ok || !data.valid) {
            throw new Error(data.error || 'Invalid or expired voucher code');
        }

        appliedVoucher = data;
        msgDiv.className = 'voucher-msg success';
        msgDiv.textContent = `✓ ${data.message}`;
        updateBookingSummary();
        showToast(`🎉 Voucher ${data.code} applied! Saved Rs. ${formatCurrency(data.discountAmountLkr)}`, 'success');
    } catch (err) {
        appliedVoucher = null;
        msgDiv.className = 'voucher-msg error';
        msgDiv.textContent = `✗ ${err.message}`;
        updateBookingSummary();
        showToast(err.message, 'error');
    }
}

async function handleConfirmBooking(e) {
    e.preventDefault();
    if (selectedBookingSeats.length === 0) {
        showToast('Please select at least one seat to book.', 'warning');
        return;
    }
    if (selectedBookingSeats.length > 10) {
        showToast('Maximum 10 seats allowed per booking transaction.', 'warning');
        return;
    }

    const showtimeId = parseInt(document.getElementById('bookingShowtimeSelect').value);
    const customerName = document.getElementById('custName').value.trim();
    const customerEmail = document.getElementById('custEmail').value.trim();
    const seatIds = selectedBookingSeats.map(s => s.id);
    const voucherCode = appliedVoucher ? appliedVoucher.code : null;

    try {
        const res = await fetch(`${API_BASE}/api/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ showtimeId, customerName, customerEmail, seatIds, voucherCode })
        });
        const booking = await res.json();
        if (!res.ok) throw new Error(booking.error || 'Failed to complete booking');

        showToast(`🎉 Booking #${booking.id} confirmed for ${booking.customerName}!`, 'success');
        openReceiptModal(booking);

        // Reset state & reload
        document.getElementById('confirmBookingForm').reset();
        selectedBookingSeats = [];
        appliedVoucher = null;
        loadBookingLayout(showtimeId);
        loadBookings();
        loadDashboardStats();
    } catch (err) {
        showToast(`Booking Failed: ${err.message}`, 'error');
    }
}

function openReceiptModal(b) {
    const container = document.getElementById('receiptContent');
    const seatsStr = (b.seatLabels || []).join(', ');

    container.innerHTML = `
        <div class="receipt-item"><span><b>Booking Reference:</b></span><span>#${b.id}</span></div>
        <div class="receipt-item"><span><b>Customer:</b></span><span>${escapeHtml(b.customerName)}</span></div>
        <div class="receipt-item"><span><b>Email:</b></span><span>${escapeHtml(b.customerEmail)}</span></div>
        <div class="receipt-item"><span><b>Movie:</b></span><span>${escapeHtml(b.movieTitle || 'Cinema Screening')}</span></div>
        <div class="receipt-item"><span><b>Hall &amp; Screen:</b></span><span>${escapeHtml(b.hallName || 'Hall')}</span></div>
        <div class="receipt-item"><span><b>Session:</b></span><span>${b.showDate || ''} @ ${b.startTime || ''}</span></div>
        <div class="receipt-item"><span><b>Reserved Seats:</b></span><span><b>${seatsStr}</b></span></div>
        ${b.voucherCode ? `<div class="receipt-item" style="color:var(--success)"><span><b>Voucher Applied:</b></span><span>${escapeHtml(b.voucherCode)} (-Rs. ${formatCurrency(b.discountLkr)})</span></div>` : ''}
        <div class="receipt-total receipt-item"><span>Total Paid:</span><span>Rs. ${formatCurrency(b.totalAmountLkr)} LKR</span></div>
    `;

    document.getElementById('receiptModal').classList.add('show');
}

function closeReceiptModal() {
    document.getElementById('receiptModal').classList.remove('show');
}

async function loadBookings() {
    const tbody = document.getElementById('bookingsTableBody');
    try {
        const res = await fetch(`${API_BASE}/api/bookings`);
        if (!res.ok) throw new Error('Could not fetch bookings');
        const bookings = await res.json();

        if (bookings.length === 0) {
            tbody.innerHTML = `<tr><td colspan="9" class="loading-td">No booking records yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = bookings.map(b => {
            const seatsStr = (b.seatLabels || []).join(', ');
            const isCancelled = b.status === 'CANCELLED';
            return `
                <tr>
                    <td><b>#${b.id}</b></td>
                    <td>${escapeHtml(b.customerName)}<br><small class="text-muted">${escapeHtml(b.customerEmail)}</small></td>
                    <td><b>${escapeHtml(b.movieTitle || 'Movie #' + b.showtimeId)}</b></td>
                    <td>${escapeHtml(b.hallName || '-')}</td>
                    <td>${b.showDate || ''} ${b.startTime || ''}</td>
                    <td><span class="badge">${seatsStr || '-'}</span></td>
                    <td><b>Rs. ${formatCurrency(b.totalAmountLkr)}</b></td>
                    <td><span class="status-badge-table ${isCancelled ? 'status-cancelled' : 'status-confirmed'}">${b.status}</span></td>
                    <td>
                        <button class="btn btn-danger btn-sm" onclick="deleteBooking(${b.id})">🗑️ Delete</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="9" class="loading-td" style="color:var(--danger)">${err.message}</td></tr>`;
    }
}

async function deleteBooking(id) {
    if (!confirm(`Delete booking record #${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/api/bookings/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete booking');
        showToast(`Booking #${id} deleted.`, 'info');
        loadBookings();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ======================================================================
// 7. MODULE 5: REFUNDS & CANCELLATIONS (IT25102432)
// ======================================================================
async function loadRefunds() {
    const tbody = document.getElementById('refundsTableBody');
    try {
        const res = await fetch(`${API_BASE}/api/refunds`);
        if (!res.ok) throw new Error('Could not fetch refunds');
        const refunds = await res.json();

        document.getElementById('refundsBadge').textContent = `${refunds.length} requests`;

        if (refunds.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="loading-td">No refund cancellation requests found.</td></tr>`;
            return;
        }

        tbody.innerHTML = refunds.map(r => {
            const statusClass = `status-${r.status.toLowerCase()}`;
            const isPending = r.status === 'PENDING';

            return `
                <tr>
                    <td><b>#${r.id}</b></td>
                    <td>Booking #${r.bookingId}</td>
                    <td>${escapeHtml(r.customerName || 'Customer')}</td>
                    <td><b>Rs. ${formatCurrency(r.refundAmountLkr)}</b></td>
                    <td>${escapeHtml(r.reason || 'Requested by customer')}</td>
                    <td><span class="status-badge-table ${statusClass}">${r.status}</span></td>
                    <td>
                        ${isPending ? `
                            <div class="action-btns">
                                <button class="btn btn-success btn-sm" onclick="handleRefundStatus(${r.id}, 'APPROVED')">✓ Approve (Release Seats)</button>
                                <button class="btn btn-danger btn-sm" onclick="handleRefundStatus(${r.id}, 'REJECTED')">✗ Reject</button>
                            </div>
                        ` : `<span class="text-muted">Settled</span>`}
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="loading-td" style="color:var(--danger)">${err.message}</td></tr>`;
    }
}

async function handleCreateRefund(e) {
    e.preventDefault();
    const bookingId = parseInt(document.getElementById('refundBookingId').value);
    const reason = document.getElementById('refundReason').value.trim();
    const amountVal = document.getElementById('refundAmount').value;
    const refundAmountLkr = amountVal ? parseFloat(amountVal) : null;

    try {
        const res = await fetch(`${API_BASE}/api/refunds`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ bookingId, reason, refundAmountLkr })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to submit refund request');

        showToast(`Refund request #${data.id} submitted with status PENDING.`, 'success');
        document.getElementById('createRefundForm').reset();
        loadRefunds();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

/**
 * Approving refund executes SEAT RELEASE LOGIC:
 * marks booking as CANCELLED and unlocks seats!
 */
async function handleRefundStatus(refundId, newStatus) {
    if (!confirm(`Are you sure you want to set Refund #${refundId} to ${newStatus}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/api/refunds/${refundId}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to update refund status');

        if (newStatus === 'APPROVED') {
            showToast(`🎉 Refund #${refundId} APPROVED! SEAT RELEASE LOGIC EXECUTED: Booking #${data.bookingId} cancelled and seats freed!`, 'success');
        } else {
            showToast(`Refund #${refundId} was marked as REJECTED.`, 'info');
        }

        loadRefunds();
        loadBookings();
        loadDashboardStats();
        // Refresh booking seat matrix if active
        const curShowtime = document.getElementById('bookingShowtimeSelect').value;
        if (curShowtime) loadBookingLayout(curShowtime);
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ======================================================================
// 8. MODULE 6: LOYALTY PROGRAM & DISCOUNT VOUCHERS (IT24100907)
// ======================================================================
async function loadVouchers() {
    const tbody = document.getElementById('vouchersTableBody');
    try {
        const res = await fetch(`${API_BASE}/api/vouchers`);
        if (!res.ok) throw new Error('Could not fetch vouchers');
        const vouchers = await res.json();

        document.getElementById('vouchersBadge').textContent = `${vouchers.length} vouchers`;

        if (vouchers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="loading-td">No promotional vouchers created yet.</td></tr>`;
            return;
        }

        tbody.innerHTML = vouchers.map(v => `
            <tr>
                <td><b>#${v.id}</b></td>
                <td><b style="color:var(--primary); font-family:monospace; font-size:1rem;">${escapeHtml(v.code)}</b></td>
                <td><b>Rs. ${formatCurrency(v.discountAmountLkr)}</b></td>
                <td><span class="status-badge-table ${v.active ? 'status-confirmed' : 'status-cancelled'}">${v.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-secondary btn-sm" onclick="toggleVoucher(${v.id})">${v.active ? 'Disable' : 'Activate'}</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteVoucher(${v.id})">🗑️</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" class="loading-td" style="color:var(--danger)">${err.message}</td></tr>`;
    }
}

async function handleCreateVoucher(e) {
    e.preventDefault();
    const code = document.getElementById('voucherCodeInput').value.trim();
    const discountAmountLkr = parseFloat(document.getElementById('voucherDiscountInput').value);
    const isActive = document.getElementById('voucherActiveCheckbox').checked;

    if (isNaN(discountAmountLkr) || discountAmountLkr <= 0) {
        showToast('Discount amount must be greater than 0 LKR.', 'warning');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/vouchers`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ code: code || null, discountAmountLkr, isActive })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Failed to create voucher');

        showToast(`🎉 Voucher "${data.code}" with Rs. ${formatCurrency(data.discountAmountLkr)} discount created!`, 'success');
        document.getElementById('createVoucherForm').reset();
        document.getElementById('voucherDiscountInput').value = 200;
        document.getElementById('voucherActiveCheckbox').checked = true;
        loadVouchers();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function toggleVoucher(id) {
    try {
        const res = await fetch(`${API_BASE}/api/vouchers/${id}/toggle`, { method: 'PATCH' });
        if (!res.ok) throw new Error('Failed to toggle voucher');
        const updated = await res.json();
        showToast(`Voucher ${updated.code} is now ${updated.active ? 'ACTIVE' : 'INACTIVE'}`, 'info');
        loadVouchers();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteVoucher(id) {
    if (!confirm(`Delete voucher #${id}?`)) return;
    try {
        const res = await fetch(`${API_BASE}/api/vouchers/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete voucher');
        showToast(`Voucher #${id} deleted.`, 'info');
        loadVouchers();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function testCheckCode() {
    const code = document.getElementById('checkCodeInput').value.trim();
    const resultDiv = document.getElementById('checkCodeResult');

    if (!code) {
        resultDiv.className = 'check-result text-muted';
        resultDiv.textContent = 'Please enter a code.';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/vouchers/validate/${encodeURIComponent(code)}`);
        const data = await res.json();
        if (res.ok && data.valid) {
            resultDiv.className = 'check-result' style = "color:var(--success); font-weight:700;";
            resultDiv.textContent = `✓ Valid Voucher! Discount: Rs. ${formatCurrency(data.discountAmountLkr)}`;
        } else {
            resultDiv.className = 'check-result';
            resultDiv.style = "color:var(--danger); font-weight:600;";
            resultDiv.textContent = `✗ ${data.error || 'Invalid or inactive voucher'}`;
        }
    } catch (err) {
        resultDiv.style = "color:var(--danger); font-weight:600;";
        resultDiv.textContent = `✗ ${err.message}`;
    }
}

// ======================================================================
// 9. UTILITIES: TOASTS, TOOLTIPS, FORMATTERS
// ======================================================================
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let icon = 'ℹ️';
    if (type === 'success') icon = '✅';
    if (type === 'error') icon = '❌';
    if (type === 'warning') icon = '⚠️';

    toast.innerHTML = `<span>${icon}</span><span>${escapeHtml(message)}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(12px)';
        setTimeout(() => toast.remove(), 300);
    }, 4500);
}

function initSeatTooltip() {
    const tooltip = document.getElementById('seatTooltip');
    window.showTooltip = (e, text) => {
        tooltip.textContent = text;
        tooltip.style.display = 'block';
        tooltip.style.left = `${e.clientX}px`;
        tooltip.style.top = `${e.clientY}px`;
    };
    window.moveTooltip = (e) => {
        tooltip.style.left = `${e.clientX}px`;
        tooltip.style.top = `${e.clientY}px`;
    };
    window.hideTooltip = () => {
        tooltip.style.display = 'none';
    };
}

function formatCurrency(amount) {
    if (amount === undefined || amount === null || isNaN(amount)) return '0.00';
    return Number(amount).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
