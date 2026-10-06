/**
 * Frontend JavaScript for Cinema Seating Layout & Hall Allocation
 * Component Student ID: IT25102154
 * Implements Full CRUD REST API operations:
 *   [C] CREATE: POST /api/halls
 *   [R] READ:   GET /api/halls & GET /api/halls/{id}/seats
 *   [U] UPDATE: PUT /api/halls/{id} & PUT /api/halls/seats/{seatId}/status
 *   [D] DELETE: DELETE /api/halls/{id}
 */

// API Base URL (defaults to current origin if served via Spring Boot, or localhost:8080 if opened standalone)
const API_BASE_URL = (window.location.port === '8080' || window.location.hostname.length > 0) && window.location.protocol.startsWith('http')
    ? ''
    : 'http://localhost:8080';

// State management
let currentHalls = [];
let currentHall = null;
let currentSeats = [];
const selectedSeats = new Map(); // seatId -> seatObj
let activeMode = 'booking'; // 'booking' | 'maintenance'
let inspectedSeat = null;

// DOM Elements
const connectionBadge = document.getElementById('connectionBadge');
const hallsTableBody = document.getElementById('hallsTableBody');
const hallCountBadge = document.getElementById('hallCountBadge');
const refreshHallsBtn = document.getElementById('refreshHallsBtn');
const createHallForm = document.getElementById('createHallForm');
const submitHallBtn = document.getElementById('submitHallBtn');

const editHallModal = document.getElementById('editHallModal');
const editHallForm = document.getElementById('editHallForm');
const editHallId = document.getElementById('editHallId');
const editHallName = document.getElementById('editHallName');
const editHallType = document.getElementById('editHallType');
const editBasePrice = document.getElementById('editBasePrice');
const closeEditModalBtn = document.getElementById('closeEditModalBtn');
const cancelEditModalBtn = document.getElementById('cancelEditModalBtn');
const saveEditHallBtn = document.getElementById('saveEditHallBtn');

const hallSelect = document.getElementById('hallSelect');
const refreshLayoutBtn = document.getElementById('refreshLayoutBtn');
const hallDetails = document.getElementById('hallDetails');
const seatGrid = document.getElementById('seatGrid');
const seatInspector = document.getElementById('seatInspector');
const inspectorBody = document.getElementById('inspectorBody');

const modeBookingBtn = document.getElementById('modeBookingBtn');
const modeMaintenanceBtn = document.getElementById('modeMaintenanceBtn');
const modeAlert = document.getElementById('modeAlert');
const modeAlertText = document.getElementById('modeAlertText');

const legendStandardPrice = document.getElementById('legendStandardPrice');
const legendVipPrice = document.getElementById('legendVipPrice');

const selectedChips = document.getElementById('selectedChips');
const priceSummary = document.getElementById('priceSummary');
const clearSelectionBtn = document.getElementById('clearSelectionBtn');
const toast = document.getElementById('toast');

// Number formatter for Sri Lankan Rupees (LKR / Rs.)
function formatLkr(amount) {
    const num = Number(amount) || 0;
    return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Fallback Mock Data Generator (when backend is not reachable)
function generateFallbackData() {
    const mockHalls = [
        { id: 1, name: "Hall 1 - IMAX (Demo)", totalRows: 5, seatsPerRow: 8, hallType: "IMAX", basePrice: 1500, totalCapacity: 40 },
        { id: 2, name: "Hall 2 - VIP Lounge (Demo)", totalRows: 4, seatsPerRow: 6, hallType: "VIP", basePrice: 2000, totalCapacity: 24 },
        { id: 3, name: "Hall 3 - Standard Dolby (Demo)", totalRows: 5, seatsPerRow: 8, hallType: "STANDARD", basePrice: 1200, totalCapacity: 40 }
    ];

    const mockSeats = [];
    const rows = ['A', 'B', 'C', 'D', 'E'];
    let idCounter = 1;

    rows.forEach((rowLetter, rIdx) => {
        const isVip = (rIdx < 2); // First 2 rows are VIP
        for (let s = 1; s <= 8; s++) {
            mockSeats.push({
                id: idCounter++,
                hallId: 1,
                seatRow: rowLetter,
                seatNumber: s,
                seatType: isVip ? 'VIP' : 'STANDARD',
                isActive: (s !== 3 || rIdx !== 1) // Set B3 to disabled as demo
            });
        }
    });

    return { mockHalls, mockSeats };
}

// Show Toast Notification
function showToast(message, isError = false) {
    if (!toast) return;
    toast.textContent = message;
    toast.style.borderLeftColor = isError ? '#ef4444' : '#10b981';
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3800);
}

