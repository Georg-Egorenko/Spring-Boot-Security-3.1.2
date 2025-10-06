class UserManager {
    constructor() {
        this.apiBase = '/api/admin';
        this.userModal = new bootstrap.Modal(document.getElementById('userModal'));
        this.deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'))
        this.roles = [];
        this.userToDelete = null;
        this.init();
    }

    async init() {
        await this.loadRoles();
        await this.loadUsers();
        this.setupEventListeners();
    }

    // Загрузка всех ролей
    async loadRoles() {
        try {
            const response = await fetch(`${this.apiBase}/roles`);
            this.roles = await response.json();
            console.log('Loaded roles:', this.roles);
            this.renderRolesSelect();
        } catch (error) {
            this.showAlert('Error loading roles', 'danger');
            console.error('Error loading roles:', error);
        }
    }

    // Загрузка всех пользователей
    async loadUsers() {
        try {
            const response = await fetch(`${this.apiBase}/users`);
            const users = await response.json();
            this.renderUsersTable(users);
        } catch (error) {
            this.showAlert('Error loading users', 'danger');
            console.error('Error loading users:', error);
        }
    }

    // Отрисовка таблицы пользователей
    renderUsersTable(users) {
        const tbody = document.getElementById('usersTableBody');
        tbody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.firstName || ''}</td>
            <td>${user.lastName || ''}</td>
            <td>${user.age || ''}</td>
            <td>${user.email || ''}</td>
            <td>${user.roles.map(role => role.name).join(', ')}</td>
            <td>
                <button class="btn btn-info btn-sm" onclick="userManager.openEditModal(${user.id})">
                    Edit
                </button>
                <button class="btn btn-danger btn-sm" onclick="userManager.openDeleteModal(${user.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join('');
    }
// Отрисовка select с ролями
    renderRolesSelect() {
        const rolesSelect = document.getElementById('rolesSelect');

        // Очищаем существующие options
        rolesSelect.innerHTML = '';

        // Добавляем роли в select
        this.roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.id;  // Используем ID роли как value
            option.textContent = role.name;  // Отображаем название роли
            rolesSelect.appendChild(option);
        });
    }
    // Открытие модального окна для создания
    openCreateModal() {
        document.getElementById('userModalTitle').textContent = 'Create User';
        document.getElementById('userForm').reset();
        document.getElementById('userId').value = '';
        document.getElementById('password').placeholder = 'Enter password';
        document.getElementById('password').required = true;

        // Сбросить выбор ролей
        const rolesSelect = document.getElementById('rolesSelect');
        Array.from(rolesSelect.options).forEach(option => option.selected = false);

        this.userModal.show();
    }

    // Открытие модального окна для редактирования
    async openEditModal(userId) {
        try {
            const response = await fetch(`${this.apiBase}/users/${userId}`);
            const user = await response.json();

            document.getElementById('userModalTitle').textContent = 'Edit User';
            document.getElementById('userId').value = user.id;
            document.getElementById('username').value = user.username || '';
            document.getElementById('firstName').value = user.firstName || '';
            document.getElementById('lastName').value = user.lastName || '';
            document.getElementById('email').value = user.email || '';
            document.getElementById('age').value = user.age || '';
            document.getElementById('password').value = '';
            document.getElementById('password').placeholder = 'Leave blank to keep current password';
            document.getElementById('password').required = false;

            // Выбор ролей пользователя
            const rolesSelect = document.getElementById('rolesSelect');
            Array.from(rolesSelect.options).forEach(option => {
                const isSelected = user.roles.some(role => role.id == option.value);
                option.selected = isSelected;
            });

            this.userModal.show();
        } catch (error) {
            this.showAlert('Error loading user data', 'danger');
            console.error('Error loading user:', error);
        }
    }

    // Открытие модального окна для удаления
    async openDeleteModal(userId) {
        try {
            // Загружаем данные пользователя
            const response = await fetch(`${this.apiBase}/users/${userId}`);
            const user = await response.json();

            // Заполняем модальное окно данными
            document.getElementById('deleteUserId').textContent = user.id;
            document.getElementById('deleteUsername').textContent = user.username;
            document.getElementById('deleteFirstName').textContent = user.firstName || '-';
            document.getElementById('deleteLastName').textContent = user.lastName || '-';
            document.getElementById('deleteEmail').textContent = user.email || '-';
            document.getElementById('deleteAge').textContent = user.age || '-';
            document.getElementById('deleteRoles').textContent = user.roles.map(role => role.name).join(', ');

            // Сохраняем ID пользователя для удаления
            this.userToDelete = userId;

            // Показываем модальное окно
            this.deleteModal.show();
        } catch (error) {
            this.showAlert('Error loading user data for deletion', 'danger');
            console.error('Error loading user:', error);
        }
    }

// Подтверждение удаления
    async confirmDelete() {
        if (!this.userToDelete) return;

        try {
            const response = await fetch(`${this.apiBase}/users/${this.userToDelete}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.deleteModal.hide();
                this.showAlert('User deleted successfully', 'success');
                await this.loadUsers(); // Обновляем таблицу
            } else {
                throw new Error('Failed to delete user');
            }
        } catch (error) {
            this.showAlert('Error deleting user', 'danger');
            console.error('Error deleting user:', error);
        } finally {
            this.userToDelete = null; // Сбрасываем ID
        }
    }


    // Сохранение пользователя
    // Сохранение пользователя
    async saveUser(event) {
        event.preventDefault();

        const formData = new FormData(event.target);

        // Получаем выбранные роли как объекты
        const rolesSelect = document.getElementById('rolesSelect');
        const selectedRoleIds = Array.from(rolesSelect.selectedOptions).map(option => parseInt(option.value));

        // Создаем объекты Role из ID
        const selectedRoles = selectedRoleIds.map(roleId => ({ id: roleId }));

        const userData = {
            id: formData.get('id') || null,
            username: formData.get('username'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            age: formData.get('age') ? parseInt(formData.get('age')) : null,
            password: formData.get('password'),
            roles: selectedRoles  // Теперь это массив объектов {id: number}
        };

        console.log('Sending user data:', userData); // Для отладки

        try {
            let response;
            if (userData.id) {
                // Обновление
                response = await fetch(`${this.apiBase}/users/${userData.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(userData)
                });
            } else {
                // Создание
                response = await fetch(`${this.apiBase}/users`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(userData)
                });
            }

            if (response.ok) {
                this.userModal.hide();
                this.showAlert(`User ${userData.id ? 'updated' : 'created'} successfully`, 'success');
                await this.loadUsers(); // Обновляем таблицу
            } else {
                throw new Error('Failed to save user');
            }
        } catch (error) {
            this.showAlert('Error saving user', 'danger');
            console.error('Error saving user:', error);
        }
    }

    // Показать уведомление
    showAlert(message, type) {
        const alertContainer = document.getElementById('alertContainer');
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        alertContainer.appendChild(alertDiv);

        // Автоматическое удаление через 5 секунд
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }

    setupEventListeners() {
        document.getElementById('userForm').addEventListener('submit', (e) => this.saveUser(e));
        document.getElementById('confirmDeleteBtn').addEventListener('click', () => this.confirmDelete());
    }
}

// Глобальная переменная для доступа из HTML
let userManager;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    userManager = new UserManager();
});