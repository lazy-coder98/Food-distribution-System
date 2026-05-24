const state = {
  token: localStorage.getItem("fds.token"),
  user: JSON.parse(localStorage.getItem("fds.user") || "null"),
  view: "overview",
  mode: "login",
  loading: false
};

const API = "";
const app = document.getElementById("app");
const DEFAULT_LOCATION = { latitude: 28.6139, longitude: 77.2090 };

const icons = {
  overview: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>`,
  food: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M21 15a2 2 0 01-2 2H5a2 2 0 01-2-2V9a2 2 0 012-2h14a2 2 0 012 2v6z" /><path stroke-linecap="round" stroke-linejoin="round" d="M12 7V3m-4 4V5m8 2V5" /></svg>`,
  nearby: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>`,
  claims: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`,
  profile: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>`,
  admin: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>`
};

function setSession(auth) {
  state.token = auth.token;
  state.user = auth.user;
  localStorage.setItem("fds.token", auth.token);
  localStorage.setItem("fds.user", JSON.stringify(auth.user));
}

function clearSession() {
  state.token = null;
  state.user = null;
  localStorage.removeItem("fds.token");
  localStorage.removeItem("fds.user");
}

async function api(path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {})
  };
  if (state.token) headers.Authorization = `Bearer ${state.token}`;

  const response = await fetch(`${API}${path}`, {
    ...options,
    headers,
    body: options.body && typeof options.body !== "string" ? JSON.stringify(options.body) : options.body
  });

  if (response.status === 204) return null;
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;
  if (!response.ok) {
    const message = data?.message || data?.error || "Request failed";
    throw new Error(message);
  }
  return data;
}

function pageItems(page) {
  return page?.content || [];
}

function html(strings, ...values) {
  return strings.reduce((out, str, i) => out + str + (values[i] ?? ""), "");
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function fmtDate(value) {
  if (!value) return "Not set";
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short"
  }).format(new Date(value));
}

function statusBadge(status) {
  return `<span class="status ${String(status || "").toLowerCase()}">${escapeHtml(status || "UNKNOWN")}</span>`;
}

function navItems() {
  const role = state.user?.role;
  const items = [
    ["overview", "Overview"],
    ["nearby", "Nearby food"],
    ["claims", "Claims"]
  ];
  if (role === "RESTAURANT") items.splice(1, 0, ["food", "Food posts"]);
  if (role === "RESTAURANT" || role === "NGO") items.push(["profile", "Profile"]);
  if (role === "ADMIN") items.push(["admin", "Admin"]);
  return items;
}

function render() {
  if (!state.token || !state.user) {
    renderAuth();
    return;
  }
  renderApp();
}

function renderAuth(message = "", type = "error") {
  app.innerHTML = html`
    <main class="auth-page">
      <section class="auth-art">
        <div class="auth-copy">
          <h1>Food Distribution System</h1>
          <p>Sign in to post food, discover nearby donations, claim meals, and monitor distribution workflows.</p>
        </div>
      </section>
      <section class="auth-form-wrap">
        <div class="auth-card">
          <div class="tabs">
            <button class="${state.mode === "login" ? "active" : ""}" data-auth-mode="login">Login</button>
            <button class="${state.mode === "register" ? "active" : ""}" data-auth-mode="register">Register</button>
          </div>
          ${message ? `<div class="alert ${type}">${escapeHtml(message)}</div>` : ""}
          ${state.mode === "login" ? loginForm() : registerForm()}
        </div>
      </section>
    </main>
  `;
}

function loginForm() {
  return html`
    <form class="form" id="loginForm">
      <label>Email
        <input name="email" type="email" autocomplete="email" required placeholder="restaurant@example.com">
      </label>
      <label>Password
        <input name="password" type="password" autocomplete="current-password" required placeholder="Password123!">
      </label>
      <button class="btn primary" type="submit">Sign in</button>
    </form>
  `;
}