// Set connection status badge
function setConnectionStatus(isLive, message = '') {
    if (!connectionBadge) return;
    if (isLive) {
        connectionBadge.className = 'notice-badge badge-live';
        connectionBadge.textContent = '● Connected to Backend';
    } else {
        connectionBadge.className = 'notice-badge badge-fallback';
        connectionBadge.textContent = message || '● Fallback / Demo Mode';
    }
}

// ============================================================================
// [R] READ OPERATIONS: GET /api/halls & GET /api/halls/{id}/seats
// ============================================================================

/**
 * Fetch all cinema halls from backend (GET /api/halls)
 * and update both the Hall Directory table and the Layout dropdown.
 */
async function loadHalls(targetHallIdToSelect = null) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/halls`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();

        if (!Array.isArray(data) || data.length === 0) {
            throw new Error("No cinema halls returned from backend.");
        }

        currentHalls = data;
        setConnectionStatus(true);
        renderHallsTable(currentHalls);
        populateHallDropdown(currentHalls, targetHallIdToSelect);

    } catch (error) {
        console.warn("Backend unreachable or empty, falling back to mock mode:", error);
        const { mockHalls } = generateFallbackData();
        currentHalls = mockHalls;
        setConnectionStatus(false, '● Fallback / Demo Mode');
        renderHallsTable(currentHalls);
        populateHallDropdown(currentHalls, targetHallIdToSelect);
        showToast("Loaded fallback mock layout (Backend unreachable)", true);
    }
}

/**
 * Render [R] Cinema Halls Directory Table
 */
function renderHallsTable(halls) {
    if (!hallsTableBody) return;
    hallsTableBody.innerHTML = '';

    if (hallCountBadge) {
        hallCountBadge.textContent = `${halls.length} hall${halls.length === 1 ? '' : 's'}`;
    }

    if (!halls || halls.length === 0) {
        hallsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="table-loading">No cinema halls found. Allocate one above!</td>
            </tr>
        `;
        return;
    }

    halls.forEach(hall => {
        const tr = document.createElement('tr');
        if (currentHall && currentHall.id === hall.id) {
            tr.classList.add('active-row');
        }

        const capacity = hall.totalCapacity || (hall.totalRows * hall.seatsPerRow);
        const hallTypeLower = (hall.hallType || 'STANDARD').toLowerCase().replace(/\s+/g, '');
        const basePrice = hall.basePrice || 1200;

        tr.innerHTML = `
            <td><strong>#${hall.id}</strong></td>
            <td><strong>${hall.name}</strong></td>
            <td>${capacity} seats <span style="color: var(--text-muted); font-size: 0.8rem;">(${hall.totalRows}R × ${hall.seatsPerRow}C)</span></td>
            <td><span class="type-badge type-${hallTypeLower}">${hall.hallType}</span></td>
            <td><strong>Rs. ${formatLkr(basePrice)}</strong></td>
            <td>
                <div class="table-actions">
                    <button class="btn btn-xs btn-view" onclick="viewHallLayout(${hall.id})" title="View Seating Layout">
                        👁️ View Layout
                    </button>
                    <button class="btn btn-xs btn-edit" onclick="openEditModal(${hall.id})" title="Edit Hall Details">
                        ✏️ Edit
                    </button>
                    <button class="btn btn-xs btn-delete" onclick="deleteHall(${hall.id}, '${hall.name.replace(/'/g, "\\'")}')" title="Delete Hall & Seats">
                        🗑️ Delete
                    </button>
                </div>
            </td>
        `;
        hallsTableBody.appendChild(tr);
    });
}

/**
 * Populate Layout Hall Dropdown
 */
function populateHallDropdown(halls, targetIdToSelect = null) {
    if (!hallSelect) return;
    hallSelect.innerHTML = '';

    halls.forEach(hall => {
        const opt = document.createElement('option');
        opt.value = hall.id;
        const capacity = hall.totalCapacity || (hall.totalRows * hall.seatsPerRow);
        opt.textContent = `${hall.name} (${hall.hallType}) - ${capacity} seats`;
        hallSelect.appendChild(opt);
    });

    let selectedId = targetIdToSelect;
    if (!selectedId && currentHall && halls.some(h => h.id == currentHall.id)) {
        selectedId = currentHall.id;
    } else if (!selectedId && halls.length > 0) {
        selectedId = halls[0].id;
    }

    if (selectedId) {
        hallSelect.value = selectedId;
        loadSeatsForHall(selectedId);
    }
}

