const API_BASE = '';

// Lấy accessToken từ localStorage
const getAccessToken = () => {
    return localStorage.getItem('accessToken');
};

async function request(url, options = {}) {
    try {
        const accessToken = getAccessToken();
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        // Thêm Authorization header nếu có accessToken
        if (accessToken) {
            headers['Authorization'] = `Bearer ${accessToken}`;
        }

        const response = await fetch(`${API_BASE}${url}`, {
            headers,
            ...options
        });
        
        // Nếu response status 401 → token hết hạn, logout
        if (response.status === 401) {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('user');
            window.location.reload();
            throw new Error('Unauthorized');
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

export const authApi = {
    login: (data) => request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
    logout: () => request('/auth/logout', { method: 'POST' }),
    getAccount: () => request('/auth/account')
};

export const userApi = {
    getAll: () => request('/api/user'),
    getById: (id) => request(`/api/user/${id}`),
    getByUsername: (username) => request(`/api/user?username=${encodeURIComponent(username)}`),
    create: (user) => request('/api/user', { method: 'POST', body: JSON.stringify(user) }),
    update: (id, user) => request(`/api/user/${id}`, { method: 'PUT', body: JSON.stringify(user) }),
    delete: (id) => request(`/api/user/${id}`, { method: 'DELETE' })
};

export const conversationApi = {
    getAll: () => request('/api/conversation'),
    getById: (id) => request(`/api/conversation/${id}`),
    create: (data) => request('/api/conversation', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`/api/conversation/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => request(`/api/conversation/${id}`, { method: 'DELETE' }),
    addUser: (conversationId, userId) => request(`/api/conversation/${conversationId}/user/${userId}`, { method: 'POST' }),
    removeUser: (conversationId, userId) => request(`/api/conversation/${conversationId}/user/${userId}`, { method: 'DELETE' })
};

export const messageApi = {
    getAll: () => request('/api/message'),
    getById: (id) => request(`/api/message/${id}`),
    getByConversationId: (conversationId) => request(`/api/message/conversation/${conversationId}`),
    create: (data) => request('/api/message', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`/api/message/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => request(`/api/message/${id}`, { method: 'DELETE' })
};