function registerForm() {
  return html`
    <form class="form" id="registerForm">
      <label>Full name
        <input name="fullName" required minlength="2" placeholder="Asha Sharma">
      </label>
      <label>Email
        <input name="email" type="email" autocomplete="email" required placeholder="you@example.com">
      </label>
      <div class="form-row">
        <label>Password
          <input name="password" type="password" autocomplete="new-password" required minlength="8" placeholder="Password123!">
        </label>
        <label>Phone
          <input name="phoneNumber" required minlength="7" placeholder="9999999999">
        </label>
      </div>
      <label>Role
        <select name="role" required>
          <option value="RESTAURANT">Restaurant</option>
          <option value="NGO">NGO</option>
        </select>
      </label>
      <button class="btn primary" type="submit">Create account</button>
    </form>
  `;
}

function renderApp() {
  app.innerHTML = html`
    <main class="shell">
      <aside class="sidebar">
        <div class="brand">
          <div>
            <h1>Food Distribution</h1>
            <p>${escapeHtml(state.user.role)}</p>
          </div>
        </div>
        <nav class="nav">
          ${navItems().map(([id, label]) => `
            <button class="${state.view === id ? "active" : ""}" data-view="${id}">
              <span class="nav-icon">${icons[id]}</span>${label}
            </button>
          `).join("")}
        </nav>
        <div class="sidebar-footer">
          <span>${escapeHtml(state.user.email)}</span>
          <button class="btn secondary" id="logoutBtn">Log out</button>
        </div>
      </aside>
      <section class="content">
        <div class="topbar">
          <div>
            <h2>${viewTitle()}</h2>
            <p>${viewSubtitle()}</p>
          </div>
          <div class="user-pill">
            <div class="avatar">${escapeHtml((state.user.fullName || "U").slice(0, 1).toUpperCase())}</div>
            <div>
              <strong>${escapeHtml(state.user.fullName)}</strong>
              <div class="meta">${escapeHtml(state.user.role)}</div>
            </div>
          </div>
        </div>
        <div id="viewRoot"></div>
      </section>
    </main>
  `;
  loadView();
}

function viewTitle() {
  return {
    overview: "Operations overview",
    food: "Food posts",
    nearby: "Nearby food",
    claims: "Claims",
    profile: "Profile",
    admin: "Admin console"
  }[state.view] || "Dashboard";
}

function viewSubtitle() {
  return {
    overview: "Live system overview and operations data.",
    food: "Create, update, and retire restaurant food posts.",
    nearby: "Find available food by latitude, longitude, and radius.",
    claims: "Claim food and move requests through approval.",
    profile: "Keep location and contact data current.",
    admin: "Monitor users, food inventory, and claims."
  }[state.view] || "";
}

async function loadView() {
  const root = document.getElementById("viewRoot");
  root.innerHTML = `<div class="empty">Loading...</div>`;
  try {
    if (state.view === "overview") root.innerHTML = await overviewView();
    if (state.view === "food") root.innerHTML = await foodView();
    if (state.view === "nearby") root.innerHTML = nearbyView();
    if (state.view === "claims") root.innerHTML = await claimsView();
    if (state.view === "profile") root.innerHTML = await profileView();
    if (state.view === "admin") root.innerHTML = await adminView();
  } catch (error) {
    root.innerHTML = `<div class="alert error">${escapeHtml(error.message)}</div>`;
  }
  initLocationPickers();
}

async function overviewView() {
  const requests = [api("/api/food-posts?size=5&sort=createdAt,desc")];
  if (state.user.role === "NGO") requests.push(api("/api/claims/me?size=5&sort=requestedAt,desc"));
  if (state.user.role === "ADMIN") requests.push(api("/api/admin/dashboard"));
  const [food, second] = await Promise.all(requests);
  const foodItems = pageItems(food);

  return html`
    <div class="grid cols-3">
      <div class="panel metric"><span>Visible food posts</span><strong>${food.totalElements ?? foodItems.length}</strong></div>
      <div class="panel metric"><span>Your role</span><strong>${escapeHtml(state.user.role)}</strong></div>
      <div class="panel metric"><span>Session</span><strong>Active</strong></div>
    </div>
    <div class="panel" style="margin-top:16px">
      <div class="panel-title">
        <div><h3>Recent food posts</h3><p>Newest available items</p></div>
      </div>
      ${foodList(foodItems)}
    </div>
    ${state.user.role === "ADMIN" && second ? `
      <div class="panel" style="margin-top:16px">
        <div class="panel-title"><div><h3>Admin summary</h3><p>System totals</p></div></div>
        <div class="grid cols-3">
          <div class="card metric"><span>Users</span><strong>${second.totalUsers}</strong></div>
          <div class="card metric"><span>Food posts</span><strong>${second.totalFoodPosts}</strong></div>
          <div class="card metric"><span>Claims</span><strong>${second.totalClaims}</strong></div>
        </div>
      </div>
    ` : ""}
  `;
}

