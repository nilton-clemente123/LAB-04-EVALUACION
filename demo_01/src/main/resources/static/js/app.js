/* ===================== estado global ===================== */
let API_BASE = document.getElementById('apiBase').value.trim();
let medicos = [];
let especialidades = [];
let medicoFilter = 'TODOS';
let espFilter = 'TODOS';
let medicoEspecialidades = [];

const $ = (sel, root=document) => root.querySelector(sel);
const $$ = (sel, root=document) => Array.from(root.querySelectorAll(sel));

/* ===================== helpers de red ===================== */
async function apiFetch(path, options={}) {
  const res = await fetch(API_BASE + path, {
    headers: {'Content-Type':'application/json'},
    ...options
  });
  let body = null;
  const text = await res.text();
  if (text) { try { body = JSON.parse(text); } catch(e) { body = text; } }
  if (!res.ok) {
    const err = new Error((body && body.message) ? body.message : ('Error ' + res.status));
    err.status = res.status;
    err.fieldErrors = body && body.fieldErrors;
    throw err;
  }
  return body;
}

function toast(msg, isErr=false){
  const wrap = document.getElementById('toast-wrap');
  const el = document.createElement('div');
  el.className = 'toast' + (isErr ? ' err' : '');
  el.textContent = msg;
  wrap.appendChild(el);
  setTimeout(()=>{ el.remove(); }, 4200);
}

async function checkApi(){
  const dot = document.getElementById('apiDot');
  const text = document.getElementById('apiStatusText');
  try {
    await apiFetch('/medicos');
    dot.className = 'dot ok';
    text.textContent = 'Conectado';
  } catch(e){
    dot.className = 'dot bad';
    text.textContent = 'Sin conexión';
  }
}

/* ===================== navegación de pestañas ===================== */
$$('.rail-tab').forEach(tab => {
  tab.addEventListener('click', () => {
    $$('.rail-tab').forEach(t => t.classList.remove('active'));
    tab.classList.add('active');
    const target = tab.dataset.tab;
    document.getElementById('view-medicos').style.display = target === 'medicos' ? '' : 'none';
    document.getElementById('view-especialidades').style.display = target === 'especialidades' ? '' : 'none';
  });
});

document.getElementById('apiBase').addEventListener('change', (e) => {
  API_BASE = e.target.value.trim().replace(/\/$/, '');
  checkApi();
  loadMedicos();
  loadEspecialidades();
});

/* ===================== MEDICOS: carga y render ===================== */
async function loadMedicos(){
  try {
    medicos = await apiFetch('/medicos');
    renderMedicos();
    checkApi();
  } catch(e){
    document.getElementById('tblMedicos').innerHTML = '';
    const empty = document.getElementById('emptyMedicos');
    empty.style.display = '';
    empty.innerHTML = `<h3>No se pudo cargar el registro</h3><p>${escapeHtml(e.message)}. Verifica la URL del backend y que el servidor esté corriendo.</p>`;
    document.getElementById('apiDot').className = 'dot bad';
    document.getElementById('apiStatusText').textContent = 'Sin conexión';
  }
}