/**
 * Switch viewer to selected hall and smoothly scroll to Seating Layout section
 */
window.viewHallLayout = function(hallId) {
    if (hallSelect) {
        hallSelect.value = hallId;
    }
    loadSeatsForHall(hallId);

    const layoutSection = document.getElementById('seatingLayoutSection');
    if (layoutSection) {
        layoutSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
    showToast(`Loaded seating layout for Hall #${hallId}`);
};

/**
 * Fetch Seats for Hall (GET /api/halls/{id}/seats)
 */
async function loadSeatsForHall(hallId) {
    selectedSeats.clear();
    updateSelectionSummary();
    inspectedSeat = null;
    clearSeatInspector();

    currentHall = currentHalls.find(h => h.id == hallId) || null;
    renderHallsTable(currentHalls); // Update active row highlight

    const basePrice = (currentHall && currentHall.basePrice) ? currentHall.basePrice : 1200;
    const vipPrice = (currentHall && currentHall.hallType === 'VIP') ? basePrice : Math.round(basePrice * 1.6);
    const standardPrice = basePrice;

    if (legendStandardPrice) {
        legendStandardPrice.textContent = `Standard (Rs. ${formatLkr(standardPrice)})`;
    }
    if (legendVipPrice) {
        legendVipPrice.textContent = `VIP (Rs. ${formatLkr(vipPrice)})`;
    }

    if (currentHall) {
        const capacity = currentHall.totalCapacity || (currentHall.totalRows * currentHall.seatsPerRow);
        hallDetails.textContent = `${currentHall.name} | Type: ${currentHall.hallType} | Base: Rs. ${formatLkr(basePrice)} | Capacity: ${capacity} seats (${currentHall.totalRows} rows × ${currentHall.seatsPerRow} cols)`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/halls/${hallId}/seats`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const seats = await response.json();

        if (!Array.isArray(seats) || seats.length === 0) {
            throw new Error("No seats found for this hall.");
        }

        currentSeats = seats;
        renderSeatGrid(currentSeats);

    } catch (error) {
        console.warn("Error fetching seats from backend, using fallback generator:", error);
        const { mockSeats } = generateFallbackData();
        currentSeats = mockSeats;
        renderSeatGrid(currentSeats);
        setConnectionStatus(false, '● Fallback Layout Mode');
    }
}

/**
 * Render Interactive Seating Grid
 */
function renderSeatGrid(seats) {
    if (!seatGrid) return;
    seatGrid.innerHTML = '';

    // Group seats by row
    const rowMap = new Map();
    seats.forEach(seat => {
        if (!rowMap.has(seat.seatRow)) {
            rowMap.set(seat.seatRow, []);
        }
        rowMap.get(seat.seatRow).push(seat);
    });

    // Sort rows alphabetically (A, B, C...)
    const sortedRows = Array.from(rowMap.keys()).sort();

    sortedRows.forEach(rowLetter => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        // Left Row Label
        const leftLabel = document.createElement('span');
        leftLabel.className = 'row-label';
        leftLabel.textContent = rowLetter;
        rowDiv.appendChild(leftLabel);

        // Sort seats by number
        const rowSeats = rowMap.get(rowLetter).sort((a, b) => a.seatNumber - b.seatNumber);
        rowSeats.forEach(seat => {
            const seatBtn = document.createElement('div');
            const isVip = (seat.seatType && seat.seatType.toUpperCase() === 'VIP');
            const isActive = (seat.isActive !== false && seat.active !== false);
            const isSelected = selectedSeats.has(seat.id);
            const isInspected = inspectedSeat && inspectedSeat.id === seat.id;

            seatBtn.className = `seat ${isVip ? 'vip' : 'standard'} ${!isActive ? 'disabled' : ''} ${isSelected ? 'selected' : ''} ${isInspected ? 'inspected-seat' : ''}`;
            seatBtn.textContent = seat.seatNumber;
            seatBtn.dataset.seatId = seat.id;
            seatBtn.title = `Seat ${seat.seatRow}${seat.seatNumber} (${seat.seatType}) - ${isActive ? 'Available' : 'Under Maintenance / Disabled'}`;

            // Click Handler
            seatBtn.addEventListener('click', () => {
                if (activeMode === 'maintenance') {
                    // Admin Maintenance Toggle Mode: Click toggles status directly!
                    toggleSeatMaintenance(seat.id);
                } else {
                    // Customer Booking Mode: Click selects for booking and inspects
                    inspectSeat(seat);
                    if (isActive) {
                        toggleSeatSelection(seat, seatBtn);
                    } else {
                        showToast(`Seat ${seat.seatRow}${seat.seatNumber} is Under Maintenance and cannot be booked.`, true);
                    }
                }
            });

            rowDiv.appendChild(seatBtn);
        });

        // Right Row Label
        const rightLabel = document.createElement('span');
        rightLabel.className = 'row-label';
        rightLabel.textContent = rowLetter;
        rowDiv.appendChild(rightLabel);

        seatGrid.appendChild(rowDiv);
    });
}

// ============================================================================
// [C] CREATE OPERATION: POST /api/halls
// ============================================================================

createHallForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const hallPayload = {
        name: document.getElementById('hallName').value.trim(),
        totalRows: parseInt(document.getElementById('totalRows').value, 10),
        seatsPerRow: parseInt(document.getElementById('seatsPerRow').value, 10),
        hallType: document.getElementById('hallType').value,
        basePrice: parseFloat(document.getElementById('basePrice').value) || 1200.0
    };

    submitHallBtn.disabled = true;
    submitHallBtn.textContent = 'Allocating & Generating Seats...';

    try {
        const response = await fetch(`${API_BASE_URL}/api/halls`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(hallPayload)
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.error || `Server responded with ${response.status}`);
        }

        const newHall = await response.json();
        showToast(`[C] Cinema hall "${newHall.name}" created with auto-generated seats!`);

        // Reset form
        createHallForm.reset();
        document.getElementById('totalRows').value = 5;
        document.getElementById('seatsPerRow').value = 8;
        document.getElementById('basePrice').value = 1200;

        // Reload halls and select newly created hall
        await loadHalls(newHall.id);

    } catch (error) {
        console.error("Error creating cinema hall:", error);
        showToast(`Error creating hall: ${error.message}`, true);
    } finally {
        submitHallBtn.disabled = false;
        submitHallBtn.textContent = '➕ Allocate Hall & Auto-Generate Seats (POST)';
    }
});

// ============================================================================
// [U] UPDATE OPERATIONS: PUT /api/halls/{id} & PUT /api/halls/seats/{id}/status
// ============================================================================

/**
 * Open Edit Cinema Hall Modal (PUT /api/halls/{id})
 */
window.openEditModal = function(hallId) {
    const hall = currentHalls.find(h => h.id == hallId);
    if (!hall) return;

    editHallId.value = hall.id;
    editHallName.value = hall.name;
    editHallType.value = hall.hallType;
    editBasePrice.value = hall.basePrice || 1200;

    editHallModal.classList.add('show');
};

function closeEditModal() {
    editHallModal.classList.remove('show');
}

closeEditModalBtn.addEventListener('click', closeEditModal);
cancelEditModalBtn.addEventListener('click', closeEditModal);

// Close on clicking backdrop outside modal box
editHallModal.addEventListener('click', (e) => {
    if (e.target === editHallModal) {
        closeEditModal();
    }
});

/**
 * Handle Edit Cinema Hall Form Submit (PUT /api/halls/{id})
 */
editHallForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const hallId = editHallId.value;
    const updatePayload = {
        name: editHallName.value.trim(),
        hallType: editHallType.value,
        basePrice: parseFloat(editBasePrice.value) || 1200.0
    };

    saveEditHallBtn.disabled = true;
    saveEditHallBtn.textContent = 'Saving Changes...';

    try {
        const response = await fetch(`${API_BASE_URL}/api/halls/${hallId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatePayload)
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.error || `Server responded with ${response.status}`);
        }

        const updatedHall = await response.json();
        showToast(`[U] Cinema hall #${hallId} updated successfully!`);
        closeEditModal();

        // Reload halls and keep this hall active
        await loadHalls(updatedHall.id);

    } catch (error) {
        console.error("Error updating cinema hall:", error);
        showToast(`Error updating hall: ${error.message}`, true);
    } finally {
        saveEditHallBtn.disabled = false;
        saveEditHallBtn.textContent = '💾 Save Changes (PUT)';
    }
});

