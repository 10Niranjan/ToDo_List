const taskList = document.getElementById('taskList');
const taskInput = document.getElementById('taskInput');
const addTaskBtn = document.getElementById('addTaskBtn');
const filterBtns = document.querySelectorAll('.filter-btn');

let currentFilter = 'all';


fetchTasks();

addTaskBtn.addEventListener('click', addTask);
taskInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') addTask();
});

filterBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelector('.filter-btn.active').classList.remove('active');
        btn.classList.add('active');
        currentFilter = btn.dataset.filter;
        renderTasks(currentTasks);
    });
});

let currentTasks = [];

async function fetchTasks() {
    try {
        const res = await fetch('/api/tasks');
        const tasks = await res.json();
        currentTasks = tasks;
        renderTasks(tasks);
    } catch (e) {
        console.error('Failed to fetch tasks', e);
    }
}

async function addTask() {
    const title = taskInput.value.trim();
    if (!title) return;

    try {
        const res = await fetch('/api/tasks', {
            method: 'POST',
            body: JSON.stringify({ title: title })
        });
        if (res.ok) {
            taskInput.value = '';
            fetchTasks();
        }
    } catch (e) {
        console.error('Failed to add task', e);
    }
}

async function toggleStatus(id, currentStatus) {
    const newStatus = currentStatus === 'completed' ? 'ongoing' : 'completed';
    
    try {
        const res = await fetch(`/api/tasks/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ status: newStatus })
        });
        if (res.ok) fetchTasks();
    } catch (e) {
        console.error('Failed to update status', e);
    }
}

function editTask(id, currentTitle) {
    const taskItem = document.querySelector(`li[data-id="${id}"]`);
    if (!taskItem) return;
    
    const contentDiv = taskItem.querySelector('.task-content');

    const originalContent = contentDiv.innerHTML;
    
    contentDiv.innerHTML = `
        <input type="text" class="edit-input" id="edit-${id}" value="${escapeQuote(currentTitle)}">
        <div class="edit-actions">
            <button class="save-btn" onclick="saveEdit(${id})">Save</button>
            <button class="cancel-btn" onclick="fetchTasks()">Cancel</button>
        </div>
    `;
    
    const input = document.getElementById(`edit-${id}`);
    input.focus();
    input.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') saveEdit(id);
    });
}

async function saveEdit(id) {
    const input = document.getElementById(`edit-${id}`);
    const newTitle = input.value.trim();
    if (!newTitle) return;

    try {
        const res = await fetch(`/api/tasks/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ title: newTitle })
        });
        if (res.ok) fetchTasks();
    } catch (e) {
        console.error('Failed to update task', e);
    }
}

async function deleteTask(id) {
    if(!confirm('Are you sure you want to delete this task?')) return;
    
    try {
        const res = await fetch(`/api/tasks/${id}`, {
            method: 'DELETE'
        });
        if (res.ok) fetchTasks();
    } catch (e) {
        console.error('Failed to delete task', e);
    }
}

function renderTasks(tasks) {
    taskList.innerHTML = '';
    
    const filteredTasks = tasks.filter(task => {
        if (currentFilter === 'all') return true;
        return task.status === currentFilter;
    });

    filteredTasks.forEach(task => {
        const li = document.createElement('li');
        li.className = `task-item status-${task.status}`;
        li.setAttribute('data-id', task.id);
        

        li.innerHTML = `
            <div class="task-content">
                <span class="task-title">${escapeHtml(task.title)}</span>
                <span class="status-badge">${task.status}</span>
            </div>
            <div class="task-actions">
                <button class="action-btn edit-btn" onclick="editTask(${task.id}, '${escapeQuote(task.title)}')" title="Edit" aria-label="Edit">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
                <button class="action-btn toggle-btn" onclick="toggleStatus(${task.id}, '${task.status}')" title="${task.status === 'completed' ? 'Mark Undone' : 'Mark Done'}" aria-label="Toggle Status">
                    ${task.status === 'completed' 
                        ? '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 14 4 9 9 4"></polyline><path d="M20 20v-7a4 4 0 0 0-4-4H4"></path></svg>'
                        : '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>'
                    }
                </button>
                <button class="action-btn delete-btn" onclick="deleteTask(${task.id})" title="Delete" aria-label="Delete">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                </button>
            </div>
        `;
        taskList.appendChild(li);
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function escapeQuote(text) {
    return text.replace(/'/g, "\\'").replace(/"/g, '&quot;');
}

window.toggleStatus = toggleStatus;
window.editTask = editTask;
window.saveEdit = saveEdit;
window.deleteTask = deleteTask;