async function foodView() {
  let profile = null;
  try {
    profile = await api("/api/restaurants/me");
  } catch (error) {
    return restaurantProfileRequiredView();
  }
  const food = await api("/api/food-posts/me?size=20&sort=createdAt,desc");
  return html`
    <div class="grid cols-2">
      <div class="panel">
        <div class="panel-title">
          <div><h3>Create food post</h3><p>Posting as ${escapeHtml(profile.restaurantName)}</p></div>
        </div>
        ${foodPostForm()}
      </div>
      <div class="panel">
        <div class="panel-title">
          <div><h3>My food posts</h3><p>${food.totalElements ?? 0} total</p></div>
        </div>
        ${foodList(pageItems(food), true)}
      </div>
    </div>
  `;
}

function restaurantProfileRequiredView() {
  return html`
    <div class="panel">
      <div class="panel-title">
        <div>
          <h3>Finish restaurant profile</h3>
          <p>A restaurant account exists, but food posts need the restaurant profile location and contact record first.</p>
        </div>
      </div>
      ${profileFormMarkup(true, null)}
    </div>
  `;
}

function foodPostForm() {
  const defaultExpiry = new Date(Date.now() + 6 * 60 * 60 * 1000).toISOString().slice(0, 16);
  return html`
    <form class="form" id="foodPostForm">
      <label>Food name <input name="foodName" required placeholder="Veg meals"></label>
      <label>Description <textarea name="description" required placeholder="Fresh packed meals ready for pickup"></textarea></label>
      <div class="form-row">
        <label>Quantity <input name="quantity" type="number" min="1" required value="10"></label>
        <label>Food type <input name="foodType" required placeholder="VEGETARIAN"></label>
      </div>
      <div class="form-row">
        <label>Expiry time <input name="expiryTime" type="datetime-local" required value="${defaultExpiry}"></label>
        <label>Image URL <input name="imageUrl" type="url" placeholder="https://example.com/photo.jpg"></label>
      </div>
      <div class="form-row">
        <label>Latitude <input name="latitude" type="number" step="0.000001" required placeholder="28.6139"></label>
        <label>Longitude <input name="longitude" type="number" step="0.000001" required placeholder="77.2090"></label>
      </div>
      ${locationPickerMarkup("foodLocationMap")}
      <button class="btn primary" type="submit">Create post</button>
    </form>
  `;
}

function nearbyView() {
  return html`
    <div class="panel">
      <div class="panel-title">
        <div><h3>Find nearby food</h3><p>Search available posts within a radius</p></div>
      </div>
      <form class="form" id="nearbyForm">
        <div class="form-row">
          <label>Latitude <input name="latitude" type="number" step="0.000001" required value="28.6139"></label>
          <label>Longitude <input name="longitude" type="number" step="0.000001" required value="77.2090"></label>
        </div>
        ${locationPickerMarkup("nearbyLocationMap")}
        <label>Radius km <input name="radiusKm" type="number" step="0.1" min="0.1" required value="10"></label>
        <button class="btn primary" type="submit">Search food</button>
      </form>
    </div>
    <div class="panel" style="margin-top:16px">
      <div class="panel-title"><div><h3>Results</h3><p>Available food posts appear here</p></div></div>
      <div id="nearbyResults" class="empty">Run a search to see food near you.</div>
    </div>
  `;
}

async function claimsView() {
  const endpoint = state.user.role === "NGO" ? "/api/claims/me?size=20&sort=requestedAt,desc" : "/api/claims?size=20&sort=requestedAt,desc";
  const claims = await api(endpoint);
  return html`
    <div class="panel">
      <div class="panel-title">
        <div><h3>${state.user.role === "NGO" ? "My claims" : "Claim queue"}</h3><p>Approve, reject, or complete distribution requests</p></div>
      </div>
      ${claimList(pageItems(claims))}
    </div>
  `;
}