function renderMedicos(){
  const q = document.getElementById('searchMedico').value.trim().toLowerCase();
  const tbody = document.getElementById('tblMedicos');
  const empty = document.getElementById('emptyMedicos');
  let list = medicos.filter(m => medicoFilter === 'TODOS' || m.estado === medicoFilter);
  if (q) {
    list = list.filter(m => [m.codigo, m.nombres, m.apellidoPaterno, m.apellidoMaterno, m.cmp, m.numeroDocumento]
      .filter(Boolean).join(' ').toLowerCase().includes(q));
  }
  if (!list.length){
    tbody.innerHTML = '';
    empty.style.display = '';
    empty.innerHTML = medicos.length
      ? '<h3>Sin resultados</h3><p>Ningún médico coincide con la búsqueda o filtro actual.</p>'
      : '<h3>Todavía no hay médicos</h3><p>Registra el primero con “Registrar médico”.</p>';
    return;
  }
  empty.style.display = 'none';
  tbody.innerHTML = list.map(m => `
    <tr data-id="${m.id}" class="row-medico">
      <td class="cell-code">${escapeHtml(m.codigo)}</td>
      <td>
        <div class="cell-name">${escapeHtml(m.nombres)} ${escapeHtml(m.apellidoPaterno)} ${escapeHtml(m.apellidoMaterno)}</div>
      </td>
      <td>${escapeHtml(m.tipoDocumento)} · ${escapeHtml(m.numeroDocumento)}</td>
      <td class="cell-code">${escapeHtml(m.cmp)}</td>
      <td>
        <span class="badge ${m.estado === 'ACTIVO' ? 'on' : 'off'}">
          <span class="dot" style="background:currentColor"></span>${m.estado === 'ACTIVO' ? 'Activo' : 'Inactivo'}
        </span>
      </td>
      <td class="row-actions">
        <button class="btn-text" data-toggle-medico="${m.id}">${m.estado === 'ACTIVO' ? 'Desactivar' : 'Activar'}</button>
      </td>
    </tr>
  `).join('');

  $$('.row-medico').forEach(row => {
    row.addEventListener('click', (e) => {
      if (e.target.closest('[data-toggle-medico]')) return;
      openMedicoPanel(medicos.find(m => String(m.id) === row.dataset.id));
    });
  });
  $$('[data-toggle-medico]').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.stopPropagation();
      const id = btn.dataset.toggleMedico;
      const m = medicos.find(x => String(x.id) === id);
      const nuevo = m.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
      try {
        await apiFetch(`/medicos/${id}/estado`, { method:'PATCH', body: JSON.stringify({estado: nuevo}) });
        toast(`Médico ${nuevo === 'ACTIVO' ? 'activado' : 'desactivado'}.`);
        loadMedicos();
      } catch(err){ toast(err.message, true); }
    });
  });
}

document.getElementById('searchMedico').addEventListener('input', renderMedicos);
document.getElementById('filterMedico').addEventListener('click', (e) => {
  const chip = e.target.closest('.filter-chip');
  if (!chip) return;
  $$('.filter-chip', document.getElementById('filterMedico')).forEach(c => c.classList.remove('active'));
  chip.classList.add('active');
  medicoFilter = chip.dataset.estado;
  renderMedicos();
});

/* ===================== MEDICOS: panel / formulario ===================== */
const panelMedico = document.getElementById('panelMedico');
const scrim = document.getElementById('scrim');

function openMedicoPanel(medico){
  clearMedicoErrors();
  document.getElementById('medicoFormAlert').innerHTML = '';
  const isEdit = !!medico;
  document.getElementById('medicoPanelTag').textContent = isEdit ? 'EDITAR FICHA' : 'NUEVA FICHA';
  document.getElementById('medicoPanelTitle').textContent = isEdit ? 'Editar médico' : 'Registrar médico';
  document.getElementById('medicoId').value = medico ? medico.id : '';
  document.getElementById('tipoDocumento').value = medico ? medico.tipoDocumento : 'DNI';
  document.getElementById('numeroDocumento').value = medico ? medico.numeroDocumento : '';
  document.getElementById('nombres').value = medico ? medico.nombres : '';
  document.getElementById('apellidoPaterno').value = medico ? medico.apellidoPaterno : '';
  document.getElementById('apellidoMaterno').value = medico ? medico.apellidoMaterno : '';
  document.getElementById('cmp').value = medico ? medico.cmp : '';
  setEstadoToggle('medicoEstadoToggle', medico ? medico.estado : 'ACTIVO');
  document.getElementById('btnDeleteMedico').style.display = isEdit ? '' : 'none';
  document.getElementById('btnRemoveMedico').style.display = isEdit ? '' : 'none';

  const espAssign = document.getElementById('espAssign');
  const espHint = document.getElementById('espHint');
  if (isEdit) {
    espAssign.style.display = '';
    espHint.style.display = 'none';
    loadMedicoEspecialidades(medico.id);
  } else {
    espAssign.style.display = 'none';
    espHint.style.display = '';
    renderMedicoEspecialidades([]);
  }

  openPanel(panelMedico);
}

