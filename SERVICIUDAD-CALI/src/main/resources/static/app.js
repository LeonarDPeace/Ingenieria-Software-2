// ==================== CONFIGURACIÓN ====================
const API_CONFIG = {
    baseUrl: 'http://localhost:8080',  // Docker expone puerto 8080
    username: 'serviciudad',
    password: 'dev2025',
    timeout: 10000  // 10 segundos
};

// ==================== UTILIDADES ====================

// Crear header de autenticación HTTP Basic Auth
function getAuthHeader() {
    const credentials = btoa(`${API_CONFIG.username}:${API_CONFIG.password}`);
    return `Basic ${credentials}`;
}

// Validar formato de clienteId (10 dígitos)
function validarClienteId(clienteId) {
    const regex = /^\d{10}$/;
    return regex.test(clienteId);
}

// Formatear moneda colombiana (COP)
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    }).format(valor);
}

// Formatear fecha ISO a formato legible
function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Formatear fecha corta
function formatearFechaCorta(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// ==================== VERIFICACIÓN DOCKER ====================

// Verificar conexión con Docker al cargar la página
async function verificarConexionDocker() {
    const statusEl = document.getElementById('dockerStatus');
    try {
        const response = await fetch(`${API_CONFIG.baseUrl}/actuator/health`, {
            method: 'GET',
            signal: AbortSignal.timeout(3000)
        });

        if (response.ok) {
            statusEl.className = 'docker-status';
            statusEl.innerHTML = '🐳 Conectado a Docker (localhost:8080)';
        } else {
            throw new Error('API no responde');
        }
    } catch (error) {
        statusEl.className = 'docker-status disconnected';
        statusEl.innerHTML = '❌ Docker no conectado. Ejecute: docker-compose up -d';
    }
}

// ==================== FUNCIÓN PRINCIPAL ====================

async function consultarDeuda() {
    const clienteId = document.getElementById('clienteId').value.trim();
    const loadingEl = document.getElementById('loading');
    const resultEl = document.getElementById('result');
    const errorEl = document.getElementById('error');
    const consultarBtn = document.getElementById('consultarBtn');

    // Limpiar estados previos
    resultEl.classList.remove('active');
    errorEl.classList.remove('active');

    // Validar input
    if (!validarClienteId(clienteId)) {
        mostrarError(
            'Formato inválido',
            'Por favor ingrese un número de identificación válido de 10 dígitos. Ejemplo: 0001234567'
        );
        return;
    }

    // Mostrar loading
    loadingEl.classList.add('active');
    consultarBtn.disabled = true;

    try {
        // Llamada a la API con timeout
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.timeout);

        const response = await fetch(`${API_CONFIG.baseUrl}/api/deuda/cliente/${clienteId}`, {
            method: 'GET',
            headers: {
                'Authorization': getAuthHeader(),
                'Content-Type': 'application/json'
            },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        // Ocultar loading
        loadingEl.classList.remove('active');
        consultarBtn.disabled = false;

        // Manejo de errores HTTP
        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Error de autenticación. Verifique las credenciales de la API.');
            } else if (response.status === 404) {
                throw new Error(`No se encontraron datos para el cliente ${clienteId}. Verifique que el ID sea correcto.`);
            } else if (response.status === 429) {
                throw new Error('Demasiadas solicitudes. Se excedió el límite de 100 requests por minuto. Por favor espere un momento.');
            } else if (response.status === 500) {
                const errorData = await response.json();
                throw new Error(`Error del servidor: ${errorData.mensaje || 'Error interno'}`);
            } else {
                throw new Error(`Error del servidor (HTTP ${response.status})`);
            }
        }

        const data = await response.json();
        mostrarResultado(data);

    } catch (error) {
        loadingEl.classList.remove('active');
        consultarBtn.disabled = false;

        if (error.name === 'AbortError') {
            mostrarError(
                'Tiempo de espera agotado',
                'La solicitud tardó demasiado tiempo. Verifique que Docker esté corriendo con: docker-compose ps'
            );
        } else {
            mostrarError(
                'Error en la consulta',
                error.message || 'Error al consultar la deuda. Verifique que Docker esté corriendo con: docker-compose up -d'
            );
        }
    }
}

