// ============================================================
//  NeighborLink — shared utilities (utils.js)
// ============================================================

const API = 'http://localhost:8080/api';

// ── Auth helpers ─────────────────────────────────────────────
const Auth = {
  save(user)  { localStorage.setItem('nl_user', JSON.stringify(user)); },
  get()       { try { return JSON.parse(localStorage.getItem('nl_user')); } catch { return null; } },
  clear()     { localStorage.removeItem('nl_user'); },
  require()   {
    const u = Auth.get();
    if (!u) { window.location.href = 'index.html'; return null; }
    return u;
  }
};

// ── API fetch wrapper ─────────────────────────────────────────
async function api(method, path, body) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' }
  };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API + path, opts);
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'API error');
  return data;
}

const GET    = (path)       => api('GET',    path);
const POST   = (path, body) => api('POST',   path, body);
const PUT    = (path, body) => api('PUT',    path, body);
const DELETE = (path)       => api('DELETE', path);

// ── Alert helpers ─────────────────────────────────────────────
function showAlert(elId, msg, type = 'success') {
  const el = document.getElementById(elId);
  if (!el) return;
  el.textContent = msg;
  el.className = `alert alert-${type} show`;
  setTimeout(() => el.classList.remove('show'), 5000);
}

// ── Spinner on button ─────────────────────────────────────────
function setBtnLoading(btn, loading) {
  if (loading) {
    btn.dataset.orig = btn.innerHTML;
    btn.innerHTML = '<span class="spinner"></span> Loading…';
    btn.disabled = true;
  } else {
    btn.innerHTML = btn.dataset.orig;
    btn.disabled = false;
  }
}

// ── Render navbar ─────────────────────────────────────────────
function renderNav(active) {
  const user = Auth.get();
  const pages = [
    { href: 'dashboard.html',       label: 'Dashboard' },
    { href: 'browse_listings.html',          label: 'Browse' },
    { href: 'create_listing.html',  label: 'Add Listing' },
    { href: 'my_rentals.html',      label: 'My Rentals' },
    { href: 'dbms_lab.html',        label: '🧪 DBMS Lab' },
  ];
  const links = pages.map(p =>
    `<a href="${p.href}" class="${p.href === active ? 'active' : ''}">${p.label}</a>`
  ).join('');

  const initials = user ? user.username.slice(0, 2).toUpperCase() : '?';
  document.getElementById('navbar').innerHTML = `
    <a class="logo" href="dashboard.html">Neighbor<span>Link</span></a>
    <nav class="nav-links">${links}</nav>
    <div class="nav-user">
      <span>${user ? user.username : ''}</span>
      <div class="avatar">${initials}</div>
      <button class="btn-logout" onclick="logout()">Logout</button>
    </div>
  `;
}

function logout() {
  Auth.clear();
  window.location.href = 'index.html';
}

// ── Status badge helper ───────────────────────────────────────
function statusBadge(status) {
  const map = {
    COMPLETED: 'badge-green',
    ACTIVE:    'badge-blue',
    PENDING:   'badge-orange',
    CANCELLED: 'badge-red'
  };
  return `<span class="badge ${map[status] || 'badge-gray'}">${status}</span>`;
}

// ── Star rating display ───────────────────────────────────────
function starDisplay(rating) {
  const full  = Math.round(rating || 0);
  return '★'.repeat(full) + '☆'.repeat(5 - full);
}

// ── Format currency ───────────────────────────────────────────
function formatRupee(val) {
  return '₹' + parseFloat(val || 0).toLocaleString('en-IN');
}

// ── Format output steps ───────────────────────────────────────
function formatOutput(obj, elId) {
  const el = document.getElementById(elId);
  if (!el) return;
  let html = '';
  for (const [k, v] of Object.entries(obj)) {
    const str = typeof v === 'object' ? JSON.stringify(v, null, 2) : String(v);
    let cls = 'step-info';
    if (str.includes('✅') || str.includes('COMMITTED'))  cls = 'step-ok';
    if (str.includes('❌') || str.includes('ERROR'))       cls = 'step-err';
    if (str.includes('↩️') || str.includes('ROLLBACK'))   cls = 'step-warn';
    if (str.includes('📍') || str.includes('SAVEPOINT'))  cls = 'step-info';
    if (str.includes('🔒') || str.includes('LOCK'))        cls = 'step-info';
    html += `<span class="${cls}">${k}: ${str}\n</span>`;
  }
  el.innerHTML = html;
}