/* ===================== MEDICOS: especialidades asignadas ===================== */
async function loadMedicoEspecialidades(medicoId){
  try {
    medicoEspecialidades = await apiFetch(`/medicos/${medicoId}/especialidades`);
    renderMedicoEspecialidades(medicoEspecialidades);
    populateEspecialidadSelect();
  } catch(err){
    medicoEspecialidades = [];
    renderMedicoEspecialidades([]);
    toast(err.message, true);
  }
}

function renderMedicoEspecialidades(list){
  const wrap = document.getElementById('medicoEspecialidades');
  if (!list || !list.length){
    wrap.innerHTML = '<span class="esp-empty">Sin especialidades asignadas.</span>';
    return;
  }
  wrap.innerHTML = list.map(e => `
    <span class="esp-chip">
      ${escapeHtml(e.nombre)}
      <button type="button" data-quitar-esp="${e.id}" title="Quitar especialidad">&times;</button>
    </span>
  `).join('');
}

function populateEspecialidadSelect(){
  const sel = document.getElementById('selectEspecialidad');
  const asignados = new Set((medicoEspecialidades || []).map(e => String(e.id)));
  const disponibles = especialidades.filter(e => !asignados.has(String(e.id)));
  sel.innerHTML = '<option value="">Selecciona una especialidad…</option>' +
    disponibles.map(e => `<option value="${e.id}">${escapeHtml(e.nombre)}</option>`).join('');
}

document.getElementById('btnAsignarEspecialidad').addEventListener('click', async () => {
  const medicoId = document.getElementById('medicoId').value;
  const espId = document.getElementById('selectEspecialidad').value;
  if (!medicoId || !espId){ toast('Selecciona una especialidad para asignar.', true); return; }
  try {
    await apiFetch(`/medicos/${medicoId}/especialidades/${espId}`, { method:'POST' });
    toast('Especialidad asignada.');
    loadMedicoEspecialidades(medicoId);
  } catch(err){ toast(err.message, true); }
});

document.getElementById('medicoEspecialidades').addEventListener('click', async (e) => {
  const btn = e.target.closest('[data-quitar-esp]');
  if (!btn) return;
  const medicoId = document.getElementById('medicoId').value;
  const espId = btn.dataset.quitarEsp;
  try {
    await apiFetch(`/medicos/${medicoId}/especialidades/${espId}`, { method:'DELETE' });
    toast('Especialidad quitada.');
    loadMedicoEspecialidades(medicoId);
  } catch(err){ toast(err.message, true); }
});

function setEstadoToggle(id, val){
  const wrap = document.getElementById(id);
  wrap.dataset.value = val;
  $$('button', wrap).forEach(b => {
    b.classList.remove('sel-on','sel-off');
    if (b.dataset.val === val){
      b.classList.add((val === 'ACTIVO' || val === 'ACTIVA') ? 'sel-on' : 'sel-off');
    }
  });
}
$$('.estado-toggle').forEach(wrap => {
  wrap.addEventListener('click', (e) => {
    const btn = e.target.closest('button');
    if (!btn) return;
    setEstadoToggle(wrap.id, btn.dataset.val);
  });
});

function clearMedicoErrors(){
  $$('#formMedico .field').forEach(f => f.classList.remove('has-err'));
  $$('#formMedico .err').forEach(e => e.textContent = '');
}

