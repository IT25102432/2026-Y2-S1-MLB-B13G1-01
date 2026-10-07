import { useEffect, useState } from "react";
import {
  Plus,
  Pencil,
  Trash2,
  Search,
  X,
  Armchair,
} from "lucide-react";

import api from "../services/api";
import "../styles/halls.css";

const emptyForm = {
  hallName: "",
  rowsCount: "",
  columnsCount: "",
  active: true,
};

function Halls() {
  const [halls, setHalls] = useState([]);
  const [search, setSearch] = useState("");

  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [showSeats, setShowSeats] = useState(false);

  const [selectedHall, setSelectedHall] = useState(null);
  const [seats, setSeats] = useState([]);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadHalls = async () => {
    try {
      const response = await api.get("/halls");
      setHalls(response.data);
    } catch {
      setError("Failed to load cinema halls");
    }
  };

  useEffect(() => {
    loadHalls();
  }, []);

  const validateForm = () => {
    if (!form.hallName.trim()) {
      return "Hall name is required";
    }

    if (!form.rowsCount || Number(form.rowsCount) <= 0) {
      return "Rows must be greater than 0";
    }

    if (Number(form.rowsCount) > 26) {
      return "Rows cannot exceed 26";
    }

    if (!form.columnsCount || Number(form.columnsCount) <= 0) {
      return "Columns must be greater than 0";
    }

    if (Number(form.columnsCount) > 30) {
      return "Columns cannot exceed 30";
    }

    const capacity =
      Number(form.rowsCount) *
      Number(form.columnsCount);

    if (capacity > 500) {
      return "Hall capacity cannot exceed 500 seats";
    }

    return "";
  };

  const openAddModal = () => {
    setEditingId(null);
    setForm(emptyForm);
    setError("");
    setShowModal(true);
  };

  const openEditModal = (hall) => {
    setEditingId(hall.id);

    setForm({
      hallName: hall.hallName || "",
      rowsCount: hall.rowsCount || "",
      columnsCount: hall.columnsCount || "",
      active: hall.active ?? true,
    });

    setError("");
    setShowModal(true);
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    const validationError = validateForm();

    if (validationError) {
      setError(validationError);
      return;
    }

    const payload = {
      hallName: form.hallName.trim(),
      rowsCount: Number(form.rowsCount),
      columnsCount: Number(form.columnsCount),
      active: form.active,
    };

    try {
      if (editingId) {
        await api.put(
          `/halls/${editingId}`,
          payload
        );

        setSuccess(
          "Cinema hall updated successfully"
        );
      } else {
        await api.post(
          "/halls",
          payload
        );

        setSuccess(
          "Cinema hall created successfully"
        );
      }

      setShowModal(false);
      setForm(emptyForm);

      await loadHalls();
    } catch (err) {
      setError(
        err.response?.data ||
          "Failed to save cinema hall"
      );
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this cinema hall?"
    );

    if (!confirmed) return;

    try {
      await api.delete(`/halls/${id}`);

      setSuccess(
        "Cinema hall deleted successfully"
      );

      await loadHalls();
    } catch (err) {
      setError(
        err.response?.data ||
          "Failed to delete cinema hall"
      );
    }
  };

  const openSeatLayout = async (hall) => {
    try {
      setError("");
      setSelectedHall(hall);

      const response = await api.get(
        `/halls/${hall.id}/seats`
      );

      setSeats(response.data);
      setShowSeats(true);
    } catch (err) {
      setError(
        err.response?.data ||
          "Failed to load seats"
      );
    }
  };

  const updateSeat = async (
    seat,
    newType,
    newStatus
  ) => {
    try {
      await api.put(
        `/halls/seats/${seat.id}`,
        {
          seatType: newType,
          status: newStatus,
        }
      );

      const response = await api.get(
        `/halls/${selectedHall.id}/seats`
      );

      setSeats(response.data);

      setSuccess(
        `Seat ${seat.seatCode} updated successfully`
      );
    } catch (err) {
      setError(
        err.response?.data ||
          "Failed to update seat"
      );
    }
  };

  const filteredHalls = halls.filter((hall) =>
    hall.hallName
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div className="halls-page">
      <div className="page-heading">
        <div>
          <h2>Cinema Halls</h2>

          <p>
            Manage halls, capacities and seat layouts.
          </p>
        </div>

        <button
          className="primary-button"
          onClick={openAddModal}
        >
          <Plus size={18} />
          Add Hall
        </button>
      </div>

      {success && (
        <div className="alert success-alert">
          {success}
        </div>
      )}

      {error && !showModal && !showSeats && (
        <div className="alert error-alert">
          {error}
        </div>
      )}

      <div className="content-card">
        <div className="table-toolbar">
          <div className="table-search">
            <Search size={18} />

            <input
              type="text"
              placeholder="Search halls..."
              value={search}
              onChange={(e) =>
                setSearch(e.target.value)
              }
            />
          </div>

          <span>
            {filteredHalls.length} halls
          </span>
        </div>

        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Hall</th>
                <th>Rows</th>
                <th>Columns</th>
                <th>Capacity</th>
                <th>Status</th>
                <th>Seat Layout</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredHalls.length === 0 ? (
                <tr>
                  <td
                    colSpan="7"
                    className="empty-table"
                  >
                    No cinema halls found
                  </td>
                </tr>
              ) : (
                filteredHalls.map((hall) => (
                  <tr key={hall.id}>
                    <td>
                      <strong>
                        {hall.hallName}
                      </strong>
                    </td>

                    <td>{hall.rowsCount}</td>

                    <td>{hall.columnsCount}</td>

                    <td>{hall.capacity}</td>

                    <td>
                      <span
                        className={`status-badge ${
                          hall.active
                            ? "success"
                            : "danger"
                        }`}
                      >
                        {hall.active
                          ? "Active"
                          : "Inactive"}
                      </span>
                    </td>

                    <td>
                      <button
                        className="seat-layout-button"
                        onClick={() =>
                          openSeatLayout(hall)
                        }
                      >
                        <Armchair size={16} />
                        View Seats
                      </button>
                    </td>

                    <td>
                      <div className="table-actions">
                        <button
                          className="edit-button"
                          onClick={() =>
                            openEditModal(hall)
                          }
                        >
                          <Pencil size={16} />
                        </button>

                        <button
                          className="delete-button"
                          onClick={() =>
                            handleDelete(hall.id)
                          }
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-card">
            <div className="modal-header">
              <div>
                <h3>
                  {editingId
                    ? "Edit Cinema Hall"
                    : "Add Cinema Hall"}
                </h3>

                <p>
                  Seats are generated automatically.
                </p>
              </div>

              <button
                className="modal-close"
                onClick={() =>
                  setShowModal(false)
                }
              >
                <X size={20} />
              </button>
            </div>

            <form
              onSubmit={handleSubmit}
              className="movie-form"
            >
              {error && (
                <div className="alert error-alert">
                  {error}
                </div>
              )}

              <div className="form-grid">
                <div className="form-group full-width">
                  <label>Hall Name *</label>

                  <input
                    type="text"
                    name="hallName"
                    value={form.hallName}
                    onChange={handleChange}
                    placeholder="Hall A"
                  />
                </div>

                <div className="form-group">
                  <label>Rows *</label>

                  <input
                    type="number"
                    name="rowsCount"
                    value={form.rowsCount}
                    onChange={handleChange}
                    min="1"
                    max="26"
                  />
                </div>

                <div className="form-group">
                  <label>
                    Seats Per Row *
                  </label>

                  <input
                    type="number"
                    name="columnsCount"
                    value={form.columnsCount}
                    onChange={handleChange}
                    min="1"
                    max="30"
                  />
                </div>

                <div className="form-group full-width">
                  <div className="capacity-preview">
                    Estimated Capacity:{" "}
                    <strong>
                      {Number(
                        form.rowsCount || 0
                      ) *
                        Number(
                          form.columnsCount || 0
                        )}{" "}
                      seats
                    </strong>
                  </div>
                </div>

                <div className="form-group full-width">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      name="active"
                      checked={form.active}
                      onChange={handleChange}
                    />

                    Hall is active
                  </label>
                </div>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() =>
                    setShowModal(false)
                  }
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                >
                  {editingId
                    ? "Update Hall"
                    : "Create Hall"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showSeats && (
        <div className="modal-overlay">
          <div className="modal-card seat-modal">
            <div className="modal-header">
              <div>
                <h3>
                  {selectedHall?.hallName} Seat Layout
                </h3>

                <p>
                  Manage seat type and seat status.
                </p>
              </div>

              <button
                className="modal-close"
                onClick={() =>
                  setShowSeats(false)
                }
              >
                <X size={20} />
              </button>
            </div>

            <div className="seat-layout-content">
              {error && (
                <div className="alert error-alert">
                  {error}
                </div>
              )}

              <div className="cinema-screen">
                SCREEN
              </div>

              <div className="seat-grid">
                {seats.map((seat) => (
                  <div
                    key={seat.id}
                    className={`seat-card ${seat.status
                      ?.toLowerCase()}`}
                  >
                    <Armchair size={18} />

                    <strong>
                      {seat.seatCode}
                    </strong>

                    <select
                      value={seat.seatType}
                      onChange={(e) =>
                        updateSeat(
                          seat,
                          e.target.value,
                          seat.status
                        )
                      }
                    >
                      <option value="STANDARD">
                        Standard
                      </option>

                      <option value="PREMIUM">
                        Premium
                      </option>

                      <option value="RECLINER">
                        Recliner
                      </option>
                    </select>

                    <select
                      value={seat.status}
                      onChange={(e) =>
                        updateSeat(
                          seat,
                          seat.seatType,
                          e.target.value
                        )
                      }
                    >
                      <option value="AVAILABLE">
                        Available
                      </option>

                      <option value="HELD">
                        Held
                      </option>

                      <option value="BOOKED">
                        Booked
                      </option>

                      <option value="OUT_OF_ORDER">
                        Out of Order
                      </option>
                    </select>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Halls;