async function profileView() {
  const isRestaurant = state.user.role === "RESTAURANT";
  const endpoint = isRestaurant ? "/api/restaurants/me" : "/api/ngos/me";
  let profile = null;
  try {
    profile = await api(endpoint);
  } catch (error) {
    profile = null;
  }
  return html`
    <div class="panel">
      <div class="panel-title">
        <div><h3>${isRestaurant ? "Restaurant" : "NGO"} profile</h3><p>Create or update your operational location</p></div>
      </div>
      ${profileFormMarkup(isRestaurant, profile)}
    </div>
  `;
}

function profileFormMarkup(isRestaurant, profile) {
  return html`
    <form class="form" id="profileForm" data-existing="${profile ? "true" : "false"}">
      <label>${isRestaurant ? "Restaurant name" : "NGO name"}
        <input name="${isRestaurant ? "restaurantName" : "ngoName"}" required value="${escapeHtml(profile?.restaurantName || profile?.ngoName || "")}">
      </label>
      <label>Address <input name="address" required value="${escapeHtml(profile?.address || "")}"></label>
      <div class="form-row">
        <label>Latitude <input name="latitude" type="number" step="0.000001" required value="${escapeHtml(profile?.latitude || "")}"></label>
        <label>Longitude <input name="longitude" type="number" step="0.000001" required value="${escapeHtml(profile?.longitude || "")}"></label>
      </div>
      ${locationPickerMarkup("profileLocationMap")}
      <label>Contact person <input name="contactPerson" required value="${escapeHtml(profile?.contactPerson || "")}"></label>
      <button class="btn primary" type="submit">${profile ? "Update profile" : "Create profile"}</button>
    </form>
  `;
}

function locationPickerMarkup(id) {
  return html`
    <div class="map-picker">
      <div class="map-picker-head">
        <span>Select location on map</span>
        <button class="btn secondary" type="button" data-use-current-location>Use current location</button>
      </div>
      <div id="${id}" class="location-map" data-location-map></div>
    </div>
  `;
}

function initLocationPickers() {
  if (!window.L) return;
  document.querySelectorAll("[data-location-map]").forEach(mapNode => {
    if (mapNode.dataset.ready === "true") return;

    const form = mapNode.closest("form");
    const latInput = form?.querySelector('input[name="latitude"]');
    const lngInput = form?.querySelector('input[name="longitude"]');
    if (!form || !latInput || !lngInput) return;

    const initialLat = Number(latInput.value) || DEFAULT_LOCATION.latitude;
    const initialLng = Number(lngInput.value) || DEFAULT_LOCATION.longitude;
    const map = L.map(mapNode).setView([initialLat, initialLng], 13);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    const marker = L.marker([initialLat, initialLng], { draggable: true }).addTo(map);
    const setLocation = latLng => {
      const lat = Number(latLng.lat).toFixed(6);
      const lng = Number(latLng.lng).toFixed(6);
      latInput.value = lat;
      lngInput.value = lng;
      marker.setLatLng([lat, lng]);
      map.panTo([lat, lng]);
    };

    map.on("click", event => setLocation(event.latlng));
    marker.on("dragend", event => setLocation(event.target.getLatLng()));
    latInput.addEventListener("change", () => {
      const lat = Number(latInput.value);
      const lng = Number(lngInput.value);
      if (Number.isFinite(lat) && Number.isFinite(lng)) setLocation({ lat, lng });
    });
    lngInput.addEventListener("change", () => {
      const lat = Number(latInput.value);
      const lng = Number(lngInput.value);
      if (Number.isFinite(lat) && Number.isFinite(lng)) setLocation({ lat, lng });
    });

    form.querySelector("[data-use-current-location]")?.addEventListener("click", () => {
      if (!navigator.geolocation) return;
      navigator.geolocation.getCurrentPosition(position => {
        setLocation({
          lat: position.coords.latitude,
          lng: position.coords.longitude
        });
      });
    });

    mapNode.dataset.ready = "true";
    setTimeout(() => map.invalidateSize(), 50);
  });
}