/**
 * Toggle Seat Active / Maintenance Status (PUT /api/halls/seats/{seatId}/status)
 */
window.toggleSeatMaintenance = async function(seatId, explicitStatus = null) {
    const seat = currentSeats.find(s => s.id == seatId);
    if (!seat) return;

    const requestBody = explicitStatus !== null
        ? JSON.stringify({ isActive: explicitStatus })
        : null;

    try {
        const response = await fetch(`${API_BASE_URL}/api/halls/seats/${seatId}/status`, {
            method: 'PUT',
            headers: requestBody ? { 'Content-Type': 'application/json' } : {},
            body: requestBody
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.error || `Server responded with ${response.status}`);
        }

        const updatedSeat = await response.json();

        // Update local seat data
        seat.isActive = (updatedSeat.isActive !== false && updatedSeat.active !== false);

        // If seat became inactive, remove from customer booking selection
        if (!seat.isActive && selectedSeats.has(seat.id)) {
            selectedSeats.delete(seat.id);
            updateSelectionSummary();
        }

        renderSeatGrid(currentSeats);
        inspectSeat(seat);

        const statusLabel = seat.isActive ? 'Available (Active)' : 'Under Maintenance (Inactive)';
        showToast(`[U] Seat ${seat.seatRow}${seat.seatNumber} set to: ${statusLabel}`);

    } catch (error) {
        console.warn("Backend error updating seat status, updating locally:", error);
        // Fallback local update
        seat.isActive = explicitStatus !== null ? explicitStatus : !seat.isActive;
        if (!seat.isActive && selectedSeats.has(seat.id)) {
            selectedSeats.delete(seat.id);
            updateSelectionSummary();
        }
        renderSeatGrid(currentSeats);
        inspectSeat(seat);
        showToast(`[Demo] Seat ${seat.seatRow}${seat.seatNumber} toggled to: ${seat.isActive ? 'Available' : 'Under Maintenance'}`);
    }
};