document.getElementById('btnSaveMedico').addEventListener('click', async () => {
  clearMedicoErrors();
  document.getElementById('medicoFormAlert').innerHTML = '';
  const id = document.getElementById('medicoId').value;
  const payload = {
    tipoDocumento: document.getElementById('tipoDocumento').value,
    numeroDocumento: document.getElementById('numeroDocumento').value.trim(),
    nombres: document.getElementById('nombres').value.trim(),
    apellidoPaterno: document.getElementById('apellidoPaterno').value.trim(),
    apellidoMaterno: document.getElementById('apellidoMaterno').value.trim(),
    cmp: document.getElementById('cmp').value.trim(),
    estado: document.getElementById('medicoEstadoToggle').dataset.value || 'ACTIVO'
  };

  const missing = ['numeroDocumento','nombres','apellidoPaterno','apellidoMaterno','cmp']
    .filter(k => !payload[k]);
  if (missing.length){
    missing.forEach(k => showFieldError('f-' + k, 'Este campo es obligatorio.'));
    return;
  }

  try {
    if (id) {
      await apiFetch(`/medicos/${id}`, { method:'PUT', body: JSON.stringify(payload) });
      toast('Ficha del médico actualizada.');
    } else {
      await apiFetch('/medicos', { method:'POST', body: JSON.stringify(payload) });
      toast('Médico registrado.');
    }
    closePanel(panelMedico);
    loadMedicos();
  } catch(err){
    handleFormError(err, 'medicoFormAlert', {
      tipoDocumento:'f-tipoDocumento', numeroDocumento:'f-numeroDocumento',
      nombres:'f-nombres', apellidoPaterno:'f-apellidoPaterno', apellidoMaterno:'f-apellidoMaterno', cmp:'f-cmp'
    });
  }
});

document.getElementById('btnDeleteMedico').addEventListener('click', async () => {
  const id = document.getElementById('medicoId').value;
  if (!id) return;
  try {
    await apiFetch(`/medicos/${id}/estado`, { method:'PATCH', body: JSON.stringify({estado:'INACTIVO'}) });
    toast('Médico desactivado.');
    closePanel(panelMedico);
    loadMedicos();
  } catch(err){ toast(err.message, true); }
});

document.getElementById('btnRemoveMedico').addEventListener('click', async () => {
  const id = document.getElementById('medicoId').value;
  if (!id) return;
  if (!confirm('¿Eliminar definitivamente este médico? También se eliminarán sus especialidades asignadas.')) return;
  try {
    await apiFetch(`/medicos/${id}`, { method:'DELETE' });
    toast('Médico eliminado.');
    closePanel(panelMedico);
    loadMedicos();
  } catch(err){ toast(err.message, true); }
});

document.getElementById('btnNewMedico').addEventListener('click', () => openMedicoPanel(null));

/* ===================== ESPECIALIDADES: carga y render ===================== */
async function loadEspecialidades(){
  try {
    especialidades = await apiFetch('/especialidades');
    renderEspecialidades();
  } catch(e){
    document.getElementById('tblEspecialidades').innerHTML = '';
    const empty = document.getElementById('emptyEspecialidades');
    empty.style.display = '';
    empty.innerHTML = `<h3>No se pudo cargar el catálogo</h3><p>${escapeHtml(e.message)}. Verifica la URL del backend y que el servidor esté corriendo.</p>`;
  }
}