async function adminView() {
  const [stats, users, claims] = await Promise.all([
    api("/api/admin/dashboard"),
    api("/api/admin/users?size=20&sort=createdAt,desc"),
    api("/api/admin/claims?size=20&sort=requestedAt,desc")
  ]);
  return html`
    <div class="grid cols-3">
      <div class="panel metric"><span>Users</span><strong>${stats.totalUsers}</strong></div>
      <div class="panel metric"><span>Food posts</span><strong>${stats.totalFoodPosts}</strong></div>
      <div class="panel metric"><span>Claims</span><strong>${stats.totalClaims}</strong></div>
    </div>
    <div class="grid cols-2" style="margin-top:16px">
      <div class="panel">
        <div class="panel-title"><div><h3>Users</h3><p>Admin user management</p></div></div>
        ${userList(pageItems(users))}
      </div>
      <div class="panel">
        <div class="panel-title"><div><h3>Claims</h3><p>System claim monitor</p></div></div>
        ${claimList(pageItems(claims))}
      </div>
    </div>
  `;
}

function foodList(items, management = false) {
  if (!items.length) return `<div class="empty">No food posts found.</div>`;
  return `<div class="list">${items.map(item => `
    <article class="card">
      <div class="item-head">
        <div>
          <h4>${escapeHtml(item.foodName)}</h4>
          <div class="meta">${escapeHtml(item.foodType)} · Qty ${escapeHtml(item.quantity)} · Expires ${fmtDate(item.expiryTime)}</div>
        </div>
        ${statusBadge(item.status)}
      </div>
      <p class="meta">${escapeHtml(item.description)}</p>
      <div class="meta">Lat ${escapeHtml(item.latitude)}, Lng ${escapeHtml(item.longitude)}${item.distanceMeters ? ` · ${Math.round(item.distanceMeters)} m away` : ""}</div>
      <div class="actions" style="margin-top:12px">
        ${state.user.role === "NGO" && item.status === "AVAILABLE" ? `<button class="btn primary" data-claim-food="${item.id}">Claim</button>` : ""}
        ${management ? `<button class="btn secondary" data-expire-food="${item.id}">Mark expired</button>` : ""}
      </div>
    </article>
  `).join("")}</div>`;
}

function claimList(items) {
  if (!items.length) return `<div class="empty">No claims found.</div>`;
  return `<div class="list">${items.map(item => `
    <article class="card">
      <div class="item-head">
        <div>
          <h4>Claim ${escapeHtml(item.id)}</h4>
          <div class="meta">Food ${escapeHtml(item.foodPostId)} · NGO ${escapeHtml(item.ngoId)}</div>
          <div class="meta">Requested ${fmtDate(item.requestedAt)}${item.approvedAt ? ` · Approved ${fmtDate(item.approvedAt)}` : ""}</div>
        </div>
        ${statusBadge(item.status)}
      </div>
      <div class="actions" style="margin-top:12px">
        ${(state.user.role === "ADMIN" || state.user.role === "RESTAURANT") && item.status === "PENDING" ? `
          <button class="btn primary" data-approve-claim="${item.id}">Approve</button>
          <button class="btn danger" data-reject-claim="${item.id}">Reject</button>
        ` : ""}
        ${item.status === "APPROVED" ? `<button class="btn secondary" data-complete-claim="${item.id}">Complete</button>` : ""}
      </div>
    </article>
  `).join("")}</div>`;
}