// ==================== MOSTRAR RESULTADO ====================

function mostrarResultado(data) {
    // Datos básicos
    document.getElementById('nombreCliente').textContent = data.nombreCliente || 'No disponible';
    document.getElementById('clienteIdDisplay').textContent = data.clienteId;
    document.getElementById('fechaConsulta').textContent = formatearFecha(data.fechaConsulta);
    document.getElementById('totalAPagar').textContent = formatearMoneda(data.totalAPagar);

    // Estadísticas
    renderEstadisticas(data.estadisticas);

    // Alertas
    renderAlertas(data.alertas);

    // Facturas de acueducto
    renderFacturasAcueducto(data.facturasAcueducto);

    // Consumos de energía
    renderConsumosEnergia(data.consumosEnergia);

    // Mostrar resultado
    document.getElementById('result').classList.add('active');
    
    // Scroll suave al resultado
    document.getElementById('result').scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
    });
}

// Renderizar estadísticas
function renderEstadisticas(stats) {
    const estadisticasEl = document.getElementById('estadisticas');
    estadisticasEl.innerHTML = '';

    if (!stats) return;

    const estadisticas = [
        { 
            icon: '💧', 
            label: 'Facturas Acueducto', 
            value: stats.totalFacturasAcueducto 
        },
        { 
            icon: '📊', 
            label: 'Deuda Acueducto', 
            value: formatearMoneda(stats.deudaAcumuladaAcueducto) 
        },
        { 
            icon: '🚰', 
            label: 'Consumo Acueducto', 
            value: `${stats.totalConsumoAcueducto} m³` 
        },
        { 
            icon: '⚡', 
            label: 'Deuda Energía', 
            value: formatearMoneda(stats.deudaAcumuladaEnergia) 
        },
        { 
            icon: '💡', 
            label: 'Consumo Energía', 
            value: `${stats.totalConsumoEnergia} kWh` 
        },
        { 
            icon: '📈', 
            label: 'Promedio Acueducto', 
            value: `${stats.promedioConsumoAcueducto.toFixed(1)} m³` 
        }
    ];

    estadisticas.forEach(stat => {
        const card = document.createElement('div');
        card.className = 'stat-card';
        card.innerHTML = `
            <div class="stat-icon">${stat.icon}</div>
            <div class="stat-value">${stat.value}</div>
            <div class="stat-label">${stat.label}</div>
        `;
        estadisticasEl.appendChild(card);
    });
}

// Renderizar alertas
function renderAlertas(alertas) {
    const alertasEl = document.getElementById('alertas');
    alertasEl.innerHTML = '';

    if (!alertas || alertas.length === 0) {
        return;
    }

    alertas.forEach(alerta => {
        const alertaDiv = document.createElement('div');
        
        // Determinar tipo de alerta
        if (alerta.includes('VENCIMIENTO_PROXIMO')) {
            alertaDiv.className = 'alert alert-warning';
            alertaDiv.innerHTML = `⚠️ ${alerta}`;
        } else if (alerta.includes('VENCIDA')) {
            alertaDiv.className = 'alert alert-danger';
            alertaDiv.innerHTML = `🚨 ${alerta}`;
        } else {
            alertaDiv.className = 'alert alert-info';
            alertaDiv.innerHTML = `ℹ️ ${alerta}`;
        }
        
        alertasEl.appendChild(alertaDiv);
    });
}