function renderEspecialidades(){
  const q = document.getElementById('searchEspecialidad').value.trim().toLowerCase();
  const tbody = document.getElementById('tblEspecialidades');
  const empty = document.getElementById('emptyEspecialidades');
  let list = especialidades.filter(e => espFilter === 'TODOS' || e.estado === espFilter);
  if (q) list = list.filter(e => (e.codigo + ' ' + e.nombre).toLowerCase().includes(q));

  if (!list.length){
    tbody.innerHTML = '';
    empty.style.display = '';
    empty.innerHTML = especialidades.length
      ? '<h3>Sin resultados</h3><p>Ninguna especialidad coincide con la búsqueda o filtro actual.</p>'
      : '<h3>Todavía no hay especialidades</h3><p>Registra la primera con “Registrar especialidad”.</p>';
    return;
  }
  empty.style.display = 'none';
  tbody.innerHTML = list.map(e => `
    <tr>
      <td class="cell-code">${escapeHtml(e.codigo)}</td>
      <td class="row-esp" data-id="${e.id}" style="cursor:pointer;">
        <div class="cell-name">${escapeHtml(e.nombre)}</div>
        ${e.descripcion ? `<div class="cell-sub">${escapeHtml(e.descripcion)}</div>` : ''}
      </td>
      <td>
        <div class="duracion-inline">
          <input type="number" min="1" value="${e.duracionConsulta}" data-duracion="${e.id}">
          <span style="color:var(--text-mute);font-size:12.5px;">min</span>
          <button data-save-duracion="${e.id}">Guardar</button>
        </div>
      </td>
      <td>
        <span class="badge ${e.estado === 'ACTIVA' ? 'on' : 'off'}">
          <span class="dot" style="background:currentColor"></span>${e.estado === 'ACTIVA' ? 'Activa' : 'Inactiva'}
        </span>
      </td>
      <td class="row-actions">
        <button class="btn-text" data-toggle-esp="${e.id}">${e.estado === 'ACTIVA' ? 'Desactivar' : 'Activar'}</button>
      </td>
    </tr>
  `).join('');

  $$('.row-esp').forEach(cell => {
    cell.addEventListener('click', () => openEspecialidadPanel(especialidades.find(e => String(e.id) === cell.dataset.id)));
  });
  $$('[data-toggle-esp]').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.stopPropagation();
      const id = btn.dataset.toggleEsp;
      const esp = especialidades.find(x => String(x.id) === id);
      const nuevo = esp.estado === 'ACTIVA' ? 'INACTIVA' : 'ACTIVA';
      try {
        await apiFetch(`/especialidades/${id}/estado`, { method:'PATCH', body: JSON.stringify({estado: nuevo}) });
        toast(`Especialidad ${nuevo === 'ACTIVA' ? 'activada' : 'desactivada'}.`);
        loadEspecialidades();
      } catch(err){ toast(err.message, true); }
    });
  });
  $$('[data-save-duracion]').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.stopPropagation();
      const id = btn.dataset.saveDuracion;
      const input = document.querySelector(`[data-duracion="${id}"]`);
      const val = parseInt(input.value, 10);
      if (!val || val <= 0){ toast('La duración debe ser mayor que cero.', true); return; }
      try {
        await apiFetch(`/especialidades/${id}/duracion`, { method:'PATCH', body: JSON.stringify({duracionConsulta: val}) });
        toast('Duración de consulta actualizada.');
        loadEspecialidades();
      } catch(err){ toast(err.message, true); }
    });
  });
}

document.getElementById('searchEspecialidad').addEventListener('input', renderEspecialidades);
document.getElementById('filterEspecialidad').addEventListener('click', (e) => {
  const chip = e.target.closest('.filter-chip');
  if (!chip) return;
  $$('.filter-chip', document.getElementById('filterEspecialidad')).forEach(c => c.classList.remove('active'));
  chip.classList.add('active');
  espFilter = chip.dataset.estado;
  renderEspecialidades();
});

/* ===================== ESPECIALIDADES: panel / formulario ===================== */
const panelEspecialidad = document.getElementById('panelEspecialidad');

function openEspecialidadPanel(esp){
  clearEspErrors();
  document.getElementById('espFormAlert').innerHTML = '';
  const isEdit = !!esp;
  document.getElementById('espPanelTag').textContent = isEdit ? 'EDITAR ENTRADA' : 'NUEVA ENTRADA';
  document.getElementById('espPanelTitle').textContent = isEdit ? 'Editar especialidad' : 'Registrar especialidad';
  document.getElementById('espId').value = esp ? esp.id : '';
  document.getElementById('enombre').value = esp ? esp.nombre : '';
  document.getElementById('edescripcion').value = esp ? (esp.descripcion || '') : '';
  document.getElementById('eduracion').value = esp ? esp.duracionConsulta : '';
  setEstadoToggle('espEstadoToggle', esp ? esp.estado : 'ACTIVA');
  document.getElementById('btnDeleteEspecialidad').style.display = isEdit ? '' : 'none';
  document.getElementById('btnRemoveEspecialidad').style.display = isEdit ? '' : 'none';
  openPanel(panelEspecialidad);
}

function clearEspErrors(){
  $$('#formEspecialidad .field').forEach(f => f.classList.remove('has-err'));
  $$('#formEspecialidad .err').forEach(e => e.textContent = '');
}