function userList(items) {
  if (!items.length) return `<div class="empty">No users found.</div>`;
  return `<div class="list">${items.map(item => `
    <article class="card">
      <div class="item-head">
        <div>
          <h4>${escapeHtml(item.fullName)}</h4>
          <div class="meta">${escapeHtml(item.email)} · ${escapeHtml(item.phoneNumber || "")}</div>
          <div class="meta">Joined ${fmtDate(item.createdAt)}</div>
        </div>
        ${statusBadge(item.role)}
      </div>
      <div class="actions" style="margin-top:12px">
        <button class="btn danger" data-delete-user="${item.id}">Delete</button>
      </div>
    </article>
  `).join("")}</div>`;
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function isoFromLocal(value) {
  return new Date(value).toISOString();
}

document.addEventListener("click", async event => {
  const modeBtn = event.target.closest("[data-auth-mode]");
  if (modeBtn) {
    state.mode = modeBtn.dataset.authMode;
    renderAuth();
    return;
  }

  const viewBtn = event.target.closest("[data-view]");
  if (viewBtn) {
    state.view = viewBtn.dataset.view;
    renderApp();
    return;
  }

  if (event.target.closest("#logoutBtn")) {
    clearSession();
    state.view = "overview";
    render();
    return;
  }

  await handleActionButton(event);
});

document.addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.target;
  try {
    if (form.id === "loginForm") {
      const auth = await api("/api/auth/login", { method: "POST", body: formData(form) });
      setSession(auth);
      state.view = "overview";
      render();
    }
    if (form.id === "registerForm") {
      const auth = await api("/api/auth/register", { method: "POST", body: formData(form) });
      setSession(auth);
      state.view = "profile";
      render();
    }
    if (form.id === "foodPostForm") {
      const data = formData(form);
      data.quantity = Number(data.quantity);
      data.latitude = Number(data.latitude);
      data.longitude = Number(data.longitude);
      data.expiryTime = isoFromLocal(data.expiryTime);
      await api("/api/food-posts", { method: "POST", body: data });
      await loadView();
    }
    if (form.id === "nearbyForm") {
      const data = formData(form);
      const result = await api(`/api/food-posts/nearby?latitude=${encodeURIComponent(data.latitude)}&longitude=${encodeURIComponent(data.longitude)}&radiusKm=${encodeURIComponent(data.radiusKm)}`);
      document.getElementById("nearbyResults").innerHTML = foodList(pageItems(result));
    }
    if (form.id === "profileForm") {
      const data = formData(form);
      data.latitude = Number(data.latitude);
      data.longitude = Number(data.longitude);
      const isRestaurant = state.user.role === "RESTAURANT";
      const endpoint = isRestaurant ? "/api/restaurants/me" : "/api/ngos/me";
      const method = form.dataset.existing === "true" ? "PUT" : "POST";
      await api(endpoint, { method, body: data });
      await loadView();
    }
  } catch (error) {
    if (form.id === "loginForm" || form.id === "registerForm") renderAuth(error.message);
    else document.getElementById("viewRoot").insertAdjacentHTML("afterbegin", `<div class="alert error">${escapeHtml(error.message)}</div>`);
  }
});

async function handleActionButton(event) {
  const claimFood = event.target.closest("[data-claim-food]");
  const expireFood = event.target.closest("[data-expire-food]");
  const approveClaim = event.target.closest("[data-approve-claim]");
  const rejectClaim = event.target.closest("[data-reject-claim]");
  const completeClaim = event.target.closest("[data-complete-claim]");
  const deleteUser = event.target.closest("[data-delete-user]");

  try {
    if (claimFood) {
      await api("/api/claims", { method: "POST", body: { foodPostId: claimFood.dataset.claimFood } });
      state.view = "claims";
      renderApp();
    }
    if (expireFood) {
      await api(`/api/food-posts/${expireFood.dataset.expireFood}/status`, { method: "PATCH", body: { status: "EXPIRED" } });
      await loadView();
    }
    if (approveClaim) {
      await api(`/api/claims/${approveClaim.dataset.approveClaim}/approve`, { method: "PATCH" });
      await loadView();
    }
    if (rejectClaim) {
      await api(`/api/claims/${rejectClaim.dataset.rejectClaim}/reject`, { method: "PATCH" });
      await loadView();
    }
    if (completeClaim) {
      await api(`/api/claims/${completeClaim.dataset.completeClaim}/complete`, { method: "PATCH" });
      await loadView();
    }
    if (deleteUser) {
      await api(`/api/admin/users/${deleteUser.dataset.deleteUser}`, { method: "DELETE" });
      await loadView();
    }
  } catch (error) {
    document.getElementById("viewRoot")?.insertAdjacentHTML("afterbegin", `<div class="alert error">${escapeHtml(error.message)}</div>`);
  }
}

render();
