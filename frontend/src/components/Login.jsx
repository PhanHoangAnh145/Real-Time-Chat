import { useState } from 'react';
import { authApi } from '../services/api';

function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await authApi.login({ username, password });
      if (response.data && response.data.user && response.data.accessToken) {
        // Lưu accessToken và user vào localStorage
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        onLogin(response.data.user);
      } else {
        setError('Đăng nhập thất bại!');
      }
    } catch (err) {
      setError('Sai tài khoản hoặc mật khẩu! Vui lòng thử lại.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-wrapper">
        <div className="login-left">
          <div className="illustration-circle">
            <div className="floating-icons">
              <span className="icon small blue">○</span>
              <span className="icon tiny green">△</span>
              <span className="icon small cyan">○</span>
              <span className="icon tiny green">△</span>
            </div>
            <div className="avatar-placeholder">
              <svg width="80" height="100" viewBox="0 0 80 100" fill="none">
                <rect x="10" y="40" width="60" height="50" rx="4" fill="#4A5568"/>
                <rect x="20" y="15" width="40" height="30" rx="20" fill="#4A5568"/>
                <line x1="25" y1="55" x2="55" y2="55" stroke="#718096" strokeWidth="3"/>
                <line x1="25" y1="65" x2="50" y2="65" stroke="#718096" strokeWidth="3"/>
              </svg>
            </div>
          </div>
        </div>
        <div className="login-right">
          <div className="login-box">
            <h2>Member Login</h2>
            <form onSubmit={handleLogin}>
              <div className="input-group">
                <div className="input-icon">👤</div>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Username"
                  required
                />
              </div>
              <div className="input-group">
                <div className="input-icon">🔒</div>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                  required
                />
              </div>
              {error && <div className="error-message">{error}</div>}
              <button
                type="submit"
                className="btn-login"
                disabled={isLoading}
              >
                {isLoading ? (
                  <div className="spinner"></div>
                ) : (
                  'LOGIN'
                )}
              </button>
            </form>
            <div className="forgot-password">
              Forgot Username / Password?
            </div>
            <div className="create-account">
              Create your Account →
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