document.getElementById('btnSaveEspecialidad').addEventListener('click', async () => {
  clearEspErrors();
  document.getElementById('espFormAlert').innerHTML = '';
  const id = document.getElementById('espId').value;
  const duracion = parseInt(document.getElementById('eduracion').value, 10);
  const payload = {
    nombre: document.getElementById('enombre').value.trim(),
    descripcion: document.getElementById('edescripcion').value.trim(),
    duracionConsulta: duracion,
    estado: document.getElementById('espEstadoToggle').dataset.value || 'ACTIVA'
  };

  let hasErr = false;
  if (!payload.nombre){ showFieldError('f-enombre','Este campo es obligatorio.'); hasErr = true; }
  if (!duracion || duracion <= 0){ showFieldError('f-eduracion','Ingresa una duración mayor que cero.'); hasErr = true; }
  if (hasErr) return;

  try {
    if (id) {
      await apiFetch(`/especialidades/${id}`, { method:'PUT', body: JSON.stringify(payload) });
      toast('Especialidad actualizada.');
    } else {
      await apiFetch('/especialidades', { method:'POST', body: JSON.stringify(payload) });
      toast('Especialidad registrada.');
    }
    closePanel(panelEspecialidad);
    loadEspecialidades();
  } catch(err){
    handleFormError(err, 'espFormAlert', {
      nombre:'f-enombre', duracionConsulta:'f-eduracion'
    });
  }
});

document.getElementById('btnDeleteEspecialidad').addEventListener('click', async () => {
  const id = document.getElementById('espId').value;
  if (!id) return;
  try {
    await apiFetch(`/especialidades/${id}/estado`, { method:'PATCH', body: JSON.stringify({estado:'INACTIVA'}) });
    toast('Especialidad desactivada.');
    closePanel(panelEspecialidad);
    loadEspecialidades();
  } catch(err){ toast(err.message, true); }
});

document.getElementById('btnRemoveEspecialidad').addEventListener('click', async () => {
  const id = document.getElementById('espId').value;
  if (!id) return;
  if (!confirm('¿Eliminar definitivamente esta especialidad? También se eliminarán sus asignaciones a médicos.')) return;
  try {
    await apiFetch(`/especialidades/${id}`, { method:'DELETE' });
    toast('Especialidad eliminada.');
    closePanel(panelEspecialidad);
    loadEspecialidades();
  } catch(err){ toast(err.message, true); }
});

document.getElementById('btnNewEspecialidad').addEventListener('click', () => openEspecialidadPanel(null));

/* ===================== panel/scrim genérico ===================== */
function openPanel(panel){
  panel.classList.add('open');
  scrim.classList.add('open');
}
function closePanel(panel){
  panel.classList.remove('open');
  scrim.classList.remove('open');
}
scrim.addEventListener('click', () => { closePanel(panelMedico); closePanel(panelEspecialidad); });
$$('[data-close]').forEach(btn => btn.addEventListener('click', () => { closePanel(panelMedico); closePanel(panelEspecialidad); }));
document.addEventListener('keydown', (e) => { if (e.key === 'Escape'){ closePanel(panelMedico); closePanel(panelEspecialidad); } });

/* ===================== utilidades ===================== */
function showFieldError(fieldId, msg){
  const field = document.getElementById(fieldId);
  if (!field) return;
  field.classList.add('has-err');
  const err = field.querySelector('.err');
  if (err) err.textContent = msg;
}

function handleFormError(err, alertId, fieldMap){
  const alertBox = document.getElementById(alertId);
  if (err.status === 400 && err.fieldErrors){
    Object.entries(err.fieldErrors).forEach(([key, msg]) => {
      const fieldId = fieldMap[key];
      if (fieldId) showFieldError(fieldId, msg);
    });
    alertBox.innerHTML = `<div class="form-alert">Revisa los campos marcados: hay datos inválidos.</div>`;
  } else {
    alertBox.innerHTML = `<div class="form-alert">${escapeHtml(err.message)}</div>`;
  }
}

function escapeHtml(str){
  return String(str ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}

/* ===================== arranque ===================== */
checkApi();
loadMedicos();
loadEspecialidades();