// Renderizar facturas de acueducto
function renderFacturasAcueducto(facturas) {
    const facturasEl = document.getElementById('facturasAcueducto');
    facturasEl.innerHTML = '';

    if (!facturas || facturas.length === 0) {
        facturasEl.innerHTML = '<p style="text-align: center; color: #666;">No hay facturas de acueducto registradas</p>';
        return;
    }

    facturas.forEach(factura => {
        const facturaCard = document.createElement('div');
        facturaCard.className = 'factura-card';
        
        const estadoClass = factura.estado === 'PAGADA' ? 'estado-pagada' : 
                           factura.estado === 'VENCIDA' ? 'estado-vencida' : 'estado-pendiente';
        
        facturaCard.innerHTML = `
            <div class="factura-header">
                <strong>📄 Factura #${factura.id} - Periodo ${factura.periodo}</strong>
                <span class="estado-badge ${estadoClass}">${factura.estado}</span>
            </div>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 10px; margin-top: 10px;">
                <div>
                    <div class="result-label">Consumo</div>
                    <div>${factura.consumoMetrosCubicos} m³</div>
                </div>
                <div>
                    <div class="result-label">Valor</div>
                    <div><strong>${formatearMoneda(factura.valorPagar)}</strong></div>
                </div>
                <div>
                    <div class="result-label">Vencimiento</div>
                    <div>${formatearFechaCorta(factura.fechaVencimiento)}</div>
                </div>
                ${factura.diasHastaVencimiento !== undefined ? `
                <div>
                    <div class="result-label">Días</div>
                    <div>${factura.diasHastaVencimiento} días</div>
                </div>
                ` : ''}
            </div>
        `;
        
        facturasEl.appendChild(facturaCard);
    });
}

// Renderizar consumos de energía
function renderConsumosEnergia(consumos) {
    const energiaEl = document.getElementById('consumosEnergia');
    energiaEl.innerHTML = '';

    if (!consumos || consumos.length === 0) {
        energiaEl.innerHTML = '<p style="text-align: center; color: #666;">No hay consumos de energía registrados</p>';
        return;
    }

    consumos.forEach(consumo => {
        const energiaCard = document.createElement('div');
        energiaCard.className = 'energia-card';
        
        energiaCard.innerHTML = `
            <div class="factura-header">
                <strong>⚡ Periodo ${consumo.periodo}</strong>
                ${consumo.valido ? '<span class="estado-badge estado-pagada">✓ Válido</span>' : '<span class="estado-badge estado-vencida">✗ Inválido</span>'}
            </div>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 10px; margin-top: 10px;">
                <div>
                    <div class="result-label">Consumo</div>
                    <div>${consumo.consumoKwh} kWh</div>
                </div>
                <div>
                    <div class="result-label">Valor</div>
                    <div><strong>${formatearMoneda(consumo.valorPagar)}</strong></div>
                </div>
                <div>
                    <div class="result-label">Fecha Lectura</div>
                    <div>${formatearFechaCorta(consumo.fechaLectura)}</div>
                </div>
            </div>
        `;
        
        energiaEl.appendChild(energiaCard);
    });
}

// ==================== MANEJO DE ERRORES ====================

function mostrarError(titulo, mensaje) {
    const errorEl = document.getElementById('error');
    document.getElementById('errorMessage').innerHTML = `<strong>${titulo}:</strong><br>${mensaje}`;
    errorEl.classList.add('active');
    
    // Scroll al error
    errorEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// ==================== NUEVA CONSULTA ====================

function nuevaConsulta() {
    document.getElementById('clienteId').value = '';
    document.getElementById('result').classList.remove('active');
    document.getElementById('error').classList.remove('active');
    document.getElementById('clienteId').focus();
    
    // Scroll al formulario
    document.querySelector('.form-section').scrollIntoView({ 
        behavior: 'smooth', 
        block: 'center' 
    });
}

// ==================== EVENT LISTENERS ====================

// Permitir consulta con Enter
document.getElementById('clienteId').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        consultarDeuda();
    }
});

// Validación en tiempo real (solo números, máx 10)
document.getElementById('clienteId').addEventListener('input', function(e) {
    this.value = this.value.replace(/\D/g, '').substring(0, 10);
});

// Verificar conexión Docker al cargar página
window.addEventListener('load', verificarConexionDocker);
