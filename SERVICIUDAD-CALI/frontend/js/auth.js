// ==================== CONFIGURACIÓN DE AUTENTICACIÓN ====================
console.log('🔐 auth.js cargado correctamente');

const API_CONFIG = {
    baseUrl: 'http://localhost:8080',
    username: 'serviciudad',
    password: 'dev2025',
    timeout: 5000
};

// ==================== VERIFICAR SESIÓN ====================

// Verificar si el usuario ya está autenticado (para index.html)
function verificarSesion() {
    const isAuthenticated = sessionStorage.getItem('isAuthenticated');
    const currentPage = window.location.pathname;
    
    // Detectar si está en la página principal (raíz / o index.html)
    const isMainPage = currentPage === '/' || currentPage.includes('index.html') || currentPage.endsWith('/');
    const isLoginPage = currentPage.includes('login.html');

    // Si está en index.html (o raíz) y NO está autenticado, redirigir a login
    if (isMainPage && !isAuthenticated) {
        console.log('❌ No autenticado. Redirigiendo a login...');
        window.location.href = 'login.html';
        return false;
    }

    // Si está en login.html y YA está autenticado, redirigir a index
    if (isLoginPage && isAuthenticated) {
        console.log('✅ Ya autenticado. Redirigiendo a index...');
        window.location.href = 'index.html';
        return true;
    }

    return !!isAuthenticated;
}

// ==================== MANEJO DE LOGIN ====================

async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMessageEl = document.getElementById('errorMessage');
    const loginBtn = document.getElementById('loginBtn');

    // Limpiar errores previos
    errorMessageEl.classList.remove('show');
    loginBtn.disabled = true;
    loginBtn.textContent = '🔄 Verificando...';

    try {
        // Verificar credenciales contra el API
        const credentials = btoa(`${username}:${password}`);
        const response = await fetch(`${API_CONFIG.baseUrl}/actuator/health`, {
            method: 'GET',
            headers: {
                'Authorization': `Basic ${credentials}`
            },
            signal: AbortSignal.timeout(API_CONFIG.timeout)
        });

        if (response.ok) {
            // Verificar que las credenciales sean correctas
            if (username === API_CONFIG.username && password === API_CONFIG.password) {
                // Guardar sesión
                sessionStorage.setItem('isAuthenticated', 'true');
                sessionStorage.setItem('username', username);
                sessionStorage.setItem('loginTime', new Date().toISOString());

                console.log('✅ Login exitoso. Redirigiendo...');

                // Mostrar éxito brevemente
                loginBtn.textContent = '✅ ¡Bienvenido!';
                loginBtn.style.background = 'linear-gradient(135deg, #43a047 0%, #66bb6a 100%)';

                // Redirigir a la página principal
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 500);
            } else {
                throw new Error('Credenciales incorrectas');
            }
        } else if (response.status === 401) {
            throw new Error('Usuario o contraseña incorrectos');
        } else {
            throw new Error('Error al verificar credenciales');
        }
    } catch (error) {
        console.error('❌ Error en login:', error);

        let errorMessage = 'Error al iniciar sesión';
        
        if (error.name === 'AbortError') {
            errorMessage = '⏱️ Tiempo de espera agotado. Verifique que Docker esté corriendo.';
        } else if (error.message.includes('incorrectas') || error.message.includes('incorrectos')) {
            errorMessage = '❌ Usuario o contraseña incorrectos. Intente nuevamente.';
        } else if (error.message.includes('fetch')) {
            errorMessage = '🐳 No se puede conectar con el servidor. Verifique que Docker esté corriendo: <code>docker-compose up -d</code>';
        } else {
            errorMessage = `⚠️ ${error.message}`;
        }

        errorMessageEl.innerHTML = errorMessage;
        errorMessageEl.classList.add('show');

        // Restaurar botón
        loginBtn.disabled = false;
        loginBtn.textContent = '🔐 Iniciar Sesión';
    }
}

// ==================== CERRAR SESIÓN ====================

function cerrarSesion() {
    // Limpiar sesión
    sessionStorage.removeItem('isAuthenticated');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('loginTime');

    console.log('👋 Sesión cerrada. Redirigiendo a login...');

    // Redirigir a login
    window.location.href = 'login.html';
}

// ==================== VERIFICAR CONEXIÓN DOCKER (LOGIN) ====================

async function verificarConexionDockerLogin() {
    const statusEl = document.getElementById('dockerStatusLogin');
    
    try {
        const response = await fetch(`${API_CONFIG.baseUrl}/actuator/health`, {
            method: 'GET',
            signal: AbortSignal.timeout(3000)
        });

        if (response.ok) {
            statusEl.className = 'docker-status-login connected';
            statusEl.innerHTML = '🐳 Servidor conectado (localhost:8080)';
        } else {
            throw new Error('API no responde');
        }
    } catch (error) {
        statusEl.className = 'docker-status-login disconnected';
        statusEl.innerHTML = '❌ Servidor desconectado. Ejecute: docker-compose up -d';
    }
}

// ==================== INICIALIZACIÓN ====================

// Verificar conexión Docker al cargar la página de login
if (window.location.pathname.includes('login.html')) {
    window.addEventListener('load', () => {
        console.log('📄 Página de login cargada');
        verificarConexionDockerLogin();
        verificarSesion(); // Por si ya está autenticado
    });
}

// Auto-logout después de 30 minutos de inactividad
let inactivityTimer;

function resetInactivityTimer() {
    clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(() => {
        if (sessionStorage.getItem('isAuthenticated')) {
            alert('⏱️ Su sesión ha expirado por inactividad. Por favor inicie sesión nuevamente.');
            cerrarSesion();
        }
    }, 30 * 60 * 1000); // 30 minutos
}

// Eventos que resetean el timer de inactividad
if (sessionStorage.getItem('isAuthenticated')) {
    ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
        document.addEventListener(event, resetInactivityTimer, true);
    });
    resetInactivityTimer();
}
