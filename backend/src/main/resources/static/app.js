/**
 * Frontend JavaScript for Cinema Seating Layout & Hall Allocation
 * Component Student ID: IT25102154
 */

// API Base URL (defaults to current origin if served via Spring Boot, or localhost:8080 if opened standalone)
const API_BASE_URL = (window.location.port === '8080' || window.location.hostname.length > 0) && window.location.protocol.startsWith('http')
    ? ''
    : 'http://localhost:8080';

// Pricing Constants (Sri Lankan Rupees - LKR / Rs.)
const VIP_SEAT_PRICE_LKR = 2000;
const STANDARD_SEAT_PRICE_LKR = 1200;

// State management
let currentHalls = [];
let currentSeats = [];
const selectedSeats = new Map(); // seatId -> seatObj

// DOM Elements
const hallSelect = document.getElementById('hallSelect');
const refreshBtn = document.getElementById('refreshBtn');
const seatGrid = document.getElementById('seatGrid');
const hallDetails = document.getElementById('hallDetails');
const connectionBadge = document.getElementById('connectionBadge');
const selectedChips = document.getElementById('selectedChips');
const priceSummary = document.getElementById('priceSummary');
const clearSelectionBtn = document.getElementById('clearSelectionBtn');
const createHallForm = document.getElementById('createHallForm');
const toast = document.getElementById('toast');

// Fallback Mock Data Generator (5 rows x 8 seats = 40 seats)
function generateFallbackData() {
    const mockHalls = [
        { id: 1, name: "Hall 1 - IMAX (Demo)", totalRows: 5, seatsPerRow: 8, hallType: "IMAX", totalCapacity: 40 }
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
                isActive: true
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
    setTimeout(() => toast.classList.remove('show'), 3500);
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

// Fetch all cinema halls from GET /api/halls
async function loadHalls() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/halls`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const data = await response.json();

        if (!Array.isArray(data) || data.length === 0) {
            throw new Error("No halls returned from backend.");
        }

        currentHalls = data;
        setConnectionStatus(true);
        populateHallDropdown(currentHalls);
    } catch (error) {
        console.warn("Backend unreachable or empty, activating fallback mode:", error);
        const { mockHalls } = generateFallbackData();
        currentHalls = mockHalls;
        setConnectionStatus(false, '● Fallback / Demo Mode');
        populateHallDropdown(currentHalls);
        showToast("Loaded fallback mock layout (Backend unreachable)", true);
    }
}

// Populate Hall Dropdown
function populateHallDropdown(halls) {
    hallSelect.innerHTML = '';
    halls.forEach(hall => {
        const opt = document.createElement('option');
        opt.value = hall.id;
        const capacity = hall.totalCapacity || (hall.totalRows * hall.seatsPerRow);
        opt.textContent = `${hall.name} (${hall.hallType}) - ${capacity} seats`;
        hallSelect.appendChild(opt);
    });

    if (halls.length > 0) {
        hallSelect.value = halls[0].id;
        loadSeatsForHall(halls[0].id);
    }
}

// Fetch Seats from GET /api/halls/{id}/seats
async function loadSeatsForHall(hallId) {
    selectedSeats.clear();
    updateSelectionSummary();

    const hall = currentHalls.find(h => h.id == hallId);
    if (hall) {
        const capacity = hall.totalCapacity || (hall.totalRows * hall.seatsPerRow);
        hallDetails.textContent = `${hall.name} | Type: ${hall.hallType} | Capacity: ${capacity} seats (${hall.totalRows} rows × ${hall.seatsPerRow} cols)`;
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

// Render Seating Grid
function renderSeatGrid(seats) {
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

            seatBtn.className = `seat ${isVip ? 'vip' : 'standard'} ${!isActive ? 'disabled' : ''}`;
            seatBtn.textContent = seat.seatNumber;
            seatBtn.title = `Seat ${seat.seatRow}${seat.seatNumber} (${seat.seatType}) - ${isActive ? 'Available' : 'Disabled'}`;
            seatBtn.dataset.seatId = seat.id;

            if (isActive) {
                seatBtn.addEventListener('click', () => toggleSeatSelection(seat, seatBtn));
            }

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

// Toggle Seat Selection (Green highlight)
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

// Update Selection Summary
function updateSelectionSummary() {
    selectedChips.innerHTML = '';
    if (selectedSeats.size === 0) {
        selectedChips.innerHTML = '<span class="empty-selection">No seats selected yet. Click any available seat above!</span>';
        priceSummary.textContent = 'Total: Rs. 0.00';
        return;
    }

    let totalPrice = 0;
    selectedSeats.forEach(seat => {
        const isVip = (seat.seatType && seat.seatType.toUpperCase() === 'VIP');
        const price = isVip ? VIP_SEAT_PRICE_LKR : STANDARD_SEAT_PRICE_LKR;
        totalPrice += price;

        const chip = document.createElement('div');
        chip.className = 'chip';
        chip.innerHTML = `
            <span>${seat.seatRow}${seat.seatNumber}</span>
            <span class="chip-type">(${seat.seatType} - Rs. ${price.toLocaleString()})</span>
        `;
        selectedChips.appendChild(chip);
    });

    priceSummary.textContent = `Total: Rs. ${totalPrice.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} (${selectedSeats.size} seat${selectedSeats.size > 1 ? 's' : ''})`;
}

// Clear Selection Button
clearSelectionBtn.addEventListener('click', () => {
    selectedSeats.clear();
    document.querySelectorAll('.seat.selected').forEach(el => el.classList.remove('selected'));
    updateSelectionSummary();
});

// Handle Create Hall Form Submission (POST /api/halls)
createHallForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = document.getElementById('submitHallBtn');

    const hallPayload = {
        name: document.getElementById('hallName').value.trim(),
        totalRows: parseInt(document.getElementById('totalRows').value, 10),
        seatsPerRow: parseInt(document.getElementById('seatsPerRow').value, 10),
        hallType: document.getElementById('hallType').value
    };

    submitBtn.disabled = true;
    submitBtn.textContent = 'Allocating & Generating Layout...';

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
        showToast(`Cinema hall "${newHall.name}" allocated successfully!`);

        // Reset form to defaults
        createHallForm.reset();
        document.getElementById('totalRows').value = 5;
        document.getElementById('seatsPerRow').value = 8;

        // Refresh halls list and auto-select newly created hall
        await loadHalls();
        hallSelect.value = newHall.id;
        await loadSeatsForHall(newHall.id);

    } catch (error) {
        console.error("Error creating hall:", error);
        showToast(`Error: ${error.message}`, true);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Allocate Hall & Generate Layout';
    }
});

// Event Listeners
hallSelect.addEventListener('change', (e) => {
    if (e.target.value) {
        loadSeatsForHall(e.target.value);
    }
});

refreshBtn.addEventListener('click', () => {
    loadHalls();
    showToast("Refreshed halls list from backend");
});

// Initial Load
loadHalls();
