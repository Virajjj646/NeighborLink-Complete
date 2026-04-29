// NeighborLink — API Configuration
const API_BASE = 'http://localhost:8080/api';

// Save logged in user
const Auth = {
    save: (user) => localStorage.setItem('nl_user', JSON.stringify(user)),
    get:  ()     => JSON.parse(localStorage.getItem('nl_user') || 'null'),
    clear:()     => localStorage.removeItem('nl_user'),
    require: ()  => {
        const u = JSON.parse(localStorage.getItem('nl_user') || 'null');
        if (!u) window.location.href = 'index.html';
        return u;
    }
};

// Universal fetch helper
async function apiFetch(method, path, body = null) {
    try {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);
        const res  = await fetch(API_BASE + path, options);
        const data = await res.json();
        return { ok: res.ok, data };
    } catch (err) {
        return { ok: false, data: { message: 'Cannot connect to backend. Is Spring Boot running?' } };
    }
}