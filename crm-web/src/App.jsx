
import { useState } from "react";
import "./App.css";

const leads = [
  {
    id: 1,
    name: "Rahul Sharma",
    phone: "+91 98765 43210",
    stage: "Interested",
    initials: "RS",
  },
  {
    id: 2,
    name: "Priya Verma",
    phone: "+91 98765 43211",
    stage: "Follow-up",
    initials: "PV",
  },
  {
    id: 3,
    name: "Aman Gupta",
    phone: "+91 98765 43212",
    stage: "New Lead",
    initials: "AG",
  },
];

const API_URL = "http://localhost:5000/api/calls";

export default function App() {
  const [messages, setMessages] = useState({});
  const [statuses, setStatuses] = useState({});
  const [requestCount, setRequestCount] = useState(0);
  const [loadingLeadId, setLoadingLeadId] = useState(null);

  const handleCall = async (lead) => {
    if (loadingLeadId !== null) return;

    setLoadingLeadId(lead.id);
    setStatuses((previous) => ({
      ...previous,
      [lead.id]: "sending",
    }));
    setMessages((previous) => ({
      ...previous,
      [lead.id]: "Sending call request to your Android device...",
    }));

    try {
      const response = await fetch(API_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          leadId: String(lead.id),
          name: lead.name,
          phone: lead.phone,
        }),
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.message || `Request failed (${response.status})`);
      }

      setStatuses((previous) => ({
        ...previous,
        [lead.id]: "accepted",
      }));
      setMessages((previous) => ({
        ...previous,
        [lead.id]: data.message || "Call request accepted by backend.",
      }));
      setRequestCount((count) => count + 1);

      console.log("Call request response:", data);
    } catch (error) {
      setStatuses((previous) => ({
        ...previous,
        [lead.id]: "error",
      }));
      setMessages((previous) => ({
        ...previous,
        [lead.id]: `Error: ${error.message || "Could not reach the backend."}`,
      }));
      console.error("Call request failed:", error);
    } finally {
      setLoadingLeadId(null);
    }
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">L</div>
          <span>LeadZam</span>
        </div>

        <p className="nav-heading">WORKSPACE</p>
        <div className="nav-item active">▦ &nbsp; Leads</div>
        <div className="nav-item">◷ &nbsp; Follow-ups</div>
        <div className="nav-item">⚙ &nbsp; Settings</div>

        <div className="sidebar-footer">
          <div className="avatar">AD</div>
          <div>
            <strong>Demo User</strong>
            <p>Sales workspace</p>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <p className="eyebrow">WORKSPACE / LEADS</p>
            <h1>Lead Dashboard</h1>
            <p className="subtitle">
              Manage your leads and connect with customers.
            </p>
          </div>

          <div className="connection-badge">
            <span className="status-dot" />
            Backend connected
          </div>
        </header>

        <section className="summary-grid">
          <article className="summary-card">
            <p>Total leads</p>
            <h2>{leads.length}</h2>
            <span>Sample contacts</span>
          </article>

          <article className="summary-card">
            <p>Interested</p>
            <h2>
              {leads.filter((lead) => lead.stage === "Interested").length}
            </h2>
            <span>Ready for follow-up</span>
          </article>

          <article className="summary-card">
            <p>Call requests</p>
            <h2>{requestCount}</h2>
            <span>Accepted by backend this session</span>
          </article>
        </section>

        <section className="leads-section">
          <div className="section-header">
            <div>
              <h2>Your leads</h2>
              <p>
                Select a contact to request a call on your Android phone.
              </p>
            </div>

            <span className="lead-count">{leads.length} contacts</span>
          </div>

          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>CONTACT</th>
                  <th>PHONE NUMBER</th>
                  <th>STAGE</th>
                  <th>CALL STATUS / ACTION</th>
                </tr>
              </thead>

              <tbody>
                {leads.map((lead) => {
                  const status = statuses[lead.id] || "ready";

                  return (
                    <tr key={lead.id}>
                      <td>
                        <div className="contact-cell">
                          <div className="avatar contact-avatar">
                            {lead.initials}
                          </div>
                          <strong>{lead.name}</strong>
                        </div>
                      </td>

                      <td>{lead.phone}</td>

                      <td>
                        <span className={`stage stage-${lead.id}`}>
                          {lead.stage}
                        </span>
                      </td>

                      <td>
                        <button
                          className="call-button"
                          onClick={() => handleCall(lead)}
                          disabled={loadingLeadId !== null}
                        >
                          {status === "sending"
                            ? "Sending..."
                            : status === "accepted"
                              ? "☎ Send again"
                              : "☎ Call"}
                        </button>

                        <p
                          className={`request-message status-${status}`}
                          role="status"
                          aria-live="polite"
                        >
                          {messages[lead.id] ||
                            (status === "ready"
                              ? "Ready"
                              : status)}
                        </p>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </section>

        <p className="footer-note">
          CRM Click-to-Call · Development preview
        </p>
      </main>
    </div>
  );
}