/**
 * Display Seat Details in [U] Seat Inspector Panel
 */
function inspectSeat(seat) {
    inspectedSeat = seat;
    const basePrice = (currentHall && currentHall.basePrice) ? currentHall.basePrice : 1200;
    const isVip = (seat.seatType && seat.seatType.toUpperCase() === 'VIP');
    const price = isVip ? Math.round(basePrice * 1.6) : basePrice;
    const isActive = (seat.isActive !== false && seat.active !== false);

    inspectorBody.innerHTML = `
        <div class="inspector-details">
            <div class="inspector-meta">
                <div class="inspector-seat-badge">${seat.seatRow}${seat.seatNumber}</div>
                <div class="inspector-info-item">
                    <span class="inspector-info-label">Hall</span>
                    <span class="inspector-info-value">${currentHall ? currentHall.name : 'Hall'}</span>
                </div>
                <div class="inspector-info-item">
                    <span class="inspector-info-label">Seat Type</span>
                    <span class="inspector-info-value">${seat.seatType}</span>
                </div>
                <div class="inspector-info-item">
                    <span class="inspector-info-label">Price (LKR)</span>
                    <span class="inspector-info-value" style="color: #38bdf8;">Rs. ${formatLkr(price)}</span>
                </div>
                <div class="inspector-info-item">
                    <span class="inspector-info-label">Current Status</span>
                    <span class="status-pill ${isActive ? 'status-active' : 'status-maintenance'}">
                        ${isActive ? '● Available' : '⚠️ Under Maintenance'}
                    </span>
                </div>
            </div>
            <div>
                ${isActive
                    ? `<button class="btn btn-xs btn-delete" onclick="toggleSeatMaintenance(${seat.id}, false)">
                         🛠️ [U] Set to Under Maintenance (PUT)
                       </button>`
                    : `<button class="btn btn-xs btn-create" onclick="toggleSeatMaintenance(${seat.id}, true)">
                         ✅ [U] Set to Available (PUT)
                       </button>`
                }
            </div>
        </div>
    `;

    // Highlight inspected seat in the grid
    document.querySelectorAll('.seat').forEach(el => {
        if (el.dataset.seatId == seat.id) {
            el.classList.add('inspected-seat');
        } else {
            el.classList.remove('inspected-seat');
        }
    });
}

function clearSeatInspector() {
    if (!inspectorBody) return;
    inspectorBody.innerHTML = `
        <div class="inspector-empty">No seat selected. Click any seat in the layout above to inspect details and toggle status!</div>
    `;
    document.querySelectorAll('.seat.inspected-seat').forEach(el => el.classList.remove('inspected-seat'));
}

// ============================================================================
// [D] DELETE OPERATION: DELETE /api/halls/{id}
// ============================================================================

window.deleteHall = async function(hallId, hallName) {
    const isConfirmed = confirm(`⚠️ Are you sure you want to delete cinema hall "${hallName}" (ID: #${hallId}) and all its associated seats?\n\nThis action cannot be undone.`);
    if (!isConfirmed) return;

    try {
        const response = await fetch(`${API_BASE_URL}/api/halls/${hallId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.error || `Server responded with ${response.status}`);
        }

        showToast(`[D] Cinema hall #${hallId} and all associated seats deleted successfully!`);

        // If currently inspected hall is deleted, reload and choose next hall
        const nextHall = currentHalls.find(h => h.id != hallId);
        await loadHalls(nextHall ? nextHall.id : null);

    } catch (error) {
        console.error("Error deleting cinema hall:", error);
        showToast(`Error deleting hall: ${error.message}`, true);
    }
};

// ============================================================================
// CUSTOMER BOOKING SEAT SELECTION
// ============================================================================

function toggleSeatSelection(seat, element) {
    if (selectedSeats.has(seat.id)) {
        selectedSeats.delete(seat.id);
        element.classList.remove('selected');
    } else {
        selectedSeats.set(seat.id, seat);
        element.classList.add('selected');
    }
    updateSelectionSummary();
}

function updateSelectionSummary() {
    if (!selectedChips || !priceSummary) return;
    selectedChips.innerHTML = '';

    if (selectedSeats.size === 0) {
        selectedChips.innerHTML = '<span class="empty-selection">No seats selected yet. Click any available seat above!</span>';
        priceSummary.textContent = 'Total: Rs. 0.00';
        return;
    }

    const basePrice = (currentHall && currentHall.basePrice) ? currentHall.basePrice : 1200;
    let totalPrice = 0;

    selectedSeats.forEach(seat => {
        const isVip = (seat.seatType && seat.seatType.toUpperCase() === 'VIP');
        const price = isVip ? Math.round(basePrice * 1.6) : basePrice;
        totalPrice += price;

        const chip = document.createElement('div');
        chip.className = 'chip';
        chip.innerHTML = `
            <span>${seat.seatRow}${seat.seatNumber}</span>
            <span class="chip-type">(${seat.seatType} - Rs. ${formatLkr(price)})</span>
        `;
        selectedChips.appendChild(chip);
    });

    priceSummary.textContent = `Total: Rs. ${formatLkr(totalPrice)} (${selectedSeats.size} seat${selectedSeats.size > 1 ? 's' : ''})`;
}

clearSelectionBtn.addEventListener('click', () => {
    selectedSeats.clear();
    document.querySelectorAll('.seat.selected').forEach(el => el.classList.remove('selected'));
    updateSelectionSummary();
    showToast("Cleared seat selection");
});

// ============================================================================
// MODE SWITCHER (Booking Selection vs Admin Maintenance Toggle)
// ============================================================================

modeBookingBtn.addEventListener('click', () => {
    activeMode = 'booking';
    modeBookingBtn.className = 'btn-mode active';
    modeMaintenanceBtn.className = 'btn-mode';
    modeAlert.className = 'mode-alert mode-booking';
    modeAlertText.textContent = '🎟️ Booking Mode Active: Click any available seat to select it for ticket reservation.';
});

modeMaintenanceBtn.addEventListener('click', () => {
    activeMode = 'maintenance';
    modeBookingBtn.className = 'btn-mode';
    modeMaintenanceBtn.className = 'btn-mode active btn-mode-admin';
    modeAlert.className = 'mode-alert mode-maintenance';
    modeAlertText.textContent = '🛠️ Admin Maintenance Mode Active: Click any seat on the grid to immediately toggle its maintenance status via PUT /api/halls/seats/{id}/status!';
    showToast("Switched to Admin Maintenance Toggle Mode");
});

// ============================================================================
// EVENT LISTENERS & INITIALIZATION
// ============================================================================

hallSelect.addEventListener('change', (e) => {
    if (e.target.value) {
        loadSeatsForHall(e.target.value);
    }
});

refreshLayoutBtn.addEventListener('click', () => {
    if (hallSelect.value) {
        loadSeatsForHall(hallSelect.value);
        showToast("Refreshed layout seats from backend");
    }
});

refreshHallsBtn.addEventListener('click', () => {
    loadHalls();
    showToast("Refreshed halls directory from backend");
});

// Initial Load
loadHalls();
